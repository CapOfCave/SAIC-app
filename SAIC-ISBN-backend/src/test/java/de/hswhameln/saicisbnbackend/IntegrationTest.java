package de.hswhameln.saicisbnbackend;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import de.hswhameln.saicisbnbackend.controller.BookController;
import de.hswhameln.saicisbnbackend.services.BookService;
import de.hswhameln.saicisbnbackend.services.ValidationService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;

/**
 * lädt die Dependencies und Testet die Anwendung an sich.
 */
@WebMvcTest(BookController.class)
class IntegrationTest {

    @MockBean
    private ValidationService validationService;

    @MockBean
    private BookService bookService;

    @Autowired
	private MockMvc mockMvc;

    @Test
    void integrationTestSave() throws Exception{
        when(validationService.validate(any(String.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        this.mockMvc.perform(post("/book/saveBook")).andDo(print()).andExpect(status().isOk());
        }
}