package de.hswhameln.saicisbnbackend.controller;

import de.hswhameln.saicisbnbackend.dto.DOBook;
import de.hswhameln.saicisbnbackend.services.BookService;
import javassist.tools.web.BadHttpRequest;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/book")
public class BookController {

    private BookService service;

    @Autowired
    public BookController(BookService service) {
        this.service = service;
    }

    @PostMapping(path = "/saveBook")
    public String saveBook(@RequestBody DOBook book) throws BadHttpRequest {
        String isbn = book.getIsbn13();
        String url = "http://localhost:8081";
        WebClient client = WebClient.create(url);
        Mono<ResponseEntity<String>> response = client.get().uri("/isbn/validate?isbn=" + isbn ).retrieve()
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

    @PostMapping(path = "/readBook")
    public DOBook readBook(@RequestBody String isbn)  {
        return service.readBook(isbn);
    }
}
