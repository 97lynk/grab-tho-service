package vn.edu.hcmute.grab.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.ProviderSignInAttempt;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.util.UriComponentsBuilder;
import vn.edu.hcmute.grab.config.social.facebook.FacebookConnectionSignup;
import vn.edu.hcmute.grab.entity.User;
import vn.edu.hcmute.grab.repository.UserRepository;
import vn.edu.hcmute.grab.service.AuthService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class AuhController {


    private final UserRepository userRepository;

    private final AuthService authService;

    private final UsersConnectionRepository usersConnectionRepository;

    private final ConnectionFactoryLocator connectionFactoryLocator;

    private final FacebookConnectionSignup facebookConnectionSignup;

    @Autowired
    public AuhController(UserRepository userRepository, AuthService authService, UsersConnectionRepository usersConnectionRepository,
                         ConnectionFactoryLocator connectionFactoryLocator, FacebookConnectionSignup facebookConnectionSignup) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.usersConnectionRepository = usersConnectionRepository;
        this.connectionFactoryLocator = connectionFactoryLocator;
        this.facebookConnectionSignup = facebookConnectionSignup;
    }

    @PostMapping("/login/{providerId}")
    public ResponseEntity receiveFacebookAccessToken(@PathVariable("providerId") String providerId,
                                                     @RequestParam(value = "token") String fbAccessToken, NativeWebRequest request) {
        log.info("POST {} token {}", providerId, fbAccessToken);

        // check valid fb token
        String url = UriComponentsBuilder.fromUriString("https://graph.facebook.com/v2.11/debug_token")
                .queryParam("access_token", fbAccessToken)
                .queryParam("input_token", fbAccessToken).toUriString();
        ResponseEntity<Map> responseEntity = new RestTemplateBuilder().build().getForEntity(url, Map.class);

        Long expireAt = Long.parseLong(((LinkedHashMap) responseEntity.getBody().get("data")).getOrDefault("expires_at", 0l) + "");

        OAuth2ConnectionFactory<?> connectionFactory = (OAuth2ConnectionFactory<?>) connectionFactoryLocator.getConnectionFactory(providerId);

        // add userconnection into database
        AccessGrant accessGrant = new AccessGrant(fbAccessToken, "public_profile", null, expireAt);
        Connection<?> connection = connectionFactory.createConnection(accessGrant);

        // check whether user is logged
        List<String> userIds = usersConnectionRepository.findUserIdsWithConnection(connection);
        if (userIds.size() == 0) {
            ProviderSignInAttempt signInAttempt = new ProviderSignInAttempt(connection);
            new HttpSessionSessionStrategy().setAttribute(request, ProviderSignInAttempt.SESSION_ATTRIBUTE, signInAttempt);
            facebookConnectionSignup.execute(connection);
            return receiveFacebookAccessToken(providerId, fbAccessToken, request);
        } else if (userIds.size() == 1) {
            usersConnectionRepository.createConnectionRepository(userIds.get(0)).updateConnection(connection);

            User user = userRepository.findByUsername(userIds.get(0))
                    .orElseThrow(() -> new UsernameNotFoundException(String.format("Not found user with username=%s", userIds.get(0))));

            OAuth2AccessToken accessToken = authService.obtainAccessToken(user.getUsername());

            if (accessToken != null) {
                return ResponseEntity.ok(accessToken);
            } else {
                return ResponseEntity.badRequest().body("Caused error when request access token");
            }

        } else {
            return ResponseEntity.badRequest().body("multiple");
        }
    }
}
