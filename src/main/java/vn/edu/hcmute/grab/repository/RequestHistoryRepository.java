package vn.edu.hcmute.grab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.hcmute.grab.constant.ActionStatus;
import vn.edu.hcmute.grab.entity.RequestHistory;

import java.util.List;

public interface RequestHistoryRepository extends JpaRepository<RequestHistory, Long> {

    List<RequestHistory> findAllByRequestIdAndStatusIsIn(Long requestId, List<ActionStatus> actions);
}
