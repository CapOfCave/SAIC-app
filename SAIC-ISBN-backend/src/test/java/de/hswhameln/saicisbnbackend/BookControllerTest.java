package de.hswhameln.saicisbnbackend;

import de.hswhameln.saicisbnbackend.controller.BookController;
import de.hswhameln.saicisbnbackend.dto.BookCreationDTO;
import de.hswhameln.saicisbnbackend.dto.BookResponseDTO;
import de.hswhameln.saicisbnbackend.entities.BookEntity;
import de.hswhameln.saicisbnbackend.services.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookControllerTest {

    private static final long ID = 4711L;
    private static final String TITEL = "Harry Potter";
    private static final String AUTOR = "J. K. Rowling";
    private static final String VERLAG = "Hamburger Carlsen Verlag";
    private static final String ISBN_13 = "9783551551672";

    private final BookCreationDTO bookCreationDTO = new BookCreationDTO(TITEL, AUTOR, VERLAG, ISBN_13);
    private final BookEntity bookEntity = new BookEntity(ID, TITEL, AUTOR, VERLAG, ISBN_13);

    private BookController objectUnderTest;

    @Captor
    ArgumentCaptor<BookEntity> bookEntityArgumentCaptor;

    @Mock
    public BookService service;

    @BeforeEach
    void initialize() {
        objectUnderTest = new BookController(service);
    }


    @Test
    void testSaveBookSuccess() {
        doNothing().when(service).saveBook(bookEntityArgumentCaptor.capture());

        objectUnderTest.saveBook(bookCreationDTO);

        validatePassedEntity();
        Mockito.verify(service, times(1)).saveBook(any(BookEntity.class));
    }

    @Test
    void testSaveBookFailure() {
        doThrow(new IllegalStateException("message")).when(service).saveBook(bookEntityArgumentCaptor.capture());

        ResponseEntity<String> response = objectUnderTest.saveBook(bookCreationDTO);

        validatePassedEntity();
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("message", response.getBody());

        Mockito.verify(service, times(1)).saveBook(any(BookEntity.class));
    }

    @Test
    void testReadBook() throws Exception {
        when(service.readBook(ISBN_13)).thenReturn(bookEntity);
        ResponseEntity<BookResponseDTO> response = objectUnderTest.readBook(ISBN_13);
        BookResponseDTO bookResponse = response.getBody();

        assertNotNull(bookResponse);
        assertEquals(ID, bookResponse.getId());
        assertEquals(TITEL, bookResponse.getTitel());
        assertEquals(AUTOR, bookResponse.getAutor());
        assertEquals(VERLAG, bookResponse.getVerlag());
        assertEquals(ISBN_13, bookResponse.getIsbn13());
    }

    private void validatePassedEntity() {
        BookEntity passedEntity = bookEntityArgumentCaptor.getValue();
        assertEquals(TITEL, passedEntity.getTitel());
        assertEquals(AUTOR, passedEntity.getAutor());
        assertEquals(VERLAG, passedEntity.getVerlag());
        assertEquals(ISBN_13, passedEntity.getIsbn13());
    }
}
