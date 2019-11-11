package vn.edu.hcmute.grab.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.hcmute.grab.dto.UserDto;
import vn.edu.hcmute.grab.entity.User;
import vn.edu.hcmute.grab.repository.RepairerRepository;
import vn.edu.hcmute.grab.service.UserService;

import static vn.edu.hcmute.grab.mapper.UserMapper.USER_MAPPER;

@RestController
@RequestMapping("/accounts")
@Slf4j
public class AccountController {

    private final UserService userService;

    private final RepairerRepository repairerRepository;


    @Autowired
    public AccountController(UserService userService, RepairerRepository repairerRepository) {
        this.userService = userService;
        this.repairerRepository = repairerRepository;
    }

    @RequestMapping("/me")
    public UserDto getMyInfo(Authentication auth) {
        log.info("GET profile {}", auth.getName());
        User user = userService.selectUserByUsername(auth.getName());
        repairerRepository.findByUserUsername(auth.getName()).ifPresent(r -> {
            user.setId(r.getId());
        });
        return USER_MAPPER.entityToDTOWithRoles(user);
    }
}
