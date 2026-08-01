package backend.Controller;

import backend.ExpenseDTO.ExpenseRequestDTO;
import backend.ExpenseDTO.ExpenseResponseDTO;
import backend.Service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<ExpenseResponseDTO> createExpense(@RequestBody ExpenseRequestDTO requestDTO, Authentication authentication) {
        ExpenseResponseDTO savedExpense = expenseService.createExpenseDB(requestDTO, authentication.getName());
        return new ResponseEntity<>(savedExpense, HttpStatus.CREATED);
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<ExpenseResponseDTO>> getAllExpenses(Authentication authentication) {
        return ResponseEntity.ok(expenseService.getAllExpenses(authentication.getName()));
    }

    //pagination
    @GetMapping("/page")
    public ResponseEntity<Page<ExpenseResponseDTO>> getExpenses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Authentication authentication) {

        Page<ExpenseResponseDTO> pageExpense = expenseService.getPageExpenses(page, size, authentication.getName());
        return new ResponseEntity<>(pageExpense, HttpStatus.OK);
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getExpenseById(@PathVariable Long id, Authentication authentication) {
        Optional<ExpenseResponseDTO> expense = expenseService.getExpenseById(id, authentication.getName());

        if (expense.isPresent()) {
            return ResponseEntity.ok(expense.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Expense Not Found");
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> updateExpense(@PathVariable Long id, @RequestBody ExpenseRequestDTO requestDTO, Authentication authentication) {
        ExpenseResponseDTO updatedExpense = expenseService.updateExpense(id, requestDTO, authentication.getName());

        if (updatedExpense != null) {
            return ResponseEntity.ok(updatedExpense);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Expense Not Found");
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExpense(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(expenseService.deleteExpense(id, authentication.getName()));
    }
}
