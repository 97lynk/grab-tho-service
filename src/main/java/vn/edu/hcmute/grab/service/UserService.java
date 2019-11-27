/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.edu.hcmute.grab.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.hcmute.grab.constant.RoleName;
import vn.edu.hcmute.grab.dto.ProfileDto;
import vn.edu.hcmute.grab.dto.RegisterDto;
import vn.edu.hcmute.grab.dto.UserDto;
import vn.edu.hcmute.grab.entity.Role;
import vn.edu.hcmute.grab.entity.User;

import java.util.List;

/**
 *
 * @author 97lynk
 */
public interface UserService {

    User selectUserByUsername(String userName);

    Role selectRoleByName(RoleName name);

    User changePassword(Long id, String newPassword) throws Exception;

    Page<User> selectPageOfUsersInRoles(Pageable page, List<String> rolesString);

    User blockUserById(Long id, boolean block);

    User selectUserById(Long id);

    User updateProfile(Long id, ProfileDto profileDto);

    User changeRole(Long id, List<RoleName> role);

    User registration(RegisterDto registerDto);

    void changeAvatar(Long id, byte[] fileBytes);

    User updateAvatar(Long id, String avatarUrl);
}
