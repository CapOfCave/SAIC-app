package de.hswhameln.scaicibanvalidation.services;

import org.springframework.stereotype.Service;

import static de.hswhameln.scaicibanvalidation.ISBNConstants.CHECKSUM_LENGTH;
import static de.hswhameln.scaicibanvalidation.ISBNConstants.ISBN_LENGTH;

@Service
public class IsbnChecksumService {

    public int createIsbnCheckSum(String isbnWithoutChecksum) {
        int sum = 0;
        int weight = 1;
        for (int i = 0; i < ISBN_LENGTH - CHECKSUM_LENGTH; i++) {
            sum += weight * Character.getNumericValue(isbnWithoutChecksum.charAt(i));
            weight = 4 - weight;
        }
        return (10 - sum % 10) % 10;
    }
}
