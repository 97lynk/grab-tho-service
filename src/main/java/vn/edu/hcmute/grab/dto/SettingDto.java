package vn.edu.hcmute.grab.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SettingDto {

    private boolean pushNotification;

    private boolean notification;
}
