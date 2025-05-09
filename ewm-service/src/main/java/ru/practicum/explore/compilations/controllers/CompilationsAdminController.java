package ru.practicum.explore.compilations.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.compilations.CompilationsService;
import ru.practicum.explore.compilations.dto.CompilationsDtoIn;
import ru.practicum.explore.compilations.dto.CompilationsDtoOut;
import ru.practicum.explore.compilations.dto.CompilationsUpdateDtoIn;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class CompilationsAdminController {
    private final CompilationsService compilationsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationsDtoOut addCompilation(@Valid @RequestBody CompilationsDtoIn compilationsDtoIn) {
        log.info("PATCH/ Проверка параметров запроса метода addCompilation, compilationsDtoIn - {}", compilationsDtoIn);
        return compilationsService.addCompilation(compilationsDtoIn);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<Void> deleteCompilation(@PathVariable(name = "compId") Integer compId) {
        log.info("DELETE/ Проверка параметров запроса метода deleteCompilation, compId - {}", compId);
        return compilationsService.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationsDtoOut updateCompilation(@PathVariable(name = "compId") Integer compId,
                                                @Valid @RequestBody CompilationsUpdateDtoIn compilationsDtoIn) {
        log.info("PATCH/ Проверка параметров запроса метода updateCompilation, compId - {}, compilationsDtoIn - {}",
                compId, compilationsDtoIn);
        return compilationsService.updateCompilation(compId, compilationsDtoIn);
    }

}
