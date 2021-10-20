package de.hswhameln.saicisbnbackend.services;

import de.hswhameln.saicisbnbackend.entities.BookEntity;
import de.hswhameln.saicisbnbackend.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Kontrolliert das Speichern der Buch-Datenhaltungsklassen in der Datenbank
 */
@Service
public class BookService {
    private BookRepository repository;
    private ValidationService validationService;

    @Autowired
    public BookService(BookRepository repository, ValidationService validationService) {
        this.repository = repository;
        this.validationService = validationService;
    }

    /**
     * speichert Bücher mithilfe des Repositorys
     *
     * @return
     */
    public void saveBook(BookEntity bookEntity) {
        ValidationService.ValidationResponse entity = validationService.validate(bookEntity.getIsbn13());
        if (!entity.isSuccessful()) {
            throw new IllegalArgumentException("Invalid ISBN: " + entity.getMessage());
        }
        if (repository.existsByIsbn13(bookEntity.getIsbn13())) {
            throw new IllegalStateException("ISBN already exists");
        }
        repository.save(bookEntity);

    }

    /**
     * ließt Bücher mithilfe des Repositorys
     */
    public BookEntity readBook(String isbn) throws Exception {
        Optional<BookEntity> entity = repository.findByIsbn13(isbn);

        if (entity.isPresent()) {
            return entity.get();

        }
        throw new Exception("Could not find book, please check isbn13");
    }

    public List<BookEntity> getBooks() {
        List<BookEntity> bookEntities = new ArrayList<>();
        repository.findAll().forEach(bookEntities::add);
        return bookEntities;
    }
}
