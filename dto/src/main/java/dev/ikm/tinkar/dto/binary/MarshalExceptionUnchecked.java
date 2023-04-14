package dev.ikm.tinkar.dto.binary;

public class MarshalExceptionUnchecked extends RuntimeException {

    public MarshalExceptionUnchecked(String message) {
        super(message);
    }

    public MarshalExceptionUnchecked(Throwable cause) {
        super(cause);
    }
}
