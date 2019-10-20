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

import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class AuthService {

    private final TokenEndpoint tokenEndpoint;

    @Value("${oauth2.client-id}")
    private String CLIENT_ID;

    @Value("${oauth2.client-secret}")
    private String CLIENT_SECRET;

    private final List<GrantedAuthority> ROLE_FACEBOOK_USER =
            AuthorityUtils.createAuthorityList("ROLE_FACEBOOK_USER");;

    @Autowired
    public AuthService(TokenEndpoint tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    public OAuth2AccessToken obtainAccessToken(String username) {

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(OAuth2Utils.CLIENT_ID, CLIENT_ID);
        parameters.put("client_secret", CLIENT_SECRET);
        parameters.put(OAuth2Utils.GRANT_TYPE, "password");
        parameters.put("username", username);
        parameters.put("password", "tuan");
        try {
            ResponseEntity<OAuth2AccessToken> responseToken = tokenEndpoint.postAccessToken(
                    new UsernamePasswordAuthenticationToken(CLIENT_ID, CLIENT_SECRET, ROLE_FACEBOOK_USER), parameters);

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
