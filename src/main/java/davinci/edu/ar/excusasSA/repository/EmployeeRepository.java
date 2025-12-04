package davinci.edu.ar.excusasSA.repository;

import davinci.edu.ar.excusasSA.model.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByLegajo(Long legajo);
    Optional<Employee> findByEmail(String email);
}