package davinci.edu.ar.excusasSA.repository;

import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExcuseRepository extends JpaRepository<Excuse, Long> {

    List<Excuse> findByEmployeeId(Long employeeId);

    // List<Excuse> findByStatus(ExcuseStatus status);
}