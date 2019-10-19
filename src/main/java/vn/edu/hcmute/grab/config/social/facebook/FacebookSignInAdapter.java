package vn.edu.hcmute.grab.config.social.facebook;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.expression.OAuth2ExpressionUtils;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.context.request.NativeWebRequest;
import vn.edu.hcmute.grab.entity.User;
import vn.edu.hcmute.grab.repository.UserRepository;

import java.util.HashMap;

@Service
@Slf4j
public class FacebookSignInAdapter implements SignInAdapter {

    @Value("${oauth2.client-id}")
    private String CLIENT_ID;

    @Value("${oauth2.client-secret}")
    private String CLIENT_SECRET;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenEndpoint tokenEndpoint;

    @Override
    public String signIn(String localUserId, Connection<?> connection, NativeWebRequest request) {
        log.info("Sign account username: " + localUserId);
        User user = userRepository.findByUsername(localUserId)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Not found user with username=%s", localUserId)));
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(OAuth2Utils.CLIENT_ID, CLIENT_ID);
        parameters.put("client_secret", CLIENT_SECRET);
        parameters.put(OAuth2Utils.GRANT_TYPE, "password");
        parameters.put("username", user.getUsername());
        parameters.put("password", "tuan");
        try {
            ResponseEntity<OAuth2AccessToken> responseEntity = tokenEndpoint.postAccessToken(
                    new UsernamePasswordAuthenticationToken(CLIENT_ID, CLIENT_SECRET, user.getAuthorities()), parameters);
            log.info("Request access token => status " + responseEntity.getStatusCode());
            return "/oauth/social?token=" + responseEntity.getBody().getRefreshToken().getValue();
        } catch (HttpRequestMethodNotSupportedException e) {
            log.error("Request access failed");
            log.error(e.getMessage());
            return "/index";
        }
    }
}