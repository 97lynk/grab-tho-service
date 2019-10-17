package vn.edu.hcmute.grab.config.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import vn.edu.hcmute.grab.dto.UserDTO;
import vn.edu.hcmute.grab.entity.Role;
import vn.edu.hcmute.grab.entity.User;
import vn.edu.hcmute.grab.mapper.UserMapper;
import vn.edu.hcmute.grab.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomTokenEnhancer implements TokenEnhancer {

    @Autowired
    private UserRepository userRepository;

    UserMapper mapper = UserMapper.INSTANCE;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication auth) {
        final Map<String, Object> additionalInfo = new HashMap<>();
        User user = userRepository.findByUsername(auth.getName()).get();
        UserDTO userDTO = mapper.entityToDTO(user);
        userDTO.setB64(null);
        userDTO.setFileType(null);
        userDTO.setRole(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        additionalInfo.put("account", userDTO);
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }
}
