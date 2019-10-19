package vn.edu.hcmute.grab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import vn.edu.hcmute.grab.dto.AddRequestDto;
import vn.edu.hcmute.grab.dto.RequestDto;
import vn.edu.hcmute.grab.entity.Request;

@Mapper
public interface RequestMapper {

    RequestMapper REQUEST_MAPPER =  Mappers.getMapper(RequestMapper.class);

    @Mapping(source = "user.id", target = "userId")
    RequestDto entityToDto(Request request);

    Request dtoToEntity(RequestDto requestDto);

    Request dtoToEntity(AddRequestDto requestDto);
}
