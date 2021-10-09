package de.hswhameln.scaicibanvalidation;

import java.util.Set;

public interface ISBNConstants {
    int PREFIX_LENGTH = 3;
    int CHECKSUM_LENGTH = 1;
    int ISBN_LENGTH = 13;

    Set<String> VALID_PREFIXES = Set.of("978", "979");
}
