package de.hswhameln.saicisbnbackend;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClient.UriSpec;

import de.hswhameln.saicisbnbackend.controller.BookController;
import de.hswhameln.saicisbnbackend.dto.DOBook;
import de.hswhameln.saicisbnbackend.services.BookService;
import javassist.tools.web.BadHttpRequest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class BookControllerTest {
    private static MockWebServer mockBackEnd;
    @Mock
    private WebClient webClientMock;
    
    private BookController controller;
    private DOBook testbook;
    @Mock
    public BookService service;
    @Mock
    private RequestHeadersUriSpec<Object> requestHeadersUriSpecMock;
    @Mock
    private UriSpec<?> requestHeadersUriMock;
    @Mock
    private Object requestHeadersSpecMock;
    @Mock
    private RequestHeadersSpec<?> requestHeadersMock;
    @Mock
    private ResponseSpec responseSpecMock;
    @Mock
    private ResponseSpec responseMock;

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
    void testSaveBook2() {

        String bookISBN13 = "3551551677";
        when(webClientMock.get())
          .thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriMock.uri("/book/saveBook/", bookISBN13))
          .thenReturn(requestHeadersSpecMock);
        when(requestHeadersMock.retrieve())
          .thenReturn(responseSpecMock);
        when(responseMock.bodyToMono(DOBook.class))
          .thenReturn(Mono.just(testbook));

        String response = controller.saveBook(testbook);

       assertEquals(bookISBN13, response);
    }


    @Test
    void testReadBook() throws BadHttpRequest {
        when(service.readBook(testbook.getIsbn13())).thenReturn(testbook);
        DOBook book = controller.readBook(testbook.getIsbn13());
        assertTrue(book.equals(testbook));

    }
}
