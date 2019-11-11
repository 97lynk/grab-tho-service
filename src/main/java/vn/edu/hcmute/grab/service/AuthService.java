package vn.edu.hcmute.grab.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.charset.Charset;
import java.util.List;

@Service
@Slf4j
public class AuthService {


    private String CLIENT_ID;


    private String CLIENT_SECRET;


    private HttpHeaders basicAuthHeader;

    private final RestTemplate restTemplate = new RestTemplateBuilder().build();

    private final List<GrantedAuthority> ROLE_FACEBOOK_USER =
            AuthorityUtils.createAuthorityList("ROLE_FACEBOOK_USER");
    ;

    @Autowired
    public AuthService(@Value("${oauth2.client-id}") String clientId,
                       @Value("${oauth2.client-secret}") String clientSecret) {
        CLIENT_ID = clientId;
        CLIENT_SECRET = clientSecret;

        String auth = CLIENT_ID + ":" + CLIENT_SECRET;
        byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        this.basicAuthHeader = new HttpHeaders();
        this.basicAuthHeader.set("Authorization", authHeader);
    }

    public OAuth2AccessToken obtainAccessToken(String username) {

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add(OAuth2Utils.CLIENT_ID, CLIENT_ID);
        parameters.add("client_secret", CLIENT_SECRET);
        parameters.add(OAuth2Utils.GRANT_TYPE, "password");
        parameters.add("username", username);
        parameters.add("password", "tuan");
        try {
            String TOKEN_ENDPOINT = ServletUriComponentsBuilder.fromCurrentContextPath().path("oauth/token").toUriString();

            basicAuthHeader.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            ResponseEntity<OAuth2AccessToken> responseToken = restTemplate.exchange(TOKEN_ENDPOINT, HttpMethod.POST,
                    new HttpEntity<>(parameters, basicAuthHeader), OAuth2AccessToken.class);

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
