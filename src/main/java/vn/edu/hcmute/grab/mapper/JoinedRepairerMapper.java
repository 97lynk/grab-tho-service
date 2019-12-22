package vn.edu.hcmute.grab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import vn.edu.hcmute.grab.dto.JoinedRepairerDto;
import vn.edu.hcmute.grab.entity.RequestHistory;
import vn.edu.hcmute.grab.entity.Role;

import java.util.stream.Collectors;

@Mapper
public interface JoinedRepairerMapper {

    JoinedRepairerMapper JOINED_REPAIRER_MAPPER = Mappers.getMapper(JoinedRepairerMapper.class);

    @Mappings({
            @Mapping(source = "repairer.user.username", target = "username"),
            @Mapping(source = "repairer.user.email", target = "email"),
            @Mapping(source = "repairer.user.fullName", target = "fullName"),
            @Mapping(source = "repairer.user.address", target = "address"),
            @Mapping(source = "repairer.user.phone", target = "phone"),
            @Mapping(source = "repairer.user.avatar", target = "avatar"),
            @Mapping(source = "repairer.major", target = "major"),
            @Mapping(source = "repairer.rating", target = "rate")
    })
    JoinedRepairerDto entityToDto(RequestHistory requestHistory);

    default JoinedRepairerDto entityToDtoWithRole(RequestHistory requestHistory) {
        JoinedRepairerDto joinedRepairerDto = entityToDto(requestHistory);
        if(requestHistory.getRepairer() != null)
            joinedRepairerDto.setRoles(requestHistory.getRepairer().getUser().getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        return joinedRepairerDto;
    }
}
