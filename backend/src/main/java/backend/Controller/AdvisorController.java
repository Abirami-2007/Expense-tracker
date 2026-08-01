package backend.Controller;

import backend.AdvisorDTO.ChatRequestDTO;
import backend.AdvisorDTO.ChatResponseDTO;
import backend.AdvisorDTO.InsightsResponseDTO;
import backend.Service.AdvisorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/advisor")
public class AdvisorController {

    @Autowired
    private AdvisorService advisorService;

    // Auto-generated insights for the dashboard
    @GetMapping("/insights")
    public ResponseEntity<?> getInsights(Authentication authentication) {
        try {
            List<String> insights = advisorService.generateInsights(authentication.getName());
            return ResponseEntity.ok(new InsightsResponseDTO(insights, !insights.isEmpty()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ex.getMessage());
        }
    }

    // Chat with the advisor
    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody ChatRequestDTO requestDTO, Authentication authentication) {
        if (requestDTO.getMessage() == null || requestDTO.getMessage().isBlank()) {
            return ResponseEntity.badRequest().body("Message is required.");
        }

        try {
            String reply = advisorService.chat(authentication.getName(), requestDTO.getMessage(), requestDTO.getHistory());
            return ResponseEntity.ok(new ChatResponseDTO(reply));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ex.getMessage());
        }
    }
}
