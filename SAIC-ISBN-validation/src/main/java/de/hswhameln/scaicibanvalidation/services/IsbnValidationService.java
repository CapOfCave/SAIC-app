package de.hswhameln.scaicibanvalidation.services;

import de.hswhameln.scaicibanvalidation.exceptions.InvalidIsbnException;
import de.hswhameln.scaicibanvalidation.exceptions.IsbnParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

import static de.hswhameln.scaicibanvalidation.ISBNConstants.CHECKSUM_LENGTH;
import static de.hswhameln.scaicibanvalidation.ISBNConstants.ISBN_LENGTH;
import static de.hswhameln.scaicibanvalidation.ISBNConstants.PREFIX_LENGTH;
import static de.hswhameln.scaicibanvalidation.ISBNConstants.VALID_PREFIXES;

@Service
public class IsbnValidationService {

    private final Pattern isbnPattern = Pattern.compile("\\d{" + PREFIX_LENGTH + "}-\\d+-\\d+-\\d+-\\d{" + CHECKSUM_LENGTH + "}");
    private final Pattern isbnPatternWithoutChecksum = Pattern.compile("\\d{" + PREFIX_LENGTH + "}-\\d+-\\d+-\\d+-?");

    private final Pattern numericPattern = Pattern.compile("\\d+");

    private final IsbnChecksumService isbnChecksumService;

    @Autowired
    public IsbnValidationService(IsbnChecksumService isbnChecksumService) {
        this.isbnChecksumService = isbnChecksumService;
    }

    public void validateIsbn(String isbn) throws InvalidIsbnException {
        if (isbn.contains("-")) {
            validateIsbnWithDash(isbn);
        } else {
            validateIsbnNumeric(isbn);
        }
    }

    public void validateIsbnWithoutChecksum(String isbnWithoutChecksum) throws InvalidIsbnException {
        if (isbnWithoutChecksum.contains("-")) {
            validateIsbnWithoutChecksumWithDash(isbnWithoutChecksum);
        } else {
            validateIsbnWithoutChecksumNumeric(isbnWithoutChecksum);
        }
    }

    public void validatePrefix(String isbnWithoutChecksum) throws InvalidIsbnException {
        String prefix = isbnWithoutChecksum.substring(0, PREFIX_LENGTH);
        if (!VALID_PREFIXES.contains(prefix)) {
            throw new InvalidIsbnException("Prefix must be one of " + VALID_PREFIXES + ".");
        }
    }

    private void validateIsbnWithoutChecksumWithDash(String isbnWithoutChecksum) throws InvalidIsbnException {
        if (!isbnPatternWithoutChecksum.matcher(isbnWithoutChecksum).matches()) {
            throw new IsbnParseException("An ISBN without checksum must consist of exactly 4 blocks seperated by -. It may be followed by -. The first block must have a length of 3. ");
        }
        validateIsbnWithoutChecksumNumeric(isbnWithoutChecksum.replaceAll("-", ""));
    }


    private void validateIsbnWithDash(String isbn) throws InvalidIsbnException {
        if (!isbnPattern.matcher(isbn).matches()) {
            throw new IsbnParseException("An ISBN must consist of exactly 5 blocks seperated by -. The first block must have a length of 3 and the last one must have a length of one.");
        }
        validateIsbnNumeric(isbn.replaceAll("-", ""));
    }

    private void validateIsbnWithoutChecksumNumeric(String isbnWithoutChecksum) throws InvalidIsbnException {
        checkIsbnNumeric(isbnWithoutChecksum);
        checkIsbnLength(isbnWithoutChecksum, ISBN_LENGTH - CHECKSUM_LENGTH);
        validatePrefix(isbnWithoutChecksum);
    }


    private void validateIsbnNumeric(String isbn) throws InvalidIsbnException {
        checkIsbnNumeric(isbn);
        checkIsbnLength(isbn, ISBN_LENGTH);

        String isbnWithoutChecksum = isbn.substring(0, isbn.length() - CHECKSUM_LENGTH);
        validatePrefix(isbnWithoutChecksum);

        int expectedChecksum = this.isbnChecksumService.createIsbnCheckSum(isbnWithoutChecksum);
        int actualChecksum = Integer.parseInt(isbn.substring(isbn.length() - CHECKSUM_LENGTH));

        if (expectedChecksum != actualChecksum) {
            throw new InvalidIsbnException("Checksum is incorrect.");
        }
    }

    private void checkIsbnLength(String isbnWithoutChecksum, int length) throws IsbnParseException {
        if (isbnWithoutChecksum.length() != length) {
            throw new IsbnParseException("The ISBN must have a length of " + length);
        }
    }

    private void checkIsbnNumeric(String isbnWithoutChecksum) throws IsbnParseException {
        if (!numericPattern.matcher(isbnWithoutChecksum).matches()) {
            throw new IsbnParseException("The ISBN must not contain non-digit characters.");
        }
    }
}
