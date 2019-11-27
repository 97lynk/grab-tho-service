package vn.edu.hcmute.grab.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.ObjectNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GrabThoExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<?> handlingUserException(UserException ex){
        return ResponseEntity.badRequest()
                .body(new GrabThoExceptDto("exist_user",  ex.getMessage()));
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<?> handlingObjectNotFoundException(ObjectNotFoundException ex){
        return ResponseEntity.badRequest()
                .body(new GrabThoExceptDto("not_exist_object",  ex.getMessage()));
    }

    @ExceptionHandler(TransactionFailedException.class)
    public ResponseEntity<?> handlingTransactionFailedException(TransactionFailedException ex){
        return ResponseEntity.badRequest()
                .body(new GrabThoExceptDto("not_enough_xeng",  ex.getMessage()));
    }

    @Data
    @AllArgsConstructor
    public class GrabThoExceptDto {

        @JsonProperty("error")
        private String error;

        @JsonProperty("error_description")
        private String errorDescription;
    }
}
