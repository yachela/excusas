package davinci.edu.ar.excusasSA.service;

import davinci.edu.ar.excusasSA.dto.ChainLineDTO;
import davinci.edu.ar.excusasSA.model.lineincharge.ChainLine;
import davinci.edu.ar.excusasSA.repository.ChainLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChainLineService {

    private final ChainLineRepository chainLineRepository;

    @Autowired
    public ChainLineService(ChainLineRepository chainLineRepository) {
        this.chainLineRepository = chainLineRepository;
    }

    public ChainLine createChainLine(ChainLineDTO dto) {
        // Podrías validar aquí si ya existe uno con ese nombre
        ChainLine chainLine = new ChainLine(dto.getNameCode());
        return chainLineRepository.save(chainLine);
    }

    public List<ChainLineDTO> getAllChains() {
        return chainLineRepository.findAll().stream()
                .map(chain -> new ChainLineDTO(chain.getId(), chain.getChainIdCode()))
                .collect(Collectors.toList());
    }
}