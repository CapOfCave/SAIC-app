package de.hswhameln.saicisbnbackend.repositories;

import de.hswhameln.saicisbnbackend.entities.BookEntity;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
/**
 * Repository zum Speichern und Laden von Bucheinheiten
 */
@Repository
public interface BookRepository extends CrudRepository<BookEntity, Long> {
   
    Optional<BookEntity> findByIsbn13(String isbn);
    boolean existsByIsbn13(String isbn);
}
