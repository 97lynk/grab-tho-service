package vn.edu.hcmute.grab.config.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import vn.edu.hcmute.grab.dto.UserDto;
import vn.edu.hcmute.grab.entity.User;
import vn.edu.hcmute.grab.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static vn.edu.hcmute.grab.mapper.UserMapper.USER_MAPPER;

public class CustomTokenEnhancer implements TokenEnhancer {

    @Autowired
    private UserService userService;

    @Value("${oauth2.client-id}")
    private String[] CLIENT_ID;


    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication auth) {
        final Map<String, Object> additionalInfo = new HashMap<>();
        if (auth.getOAuth2Request().getClientId().equals(CLIENT_ID[1])) {
            User user = userService.selectUserByUsername(auth.getName());
            UserDto userDTO = USER_MAPPER.entityToDTO(user);
            additionalInfo.put("account", userDTO);
        }
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }
}
