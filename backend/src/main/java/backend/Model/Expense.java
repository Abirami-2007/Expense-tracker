package backend.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Expense {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "expense_seq"
    )
    @SequenceGenerator(                    // oracle do not have auto increment like mysql it uses sequence instead
            name = "expense_seq",          //therefore hibernate do this -CREATE SEQUENCE expense_seq;
            sequenceName = "expense_seq",  //INSERT INTO expenses
            allocationSize = 1             //VALUES (expense_seq.nextval, ...);
    )

    private Long id;

    private String title;

    private Double amount;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;;

    private LocalDate expensedate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
