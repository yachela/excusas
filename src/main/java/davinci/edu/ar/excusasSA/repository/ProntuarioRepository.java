package davinci.edu.ar.excusasSA.repository;

import davinci.edu.ar.excusasSA.model.prontuario.Prontuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProntuarioRepository extends JpaRepository<Prontuario, Long> {

    List<Prontuario> findByEmployeeId(Long employeeId);
}