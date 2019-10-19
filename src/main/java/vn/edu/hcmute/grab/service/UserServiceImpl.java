package vn.edu.hcmute.grab.service;

import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.hcmute.grab.dto.UserDto;
import vn.edu.hcmute.grab.entity.Role;
import vn.edu.hcmute.grab.constant.RoleName;
import vn.edu.hcmute.grab.entity.User;
import vn.edu.hcmute.grab.repository.RoleRepository;
import vn.edu.hcmute.grab.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
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
        user.setPassword(passwordEncoder.encode(newPassword));

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
    public User changeProfile(UserDto userDTO) {
        User user = selectUserById(userDTO.getId());
        user.setPhone(userDTO.getPhone());
        user.setFullName(userDTO.getFullName());
        user.setAddress(userDTO.getAddress());
//        user.setB64(userDTO.getB64());
//        user.setFileType(userDTO.getFileType());

        return userRepository.save(user);
    }

    @Override
    public User changeRole(Long id, List<RoleName> role) {
        User user = selectUserById(id);
        user.setRoles(role.stream().map(this::selectRoleByName).collect(Collectors.toList()));
        return userRepository.save(user);
    }

//    @Override
//    public User registration(AccountDTO accountDto) {
//        userRepository.findByEmail(accountDto.getEmail()).ifPresent((user) -> new UserException("Email đã tồn tại"));
//
//        User user = new User();
//        if (accountDto.getFullName().trim().length() <= 0)
//            user.setFullName(user.getEmail());
//        else
//            user.setFullName(accountDto.getFullName());
//        user.setEmail(accountDto.getEmail());
//        user.setAddress(accountDto.getAddress());
//        user.setPhone(accountDto.getPhone());
//        user.setPassword(passwordEncoder.encode(accountDto.getPassword()));
//        if (accountDto.getRole() != null && !accountDto.getRole().isEmpty()) {
//            user.setRoles(accountDto.getRole().stream().map(this::selectRoleByName).collect(Collectors.toList()));
//        } else {
//            user.setRoles(Arrays.asList(selectRoleByName(RoleName.ROLE_USER)));
//        }
//        return userRepository.save(user);
//    }

    @Override
    public void changeAvatar(Long id, byte[] fileBytes) {
        User user = userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, User.class.getSimpleName()));
//        user.setB64(Base64.getEncoder().encodeToString(fileBytes));
        userRepository.save(user);
    }


}
