package backend.Controller;

import backend.ExpenseDTO.ExpenseRequestDTO;
import backend.ExpenseDTO.ExpenseResponseDTO;
import backend.Service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/expense")
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;

    // Add expense
    @PostMapping
    public ResponseEntity<ExpenseResponseDTO> createExpense(@RequestBody ExpenseRequestDTO requestDTO) {
        ExpenseResponseDTO savedExpense = expenseService.createExpenseDB(requestDTO);
        return new ResponseEntity<>(savedExpense, HttpStatus.CREATED);
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<ExpenseResponseDTO>> getAllExpenses() {

        return ResponseEntity.ok(
                expenseService.getAllExpenses());
    }
    //pagination
    @GetMapping("/page")  // we have give different endpoint because we already have get all
    // or else we can remove get all and just keep pagination alone
    public ResponseEntity<Page<ExpenseResponseDTO>> getExpenses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<ExpenseResponseDTO> pageExpense = expenseService.getPageExpenses(page, size);

        return new ResponseEntity<>(pageExpense, HttpStatus.OK);
    }
    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getExpenseById(
            @PathVariable Long id) {

        Optional<ExpenseResponseDTO> expense =
                expenseService.getExpenseById(id);

        if (expense.isPresent()) {
            return ResponseEntity.ok(expense.get());
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Expense Not Found");
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> updateExpense(
            @PathVariable Long id,
            @RequestBody ExpenseRequestDTO requestDTO) {

        ExpenseResponseDTO updatedExpense =
                expenseService
                        .updateExpense(
                                id,
                                requestDTO);

        if (updatedExpense != null) {
            return ResponseEntity.ok(
                    updatedExpense);
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Expense Not Found");
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExpense(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                expenseService.deleteExpense(id));
    }

}
