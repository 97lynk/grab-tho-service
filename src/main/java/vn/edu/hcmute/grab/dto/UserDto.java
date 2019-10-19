package vn.edu.hcmute.grab.dto;

import lombok.*;
import vn.edu.hcmute.grab.entity.RoleName;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;

    private String fullName;

    private String email;

    private String address;

    private String phone;

    private boolean block;

    private String b64;

    private String fileType;

    @Enumerated(EnumType.STRING)
    private List<RoleName> role;
}
