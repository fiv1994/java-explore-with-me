package ru.practicum.explore.compilations;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.practicum.explore.compilations.dto.CompilationsDtoIn;
import ru.practicum.explore.compilations.dto.CompilationsDtoOut;
import ru.practicum.explore.compilations.dto.CompilationsMapper;
import ru.practicum.explore.compilations.dto.CompilationsUpdateDtoIn;
import ru.practicum.explore.compilations.model.Compilations;
import ru.practicum.explore.event.EventService;
import ru.practicum.explore.event.dto.EventShortDtoOut;
import ru.practicum.explore.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationsServiceImpl implements CompilationsService {
    private final CompilationsRepository compilationsRepository;
    private final CompilationsMapper compilationsMapper;
    private final JdbcTemplate jdbcTemplate;
    private final EventService eventService;

    @Override
    public CompilationsDtoOut addCompilation(CompilationsDtoIn compilationsDtoIn) {
        Compilations compilations = compilationsRepository
                .save(compilationsMapper.mapCompilationsDtoInToCompilations(compilationsDtoIn));

        if (compilationsDtoIn.getEvents() != null && !compilationsDtoIn.getEvents().isEmpty()) {
            for (Integer eventId : compilationsDtoIn.getEvents()) {
                jdbcTemplate.update(
                        "INSERT INTO compilations_events (compilation_id, event_id) VALUES (?, ?)",
                        compilations.getId(), eventId
                );
            }
        }

        List<EventShortDtoOut> events = fetchEventsForCompilation(compilations.getId());
        return compilationsMapper.mapCompilationsToCompilationsDtoOut(compilations, events);
    }

    @Override
    public ResponseEntity<Void> deleteCompilation(Integer compId) {
        Compilations compilations = compilationsRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));

        compilationsRepository.delete(compilations);
        return ResponseEntity.noContent().build();
    }

    @Override
    public CompilationsDtoOut updateCompilation(Integer compId, CompilationsUpdateDtoIn compilationsDtoIn) {
        Compilations compilations = compilationsRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));

        if (compilationsDtoIn.getEvents() != null) {
            jdbcTemplate.update("DELETE FROM compilations_events WHERE compilation_id = ?", compilations.getId());
            for (Integer eventId : compilationsDtoIn.getEvents()) {
                jdbcTemplate.update(
                        "INSERT INTO compilations_events (compilation_id, event_id) VALUES (?, ?)",
                        compilations.getId(), eventId
                );
            }
        }

        if (compilationsDtoIn.getPinned() != null) {
            compilations.setPinned(compilationsDtoIn.getPinned());
        }
        if (compilationsDtoIn.getTitle() != null) {
            compilations.setTitle(compilationsDtoIn.getTitle());
        }

        compilations = compilationsRepository.save(compilations);

        List<EventShortDtoOut> events = fetchEventsForCompilation(compilations.getId());
        return compilationsMapper.mapCompilationsToCompilationsDtoOut(compilations, events);
    }

    @Override
    public List<CompilationsDtoOut> getPublicCompilations(Boolean pinned, Integer from, Integer size) {
        List<Compilations> compilationsList = pinned != null ?
                compilationsRepository.getPublicCompByPinned(pinned, from, size) :
                compilationsRepository.getPublicComp(from, size);

        return compilationsList.stream()
                .map(comp -> {
                    List<EventShortDtoOut> events = fetchEventsForCompilation(comp.getId());
                    return compilationsMapper.mapCompilationsToCompilationsDtoOut(comp, events);
                })
                .toList();
    }

    @Override
    public CompilationsDtoOut getPublicCompilationsById(Integer compId) {
        Compilations compilations = compilationsRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));

        List<EventShortDtoOut> events = fetchEventsForCompilation(compilations.getId());
        return compilationsMapper.mapCompilationsToCompilationsDtoOut(compilations, events);
    }

    private List<EventShortDtoOut> fetchEventsForCompilation(Integer compilationId) {
        List<Integer> eventIds = jdbcTemplate.query(
                "SELECT ce.event_id FROM compilations_events AS ce WHERE ce.compilation_id = ?",
                (rs, rowNum) -> rs.getInt("event_id"), compilationId
        );

        return eventService.getCompilationsEvents(eventIds);
    }
}