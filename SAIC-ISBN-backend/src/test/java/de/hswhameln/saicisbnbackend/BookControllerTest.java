package de.hswhameln.saicisbnbackend;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;


import de.hswhameln.saicisbnbackend.controller.BookController;
import de.hswhameln.saicisbnbackend.dto.DOBook;
import de.hswhameln.saicisbnbackend.services.BookService;
import de.hswhameln.saicisbnbackend.services.ValidationService;
import javassist.tools.web.BadHttpRequest;
import okhttp3.mockwebserver.MockWebServer;

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
    private ValidationService validationService;

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
        testbook = new DOBook("Harry Potter", "J. K. Rowling", "Hamburger Carlsen Verlag", "3551551677");
        controller = new BookController(service, validationService);
    }


    
    @Test
    void testSaveBook() throws BadHttpRequest {       
        when(validationService.validate(testbook.getIsbn13())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        controller.saveBook(testbook);
        Mockito.verify(validationService, times(1)).validate(any(String.class));
        Mockito.verify(service, times(1)).saveBook(any(DOBook.class));

    }


    @Test
    void testReadBook() throws Exception {
        when(service.readBook(testbook.getIsbn13())).thenReturn(testbook);
        DOBook book = controller.readBook(testbook.getIsbn13());
        assertTrue(book.equals(testbook));

    }
}
