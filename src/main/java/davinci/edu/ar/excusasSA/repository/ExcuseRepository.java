package davinci.edu.ar.excusasSA.repository;
import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import davinci.edu.ar.excusasSA.model.excuse.ExcuseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExcuseRepository extends JpaRepository<Excuse, Long> {

    // Busca todas las excusas registradas por un empleado específico utilizando su número de legajo.
    List<Excuse> findByEmployee_Legajo(Long legajo);
    // Busca todas las excusas que tienen un estado de aprobación particular (e.g., Pending, Denied, Accepted).
    List<Excuse> findByStatus(ExcuseStatus status);
    // Elimina todas las excusas cuya fecha de registro sea anterior al límite proporcionado. Retorna el conteo de eliminados.
    int deleteByRegisterDateBefore(LocalDate fechaLimite);
    // Consulta personalizada para buscar excusas con filtros opcionales de rango de fechas y estado.
    @Query("SELECT e FROM Excuse e JOIN e.typeExcuse te " +
            "WHERE (:fechaDesde IS NULL OR e.registerDate >= :fechaDesde) " +
            "AND (:fechaHasta IS NULL OR e.registerDate <= :fechaHasta) " +
            "AND (:status IS NULL OR e.status = :status)")
    List<Excuse> findFilteredSimple(
            @Param("fechaDesde") LocalDate fechaDesde,
            @Param("fechaHasta") LocalDate fechaHasta,
            @Param("status") ExcuseStatus status);
}