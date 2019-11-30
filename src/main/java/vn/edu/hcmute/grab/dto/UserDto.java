package vn.edu.hcmute.grab.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    private boolean pushNotification;

    private boolean notification;

    private boolean block;

    @Enumerated(EnumType.STRING)
    private List<RoleName> roles;

    @Builder
    public UserDto(Long id, String username, String email, String fullName, String address, String phone, String avatar, boolean pushNotification, boolean notification, boolean block, List<RoleName> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.address = address;
        this.phone = phone;
        this.avatar = avatar;
        this.pushNotification = pushNotification;
        this.notification = notification;
        this.block = block;
        this.roles = roles;
    }
}
