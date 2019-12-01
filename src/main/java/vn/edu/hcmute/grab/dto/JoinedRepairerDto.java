package vn.edu.hcmute.grab.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.hcmute.grab.constant.ActionStatus;
import vn.edu.hcmute.grab.constant.RoleName;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class JoinedRepairerDto {

    private Long id;

    private Long uid;

    private String username;

    private String email;

    private String fullName;

    private String address;

    private String phone;

    private String avatar;

    @Enumerated(EnumType.STRING)
    private List<RoleName> roles;

    @Enumerated(EnumType.STRING)
    private ActionStatus status;

    private long point;

    private LocalDateTime createAt;

    private Boolean hide = false;

    @Builder
    public JoinedRepairerDto(Long id, Long uid, String username, String email, String fullName, String address,
                             String phone, String avatar, List<RoleName> roles, ActionStatus status, long point,
                             LocalDateTime createAt, Boolean hide) {
        this.id = id;
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.address = address;
        this.phone = phone;
        this.avatar = avatar;
        this.roles = roles;
        this.status = status;
        this.point = point;
        this.createAt = createAt;
        this.hide = hide;
    }
}
