package de.hswhameln.saicisbnbackend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Kontrolliert den Aufurf des Validation Services
 */
@Service
public class ValidationService {
    @Value("${validationService.baseUrl}")
    private String baseUrl;
    private final WebClient client = WebClient.create();

    public ValidationResponse validate(String isbn13) {
        Mono<ValidationResponse> response = client.get()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUrl + "/isbn/validate")
                        .queryParam("isbn", isbn13)
                        .build()
                ).exchangeToMono(clientResponse ->
                        clientResponse
                                .bodyToMono(String.class)
                                .map(body -> {
                                    if (clientResponse.statusCode() == HttpStatus.NOT_FOUND){
                                        throw new RuntimeException("Could not reach isbn service: " + clientResponse.statusCode() + " - " + body );
                                    }
                                    return new ValidationResponse(clientResponse.statusCode().is2xxSuccessful(), body);
                                }));
        return response.block();
    }

    public static class ValidationResponse {
        private final boolean successful;
        private final String message;

        public ValidationResponse(boolean successful, String message) {
            this.successful = successful;
            this.message = message;
        }

        public boolean isSuccessful() {
            return successful;
        }

        public String getMessage() {
            return message;
        }
    }
}
