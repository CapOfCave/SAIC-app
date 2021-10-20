package de.hswhameln.saicisbnbackend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

/**
 * Kontrolliert den Aufurf des Validation Services
 */
@Service
public class ValidationService {
    @Value("validationService.baseUrl")
    private String baseUrl;
    private final WebClient client = WebClient.create(baseUrl);

    public ResponseEntity<String> validate(String isbn13) {
        Mono<ResponseEntity<String>> response = client.get()
                .uri(uriBuilder -> uriBuilder.path("/isbn/validate/{isbn13}").build(isbn13)).retrieve()
                .toEntity(String.class);
        return response.block();
    }
}
