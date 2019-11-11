package vn.edu.hcmute.grab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.hcmute.grab.constant.ActionStatus;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestHistoryDto {

    private Long id;

    @Enumerated(EnumType.STRING)
    private ActionStatus status;

    private long point;

    private LocalDateTime createAt;

    private Long requestId;

    private Long repairerId;
}
