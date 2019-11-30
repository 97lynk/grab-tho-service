package vn.edu.hcmute.grab.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
@Data
@NoArgsConstructor
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long xeng;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "repairer_id", referencedColumnName = "id")
    private Repairer repairer;

    @OneToMany(mappedBy="wallet", fetch = FetchType.LAZY)
    private List<WalletHistory> walletHistories;
}
