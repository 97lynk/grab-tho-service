package vn.edu.hcmute.grab.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GrabExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<?> handlingUserException(UserException ex){
        Map<String, String> errorMessage = new LinkedHashMap<>();
        errorMessage.put("error", "exist_user");
        errorMessage.put("error_description", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(errorMessage);
    }
}
