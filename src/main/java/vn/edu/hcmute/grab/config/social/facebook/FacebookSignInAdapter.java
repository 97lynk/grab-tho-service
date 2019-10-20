package vn.edu.hcmute.grab.config.social.facebook;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;
import vn.edu.hcmute.grab.entity.User;
import vn.edu.hcmute.grab.repository.UserRepository;
import vn.edu.hcmute.grab.service.AuthService;

@Service
@Slf4j
public class FacebookSignInAdapter implements SignInAdapter {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Override
    public String signIn(String localUserId, Connection<?> connection, NativeWebRequest request) {
        log.info("Sign account username: " + localUserId);
        User user = userRepository.findByUsername(localUserId)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Not found user with username=%s", localUserId)));

        OAuth2AccessToken accessToken = authService.obtainAccessToken(user.getUsername());

        if (accessToken != null) {
            return "/oauth/social?token=" + accessToken.getRefreshToken().getValue();
        } else {
            log.error("Request access token failed");
            return "/index";
        }
    }
}