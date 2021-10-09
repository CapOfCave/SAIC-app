package de.hswhameln.scaicibanvalidation.services;

import de.hswhameln.scaicibanvalidation.exceptions.InvalidIsbnException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IsbnCreationService {

    private final IsbnValidationService isbnValidationService;
    private final IsbnChecksumService isbnChecksumService;

    @Autowired
    public IsbnCreationService(IsbnValidationService isbnValidationService, IsbnChecksumService isbnChecksumService) {
        this.isbnValidationService = isbnValidationService;
        this.isbnChecksumService = isbnChecksumService;
    }




}
