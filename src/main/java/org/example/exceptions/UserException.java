package org.example.exceptions;

import java.io.IOException;
import java.io.Serial;

public class UserException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    public UserException() {
        super();
    }
    public UserException(String customMessage) {
        super(customMessage);
    }
    public UserException(String couldNotInitializeStorage, IOException e) {
    }
}
