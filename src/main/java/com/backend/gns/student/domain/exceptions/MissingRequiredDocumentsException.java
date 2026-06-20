package com.backend.gns.student.domain.exceptions;

import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import java.util.List;

public class MissingRequiredDocumentsException extends RuntimeException {
    private final List<TypeDocument> missingDocuments;

    public MissingRequiredDocumentsException(String message, List<TypeDocument> missingDocuments) {
        super(message);
        this.missingDocuments = missingDocuments;
    }

    public List<TypeDocument> getMissingDocuments() {
        return missingDocuments;
    }
}
