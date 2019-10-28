package vn.edu.hcmute.grab.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.hcmute.grab.constant.ActionStatus;

@Data
@NoArgsConstructor
public class HistoryDto {

    private Long requestId;

    private Long repairerId;

    private long point;

    private ActionStatus action;

}
