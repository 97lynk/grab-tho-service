package vn.edu.hcmute.grab.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.edu.hcmute.grab.constant.RoleName;
import vn.edu.hcmute.grab.entity.Role;
import vn.edu.hcmute.grab.entity.User;
import vn.edu.hcmute.grab.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    @Value("${oauth2.client-id}")
    private String[] CLIENT_ID;

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

        String clientId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<RoleName> roles = account.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        if (clientId.equals(CLIENT_ID[1]) && !roles.contains(RoleName.ROLE_ADMIN)) {
            throw new UsernameNotFoundException(String.format("Not found user with username=%s", userName));
        }

        if (clientId.equals(CLIENT_ID[0]) && roles.contains(RoleName.ROLE_ADMIN)) {
            throw new UsernameNotFoundException(String.format("Not found user with username=%s", userName));
        }
        log.info(account.toString());
        return new org.springframework.security.core.userdetails.User(
                account.getUsername(), account.getPassword(), true,
                true, true, !account.isBlock(),
                AuthorityUtils.createAuthorityList(roles.stream().map(RoleName::toString).toArray(String[]::new))
        );
    }


}
