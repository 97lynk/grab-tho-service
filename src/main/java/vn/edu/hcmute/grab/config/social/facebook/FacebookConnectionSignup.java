package vn.edu.hcmute.grab.config.social.facebook;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.stereotype.Service;
import vn.edu.hcmute.grab.constant.RoleName;
import vn.edu.hcmute.grab.entity.User;
import vn.edu.hcmute.grab.repository.RoleRepository;
import vn.edu.hcmute.grab.repository.UserRepository;

import java.util.Arrays;

@Service
@Slf4j
public class FacebookConnectionSignup implements ConnectionSignUp {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public String execute(Connection<?> connection) {
        log.info("Registering new account username: " + connection.getKey());
        final User user = User.builder()
                .username(connection.getKey().getProviderUserId())
                .password(new BCryptPasswordEncoder().encode("tuan"))
                .fullName(connection.getDisplayName())
                .roles(roleRepository.findAllByNameIn(Arrays.asList(RoleName.ROLE_CUSTOMER, RoleName.ROLE_FB)))
                .avatar(connection.getImageUrl())
                .block(false)
                .build();

        userRepository.saveAndFlush(user);
        return user.getUsername();
    }

}
