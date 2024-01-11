package org.mathhelper.equations.controllers;

import lombok.RequiredArgsConstructor;
import org.mathhelper.equations.dtos.CreateEquationDTO;
import org.mathhelper.equations.dtos.EquationMapper;
import org.mathhelper.equations.dtos.FilterEquationDTO;
import org.mathhelper.equations.services.EquationService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/equations")
@RequiredArgsConstructor
public class EquationsController {
    final EquationMapper equationMapper;
    final EquationService equationsService;

    @PostMapping
    public ResponseEntity<?> createEquation(@RequestBody CreateEquationDTO createEquationDTO) {
        var equation = equationMapper.toEquation(createEquationDTO);
        var savedEquation = equationsService.save(equation);
        return ResponseEntity.created(UriComponentsBuilder.fromPath("/equations/{id}")
                        .buildAndExpand(savedEquation.getId()).toUri())
                .body(equationMapper.toGetDTO(equation));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEquation(@PathVariable Long id) {
        var equation = equationsService.findById(id);
        return equation.map(eq ->
                        ResponseEntity.ok(equationMapper.toGetDTO(eq)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<?> getAll(Pageable pageable,
                                    FilterEquationDTO filter) {
        System.out.println("filter = " + filter);
        return ResponseEntity.ok(
                equationsService.findAll(filter, pageable)
                        .stream().map(equationMapper::toGetDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEquation(@PathVariable Long id) {
        var deletedEquation = equationsService.deleteById(id);
        return deletedEquation.map(eq ->
                        ResponseEntity.ok(equationMapper.toGetDTO(eq)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/solutions")
    public ResponseEntity<?> addSolutionIfFits(@PathVariable Long id, @RequestParam double x) {
        System.out.println("id = " + id);
        var equation = equationsService.findById(id);
        if (equation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (equationsService.addSolutionIfFits(equation.get(), x)) {
            return ResponseEntity.ok(equation);
        } else {
            return ResponseEntity.badRequest().body("Solution does not fit the equation.");
        }
    }
}
