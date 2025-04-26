package ru.practicum.explore.compilations.dto;

import org.springframework.stereotype.Component;
import ru.practicum.explore.compilations.model.Compilations;
import ru.practicum.explore.event.dto.EventShortDtoOut;

import java.util.List;

@Component
public class CompilationsMapper {

    public Compilations mapCompilationsDtoInToCompilations(CompilationsDtoIn compilationsDtoIn) {
        Compilations compilations = new Compilations();
        compilations.setPinned(Boolean.TRUE.equals(compilationsDtoIn.getPinned())); // По умолчанию false
        compilations.setTitle(compilationsDtoIn.getTitle());
        return compilations;
    }

    public CompilationsDtoOut mapCompilationsToCompilationsDtoOut(Compilations compilations,
                                                                  List<EventShortDtoOut> events) {
        CompilationsDtoOut dtoOut = new CompilationsDtoOut();
        dtoOut.setId(compilations.getId());
        dtoOut.setPinned(compilations.getPinned());
        dtoOut.setTitle(compilations.getTitle());
        dtoOut.setEvents(events);
        return dtoOut;
    }
}