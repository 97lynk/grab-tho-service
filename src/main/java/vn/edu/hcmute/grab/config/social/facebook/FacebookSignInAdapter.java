package vn.edu.hcmute.grab.config.social.facebook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;
import vn.edu.hcmute.grab.entity.User;
import vn.edu.hcmute.grab.repository.UserRepository;

@Service
public class FacebookSignInAdapter implements SignInAdapter {

    @Autowired
    private UserRepository userRepository;

    @Override
    public String signIn(String localUserId, Connection<?> connection, NativeWebRequest request) {
        System.out.println(" ======> Sign In adapter");
        User user = userRepository.findByUsername(localUserId)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Not found user with username=%s", localUserId)));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user.getUsername(),
                        user.getPassword(), user.getAuthorities()));
        return "/index";
    }
}