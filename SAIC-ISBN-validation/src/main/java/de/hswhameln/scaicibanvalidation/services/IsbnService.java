package de.hswhameln.scaicibanvalidation.services;

import de.hswhameln.scaicibanvalidation.exceptions.InvalidIsbnException;
import org.springframework.stereotype.Service;

@Service
public class IsbnService {

    private final IsbnValidationService isbnValidationService;
    private final IsbnChecksumService isbnChecksumService;

    public IsbnService(IsbnValidationService isbnValidationService, IsbnChecksumService isbnChecksumService) {
        this.isbnValidationService = isbnValidationService;
        this.isbnChecksumService = isbnChecksumService;
    }

    public int createIsbnCheckSum(String isbnWithoutChecksum) throws InvalidIsbnException {
        this.isbnValidationService.validateIsbnWithoutChecksum(isbnWithoutChecksum);
        return isbnChecksumService.createIsbnCheckSum(isbnWithoutChecksum);
    }

    public void validateIsbn(String isbn) throws InvalidIsbnException {
        this.isbnValidationService.validateIsbn(isbn);
    }

    public String generateIsbn(String prefix, String registrationGroup, String registrant, String publicationElement) throws InvalidIsbnException {
        String isbnWithoutChecksum = prefix + registrationGroup + registrant + publicationElement;
        isbnValidationService.validateIsbnWithoutChecksum(isbnWithoutChecksum);
        int isbnCheckSum = this.isbnChecksumService.createIsbnCheckSum(isbnWithoutChecksum);
        return String.join("-", prefix, registrationGroup, registrant, publicationElement, Integer.toString(isbnCheckSum));
    }
}
