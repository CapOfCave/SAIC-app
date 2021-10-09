package de.hswhameln.saicisbnbackend.services;

import de.hswhameln.saicisbnbackend.dto.DOBook;
import de.hswhameln.saicisbnbackend.entities.BookEntity;
import de.hswhameln.saicisbnbackend.repositories.BookRepository;
import javassist.tools.web.BadHttpRequest;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {
    private BookRepository repository;

    @Autowired
    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public String saveBook(DOBook book) throws BadHttpRequest {
        if (!repository.existsById(book.getId())) {
            BookEntity savedEntity = repository.save(new BookEntity(book.getTitel(), book.getAutor(), book.getVerlag(),
                    book.getIsbn13().strip().replaceAll("-", "")));
            if (savedEntity == null) {
                return "failure";
            }
            return "success";
        } else {
            return "exists";
        }
    }

    public DOBook readBook(String isbn) {
        Optional<BookEntity> entity = repository.findByIsbn13(isbn);

        if (entity.isPresent()) {
            BookEntity existingEntity = entity.orElseThrow();
            return new DOBook(existingEntity.getTitel(), existingEntity.getAutor(), existingEntity.getVerlag(),
                    existingEntity.getIsbn10());

        }
        return new DOBook();
    }
}
