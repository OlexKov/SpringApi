package org.example.exceptions;

import java.io.Serial;

public class CategoryException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;

    public CategoryException() {
        super();
    }

    public CategoryException(String customMessage) {
        super(customMessage);
    }
}
