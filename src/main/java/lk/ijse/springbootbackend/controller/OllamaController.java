package lk.ijse.springbootbackend.controller;
//
//import com.example.colloqaibackend.service.GeminiService;
//import org.springframework.web.bind.annotation.*;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/v1/interview")
//@CrossOrigin(origins = "http://localhost:3000") // Frontend permission
//public class InterviewController {
//
//    private final GeminiService geminiService;
//
//    public InterviewController(GeminiService geminiService) {
//        this.geminiService = geminiService;
//    }
//
//    @PostMapping("/chat")
//    public Map<String, String> chat(@RequestBody Map<String, String> request) {
//        String name = request.get("userName");
//        String level = request.get("level");
//        String role = request.get("role");
//        String userMsg = request.get("userMessage");
//
//        String systemPersona = String.format(
//                "You are a professional technical interviewer for a %s role at %s level. " +
//                        "The candidate's name is %s. Ask one technical question at a time. Be concise.",
//                role, level, name
//        );
//
//        String aiResponse = geminiService.getInterviewResponse(systemPersona, userMsg);
//        return Map.of("response", aiResponse);
//    }
//}



import lk.ijse.springbootbackend.service.impl.OllamaService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/interview")
@CrossOrigin(origins = "http://localhost:3000")
public class OllamaController {

    private final OllamaService ollamaService;

    public OllamaController(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        String name = request.get("userName");
        String level = request.get("level");
        String role = request.get("role");
        String userMsg = request.get("userMessage");

        String systemPersona = String.format(
                "You are a professional technical interviewer. You are interviewing %s for a %s position at %s level. " +
                        "Provide exactly one technical question and wait for the response. Keep it brief.",
                name, role, level
        );

        String aiResponse = ollamaService.getInterviewResponse(systemPersona, userMsg);
        return Map.of("response", aiResponse);
    }
}