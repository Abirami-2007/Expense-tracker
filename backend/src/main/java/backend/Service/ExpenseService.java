package backend.Service;

import backend.ExpenseDTO.ExpenseRequestDTO;
import backend.ExpenseDTO.ExpenseResponseDTO;
import backend.Model.Category;
import backend.Model.Expense;
import backend.Model.User;
import backend.Repository.CategoryRepository;
import backend.Repository.ExpenseRepository;
import backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private UserRepository userRepository;

    private ExpenseResponseDTO convertToResponseDTO(Expense expense) {
        ExpenseResponseDTO response = new ExpenseResponseDTO();
        response.setId(expense.getId());
        response.setTitle(expense.getTitle());
        response.setAmount(expense.getAmount());
        response.setCategory(expense.getCategory().getName());
        response.setExpensedate(expense.getExpensedate());
        return response;
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    //create
    public ExpenseResponseDTO createExpenseDB(ExpenseRequestDTO requestDTO, String userEmail) {

        Category category = categoryRepository.findByNameIgnoreCase(requestDTO.getCategory())
                .orElseGet(() -> {
                    Category c = new Category();
                    c.setName(requestDTO.getCategory());
                    return categoryRepository.save(c);
                });

        Expense expense = new Expense();
        expense.setTitle(requestDTO.getTitle());
        expense.setAmount(requestDTO.getAmount());
        expense.setExpensedate(requestDTO.getExpensedate());
        expense.setCategory(category);
        expense.setUser(getUserByEmail(userEmail));

        Expense savedExpense = expenseRepository.save(expense);
        return convertToResponseDTO(savedExpense);
    }

    // Read All (only the logged-in user's expenses)
    public List<ExpenseResponseDTO> getAllExpenses(String userEmail) {
        List<Expense> expenses = expenseRepository.findByUser_Email(userEmail);
        return expenses.stream().map(this::convertToResponseDTO).toList();
    }

    //pagination
    public Page<ExpenseResponseDTO> getPageExpenses(int page, int size, String userEmail) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Expense> expenses = expenseRepository.findByUser_Email(userEmail, pageable);
        return expenses.map(this::convertToResponseDTO);
    }

    // Read By Id (only if it belongs to the user)
    public Optional<ExpenseResponseDTO> getExpenseById(Long id, String userEmail) {
        return expenseRepository.findByIdAndUser_Email(id, userEmail).map(this::convertToResponseDTO);
    }

    // Update (only if it belongs to the user)
    public ExpenseResponseDTO updateExpense(Long id, ExpenseRequestDTO requestDTO, String userEmail) {
        Expense existingExpense = expenseRepository.findByIdAndUser_Email(id, userEmail).orElse(null);
        if (existingExpense == null) {
            return null;
        }

        Category category = categoryRepository.findByNameIgnoreCase(requestDTO.getCategory())
                .orElseGet(() -> {
                    Category c = new Category();
                    c.setName(requestDTO.getCategory());
                    return categoryRepository.save(c);
                });

        existingExpense.setTitle(requestDTO.getTitle());
        existingExpense.setAmount(requestDTO.getAmount());
        existingExpense.setExpensedate(requestDTO.getExpensedate());
        existingExpense.setCategory(category);

        Expense updatedExpense = expenseRepository.save(existingExpense);
        return convertToResponseDTO(updatedExpense);
    }

    // Delete (only if it belongs to the user)
    public String deleteExpense(Long id, String userEmail) {
        if (expenseRepository.existsByIdAndUser_Email(id, userEmail)) {
            expenseRepository.deleteById(id);
            return "Expense Deleted Successfully";
        }
        return "Expense Not Found";
    }

    // Used by the AI advisor to build context from raw entities (not DTOs)
    public List<Expense> getRawExpensesForUser(String userEmail) {
        return expenseRepository.findByUser_Email(userEmail);
    }
}
