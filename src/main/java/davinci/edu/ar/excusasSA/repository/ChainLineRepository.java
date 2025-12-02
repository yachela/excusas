package davinci.edu.ar.excusasSA.repository;

import davinci.edu.ar.excusasSA.model.lineincharge.ChainLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChainLineRepository extends JpaRepository<ChainLine, Long> {
    Optional<ChainLine> findByChainIdCode(String chainIdCode);
}