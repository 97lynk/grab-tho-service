package vn.edu.hcmute.grab.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.edu.hcmute.grab.entity.Role;
import vn.edu.hcmute.grab.entity.RoleName;
import vn.edu.hcmute.grab.entity.User;
import vn.edu.hcmute.grab.repository.UserRepository;


@Service
@Slf4j
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userName)
            throws UsernameNotFoundException {
        log.info("load user with username={}", userName);

        User account = userRepository.findByUsername(userName).orElseThrow(
                () -> new UsernameNotFoundException(String.format("Not found user with username=%s", userName)));

        log.info(account.toString());
        return new org.springframework.security.core.userdetails.User(
                account.getUsername(), account.getPassword(), true,
                true, true, !account.isBlock(),
                AuthorityUtils.createAuthorityList(
                        account.getRoles().stream().map(Role::getName)
                                .map(RoleName::toString)
                                .toArray(String[]::new))
        );
    }


}
