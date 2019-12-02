package vn.edu.hcmute.grab.service;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.TransactionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.edu.hcmute.grab.entity.Repairer;
import vn.edu.hcmute.grab.entity.Wallet;
import vn.edu.hcmute.grab.entity.WalletHistory;
import vn.edu.hcmute.grab.repository.RepairerRepository;
import vn.edu.hcmute.grab.repository.WalletHistoryRepository;
import vn.edu.hcmute.grab.repository.WalletRepository;

import java.util.List;

@Service
public class WalletService {

    private final WalletHistoryRepository walletHistoryRepository;

    private final WalletRepository walletRepository;

    private final RepairerRepository repairerRepository;

    @Autowired
    public WalletService(WalletHistoryRepository walletHistoryRepository, WalletRepository walletRepository, RepairerRepository repairerRepository) {
        this.walletHistoryRepository = walletHistoryRepository;
        this.walletRepository = walletRepository;
        this.repairerRepository = repairerRepository;
    }

    public List<WalletHistory> getListWalletHistories(Long repairerId) {
        Repairer repairer = repairerRepository.findByUserId(repairerId)
                .orElseThrow(() -> new ObjectNotFoundException(repairerId, Repairer.class.getSimpleName()));

        return walletHistoryRepository.findAllByWalletIdOrderByCreateAtDesc(repairer.getWallet().getId());
    }

    public Page<WalletHistory> getPageOfWalletHistories(Pageable pageable) {
        return walletHistoryRepository.findAll(pageable);
    }

    public WalletHistory transaction(WalletHistory history, Wallet wallet) {
        long xeng = wallet.getXeng() + history.getXeng();
        if (xeng < 0) {
            throw new TransactionException("Không đủ xèng");
        }

        history = walletHistoryRepository.save(history);
        wallet.setXeng(xeng);
        walletRepository.save(wallet);
        return history;
    }
}
