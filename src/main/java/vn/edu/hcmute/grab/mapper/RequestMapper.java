package vn.edu.hcmute.grab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import vn.edu.hcmute.grab.dto.AcceptedRequestDto;
import vn.edu.hcmute.grab.dto.AddRequestDto;
import vn.edu.hcmute.grab.dto.RecentRequestDto;
import vn.edu.hcmute.grab.dto.RequestDto;
import vn.edu.hcmute.grab.entity.Request;

@Mapper
public interface RequestMapper {

    RequestMapper REQUEST_MAPPER =  Mappers.getMapper(RequestMapper.class);

    @Mappings({
            @Mapping(source = "user.id", target = "userId")
    })
    RequestDto entityToDto(Request request);

    @Mappings({
            @Mapping(source = "user.id", target = "userId")
    })
    RecentRequestDto entityToRecentDto(Request request);

    @Mappings({
            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "repairer.user.fullName", target = "repairerName"),
    })
    AcceptedRequestDto entityToAcceptedDto(Request request);

    Request dtoToEntity(RecentRequestDto recentRequestDto);

    Request dtoToEntity(AddRequestDto requestDto);
}
