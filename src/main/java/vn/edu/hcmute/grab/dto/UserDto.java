package vn.edu.hcmute.grab.dto;

import lombok.*;
import vn.edu.hcmute.grab.constant.RoleName;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.List;

@Data
@NoArgsConstructor
public class UserDto {

    private Long id;

    private String username;

    private String email;

    private String fullName;

    private String address;

    private String phone;

    private String avatar;

    @Enumerated(EnumType.STRING)
    private List<RoleName> roles;

    @Builder
    public UserDto(Long id, String username, String email, String fullName, String address, String phone, String avatar, List<RoleName> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.address = address;
        this.phone = phone;
        this.avatar = avatar;
        this.roles = roles;
    }
}
