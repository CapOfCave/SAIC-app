package de.hswhameln.scaicibanvalidation.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IsbnChecksumServiceTest {

    private IsbnChecksumService isbnChecksumService;

    @BeforeEach
    void setUp() {
        this.isbnChecksumService = new IsbnChecksumService();
    }

    @Test
    void createIsbnCheckSum() {
        int isbnCheckSum = this.isbnChecksumService.createIsbnCheckSum("978054501022");
        assertEquals(1, isbnCheckSum);
    }
}