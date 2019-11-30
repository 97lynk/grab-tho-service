package vn.edu.hcmute.grab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.hcmute.grab.entity.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

}
