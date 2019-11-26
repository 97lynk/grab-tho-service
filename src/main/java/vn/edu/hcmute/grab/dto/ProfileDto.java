package vn.edu.hcmute.grab.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProfileDto {

    private String fullName;

    private String phone;

    private String email;

    private String address;
}
