package org.example.exceptions;

import java.io.IOException;
import java.io.Serial;

public class StorageException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public StorageException() {
        super();
    }

    public StorageException(String customMessage) {
        super(customMessage);
    }

    public StorageException(String couldNotInitializeStorage, IOException e) {
    }
}
