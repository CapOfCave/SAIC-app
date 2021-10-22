package de.hswhameln.saicisbnbackend;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import de.hswhameln.saicisbnbackend.services.BookService;
import de.hswhameln.saicisbnbackend.services.ValidationService;
import de.hswhameln.saicisbnbackend.services.ValidationService.ValidationResponse;

/**
 * lädt die Dependencies und Testet die Anwendung an sich.
 */
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class IntegrationTest {

    @MockBean
    private ValidationService validationService;

    @MockBean
    private BookService bookService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void integrationTestSave() throws Exception {
        when(validationService.validate(any(String.class))).thenReturn(new ValidationResponse(true, "ISBN is valid."));
        this.mockMvc.perform(post("/book/saveBook").contentType(MediaType.APPLICATION_JSON).content(
                "{ \"titel\": \"Harry Potter\", "+
                "\"autor\": \"J. K. Rowling\", "+
                "\"verlag\": \"Hamburger Carlsen Verlag\", "+
                "\"isbn13\": \"9783551551672\" }"))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    void integrationTestLoad() throws Exception {
        when(validationService.validate(any(String.class))).thenReturn(new ValidationResponse(true, "ISBN is valid."));
        this.mockMvc.perform(get("/book/readBook?isbn=9783551551672")).andDo(print())
                .andExpect(status().isOk());
    }

 //   @Test
//    public void testRepo() {
//        BookEntity testBook = new BookEntity(4711L,"Harry Potter", "J. K. Rowling", "Hamburger Carlsen Verlag", "9783551551672");
//        repository.save(testBook);
          
//          Optional<BookEntity> foundEntity = repository.findByIsbn13("9783551551672");
// 
 //       assertTrue(foundEntity.isPresent());
 //       assertEquals(testBook.getIsbn13(),foundEntity.get().getIsbn13());
//
 //   }

}
