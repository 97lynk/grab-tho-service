package vn.edu.hcmute.grab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.hcmute.grab.dto.UserDto;
import vn.edu.hcmute.grab.entity.User;
import vn.edu.hcmute.grab.service.UserService;

import static vn.edu.hcmute.grab.mapper.UserMapper.USER_MAPPER;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final UserService userService;

    @Autowired
    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/me")
    public UserDto getMyInfo(Authentication auth) {
        User user = userService.selectUserByUsername(auth.getName());
        return USER_MAPPER.entityToDTOWithRoles(user);
    }
}
