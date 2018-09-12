package com.bsuir.spolks.exception;

import java.security.PrivilegedActionException;

public class WrongCommandFormatException extends Exception {

    public WrongCommandFormatException() {
        super();
    }

    public WrongCommandFormatException(String message) {
        super(message);
    }

    public WrongCommandFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongCommandFormatException(Throwable cause) {
        super(cause);
    }

    protected WrongCommandFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
