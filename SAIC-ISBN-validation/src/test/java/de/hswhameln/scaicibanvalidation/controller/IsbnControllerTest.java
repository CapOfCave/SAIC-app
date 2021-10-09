package de.hswhameln.scaicibanvalidation.controller;

import de.hswhameln.scaicibanvalidation.exceptions.InvalidIsbnException;
import de.hswhameln.scaicibanvalidation.services.IsbnService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IsbnController.class)
public class IsbnControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IsbnService isbnService;

    @Test
    void testValidateWithValidIsbn() throws Exception {
        doNothing().when(isbnService).validateIsbn("9876543210987");

        this.mockMvc
                .perform(
                        get("/isbn/validate")
                                .param("isbn", "9876543210987")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("ISBN is valid."));
    }

    @Test
    void testValidateWithInvalidIsbn() throws Exception {
        doThrow(new InvalidIsbnException("message")).when(isbnService).validateIsbn("9876543210987");

        this.mockMvc
                .perform(
                        get("/isbn/validate")
                                .param("isbn", "9876543210987")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("message"));
    }


    @Test
    void testGenerateCheckSum() throws Exception {
        when(this.isbnService.createIsbnCheckSum("987654321999")).thenReturn(1);

        this.mockMvc
                .perform(
                        get("/isbn/generateCheckSum")
                                .param("isbnWithoutChecksum", "987654321999")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    void testGenerateIsbn() throws Exception {
        when(this.isbnService.generateIsbn("987", "6543", "543", "21")).thenReturn("9876543543211");

        this.mockMvc
                .perform(
                        get("/isbn/generateIsbn")
                                .param("prefix", "987")
                                .param("registrationGroup", "6543")
                                .param("registrant", "543")
                                .param("publicationElement", "21")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("9876543543211"));
    }

    @Test
    void testGenerateIsbnInvalid() throws Exception {
        when(this.isbnService.generateIsbn("987", "6543", "543", "21")).thenThrow(new InvalidIsbnException("message"));

        this.mockMvc
                .perform(
                        get("/isbn/generateIsbn")
                                .param("prefix", "987")
                                .param("registrationGroup", "6543")
                                .param("registrant", "543")
                                .param("publicationElement", "21")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid ISBN: message"));
    }
}