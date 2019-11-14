package vn.edu.hcmute.grab.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AuthService {

    private String CLIENT_ID;

    private String CLIENT_SECRET;

    @Inject
    private TokenEndpoint tokenEndpoint;

    private final List<GrantedAuthority> ROLE_FACEBOOK_USER =
            AuthorityUtils.createAuthorityList("ROLE_FACEBOOK_USER");
    ;

    @Autowired
    public AuthService(@Value("${oauth2.client-id}") String clientId,
                       @Value("${oauth2.client-secret}") String clientSecret) {
        CLIENT_ID = clientId;
        CLIENT_SECRET = clientSecret;
    }

    public OAuth2AccessToken obtainAccessToken(String username) {


        Map<String, String> params = new LinkedHashMap<>();
        params.put(OAuth2Utils.CLIENT_ID, CLIENT_ID);
        params.put("client_secret", CLIENT_SECRET);
        params.put(OAuth2Utils.GRANT_TYPE, "password");
        params.put("username", username);
        params.put("password", "tuan");
        try {
            ResponseEntity<OAuth2AccessToken> responseToken = tokenEndpoint.postAccessToken(
                    new UsernamePasswordAuthenticationToken(CLIENT_ID, CLIENT_SECRET, ROLE_FACEBOOK_USER), params);

            if (responseToken.getStatusCode() != HttpStatus.OK)
                throw new Exception("Caused error when request access token");

            log.info("Request access token success");
            return responseToken.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("Request access token failed {}", e.getMessage());
            return null;
        }
    }

}
