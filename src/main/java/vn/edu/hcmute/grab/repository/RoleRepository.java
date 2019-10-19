package vn.edu.hcmute.grab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.hcmute.grab.entity.Role;
import vn.edu.hcmute.grab.constant.RoleName;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(RoleName roleName);

    boolean existsByName(RoleName roleName);

    List<Role> findAllByName(RoleName roleName);
}
