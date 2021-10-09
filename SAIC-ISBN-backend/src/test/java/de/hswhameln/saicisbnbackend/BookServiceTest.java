package de.hswhameln.saicisbnbackend;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Optional;

import javax.activation.DataSource;

import org.hsqldb.jdbcDriver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import de.hswhameln.saicisbnbackend.dto.DOBook;
import de.hswhameln.saicisbnbackend.entities.BookEntity;
import de.hswhameln.saicisbnbackend.repositories.BookRepository;
import de.hswhameln.saicisbnbackend.services.BookService;
import javassist.tools.web.BadHttpRequest;
import okhttp3.mockwebserver.MockWebServer;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    private final DOBook book = new DOBook("Harry Potter", "J. K. Rowling", "Hamburger Carlsen Verlag", "3551551677");
    ;
    private final BookEntity entity= new BookEntity(book.getTitel(),book.getAutor(),book.getVerlag(),book.getIsbn13());

    @Mock
    private BookRepository repo;

    private BookService service;

    @BeforeEach
	void setUp() throws IOException {
        service = new BookService(repo);      
    }
	
	@Test
	void testSaveBook() throws BadHttpRequest {
        when(repo.save(any(BookEntity.class))).thenReturn(entity);
        when(repo.existsById(book.getId())).thenReturn(false);
        service.saveBook(book);
        Mockito.verify(repo, times(1)).save(any(BookEntity.class));
        Mockito.verify(repo, times(1)).existsById(any(Long.class));             
	}

    @Test
	void testReadBook() throws BadHttpRequest {
        when(repo.findByIsbn13(book.getIsbn13())).thenReturn(Optional.of(entity));
        service.readBook(book.getIsbn13());
        Mockito.verify(repo, times(1)).findByIsbn13(book.getIsbn13());
	}

	
}
