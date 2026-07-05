package backend.ExpenseDTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseResponseDTO {
    private Long id;
    private String title;
    private Double amount;
    private String category;
    private LocalDate expensedate;
}
