package de.hswhameln.saicisbnbackend.controller;

import de.hswhameln.saicisbnbackend.dto.BookCreationDTO;
import de.hswhameln.saicisbnbackend.dto.BookResponseDTO;
import de.hswhameln.saicisbnbackend.entities.BookEntity;
import de.hswhameln.saicisbnbackend.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/book")
public class BookController {

    private BookService service;


    @Autowired
    public BookController(BookService service) {
        this.service = service;
    }

    /**
     * Speichert das parameterübergebene Buch
     */
    @PostMapping(path = "/saveBook")
    public ResponseEntity<String> saveBook(@RequestBody BookCreationDTO bookCreationDto) {
        try {
            service.saveBook(new BookEntity(
                    bookCreationDto.getTitel(),
                    bookCreationDto.getAutor(),
                    bookCreationDto.getVerlag(),
                    bookCreationDto.getIsbn13().strip().replaceAll("-", "")));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Book saved successfully");
    }

    /**
     * Lädt das Buch auf Grundlage der parameterübergebenen isbn
     *
     * @param isbn
     * @return DOBook
     * @throws Exception
     */
    @GetMapping(path = "/readBook")
    public ResponseEntity<BookResponseDTO> readBook(@RequestParam String isbn) {
        try {
            BookEntity book = service.readBook(isbn);
            return ResponseEntity.ok(mapToBookResponse(book));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private BookResponseDTO mapToBookResponse(BookEntity book) {
        return new BookResponseDTO(book.getId(), book.getTitel(), book.getAutor(), book.getVerlag(),
                book.getIsbn13());
    }

    @GetMapping("/books")
    public ResponseEntity<List<BookResponseDTO>> getBooks() {
        try {
            List<BookEntity> books = service.getBooks();
            return ResponseEntity.ok(books.stream().map(this::mapToBookResponse).collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
