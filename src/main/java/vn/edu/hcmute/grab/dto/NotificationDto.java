package vn.edu.hcmute.grab.dto;

import lombok.Builder;
import lombok.Data;
import vn.edu.hcmute.grab.constant.ActionStatus;

@Data
@Builder
public class NotificationDto {

    private String sender;

    private String message;

    private Long sendAt;

    private Long requestId;

    private boolean seen;

    private ActionStatus action;

    private String thumbnail;
}
