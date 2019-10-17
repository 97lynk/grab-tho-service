package vn.edu.hcmute.grab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import vn.edu.hcmute.grab.dto.UserDTO;
import vn.edu.hcmute.grab.entity.Role;
import vn.edu.hcmute.grab.entity.User;

import java.util.stream.Collectors;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mappings({})
    UserDTO entityToDTO(User user);

    default UserDTO entityToDTOWithRoles(User user) {
        UserDTO userDTO = entityToDTO(user);
        userDTO.setRole(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        return userDTO;
    }
}
