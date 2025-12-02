package davinci.edu.ar.excusasSA.repository;

import davinci.edu.ar.excusasSA.model.lineincharge.LineInCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LineInChargeRepository extends JpaRepository<LineInCharge, Long> {
    /** Busca todas las configuraciones de una línea específica (chainIdCode), ordenadas por su índice. */
    List<LineInCharge> findByChainLine_ChainIdCodeOrderByOrderIndexAsc(String chainIdCode);
    /** Busca una configuración única por el legajo del empleado y el identificador de la línea (chainIdCode). */
    Optional<LineInCharge> findByEmployee_LegajoAndChainLine_ChainIdCode(Long employeeLegajo, String chainIdCode);
    /** Obtiene una lista de todos los identificadores de cadena (chainIdCode) únicos. */
    @Query("SELECT DISTINCT l.chainLine.chainIdCode FROM LineInCharge l")
    List<String> findAllDistinctChainIds();
}