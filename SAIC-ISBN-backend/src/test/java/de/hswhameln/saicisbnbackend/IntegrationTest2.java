package de.hswhameln.saicisbnbackend;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import de.hswhameln.saicisbnbackend.controller.BookController;
import de.hswhameln.saicisbnbackend.dto.BookCreationDTO;
import de.hswhameln.saicisbnbackend.dto.BookResponseDTO;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
  SaicIsbnBackendApplication.class, 
  H2TestProfileJPAConfig.class})
@ActiveProfiles("test")
class IntegrationTest2 {

    @Autowired
    private BookController controller;

    @Test
    void integrationTestLoad() throws Exception {
        BookCreationDTO book = new BookCreationDTO("Harry Potter", "J. K. Rowling", "Hamburger Carlsen Verlag", "9783551551672");
      
        controller.saveBook(book);
        BookResponseDTO readBook = controller.readBook(book.getIsbn13()).getBody();
        assertEquals(readBook, book);
    }

}
