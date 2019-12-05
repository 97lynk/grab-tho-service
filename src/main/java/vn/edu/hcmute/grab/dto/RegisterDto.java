package vn.edu.hcmute.grab.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.hcmute.grab.constant.RoleName;

@Data
@NoArgsConstructor
public class RegisterDto {

    private String username;

    private String password;

    private String email;

    private RoleName role;

    private String fullName;

    private String phone;

    private String address;

    private String major;

}
