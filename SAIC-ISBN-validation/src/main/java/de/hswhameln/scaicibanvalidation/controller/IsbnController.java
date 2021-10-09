package de.hswhameln.scaicibanvalidation.controller;

import de.hswhameln.scaicibanvalidation.exceptions.InvalidIsbnException;
import de.hswhameln.scaicibanvalidation.services.IsbnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/isbn")
public class IsbnController {

    private final IsbnService isbnService;

    @Autowired
    public IsbnController(IsbnService isbnService) {
        this.isbnService = isbnService;
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateIsbn(@RequestParam String isbn) {
        try {
            this.isbnService.validateIsbn(isbn);
            return ResponseEntity.ok("ISBN is valid.");
        } catch (InvalidIsbnException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/generateCheckSum")
    public ResponseEntity<String> generateCheckSum(@RequestParam String isbnWithoutChecksum) {
        try {
            int checkSum = this.isbnService.createIsbnCheckSum(isbnWithoutChecksum);
            return ResponseEntity.ok(Integer.toString(checkSum));
        } catch (InvalidIsbnException e) {
            return ResponseEntity.badRequest().body("Invalid ISBN: " + e.getMessage());
        }
    }

    @GetMapping("/generateIsbn")
    public ResponseEntity<String> generateCheckSum(String prefix, String registrationGroup, String registrant, String publicationElement) {
        try {
            String isbn = this.isbnService.generateIsbn(prefix, registrationGroup, registrant, publicationElement);
            return ResponseEntity.ok(isbn);
        } catch (InvalidIsbnException e) {
            return ResponseEntity.badRequest().body("Invalid ISBN: " + e.getMessage());
        }
    }

}
