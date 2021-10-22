package de.hswhameln.saicisbnbackend.controller;
import de.hswhameln.saicisbnbackend.services.ValidationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/check")
public class ApplicationHealthController {

    @Value("${validationService.baseUrl}")
    private String baseUrl;

    @GetMapping(path = "/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    @GetMapping(path = "/pingValidator")

    public ResponseEntity<String> pingValidator() {
        ResponseEntity<String> response = WebClient.create().get()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUrl + "/ping")
                        .build()
                ).retrieve()
                .toEntity(String.class)
                .block();

        return ResponseEntity.ok(String.format("ValidationService [%s] responded with '%s'", baseUrl, response));
    }
}
