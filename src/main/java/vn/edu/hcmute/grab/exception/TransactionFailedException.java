package vn.edu.hcmute.grab.exception;

public class TransactionFailedException extends RuntimeException {

    public TransactionFailedException(String message) {
        super(message);
    }

    public TransactionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
