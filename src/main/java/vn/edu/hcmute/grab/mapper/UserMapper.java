package vn.edu.hcmute.grab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import vn.edu.hcmute.grab.dto.UserDto;
import vn.edu.hcmute.grab.entity.Role;
import vn.edu.hcmute.grab.entity.User;

import java.util.stream.Collectors;

@Mapper
public interface UserMapper {

    UserMapper USER_MAPPER = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "roles", target = "roles", ignore = true)
    UserDto entityToDTO(User user);

    default UserDto entityToDTOWithRoles(User user) {
        UserDto userDTO = entityToDTO(user);
        userDTO.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        return userDTO;
    }
}
