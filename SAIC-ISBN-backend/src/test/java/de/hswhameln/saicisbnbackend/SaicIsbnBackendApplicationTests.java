package de.hswhameln.saicisbnbackend;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.Provider.Service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import de.hswhameln.saicisbnbackend.controller.BookController;
import de.hswhameln.saicisbnbackend.dto.DOBook;
import de.hswhameln.saicisbnbackend.services.BookService;
import javassist.tools.web.BadHttpRequest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class SaicIsbnBackendApplicationTests {

    private static MockWebServer mockBackEnd;
    private BookController controller;
    private DOBook testbook;
    @Mock
    public BookService service;

    @BeforeAll
    void setUp() throws IOException {
        testbook = new DOBook("Harry Potter", "J. K. Rowling", "Hamburger Carlsen Verlag", "3551551677");

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
        controller = new BookController(service);
    }

    @Test
    void testSaveBook() throws BadHttpRequest {
        mockBackEnd.enqueue(new MockResponse().setStatus(HttpStatus.OK.toString()));

        String isbn = controller.saveBook(testbook);

      //  StepVerifier.create(isbn).expectNextMatches(book -> book.getAutor().equals("Lars Kecker")).verifyComplete();

        assertTrue(isbn.equals(testbook.getIsbn13()));

    }

    @Test
    void testReadBook() throws BadHttpRequest {
        DOBook book = controller.readBook(testbook.getIsbn13());

      //  StepVerifier.create(isbn).expectNextMatches(book -> book.getAutor().equals("Lars Kecker")).verifyComplete();

        assertTrue(book.equals(testbook));

    }

}
