package lk.ijse.springbootbackend.controller;

import lk.ijse.springbootbackend.dto.EvaluationRequestDTO;
import lk.ijse.springbootbackend.service.impl.EmailService;
import lk.ijse.springbootbackend.service.impl.OllamaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/interview")
@CrossOrigin(origins = "http://localhost:5173")
public class OllamaController {

    private final OllamaService ollamaService;
    private final EmailService emailService;

    public OllamaController(OllamaService ollamaService, EmailService emailService) {
        this.ollamaService = ollamaService;
        this.emailService = emailService;
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

    @PostMapping("/evaluate")
    public ResponseEntity<?> evaluateInterview(@RequestBody EvaluationRequestDTO request) {
        StringBuilder transcript = new StringBuilder();
        for (Map<String, String> msg : request.getChatHistory()) {
            transcript.append(msg.get("speaker").toUpperCase())
                    .append(": ")
                    .append(msg.get("text"))
                    .append("\n\n");
        }

        String systemPrompt = "You are an expert IT Interview Evaluator. Evaluate the following interview transcript for a "
                + request.getLevel() + " " + request.getRole() + " developer.\n\n"
                + "TRANSCRIPT:\n" + transcript.toString() + "\n\n"
                + "CRITICAL INSTRUCTION: You MUST return ONLY a valid JSON object. Do not include any markdown formatting (like ```json), no explanations, and no <think> tags in your final output. "
                + "The JSON MUST exactly match this structure:\n"
                + "{\n"
                + "  \"score\": \"X/10\",\n"
                + "  \"strengths\": [\"strength 1\", \"strength 2\"],\n"
                + "  \"weaknesses\": [\"weakness 1\", \"weakness 2\"],\n"
                + "  \"finalFeedback\": \"detailed feedback paragraph\"\n"
                + "}";

        String jsonReport = ollamaService.getInterviewResponse(systemPrompt, "Provide the evaluation JSON now.");
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {

            String htmlBody = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f5; margin: 0; padding: 40px 20px; }
                        .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }
                        .header { background-color: #FF6B00; padding: 30px; text-align: center; }
                        .header h1 { color: #ffffff; margin: 0; font-size: 28px; letter-spacing: 2px; }
                        .header p { color: #ffe0cc; margin: 5px 0 0 0; font-size: 14px; text-transform: uppercase; letter-spacing: 1px; }
                        .content { padding: 30px; color: #333333; }
                        .content h2 { margin-top: 0; color: #1a1a1a; font-size: 22px; }
                        .content p { line-height: 1.6; color: #555555; font-size: 15px; }
                        .report-box { background-color: #f8f9fa; border-left: 4px solid #FF6B00; padding: 20px; margin: 25px 0; border-radius: 4px; overflow-x: auto; }
                        .report-box pre { margin: 0; font-family: 'Courier New', Courier, monospace; font-size: 13px; color: #2B1800; white-space: pre-wrap; word-wrap: break-word; }
                        .footer { padding: 30px; text-align: center; background-color: #ffffff; border-top: 1px solid #eaeaea; }
                        .footer p { margin: 0; color: #777777; font-size: 14px; line-height: 1.5; }
                        .footer .brand { color: #1a1a1a; font-weight: bold; margin-top: 5px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>ColloQ</h1>
                            <p>Interview Evaluation Report</p>
                        </div>
                        <div class="content">
                            <h2>Hi %s,</h2>
                            <p>Thank you for completing your mock interview. Your session has been successfully evaluated by our AI expert. Here is your detailed performance report:</p>
                            
                            <div class="report-box">
                                <pre>%s</pre>
                            </div>
                            
                            <p>Keep practicing and leveling up your skills! We are excited to see your progress in the tech industry.</p>
                        </div>
                        <div class="footer">
                            <p>Best Regards,</p>
                            <p class="brand">The ColloQ Team</p>
                            <p style="font-size: 12px; margin-top: 15px; color: #aaa;">© 2026 ColloQ. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(request.getUserName(), jsonReport);

            emailService.sendReportEmail(request.getEmail(), "Your ColloQ Interview Evaluation", htmlBody);
        }
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(jsonReport);
    }
}