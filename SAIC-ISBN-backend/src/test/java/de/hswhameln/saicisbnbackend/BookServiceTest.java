package de.hswhameln.saicisbnbackend;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Optional;

import javax.activation.DataSource;

import org.hsqldb.jdbcDriver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import de.hswhameln.saicisbnbackend.dto.DOBook;
import de.hswhameln.saicisbnbackend.entities.BookEntity;
import de.hswhameln.saicisbnbackend.repositories.BookRepository;
import de.hswhameln.saicisbnbackend.services.BookService;
import javassist.tools.web.BadHttpRequest;
import okhttp3.mockwebserver.MockWebServer;

@SpringBootTest
public class BookServiceTest {

    private DOBook book;
    @Mock
    private BookRepository repo;

    private BookService service;

    @Autowired
    DataSource ds;

	@BeforeAll
    void setUp() throws IOException {
        service = new BookService(repo);
        book = new DOBook("Harry Potter", "J. K. Rowling", "Hamburger Carlsen Verlag", "3551551677");
        BookEntity entity= new BookEntity(book.getTitel(),book.getAutor(),book.getVerlag(),book.getIsbn13());
        when(repo.save(new BookEntity())).thenReturn(entity);
        when(repo.existsById(book.getId())).thenReturn(false);
        when(repo.findByIsbn13("3551551677")).thenReturn(Optional.of(entity));
    }
	
	@Test
	void testSaveBook() throws BadHttpRequest {
        service.saveBook(book);
        Mockito.verify(repo.save(new BookEntity()));
        Mockito.verify(repo.existsById(Long.getLong("0")));             
	}

    @Test
	void testReadBook() throws BadHttpRequest {
        service.readBook(book.getIsbn13());
        Mockito.verify(repo.findByIsbn13(book.getIsbn13()));
	}

	
}
