package vn.edu.hcmute.grab.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterDto {

    private String username;

    private String password;

    private String email;
}