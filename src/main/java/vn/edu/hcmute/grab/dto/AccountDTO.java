package vn.edu.hcmute.grab.dto;

import lombok.*;
import vn.edu.hcmute.grab.entity.RoleName;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDTO {

    private String email;

    private String password;

    private String fullName;

    private String address;

    private String phone;

    private List<RoleName> role;

}
