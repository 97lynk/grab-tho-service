package vn.edu.hcmute.grab.config.social.facebook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.stereotype.Service;
import vn.edu.hcmute.grab.entity.RoleName;
import vn.edu.hcmute.grab.entity.User;
import vn.edu.hcmute.grab.repository.RoleRepository;
import vn.edu.hcmute.grab.repository.UserRepository;

import java.util.UUID;

@Service
public class FacebookConnectionSignup implements ConnectionSignUp {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public String execute(Connection<?> connection) {
        System.out.println("signup ===> ");
        final User user = User.builder()
                .username(connection.getKey().getProviderUserId())
                .password(passwordEncoder.encode("tuan"))
                .fullName(connection.getDisplayName())
                .roles(roleRepository.findAllByName(RoleName.ROLE_USER))
                .block(false)
                .build();

        userRepository.saveAndFlush(user);
        return user.getUsername();
    }

}