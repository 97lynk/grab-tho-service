package vn.edu.hcmute.grab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import vn.edu.hcmute.grab.dto.RequestHistoryDto;
import vn.edu.hcmute.grab.entity.RequestHistory;

@Mapper
public interface RequestHistoryMapper {

    RequestHistoryMapper REQUEST_HISTORY_MAPPER = Mappers.getMapper(RequestHistoryMapper.class);

    @Mapping(source = "repairer.id", target = "repairerId")
    @Mapping(source = "request.id", target = "requestId")
    RequestHistoryDto entityToDto(RequestHistory requestHistory);

}
