package davinci.edu.ar.excusasSA.repository;

import davinci.edu.ar.excusasSA.model.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}