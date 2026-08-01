package backend.AdvisorDTO;

import lombok.Data;

import java.util.List;

@Data
public class ChatRequestDTO {
    // The new message the user just typed
    private String message;

    // Prior turns of the conversation, oldest first (optional, can be empty)
    private List<ChatMessageDTO> history;
}
