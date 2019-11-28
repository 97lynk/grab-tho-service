package vn.edu.hcmute.grab.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.hcmute.grab.entity.User;

import javax.persistence.*;

@Data
@NoArgsConstructor
public class SettingDto {

    private boolean pushNotification;

    private boolean notification;
}
