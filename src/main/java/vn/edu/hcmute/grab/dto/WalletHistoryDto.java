package vn.edu.hcmute.grab.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.hcmute.grab.entity.WalletHistory;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class WalletHistoryDto {

    private Long id;

    private long xeng;

    private Long walletId;

    private RepairerDto repairer;

    private LocalDateTime createAt;

    @Enumerated(EnumType.STRING)
    private WalletHistory.WalletAction action;

    private String note;
}
