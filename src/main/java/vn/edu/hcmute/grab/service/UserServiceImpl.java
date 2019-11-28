package vn.edu.hcmute.grab.service;

import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.hcmute.grab.constant.RoleName;
import vn.edu.hcmute.grab.dto.ProfileDto;
import vn.edu.hcmute.grab.dto.RegisterDto;
import vn.edu.hcmute.grab.dto.SettingDto;
import vn.edu.hcmute.grab.entity.Role;
import vn.edu.hcmute.grab.entity.Setting;
import vn.edu.hcmute.grab.entity.User;
import vn.edu.hcmute.grab.exception.UserException;
import vn.edu.hcmute.grab.repository.RoleRepository;
import vn.edu.hcmute.grab.repository.SettingRepository;
import vn.edu.hcmute.grab.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final SettingRepository settingRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, SettingRepository settingRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.settingRepository = settingRepository;
    }

    @Override
    public User selectUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(String.format("Not found user with username=%s", username)));
    }

    @Override
    public Role selectRoleByName(RoleName name) {
        return roleRepository.findByName(name);
    }

    @Override
    public User changePassword(Long id, String newPassword) throws Exception {
        User user = selectUserById(id);

//        if (!passwordEncoder.matches(oldPassword, user.getPassword()))
//            throw new WrongPasswordException("Mật khẩu không đúng");
        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));

        return userRepository.save(user);
    }

    @Override
    public Page<User> selectPageOfUsersInRoles(Pageable page, List<String> rolesString) {
        List<Role> roles = rolesString.stream()
                .map(RoleName::valueOf)
                .map(this::selectRoleByName).collect(Collectors.toList());
        return userRepository.findAllByRolesIn(page, roles);
    }

    @Override
    public User blockUserById(Long id, boolean block) {
        User user = selectUserById(id);
        user.setBlock(block);
        return userRepository.save(user);
    }

    @Override
    public User selectUserById(Long id) {
        return userRepository.findById(id).get();
    }

    @Override
    public User changeRole(Long id, List<RoleName> role) {
        User user = selectUserById(id);
        user.setRoles(role.stream().map(this::selectRoleByName).collect(Collectors.toList()));
        return userRepository.save(user);
    }

    @Override
    public User registration(RegisterDto registerDto) {
        userRepository.findByEmail(registerDto.getEmail()).ifPresent((user) -> {
            throw new UserException("Email đã được đăng kí tài khoản khác");
        });
        userRepository.findByUsername(registerDto.getUsername()).ifPresent((user) -> {
            throw new UserException("Tài khoản đã tồn tại");
        });

        User user = new User();
        user.setEmail(registerDto.getEmail());
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setRoles(Arrays.asList(selectRoleByName(RoleName.ROLE_CUSTOMER)));
        user.setAvatar("http://tinygraphs.com/isogrids/tinygraphs?theme=frogideas&numcolors=2&size=220&fmt=svg");
        return userRepository.save(user);
    }

    @Override
    public void changeAvatar(Long id, byte[] fileBytes) {
        User user = userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, User.class.getSimpleName()));
//        user.setB64(Base64.getEncoder().encodeToString(fileBytes));
        userRepository.save(user);
    }

    @Override
    public User updateAvatar(Long id, String avatarUrl) {
        User user = userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, User.class.getSimpleName()));
        user.setAvatar(avatarUrl);
        return userRepository.save(user);
    }

    @Override
    public User updateSetting(Long id, SettingDto settingDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, User.class.getSimpleName()));
        Setting setting = settingRepository.findByUserId(id).orElse(new Setting());
        setting.setNotification(settingDto.isNotification());
        setting.setPushNotification(settingDto.isPushNotification());
        if (setting.getUser() == null) {
            setting.setUser(user);
        }
        setting = settingRepository.save(setting);

        user.setSetting(setting);
        return user;
    }

    @Override
    public User updateProfile(Long id, ProfileDto profileDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, User.class.getSimpleName()));
        user.setFullName(profileDto.getFullName());
        user.setAddress(profileDto.getAddress());
        user.setPhone(profileDto.getPhone());
        user.setEmail(profileDto.getEmail());
        return userRepository.save(user);
    }

}
