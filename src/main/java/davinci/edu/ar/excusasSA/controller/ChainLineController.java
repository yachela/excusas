package davinci.edu.ar.excusasSA.controller;

import davinci.edu.ar.excusasSA.dto.ChainLineDTO;
import davinci.edu.ar.excusasSA.model.lineincharge.ChainLine;
import davinci.edu.ar.excusasSA.service.ChainLineService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chains")
public class ChainLineController {

    private final ChainLineService chainLineService;

    @Autowired
    public ChainLineController(ChainLineService chainLineService) {
        this.chainLineService = chainLineService;
    }

    @PostMapping
    public ResponseEntity<ChainLineDTO> createChainLine(@Valid @RequestBody ChainLineDTO dto) {
        ChainLine savedChain = chainLineService.createChainLine(dto);
        // Devolvemos el DTO con el ID generado
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ChainLineDTO(savedChain.getId(), savedChain.getChainIdCode()));
    }

    @GetMapping
    public ResponseEntity<List<ChainLineDTO>> getAllChains() {
        return ResponseEntity.ok(chainLineService.getAllChains());
    }
}