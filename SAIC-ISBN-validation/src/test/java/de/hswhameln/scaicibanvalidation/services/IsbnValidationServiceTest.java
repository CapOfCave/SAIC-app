package de.hswhameln.scaicibanvalidation.services;

import de.hswhameln.scaicibanvalidation.exceptions.InvalidIsbnException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IsbnValidationServiceTest {

    private IsbnValidationService objectUnderTest;

    @Mock
    private IsbnChecksumService isbnChecksumServiceMock;

    @BeforeEach
    void setUp() {
        this.objectUnderTest = new IsbnValidationService(this.isbnChecksumServiceMock);
    }

    @Test
    void testValidateValidIsbn() {
        when(this.isbnChecksumServiceMock.createIsbnCheckSum("978059035340")).thenReturn(3);
        assertDoesNotThrow(() -> this.objectUnderTest.validateIsbn("9780590353403"));
    }

    @Test
    void testValidateValidIsbnWithDashes() {
        when(this.isbnChecksumServiceMock.createIsbnCheckSum("978316148410")).thenReturn(0);
        assertDoesNotThrow(() -> this.objectUnderTest.validateIsbn("978-31-6148-410-0"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "978059035340", "978-31-614-410-0", "979-1-234567-789-6", "97808859035340"})
    void testValidateIsbnWithWrongLength(String isbn) {
        assertThrows(InvalidIsbnException.class, () ->  this.objectUnderTest.validateIsbn(isbn));
    }

    @ParameterizedTest
    @ValueSource(strings = { "9780590353404", "978-31-6148-410-2", "979-1-8619-7271-6"})
    void testValidateIsbnWithIncorrectChecksum(String isbn) {
        when(this.isbnChecksumServiceMock.createIsbnCheckSum(anyString())).thenReturn(5);
        assertThrows(InvalidIsbnException.class, () ->  this.objectUnderTest.validateIsbn(isbn));
    }

    @ParameterizedTest
    @ValueSource(strings = { "9590590353404", "912-31-6148-410-2", "111-1-8619-7271-6"})
    void testValidateIsbnWithIncorrectPrefix(String isbn) {
        assertThrows(InvalidIsbnException.class, () ->  this.objectUnderTest.validateIsbn(isbn));
    }

    @ParameterizedTest
    @ValueSource(strings = { "AAAAAAAAAAAAA", "978-AA-AAAA-BBB-2", "XYZ"})
    void testValidateIsbnWithLetters(String isbn) {
        assertThrows(InvalidIsbnException.class, () ->  this.objectUnderTest.validateIsbn(isbn));
    }

    @ParameterizedTest
    @ValueSource(strings = { "979-322-3-24-1", "97870124000"})
    void testValidateValidPrefix(String isbn) {
        assertDoesNotThrow(() -> this.objectUnderTest.validatePrefix(isbn));
    }

    @ParameterizedTest
    @ValueSource(strings = { "939-322-3-24-1", "7870124000"})
    void testValidateInvalidPrefix(String isbn) {
        assertThrows(InvalidIsbnException.class, () -> this.objectUnderTest.validatePrefix(isbn));
    }

    @Test
    void testValidateIsbnWithoutChecksumWrongLength() {
        assertThrows(InvalidIsbnException.class, () -> this.objectUnderTest.validateIsbnWithoutChecksum("97814088222"));
    }

    @Test
    void validateIsbnWithoutChecksumInvalidPrefix() {
        assertThrows(InvalidIsbnException.class, () -> this.objectUnderTest.validateIsbnWithoutChecksum("555140888222"));
    }

    @Test
    void validateIsbnWithoutChecksumNonNumericCharacters() {
        assertThrows(InvalidIsbnException.class, () -> this.objectUnderTest.validateIsbnWithoutChecksum("978a40888222"));
    }
}