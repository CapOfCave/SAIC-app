package de.hswhameln.saicisbnbackend;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.io.IOException;

import java.util.Optional;

import de.hswhameln.saicisbnbackend.services.ValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


import de.hswhameln.saicisbnbackend.dto.DOBook;
import de.hswhameln.saicisbnbackend.entities.BookEntity;
import de.hswhameln.saicisbnbackend.repositories.BookRepository;
import de.hswhameln.saicisbnbackend.services.BookService;
import javassist.tools.web.BadHttpRequest;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    private final String isbn = "3551551677";
    private final DOBook book = new DOBook("Harry Potter", "J. K. Rowling", "Hamburger Carlsen Verlag", isbn);
    private final BookEntity entity= new BookEntity(book.getTitel(),book.getAutor(),book.getVerlag(),book.getIsbn13());

    @Mock
    private BookRepository repo;

    @Mock
    private ValidationService validationService;

    private BookService service;

    /**
     * inizialisiert den benötigten BookService
     * @throws IOException
     */
    @BeforeEach
	void setUp() {
        service = new BookService(repo, validationService);
    }
	/**
     * Testet die Speicherung von Büchern in der Datenbank
     * @throws BadHttpRequest
     */
	@Test
	void testSaveBook() throws Exception {
        when(repo.save(any(BookEntity.class))).thenReturn(entity);
        when(repo.existsById(book.getId())).thenReturn(false);
        when(validationService.validate(isbn)).thenReturn(new ValidationService.ValidationResponse(true, "message"));
        service.saveBook(book);
        Mockito.verify(repo, times(1)).save(any(BookEntity.class));
        Mockito.verify(repo, times(1)).existsById(any(Long.class));             
	}

    /**
     * Testet das Lesen von Büchern aus der Datenbank
     * @throws Exception
     */
    @Test
	void testReadBook() throws Exception {
        when(repo.findByIsbn13(book.getIsbn13())).thenReturn(Optional.of(entity));
        service.readBook(isbn);
        Mockito.verify(repo, times(1)).findByIsbn13(book.getIsbn13());
	}

	
}
