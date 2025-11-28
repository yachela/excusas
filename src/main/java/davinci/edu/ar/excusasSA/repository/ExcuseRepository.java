package davinci.edu.ar.excusasSA.repository;
import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExcuseRepository extends JpaRepository<Excuse, Long> {
}