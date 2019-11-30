package vn.edu.hcmute.grab;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import vn.edu.hcmute.grab.constant.RoleName;
import vn.edu.hcmute.grab.entity.Role;
import vn.edu.hcmute.grab.repository.RoleRepository;
import vn.edu.hcmute.grab.service.FileStorageProperties;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableConfigurationProperties({
        FileStorageProperties.class
})
@Slf4j
public class GrabApplication implements CommandLineRunner {

    @PostConstruct
    public void init(){
        // Setting Spring Boot SetTimeZone
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
    }

    public static void main(String[] args) {
        SpringApplication.run(GrabApplication.class, args);
    }


    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if(!roleRepository.existsByName(RoleName.ROLE_CUSTOMER))
            roleRepository.save(Role.builder().name(RoleName.ROLE_CUSTOMER).build());
        if(!roleRepository.existsByName(RoleName.ROLE_REPAIRER))
            roleRepository.save(Role.builder().name(RoleName.ROLE_REPAIRER).build());
        if(!roleRepository.existsByName(RoleName.ROLE_ADMIN))
            roleRepository.save(Role.builder().name(RoleName.ROLE_ADMIN).build());
        if(!roleRepository.existsByName(RoleName.ROLE_FB))
            roleRepository.save(Role.builder().name(RoleName.ROLE_FB).build());
    }
}
