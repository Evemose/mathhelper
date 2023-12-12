package org.mathhelper.controllers;

import lombok.RequiredArgsConstructor;
import org.mathhelper.model.equation.CreateEquationDTO;
import org.mathhelper.model.equation.EquationMapper;
import org.mathhelper.services.EquationsService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/equations")
@RequiredArgsConstructor
public class EquationsController {
    private final EquationMapper equationMapper;
    private final EquationsService equationsService;

    @PostMapping
    public ResponseEntity<?> createEquation(@RequestBody CreateEquationDTO createEquationDTO, UriComponentsBuilder uriBuilder) {
        var equation = equationMapper.createEquationDTOToEquation(createEquationDTO);
        var uri = uriBuilder.path("/equations/{id}").build(equationsService.save(equation).getId());
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEquation(@PathVariable Long id) {
        var equation = equationsService.findById(id);
        if (equation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(equationsService.findById(id));
    }

    @GetMapping
    public ResponseEntity<?> getAll(Pageable pageable,
                                             @RequestParam(defaultValue = "-2147483648") int min,
                                             @RequestParam(defaultValue = "2147483647") int max) {
        return ResponseEntity.ok(equationsService.findAllWithSolutionsCountBetween(min, max, pageable).getContent());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEquation(@PathVariable Long id) {
        var equation = equationsService.findById(id);
        if (equation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        equationsService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/solutions")
    public ResponseEntity<?> addSolutionIfFits(@PathVariable Long id, @RequestParam double x) {
        var equation = equationsService.findById(id);
        if (equation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (equationsService.addSolutionIfSatisfies(equation.get(), x)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("Solution does not fit the equation.");
        }
    }
}
