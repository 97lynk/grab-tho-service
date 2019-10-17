package vn.edu.hcmute.grab.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.hcmute.grab.entity.Role;
import vn.edu.hcmute.grab.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Page<User> findAll(Pageable page);

    Page<User> findAllByRolesIn(Pageable page, List<Role> roles);
}
