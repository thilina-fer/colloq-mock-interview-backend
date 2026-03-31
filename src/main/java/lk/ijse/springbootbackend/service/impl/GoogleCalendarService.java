package lk.ijse.springbootbackend.service.impl;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "ColloQ Mock Interview";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens"; // Token එක සේව් වෙන තැන
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);

    // 💡 මෙතනින් තමයි අලුත් ක්‍රමයට ලොග් වෙන්නේ
    private Credential getCredentials(NetHttpTransport HTTP_TRANSPORT) throws Exception {
        InputStream in = getClass().getResourceAsStream("/credentials.json");
        if (in == null) {
            throw new RuntimeException("credentials.json file not found in resources!");
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(-1).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public String createMeetLink(String title, String description, java.util.Date startDate, java.util.Date endDate) {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            Event event = new Event().setSummary(title).setDescription(description);
            event.setStart(new EventDateTime().setDateTime(new com.google.api.client.util.DateTime(startDate)).setTimeZone("Asia/Colombo"));
            event.setEnd(new EventDateTime().setDateTime(new com.google.api.client.util.DateTime(endDate)).setTimeZone("Asia/Colombo"));

            // ✨ Google Meet Setup
            ConferenceSolutionKey solutionKey = new ConferenceSolutionKey().setType("hangoutsMeet");
            CreateConferenceRequest createRequest = new CreateConferenceRequest()
                    .setRequestId(UUID.randomUUID().toString())
                    .setConferenceSolutionKey(solutionKey);
            ConferenceData conferenceData = new ConferenceData().setCreateRequest(createRequest);
            event.setConferenceData(conferenceData);

            // 🎯 Insert event with conferenceDataVersion = 1
            Event createdEvent = service.events().insert("primary", event)
                    .setConferenceDataVersion(1)
                    .execute();

            // 🔄 Retry logic to get the link
            String meetLink = null;
            for (int i = 0; i < 5; i++) {
                if (createdEvent.getConferenceData() != null &&
                        createdEvent.getConferenceData().getEntryPoints() != null) {
                    meetLink = createdEvent.getConferenceData().getEntryPoints().get(0).getUri();
                    break;
                }
                Thread.sleep(1500); // පොඩි වෙලාවක් ඉමු Google එක ලින්ක් එක හදනකම්
                createdEvent = service.events().get("primary", createdEvent.getId()).execute();
            }

            return meetLink != null ? meetLink : "Link generation failed.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}