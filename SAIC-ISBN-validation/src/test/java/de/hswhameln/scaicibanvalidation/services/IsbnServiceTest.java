package de.hswhameln.scaicibanvalidation.services;

import de.hswhameln.scaicibanvalidation.exceptions.InvalidIsbnException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IsbnServiceTest {

    private IsbnService objectUnderTest;
    @Mock
    private IsbnValidationService isbnValidationServiceMock;
    @Mock
    private IsbnChecksumService isbnChecksumServiceMock;

    @BeforeEach
    void setUp() {
        this.objectUnderTest = new IsbnService(this.isbnValidationServiceMock, this.isbnChecksumServiceMock);
    }

    @Test
    void generateIsbn() throws InvalidIsbnException {
        when(this.isbnChecksumServiceMock.createIsbnCheckSum("978140888222")).thenReturn(1);
        String isbn = this.objectUnderTest.generateIsbn("978", "1", "4088", "8222");
        assertEquals("978-1-4088-8222-1", isbn);
    }

    @Test
    void generateIsbnInvalid() throws InvalidIsbnException {
        doThrow(InvalidIsbnException.class).when(this.isbnValidationServiceMock).validateIsbnWithoutChecksum("97814088222");
        assertThrows(InvalidIsbnException.class, () -> this.objectUnderTest.generateIsbn("978", "1", "408", "8222"));

    }


}