package vn.edu.hcmute.grab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import vn.edu.hcmute.grab.dto.RepairerDto;
import vn.edu.hcmute.grab.dto.UserDto;
import vn.edu.hcmute.grab.entity.Repairer;
import vn.edu.hcmute.grab.entity.Role;
import vn.edu.hcmute.grab.entity.User;

import java.util.stream.Collectors;

@Mapper
public interface RepairerMapper {

    RepairerMapper REPAIRER_MAPPER = Mappers.getMapper(RepairerMapper.class);

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.fullName", target = "fullName")
    @Mapping(source = "user.address", target = "address")
    @Mapping(source = "user.phone", target = "phone")
    @Mapping(source = "user.avatar", target = "avatar")
    RepairerDto entityToDTO(Repairer repairer);

}