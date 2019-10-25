package vn.edu.hcmute.grab.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.hcmute.grab.constant.RoleName;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepairerDto {

    private Long id;

    private String username;

    private String email;

    private String fullName;

    private String address;

    private String phone;

    private String avatar;

    private float rating = 0.0f;
}
