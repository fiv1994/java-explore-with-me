package ru.practicum.explore.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.practicum.explore.dto.HitDtoIn;
import ru.practicum.explore.dto.HitDtoOut;

@RestController
@Slf4j
@RequestMapping("/events")
public class HitGatewayController {
    private final WebClient webClient;

    public HitGatewayController(WebClient.Builder webClientBuilder, @Value("${server.url}") String serverUrl) {
        this.webClient = webClientBuilder.baseUrl(serverUrl)
                .filter((request, next) -> next.exchange(request)
                        .flatMap(response -> {
                            if (response.statusCode().isError()) {
                                return response.bodyToMono(String.class)
                                        .flatMap(body -> {
                                            return Mono.error(new ResponseStatusException(response.statusCode(), body));
                                        });
                            }
                            return Mono.just(response);
                        }))
                .build();
    }

    private Mono<ResponseEntity<HitDtoOut>> sendHit(HttpServletRequest request, String path) {
        String ip = request.getRemoteAddr();
        log.info("POST/ Отправка hit: URI - {}, IP - {}", path, ip);

        return webClient.post()
                .uri("/hit")
                .bodyValue(new HitDtoIn("ewm-main-service", path, ip))
                .exchangeToMono(response -> response.toEntity(HitDtoOut.class));
    }

    @PostMapping
    public Mono<ResponseEntity<HitDtoOut>> addRecord(HttpServletRequest request) {
        return sendHit(request, request.getRequestURI());
    }

    @PostMapping("/{id}")
    public Mono<ResponseEntity<HitDtoOut>> addRecordWithId(@Positive @PathVariable(name = "id") Long id,
                                                           HttpServletRequest request) {
        String customUri = "/events/" + id;
        return sendHit(request, customUri);
    }
}