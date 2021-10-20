package de.hswhameln.saicisbnbackend.controller;

import de.hswhameln.saicisbnbackend.dto.DOBook;
import de.hswhameln.saicisbnbackend.services.BookService;
import de.hswhameln.saicisbnbackend.services.ValidationService;
import javassist.tools.web.BadHttpRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/book")
public class BookController {

    private BookService service;

    private ValidationService validationService;

    @Autowired
    public BookController(BookService service, ValidationService validationService) {
        this.service = service;
        this.validationService = validationService;

    }
    /**
     * Speichert das parameterübergebene Buch 
     * @param book
     */
    @PostMapping(path = "/saveBook")
    public ResponseEntity<String> saveBook(@RequestBody DOBook book) {
        ValidationService.ValidationResponse entity = validationService.validate(book.getIsbn13());
        if (!entity.isSuccessful()) {
            return ResponseEntity.badRequest().body("ISBN-13 is invalid: " + entity.getMessage());
        }
        try {
            service.saveBook(book);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Book saved successfully");
    }
    /**
     * Lädt das Buch auf Grundlage der parameterübergebenen isbn
     * @param isbn
     * @return DOBook
     * @throws Exception
     */
    @PostMapping(path = "/readBook")
    public ResponseEntity<DOBook> readBook(@RequestBody String isbn)  {
        try {
            return ResponseEntity.ok(service.readBook(isbn));
        } catch (BadHttpRequest e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
