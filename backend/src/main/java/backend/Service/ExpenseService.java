package backend.Service;

import backend.ExpenseDTO.ExpenseRequestDTO;
import backend.ExpenseDTO.ExpenseResponseDTO;
import backend.Model.Category;
import backend.Model.Expense;
import backend.Repository.CategoryRepository;
import backend.Repository.ExpenseRepository;
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
    // helper function to convert entity to DTO
    private ExpenseResponseDTO convertToResponseDTO(Expense expense) {

        ExpenseResponseDTO response =
                new ExpenseResponseDTO();

        response.setId(
                expense.getId());

        response.setTitle(
                expense.getTitle());

        response.setAmount(
                expense.getAmount());

        response.setCategory(
                expense.getCategory()
                        .getName());

        response.setExpensedate(
                expense.getExpensedate());

        return response;
    }
    //create
    public ExpenseResponseDTO createExpenseDB(
            ExpenseRequestDTO requestDTO) {

        // Find existing category
        // or create a new one
        Category category =
                categoryRepository
                        .findByNameIgnoreCase(
                                requestDTO.getCategory())
                        .orElseGet(() -> {
                            Category c = new Category();
                            c.setName(
                                    requestDTO.getCategory());
                            return categoryRepository.save(c);
                        });

        // DTO -> Entity
        Expense expense = new Expense();

        expense.setTitle(
                requestDTO.getTitle());

        expense.setAmount(
                requestDTO.getAmount());

        expense.setExpensedate(
                requestDTO.getExpensedate());

        expense.setCategory(category);

        // Save
        Expense savedExpense =
                expenseRepository.save(expense);

        // Entity -> Response DTO
        ExpenseResponseDTO response = convertToResponseDTO(expense);
        return response;
    }
    // Read All
    public List<ExpenseResponseDTO> getAllExpenses() {

        List<Expense> expenses = expenseRepository.findAll();

        return expenses.stream()
                .map(this::convertToResponseDTO)
                .toList();
    }
    //pagination
    public Page<ExpenseResponseDTO> getPageExpenses(int page,int size){
        Pageable pageable = PageRequest.of(page,size);
        Page<Expense> expenses = expenseRepository.findAll(pageable);
        return expenses.map(this::convertToResponseDTO);
    }

    // Read By Id
    public Optional<ExpenseResponseDTO> getExpenseById(Long id) {
        return expenseRepository.findById(id).map(this::convertToResponseDTO);
    }

    // Update
    public ExpenseResponseDTO updateExpense(Long id, ExpenseRequestDTO requestDTO) {
        Expense existingExpense =
                expenseRepository
                        .findById(id)
                        .orElse(null);

        if (existingExpense == null) {
            return null;
        }

        Category category =
                categoryRepository
                        .findByNameIgnoreCase(
                                requestDTO.getCategory())
                        .orElseGet(() -> {
                            Category c = new Category();
                            c.setName(requestDTO.getCategory());
                            return categoryRepository.save(c);
                        });

        existingExpense.setTitle(requestDTO.getTitle());
        existingExpense.setAmount(requestDTO.getAmount());
        existingExpense.setExpensedate(
                requestDTO.getExpensedate());
        existingExpense.setCategory(category);

        Expense updatedExpense =
                expenseRepository.save(existingExpense);

        return convertToResponseDTO(updatedExpense);
    }

    // Delete
    public String deleteExpense(Long id) {

        if (expenseRepository.existsById(id)) {
            expenseRepository.deleteById(id);
            return "Expense Deleted Successfully";
        }

        return "Expense Not Found";
    }
}
