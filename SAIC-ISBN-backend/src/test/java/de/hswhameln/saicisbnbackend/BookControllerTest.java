package de.hswhameln.saicisbnbackend;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.Provider.Service;

import org.hsqldb.server.WebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import de.hswhameln.saicisbnbackend.controller.BookController;
import de.hswhameln.saicisbnbackend.dto.DOBook;
import de.hswhameln.saicisbnbackend.services.BookService;
import javassist.tools.web.BadHttpRequest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@ExtendWith(MockitoExtension.class)
public class BookControllerTest {
    private static MockWebServer mockBackEnd;
    @Mock
    private WebClient client;
    
    private BookController controller;
    private DOBook testbook;
    @Mock
    public BookService service;

    @BeforeAll
    static void setUp() throws IOException{
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:8080", mockBackEnd.getPort());
        testbook = new DOBook("Harry Potter", "J. K. Rowling", "Hamburger Carlsen Verlag", "3551551677");
        controller = new BookController(service);
    }

    @Test
    void testSaveBook() throws BadHttpRequest {
        mockBackEnd.enqueue(new MockResponse().setStatus(HttpStatus.OK.toString()));
      //  when(client.get().uri(any(String.class).retrieve().toEntity(String.class)).thenReturn(any(Mono<ResponseEntity>.class));
        String isbn = controller.saveBook(testbook);

      //  StepVerifier.create(isbn).expectNextMatches(book -> book.getAutor().equals("Lars Kecker")).verifyComplete();

        assertEquals(testbook.getIsbn13(), isbn);

    }

    @Test
    void testReadBook() throws BadHttpRequest {
        when(service.readBook(testbook.getIsbn13())).thenReturn(testbook);
        DOBook book = controller.readBook(testbook.getIsbn13());

      //  StepVerifier.create(isbn).expectNextMatches(book -> book.getAutor().equals("Lars Kecker")).verifyComplete();

        assertTrue(book.equals(testbook));

    }
}
