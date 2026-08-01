package backend.AdvisorDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsightsResponseDTO {
    // Short, individual tips/observations, one per list item
    private List<String> insights;

    // Whether there was enough data to generate meaningful insights
    private boolean hasData;
}
