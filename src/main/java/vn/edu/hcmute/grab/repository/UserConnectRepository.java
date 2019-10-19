package vn.edu.hcmute.grab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.hcmute.grab.entity.UserConnection;

public interface UserConnectRepository extends JpaRepository<UserConnection, UserConnection.ProjectId> {
}
