package vn.edu.hcmute.grab.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.hcmute.grab.constant.RequestStatus;
import vn.edu.hcmute.grab.entity.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Page<Request> findAllByUserUsername(Pageable pageable, String username);

    Optional<Request> findByIdAndUserUsername(Long id, String username);

    List<Request> findByRepairerUserUsernameAndStatusIn(String username, List<RequestStatus> statuses);

    Page<Request> findAllByUserUsernameAndStatusIn(Pageable pageable, String username, List<RequestStatus> statuses);

    Page<Request> findAllByStatusIn(Pageable pageable, List<RequestStatus> statuses);

}
