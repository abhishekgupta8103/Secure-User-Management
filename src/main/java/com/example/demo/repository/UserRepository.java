package com.example.demo.repository;
import com.example.demo.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name,
            String email,
            Pageable pageable
    );
    Optional<User> findByVerificationToken(String verificationToken);
    Optional<User> findByResetPasswordToken(String resetPasswordToken);
    long countByRole(Role role);

    long countByEmailVerified(boolean emailVerified);

    long count();
    @Query(value = """
        SELECT DATE_FORMAT(created_at, '%Y-%m') AS month,
               COUNT(*) AS totalUsers
        FROM users
        WHERE created_at IS NOT NULL
        GROUP BY DATE_FORMAT(created_at, '%Y-%m')
        ORDER BY month
        """, nativeQuery = true)
    List<Object[]> getMonthlyUserReport();
}