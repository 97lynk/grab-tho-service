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
import vn.edu.hcmute.grab.entity.*;
import vn.edu.hcmute.grab.exception.UserException;
import vn.edu.hcmute.grab.exception.WrongPasswordException;
import vn.edu.hcmute.grab.repository.RepairerRepository;
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

    private final RepairerRepository repairerRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, SettingRepository settingRepository, RepairerRepository repairerRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.settingRepository = settingRepository;
        this.repairerRepository = repairerRepository;
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
    public User changePassword(Long id, String newPassword, String oldPassword, RoleName role) throws Exception {
        User user = selectUserById(id);

        if (role.equals(RoleName.ROLE_CUSTOMER) || role.equals(RoleName.ROLE_REPAIRER)) {
            if (!passwordEncoder.matches(oldPassword, user.getPassword()))
                throw new WrongPasswordException("Mật khẩu không đúng");
        }

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
        userRepository.findByUsername(registerDto.getUsername()).ifPresent((user) -> {
            throw new UserException("Tài khoản đã tồn tại");
        });

        User user = new User();
        user.setEmail(registerDto.getEmail());
        user.setUsername(registerDto.getUsername());
        if (registerDto.getFullName() == null || registerDto.getFullName().isEmpty()) {
            user.setFullName(registerDto.getUsername());
        } else {
            user.setFullName(registerDto.getFullName());
        }
        user.setAddress(registerDto.getAddress());
        user.setPhone(registerDto.getPhone());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        // public register
        if (registerDto.getRole() == null) {
            registerDto.setRole(RoleName.ROLE_CUSTOMER);
        }
        user.setRoles(Arrays.asList(selectRoleByName(registerDto.getRole())));
        user.setAvatar("http://tinygraphs.com/isogrids/tinygraphs?theme=frogideas&numcolors=2&size=220&fmt=svg");
        Setting setting = new Setting();
        setting.setNotification(true);
        setting.setPushNotification(true);
        setting = settingRepository.save(setting);
        user.setSetting(setting);

        if (registerDto.getRole() == RoleName.ROLE_REPAIRER) {
            Wallet wallet = new Wallet();
            wallet.setXeng(0l);

            Repairer repairer = new Repairer();
            repairer.setMajor(registerDto.getMajor());
            repairer.setCompletedJob(0l);
            repairer.setReviews(0l);
            repairer.setRating(0.0f);
            repairer.setWallet(wallet);
            wallet.setRepairer(repairer);
            repairer.setUser(user);
            repairer = repairerRepository.save(repairer);
            return repairer.getUser();
        }else {
            return userRepository.save(user);
        }
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
