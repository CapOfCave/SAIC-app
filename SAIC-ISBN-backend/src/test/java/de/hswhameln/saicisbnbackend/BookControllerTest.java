package de.hswhameln.saicisbnbackend;

import de.hswhameln.saicisbnbackend.controller.BookController;
import de.hswhameln.saicisbnbackend.dto.DOBook;
import de.hswhameln.saicisbnbackend.services.BookService;
import de.hswhameln.saicisbnbackend.services.ValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookControllerTest {

    private BookController controller;
    private DOBook testbook;
    @Mock
    public BookService service;

    @BeforeEach
    void initialize() {
        testbook = new DOBook("Harry Potter", "J. K. Rowling", "Hamburger Carlsen Verlag", "3551551677");
        controller = new BookController(service);
    }


    @Test
    void testSaveBookSuccess() throws Exception {
        doNothing().when(service).saveBook(testbook);

        controller.saveBook(testbook);
        Mockito.verify(service, times(1)).saveBook(testbook);
    }

    @Test
    void testSaveBookFailure() throws Exception {
        doThrow(new IllegalStateException("message")).when(service).saveBook(testbook);

        ResponseEntity<String> response = controller.saveBook(testbook);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("message", response.getBody());

        Mockito.verify(service, times(1)).saveBook(testbook);
    }

    @Test
    void testReadBook() throws Exception {
        when(service.readBook(testbook.getIsbn13())).thenReturn(testbook);
        ResponseEntity<DOBook> response = controller.readBook(testbook.getIsbn13());
        assertEquals(testbook, response.getBody());

    }
}
