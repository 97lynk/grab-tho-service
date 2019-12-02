package vn.edu.hcmute.grab.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.hcmute.grab.dto.WalletHistoryDto;
import vn.edu.hcmute.grab.mapper.WalletMapper;
import vn.edu.hcmute.grab.service.WalletService;

import java.util.List;

@RestController
@Slf4j
public class WalletController {

    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/repairers/{id}/wallet-histories")
    @PreAuthorize("hasAnyRole('REPAIRER')")
    public List<WalletHistoryDto> getWalletHistories(@PathVariable("id") Long repairerId) {
        log.info("GET list wallet histories of Repairer#{}", repairerId);
        return WalletMapper.WALLET_MAPPER.entityToDTO(walletService.getListWalletHistories(repairerId));
    }

    @GetMapping("/wallet-histories")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Page<WalletHistoryDto> getAllWalletHistories(@PageableDefault(sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("GET page wallet histories");
        return walletService.getPageOfWalletHistories(pageable).map(WalletMapper.WALLET_MAPPER::entityToDTO);
    }
}
