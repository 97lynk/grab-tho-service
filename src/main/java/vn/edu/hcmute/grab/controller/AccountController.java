package vn.edu.hcmute.grab.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.edu.hcmute.grab.dto.ProfileDto;
import vn.edu.hcmute.grab.dto.RegisterDto;
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


    @Autowired
    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/me")
    public UserDto getMyInfo(Authentication auth) {
        log.info("GET profile {}", auth.getName());
        User user = userService.selectUserByUsername(auth.getName());
        if(user.getFullName() == null || user.getFullName().trim().isEmpty())
            user.setFullName(user.getUsername());
        return USER_MAPPER.entityToDTOWithRoles(user);
    }

    @PostMapping
    public UserDto registerAccount(@RequestBody RegisterDto registerDto) {
        User user = userService.registration(registerDto);
        return USER_MAPPER.entityToDTOWithRoles(user);
    }

    @PutMapping("/{id}")
    public UserDto updateProfile(@PathVariable("id") Long id, @RequestBody ProfileDto profileDto) {
        log.info("PUT use#{} {}", id,  profileDto);
        return USER_MAPPER.entityToDTOWithRoles(userService.updateProfile(id, profileDto));
    }

}
