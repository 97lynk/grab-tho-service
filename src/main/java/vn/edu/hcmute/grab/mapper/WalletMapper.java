package vn.edu.hcmute.grab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import vn.edu.hcmute.grab.dto.UserDto;
import vn.edu.hcmute.grab.dto.WalletHistoryDto;
import vn.edu.hcmute.grab.entity.Role;
import vn.edu.hcmute.grab.entity.User;
import vn.edu.hcmute.grab.entity.WalletHistory;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface WalletMapper {

    WalletMapper WALLET_MAPPER = Mappers.getMapper(WalletMapper.class);

    @Mapping(source = "wallet.id", target = "walletId")
    WalletHistoryDto entityToDTO(WalletHistory history);

    List<WalletHistoryDto> entityToDTO(List<WalletHistory> histories);

}
