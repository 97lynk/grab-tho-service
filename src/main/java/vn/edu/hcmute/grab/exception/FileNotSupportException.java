package vn.edu.hcmute.grab.exception;

public class FileNotSupportException extends RuntimeException {

    public FileNotSupportException() {
    }

    public FileNotSupportException(String message) {
        super(message);
    }

    public FileNotSupportException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileNotSupportException(Throwable cause) {
        super(cause);
    }
}
