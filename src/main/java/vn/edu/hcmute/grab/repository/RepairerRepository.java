package vn.edu.hcmute.grab.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.hcmute.grab.constant.RequestStatus;
import vn.edu.hcmute.grab.entity.Repairer;
import vn.edu.hcmute.grab.entity.Request;

import java.util.List;

public interface RepairerRepository extends JpaRepository<Repairer, Long> {
}
