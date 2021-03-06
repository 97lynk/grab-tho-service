package vn.edu.hcmute.grab.config.resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
public class OAuth2ResourceServerConfigJwt extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/oauth/authorize**", "/requests/description-images/**", "/login/**").permitAll()
                .antMatchers("/accounts-signup", HttpMethod.POST.name()).permitAll()
                .and()
                .authorizeRequests().anyRequest().authenticated();
    }

}
