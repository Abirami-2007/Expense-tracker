package backend.ExpenseDTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseRequestDTO {
    private String title;
    private Double amount;
    private String category;
    private LocalDate expensedate;
}
