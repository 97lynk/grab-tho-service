package vn.edu.hcmute.grab.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.edu.hcmute.grab.dto.ProfileDto;
import vn.edu.hcmute.grab.dto.RegisterDto;
import vn.edu.hcmute.grab.dto.SettingDto;
import vn.edu.hcmute.grab.dto.UserDto;
import vn.edu.hcmute.grab.entity.Setting;
import vn.edu.hcmute.grab.entity.User;
import vn.edu.hcmute.grab.repository.SettingRepository;
import vn.edu.hcmute.grab.service.UserService;

import java.util.Optional;

import static vn.edu.hcmute.grab.mapper.UserMapper.USER_MAPPER;

@RestController
@RequestMapping("/accounts")
@Slf4j
public class AccountController {

    private final UserService userService;

    private final SettingRepository settingRepository;

    @Autowired
    public AccountController(UserService userService, SettingRepository settingRepository) {
        this.userService = userService;
        this.settingRepository = settingRepository;
    }

    @RequestMapping("/me")
    public UserDto getMyInfo(Authentication auth) {
        log.info("GET profile {}", auth.getName());
        User user = userService.selectUserByUsername(auth.getName());
        if (user.getFullName() == null || user.getFullName().trim().isEmpty())
            user.setFullName(user.getUsername());
        UserDto userDto = USER_MAPPER.entityToDTOWithRoles(user);
        Optional<Setting> optionalSetting = settingRepository.findByUserUsername(auth.getName());
        if(!optionalSetting.isPresent()){
            userDto.setPushNotification(true);
            userDto.setNotification(true);
        }else {
            userDto.setPushNotification(optionalSetting.get().isPushNotification());
            userDto.setNotification(optionalSetting.get().isNotification());
        }
        return userDto;
    }

    @PostMapping
    public UserDto registerAccount(@RequestBody RegisterDto registerDto) {
        User user = userService.registration(registerDto);
        return USER_MAPPER.entityToDTOWithRoles(user);
    }

    @PutMapping("/{id}")
    public UserDto updateProfile(@PathVariable("id") Long id, @RequestBody ProfileDto profileDto) {
        log.info("PUT user#{} {}", id, profileDto);
        return USER_MAPPER.entityToDTOWithRoles(userService.updateProfile(id, profileDto));
    }

    @PutMapping("/{id}/avatar")
    public UserDto updateAvatar(@PathVariable("id") Long id, @RequestBody String avatarUrl) {
        log.info("PUT avatar user#{} {}", id, avatarUrl);
        return USER_MAPPER.entityToDTOWithRoles(userService.updateAvatar(id, avatarUrl));
    }

    @PutMapping("/{id}/setting")
    public UserDto updateSetting(@PathVariable("id") Long id, @RequestBody SettingDto setting) {
        log.info("PUT setting user#{} {}", id, setting);
        return USER_MAPPER.entityToDTOWithRoles(userService.updateSetting(id, setting));
    }

}
