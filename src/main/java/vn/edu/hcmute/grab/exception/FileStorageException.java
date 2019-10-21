package vn.edu.hcmute.grab.exception;

public class FileStorageException extends RuntimeException {

    public FileStorageException(String s, Exception ex) {
        super(s, ex);
    }

    public FileStorageException(String s) {
        super(s);
    }
}
