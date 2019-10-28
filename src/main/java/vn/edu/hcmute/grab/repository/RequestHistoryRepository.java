package vn.edu.hcmute.grab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.hcmute.grab.constant.ActionStatus;
import vn.edu.hcmute.grab.entity.RequestHistory;

import java.util.List;
import java.util.Optional;

public interface RequestHistoryRepository extends JpaRepository<RequestHistory, Long> {

    List<RequestHistory> findAllByRequestIdAndStatusIsInOrderByCreateAtDesc(Long requestId, List<ActionStatus> actions);

    Optional<RequestHistory> findByRequestIdAndRepairerIdAndStatus(Long requestId, Long repairerId, ActionStatus status);

    List<RequestHistory> findByRequestIdInAndRepairerId(List<Long> requestIds, Long repairerId);
}
