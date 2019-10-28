package vn.edu.hcmute.grab.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.hcmute.grab.entity.Repairer;

import java.util.Optional;

public interface RepairerRepository extends JpaRepository<Repairer, Long> {

    Optional<Repairer> findByUserId(Long id);

    Optional<Repairer> findByUserUsername(String username);
}
