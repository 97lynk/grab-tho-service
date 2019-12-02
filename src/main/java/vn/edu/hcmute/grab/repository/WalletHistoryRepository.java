package vn.edu.hcmute.grab.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.hcmute.grab.entity.WalletHistory;

import java.util.List;

public interface WalletHistoryRepository extends JpaRepository<WalletHistory, Long> {

    List<WalletHistory> findAllByWalletIdOrderByCreateAtDesc(Long walletId);

    Page<WalletHistory> findAll(Pageable pageable);
}
