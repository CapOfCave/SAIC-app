package de.hswhameln.saicisbnbackend.controller;

import de.hswhameln.saicisbnbackend.dto.DOBook;
import de.hswhameln.saicisbnbackend.services.BookService;
import javassist.tools.web.BadHttpRequest;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
public class BookController {

    private BookService service;

    @Autowired
    public BookController(BookService service) {
        this.service = service;
    }

    @PostMapping(path = "/buch/saveBook")
    public String saveBook(DOBook book) throws BadHttpRequest {
        String isbn = book.getIsbn13();
        String url = "http://localhost:8080";
        WebClient client = WebClient.create(url);
        Mono<ResponseEntity<String>> response = client.get().uri("/validate?isbn=" + isbn + "}").retrieve()
                .toEntity(String.class);

        boolean isValid = response.block().getStatusCode().equals(HttpStatus.OK);
        if (!isValid) {
            throw new BadHttpRequest(new Exception("ISBN-13 is invalid"));
        }
        String answer = service.saveBook(book);
        if (answer.equalsIgnoreCase("Exists")) {
            throw new BadHttpRequest(new Exception("Book already exists"));
        }
        if (answer.equalsIgnoreCase("failure")) {
            throw new BadHttpRequest(new Exception("An error occured while saving"));
        }
        return isbn;
    }

    @PostMapping(path = "/buch/readBook")
    public DOBook readBook(String isbn)  {
        return service.readBook(isbn);
    }
}
