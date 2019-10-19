package vn.edu.hcmute.grab;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import vn.edu.hcmute.grab.constant.RoleName;
import vn.edu.hcmute.grab.entity.Role;
import vn.edu.hcmute.grab.repository.RoleRepository;

@SpringBootApplication
@Slf4j
public class GrabApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(GrabApplication.class, args);
    }


    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if(!roleRepository.existsByName(RoleName.ROLE_USER))
            roleRepository.save(Role.builder().name(RoleName.ROLE_USER).build());
        if(!roleRepository.existsByName(RoleName.ROLE_ADMIN))
            roleRepository.save(Role.builder().name(RoleName.ROLE_ADMIN).build());
    }
}
