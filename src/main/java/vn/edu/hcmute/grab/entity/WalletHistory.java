package vn.edu.hcmute.grab.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table
@Data
@NoArgsConstructor
public class WalletHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long xeng;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    private LocalDateTime createAt;

    @Enumerated(EnumType.STRING)
    private WalletAction action;

    private String note;

    @Builder
    public WalletHistory(long xeng, Wallet wallet, LocalDateTime createAt, WalletAction action, String note) {
        this.xeng = xeng;
        this.wallet = wallet;
        this.createAt = createAt;
        this.action = action;
        this.note = note;
    }

    public enum  WalletAction {
        QUOTE, RECHARGE, REFUND_QUOTE;
    }
}
