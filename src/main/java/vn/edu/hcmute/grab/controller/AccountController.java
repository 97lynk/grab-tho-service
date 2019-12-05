package vn.edu.hcmute.grab.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import vn.edu.hcmute.grab.constant.RoleName;
import vn.edu.hcmute.grab.dto.ProfileDto;
import vn.edu.hcmute.grab.dto.RegisterDto;
import vn.edu.hcmute.grab.dto.SettingDto;
import vn.edu.hcmute.grab.dto.UserDto;
import vn.edu.hcmute.grab.entity.Role;
import vn.edu.hcmute.grab.entity.Setting;
import vn.edu.hcmute.grab.entity.User;
import vn.edu.hcmute.grab.repository.SettingRepository;
import vn.edu.hcmute.grab.service.UserService;

import java.util.List;
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
    public UserDto registerAccount(@RequestBody RegisterDto registerDto, Authentication auth) {
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

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')") // only ADMIN can get list users
    public Page<UserDto> getAlLUser(@PageableDefault(page = 0, size = 10) Pageable page,
                                    @RequestParam(value = "role", defaultValue = "") List<String> roles) {
        return userService.selectPageOfUsersInRoles(page, roles).map(USER_MAPPER::entityToDTOWithRoles);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable("id") Long id) {
        User user = userService.selectUserById(id);
        return USER_MAPPER.entityToDTOWithRoles(user);
    }


    @PutMapping("/{id}/block")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public UserDto blockUser(@PathVariable("id") Long id) {
        User user = userService.blockUserById(id, true);

        return USER_MAPPER.entityToDTOWithRoles(user);
    }

    @PutMapping("/{id}/unblock")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public UserDto unblockUser(@PathVariable("id") Long id) {
        User user = userService.blockUserById(id, false);
        return USER_MAPPER.entityToDTOWithRoles(user);
    }

    @PostMapping("/{id}/password")
    @PreAuthorize("#oauth2.hasAnyScope('read')") // for authenticated request (logged)
    public UserDto changePasswordAUser(@PathVariable("id") Long id,
                                       @RequestParam("password") String newPassword,
                                       @RequestParam(value = "oldPassword", defaultValue = "") String oldPassword,
                                       OAuth2Authentication auth) throws Exception {
//        RoleName role;
//        if (!validRequest(auth, id)) throw new AccessDeniedException("Access dined");
//        User authUser = userService.selectUserByUsername(auth.getName());
//        if (authUser.getRoles().stream().map(Role::getName).anyMatch(roleName -> roleName == RoleName.ROLE_ADMIN)) {
//            role = RoleName.ROLE_ADMIN;
//        } else {
//            role = RoleName.ROLE_CUSTOMER;
//        }
        User user = userService.changePassword(id, newPassword, oldPassword, RoleName.ROLE_ADMIN);

        return USER_MAPPER.entityToDTO(user);
    }

    @GetMapping("/check")
    public boolean checkExistUser(@RequestParam("email") String email) {
        return (userService.selectUserByUsername(email) != null);
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public UserDto changeRole(@PathVariable("id") Long id,
                              @RequestBody List<RoleName> role) {
        User user = userService.changeRole(id, role);
        return USER_MAPPER.entityToDTO(user);
    }

    //user request change profile or admin
    public boolean validRequest(OAuth2Authentication auth, Long userId) {
        User authUser = userService.selectUserByUsername(auth.getName());

        // has ROLE_ADMIN or has userid equal userid request
        return authUser.getRoles().stream().map(Role::getName).anyMatch(roleName -> roleName == RoleName.ROLE_ADMIN)
                || authUser.getId().equals(userId);
    }
}
