package de.hswhameln.scaicibanvalidation;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testValidate() throws Exception {
        this.mockMvc
                .perform(
                        get("/isbn/validate")
                                .param("isbn", "9783551551672"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testGenerateCheckSum() throws Exception {
        this.mockMvc.perform(get("/isbn/generateCheckSum").param("isbnWithoutChecksum", "978355155167"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("2"));
    }

    @Test
    void testGenerateIsbn() throws Exception {
        this.mockMvc.perform(
                        get("/isbn/generateIsbn")
                                .param("prefix", "978")
                                .param("registrationGroup", "355")
                                .param("registrant", "155")
                                .param("publicationElement", "167")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("978-355-155-167-2"));
    }
}
