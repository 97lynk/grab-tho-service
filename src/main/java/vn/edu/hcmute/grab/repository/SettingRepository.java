package vn.edu.hcmute.grab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.hcmute.grab.entity.Setting;

import java.util.List;
import java.util.Optional;

public interface SettingRepository extends JpaRepository<Setting, Long> {

    Optional<Setting> findByUserUsername(String username);

    List<Setting> findAllByUserUsernameIn(List<String> username);

    Optional<Setting> findByUserId(Long userId);

}
