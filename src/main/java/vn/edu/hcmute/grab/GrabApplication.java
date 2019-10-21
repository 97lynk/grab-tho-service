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

@SpringBootApplication
@EnableConfigurationProperties({
        FileStorageProperties.class
})
@Slf4j
public class GrabApplication implements CommandLineRunner {

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
    }
}
