package vn.edu.hcmute.grab.exception;

public class FileInvalidException extends RuntimeException {

    public FileInvalidException() {
    }

    public FileInvalidException(String message) {
        super(message);
    }

    public FileInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileInvalidException(Throwable cause) {
        super(cause);
    }
}
