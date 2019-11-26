package vn.edu.hcmute.grab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import vn.edu.hcmute.grab.dto.RepairerDto;
import vn.edu.hcmute.grab.entity.Repairer;

@Mapper
public interface RepairerMapper {

    RepairerMapper REPAIRER_MAPPER = Mappers.getMapper(RepairerMapper.class);

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.fullName", target = "fullName")
    @Mapping(source = "user.address", target = "address")
    @Mapping(source = "user.phone", target = "phone")
    @Mapping(source = "user.avatar", target = "avatar")
    @Mapping(source = "wallet.xeng", target = "xeng")
    RepairerDto entityToDTO(Repairer repairer);

}
