package vn.edu.hcmute.grab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import vn.edu.hcmute.grab.dto.*;
import vn.edu.hcmute.grab.entity.Request;

@Mapper
public interface RequestMapper {

    RequestMapper REQUEST_MAPPER =  Mappers.getMapper(RequestMapper.class);

    @Mappings({
            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "repairer.id", target = "repairerId")
    })
    RequestDto entityToDto(Request request);

    @Mappings({
            @Mapping(source = "user.fullName", target = "userFullName")
    })
    RecentRequestDto entityToRecentDto(Request request);

    @Mappings({
//            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "repairer.user.fullName", target = "repairerName"),
    })
    AcceptedRequestDto entityToAcceptedDto(Request request);

    @Mappings({
//            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "repairer.user.fullName", target = "repairerName"),
    })
    CompletedRequestDto entityToCompletedDto(Request request);

    Request dtoToEntity(RecentRequestDto recentRequestDto);

    Request dtoToEntity(AddRequestDto requestDto);
}
