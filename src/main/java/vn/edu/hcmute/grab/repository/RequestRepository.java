package vn.edu.hcmute.grab.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.hcmute.grab.entity.Request;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Page<Request> findAllByUserUsername(Pageable pageable, String username);

}
