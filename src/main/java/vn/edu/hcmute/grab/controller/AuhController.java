package vn.edu.hcmute.grab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.ProviderSignInAttempt;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.util.UriComponentsBuilder;
import vn.edu.hcmute.grab.config.social.facebook.FacebookSignInAdapter;
import vn.edu.hcmute.grab.entity.User;
import vn.edu.hcmute.grab.repository.UserRepository;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AuhController {

    @Value("${oauth2.client-id}")
    private String CLIENT_ID;

    @Value("${oauth2.client-secret}")
    private String CLIENT_SECRET;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenEndpoint tokenEndpoint;

    @Autowired
    private UsersConnectionRepository usersConnectionRepository;

    @Autowired
    private FacebookSignInAdapter facebookSignInAdapter;

    @Autowired
    private ConnectionFactoryLocator connectionFactoryLocator;

    @PostMapping("/login/{providerId}")
    public ResponseEntity receiveFacebookAccessToken(@PathVariable("providerId") String providerId,
                                                     @RequestParam(value = "token") String accessToken, NativeWebRequest request) {

        String url = UriComponentsBuilder.fromUriString("https://graph.facebook.com/v2.11/debug_token")
                .queryParam("access_token", accessToken)
                .queryParam("input_token", accessToken).toUriString();
        ResponseEntity<Map> responseEntity = new RestTemplateBuilder().build().getForEntity(url, Map.class);

        Long expireAt = Long.parseLong(((LinkedHashMap) responseEntity.getBody().get("data")).getOrDefault("expires_at", 0l) + "");
        OAuth2ConnectionFactory<?> connectionFactory = (OAuth2ConnectionFactory<?>) connectionFactoryLocator.getConnectionFactory(providerId);
        AccessGrant accessGrant = new AccessGrant(accessToken, "public_profile", null, expireAt);
        Connection<?> connection = connectionFactory.createConnection(accessGrant);

        List<String> userIds = usersConnectionRepository.findUserIdsWithConnection(connection);
        if (userIds.size() == 0) {
            ProviderSignInAttempt signInAttempt = new ProviderSignInAttempt(connection);
            new HttpSessionSessionStrategy().setAttribute(request, ProviderSignInAttempt.SESSION_ATTRIBUTE, signInAttempt);
            return receiveFacebookAccessToken(providerId, accessToken, request);
        } else if (userIds.size() == 1) {
            usersConnectionRepository.createConnectionRepository(userIds.get(0)).updateConnection(connection);

            User user = userRepository.findByUsername(userIds.get(0))
                    .orElseThrow(() -> new UsernameNotFoundException(String.format("Not found user with username=%s", userIds.get(0))));
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put(OAuth2Utils.CLIENT_ID, CLIENT_ID);
            parameters.put("client_secret", CLIENT_SECRET);
            parameters.put(OAuth2Utils.GRANT_TYPE, "password");
            parameters.put("username", user.getUsername());
            parameters.put("password", "tuan");
            try {
                ResponseEntity<OAuth2AccessToken> responseToken = tokenEndpoint.postAccessToken(
                        new UsernamePasswordAuthenticationToken(CLIENT_ID, CLIENT_SECRET, user.getAuthorities()), parameters);
                return responseToken;
            } catch (HttpRequestMethodNotSupportedException e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().body("Exception " + e.getMessage());
            }
        }

        return ResponseEntity.badRequest().body("multiple");
    }
}
