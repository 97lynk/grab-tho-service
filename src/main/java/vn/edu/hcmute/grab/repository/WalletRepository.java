package vn.edu.hcmute.grab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.hcmute.grab.entity.Wallet;
import vn.edu.hcmute.grab.entity.WalletHistory;

import java.util.List;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

}
