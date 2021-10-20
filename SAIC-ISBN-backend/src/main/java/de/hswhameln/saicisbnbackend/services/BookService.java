package de.hswhameln.saicisbnbackend.services;

import de.hswhameln.saicisbnbackend.dto.DOBook;
import de.hswhameln.saicisbnbackend.entities.BookEntity;
import de.hswhameln.saicisbnbackend.repositories.BookRepository;
import javassist.tools.web.BadHttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Kontrolliert das Speichern der Buch-Datenhaltungsklassen in der Datenbank
 */
@Service
public class BookService {
    private BookRepository repository;

    @Autowired
    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    /**
     * speichert Bücher mithilfe des Repositorys
     *
     * @param book
     * @return
     */
    public void saveBook(DOBook book) throws Exception {

        if (repository.existsById(book.getId())) {
            throw new IllegalStateException("ISBN already exists");
        }
        repository.save(
                new BookEntity(
                        book.getTitel(),
                        book.getAutor(),
                        book.getVerlag(),
                        book.getIsbn13().strip().replaceAll("-", "")));

    }

    /**
     * ließt Bücher mithilfe des Repositorys
     */
    public DOBook readBook(String isbn) throws BadHttpRequest {
        Optional<BookEntity> entity = repository.findByIsbn13(isbn);

        if (entity.isPresent()) {
            BookEntity existingEntity = entity.orElseThrow();
            return new DOBook(existingEntity.getTitel(), existingEntity.getAutor(), existingEntity.getVerlag(),
                    existingEntity.getIsbn13());

        }
        throw new BadHttpRequest(new Exception("Could not find book, please check isbn13"));
    }
}
