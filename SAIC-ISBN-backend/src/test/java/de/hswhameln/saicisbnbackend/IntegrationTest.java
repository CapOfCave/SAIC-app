package de.hswhameln.saicisbnbackend;


import de.hswhameln.saicisbnbackend.services.ValidationService;
import de.hswhameln.saicisbnbackend.services.ValidationService.ValidationResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * lädt die Dependencies und Testet die Anwendung an sich.
 */
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class IntegrationTest {

    @MockBean
    private ValidationService validationService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void integrationTestSave() throws Exception {
        when(validationService.validate(any(String.class))).thenReturn(new ValidationResponse(true, "ISBN is valid."));
        this.mockMvc.perform(post("/book/saveBook").contentType(MediaType.APPLICATION_JSON).content(
                        "{ \"titel\": \"Harry Potter\", " +
                                "\"autor\": \"J. K. Rowling\", " +
                                "\"verlag\": \"Hamburger Carlsen Verlag\", " +
                                "\"isbn13\": \"9783551551672\" }"))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    @Sql(statements = "INSERT INTO T_Book (ID, TITEL, AUTOR, VERLAG, ISBN13) VALUES (4711, 'titel', 'author', 'verlag', '9783551551672')")
    void integrationTestLoad() throws Exception {
        when(validationService.validate(any(String.class))).thenReturn(new ValidationResponse(true, "ISBN is valid."));
        this.mockMvc.perform(get("/book/readBook").param("isbn", "9783551551672")).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Sql(statements = {"INSERT INTO T_Book (ID, TITEL, AUTOR, VERLAG, ISBN13) VALUES (4711, 'titel', 'author', 'verlag', '9783551551672')","INSERT INTO T_Book (ID, TITEL, AUTOR, VERLAG, ISBN13) VALUES (4811, 'titel2', 'author2', 'verlag2', '9783608938289')"})
    void integrationTestLoadAllBooks() throws Exception {
        when(validationService.validate(any(String.class))).thenReturn(new ValidationResponse(true, "ISBN is valid."));
        this.mockMvc.perform(get("/book/books")).andDo(print())
                .andExpect(status().isOk());
    }

}
