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
     * @throws BadHttpRequest
     */
    @PostMapping(path = "/saveBook")
    public void saveBook(@RequestBody DOBook book) {
        try{
        ResponseEntity<String> entity = validationService.validate(book.getIsbn13());
        boolean isValid = entity !=null && entity.getStatusCode().equals(HttpStatus.OK);
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
    }
    catch (BadHttpRequest e){
        System.out.println(e.getMessage());
    }
    }
    /**
     * Lädt das Buch auf Grundlage der parameterübergebenen isbn
     * @param isbn
     * @return DOBook
     * @throws Exception
     */
    @PostMapping(path = "/readBook")
    public DOBook readBook(@RequestBody String isbn)  {
        try {
            return service.readBook(isbn);
        } catch (BadHttpRequest e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
