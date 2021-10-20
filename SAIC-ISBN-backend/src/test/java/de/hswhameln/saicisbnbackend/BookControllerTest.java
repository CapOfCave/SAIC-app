package de.hswhameln.saicisbnbackend;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;


import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import de.hswhameln.saicisbnbackend.controller.BookController;
import de.hswhameln.saicisbnbackend.dto.DOBook;
import de.hswhameln.saicisbnbackend.services.BookService;
import de.hswhameln.saicisbnbackend.services.ValidationService;
import javassist.tools.web.BadHttpRequest;

@ExtendWith(MockitoExtension.class)
public class BookControllerTest {
    
    private BookController controller;
    private DOBook testbook;
    @Mock
    public BookService service;
    
    @Mock
    private ValidationService validationService;

    @BeforeEach
    void initialize() {
        testbook = new DOBook("Harry Potter", "J. K. Rowling", "Hamburger Carlsen Verlag", "3551551677");
        controller = new BookController(service, validationService);
    }


    /**
     * Testet den Aufruf des Validation Service und den Aufruf des Speicherungs-Service auf Erfolg
     * 
     * @throws BadHttpRequest
     */
    @Test
    void testSaveBookSuccess() throws BadHttpRequest {       
        when(validationService.validate(testbook.getIsbn13())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(service.saveBook(testbook)).thenReturn("success");

        controller.saveBook(testbook);
        Mockito.verify(validationService, times(1)).validate(any(String.class));
        Mockito.verify(service, times(1)).saveBook(testbook);
    }

    /**
     * Testet den Aufruf des Validation Service und den Aufruf des Speicherungs-Service auf Misserfolg
     * 
     * @throws BadHttpRequest
     */
    @Test
    void testSaveBookFailure() throws BadHttpRequest {       
        when(validationService.validate(testbook.getIsbn13())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(service.saveBook(testbook)).thenReturn("failure");

        try{
            controller.saveBook(testbook);
            } catch (Exception e){
                Assert.assertTrue(true);
            }
        Mockito.verify(validationService, times(1)).validate(any(String.class));
        Mockito.verify(service, times(1)).saveBook(testbook);
    }

    /**
     * Testet den Aufruf des Validation Service und den Aufruf des Speicherungs-Service auf Misserfolg, wenn Buch bereits gespeichert ist.
     * 
     * @throws BadHttpRequest
     */
    @Test
    void testSaveBookExists() throws BadHttpRequest {    
        when(validationService.validate(testbook.getIsbn13())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(service.saveBook(testbook)).thenReturn("exists");

        try{
        controller.saveBook(testbook);
        } catch (Exception e){
            Assert.assertTrue(true);
        }
        Mockito.verify(validationService, times(1)).validate(any(String.class));
        Mockito.verify(service, times(1)).saveBook(testbook);

    }

/**
 * testet den Aufurf des BookService zum Laden von Büchern
 * @throws Exception
 */
    @Test
    void testReadBook() throws Exception {
        when(service.readBook(testbook.getIsbn13())).thenReturn(testbook);
        DOBook book = controller.readBook(testbook.getIsbn13());
        assertTrue(book.equals(testbook));

    }
}
