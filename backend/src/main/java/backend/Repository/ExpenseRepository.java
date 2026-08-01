package backend.Repository;

import backend.Model.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUser_Email(String email);

    Page<Expense> findByUser_Email(String email, Pageable pageable);

    Optional<Expense> findByIdAndUser_Email(Long id, String email);

    boolean existsByIdAndUser_Email(Long id, String email);
}



