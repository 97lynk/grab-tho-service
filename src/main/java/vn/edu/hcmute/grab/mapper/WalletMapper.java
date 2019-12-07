package vn.edu.hcmute.grab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import vn.edu.hcmute.grab.dto.RepairerDto;
import vn.edu.hcmute.grab.dto.WalletHistoryDto;
import vn.edu.hcmute.grab.entity.Repairer;
import vn.edu.hcmute.grab.entity.WalletHistory;

import java.util.List;

@Mapper
public interface WalletMapper {

    WalletMapper WALLET_MAPPER = Mappers.getMapper(WalletMapper.class);

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.fullName", target = "fullName")
    @Mapping(source = "user.address", target = "address")
    @Mapping(source = "user.phone", target = "phone")
    @Mapping(source = "user.avatar", target = "avatar")
    @Mapping(source = "wallet.xeng", target = "xeng")
    @Mapping(source = "user.id", target = "uid")
    RepairerDto entityToDTO(Repairer repairer);

    @Mapping(source = "wallet.id", target = "walletId")
    @Mapping(source = "wallet.repairer", target = "repairer")
    WalletHistoryDto entityToDTO(WalletHistory history);

    List<WalletHistoryDto> entityToDTO(List<WalletHistory> histories);

}
