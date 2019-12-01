package vn.edu.hcmute.grab.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.edu.hcmute.grab.constant.ActionStatus;
import vn.edu.hcmute.grab.constant.RoleName;
import vn.edu.hcmute.grab.dto.HistoryDto;
import vn.edu.hcmute.grab.dto.JoinedRepairerDto;
import vn.edu.hcmute.grab.dto.RequestDto;
import vn.edu.hcmute.grab.dto.RequestHistoryDto;
import vn.edu.hcmute.grab.entity.RequestHistory;
import vn.edu.hcmute.grab.service.RequestHistoryService;

import java.util.*;
import java.util.stream.Collectors;

import static vn.edu.hcmute.grab.mapper.JoinedRepairerMapper.JOINED_REPAIRER_MAPPER;
import static vn.edu.hcmute.grab.mapper.RequestHistoryMapper.REQUEST_HISTORY_MAPPER;
import static vn.edu.hcmute.grab.mapper.RequestMapper.REQUEST_MAPPER;

@RestController
@Slf4j
public class RequestHistoryController {

    private final RequestHistoryService requestHistoryService;

    @Autowired
    public RequestHistoryController(RequestHistoryService requestHistoryService) {
        this.requestHistoryService = requestHistoryService;
    }

    /**
     * get list request history (list comment)
     *
     * @param requestId
     * @param actions
     * @param auth
     * @return
     */
    @GetMapping("/requests/{id}/histories/repairers")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'REPAIRER', 'ADMIN')")
    public List<JoinedRepairerDto> getAllRepairersReceivedRequest(
            @PathVariable("id") Long requestId,
            @RequestParam(value = "actions", defaultValue = "") List<ActionStatus> actions,
            Authentication auth) {
        log.info("GET repairers of request#{} with actions={}", requestId, actions);
        return requestHistoryService.getRepairerJoinedRequest(requestId, actions, (isCustomer(auth) || isAdmin(auth) ? null : auth.getName()));
    }

    @GetMapping("/requests/{requestId}/histories/repairers/{repairerId}")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<?> getRepairersReceivedRequest(@PathVariable("requestId") Long requestId, @PathVariable("repairerId") Long repairerId) {

        log.info("GET repairer#{} and histories in request#{}", repairerId);

        List<RequestHistory> requestHistory = requestHistoryService.getRequestHistory(Arrays.asList(requestId), repairerId);

        Map<String, Object> data = new LinkedHashMap<>();
        if (!requestHistory.isEmpty()) {
            data.put("repairer", JOINED_REPAIRER_MAPPER.entityToDto(requestHistory.get(0)));
            data.put("histories", requestHistory.stream().map(REQUEST_HISTORY_MAPPER::entityToDto)
                    .collect(Collectors.toList()));
        } else {
            data.put("repairer", null);
            data.put("histories", Collections.emptyList());
        }
        return ResponseEntity.ok(data);
    }

    /**
     * quoting, receiving, closing a request
     *
     * @param historyDto
     * @return
     */
    @PostMapping("/histories")
    @PreAuthorize("hasAnyRole('REPAIRER')")
    public RequestHistoryDto quoteOrReceiveOrClosingRequest(@RequestBody HistoryDto historyDto) {
        log.info("{} request#{}", historyDto.getAction(), historyDto.getRequestId());
        // TODO the repairer QUOTE, COMPLETE request
        return REQUEST_HISTORY_MAPPER.entityToDto(requestHistoryService.addRequestHistory(historyDto));
    }

    @DeleteMapping("/histories")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public RequestHistoryDto closeRequest(@RequestParam("requestId") Long requestId) {
        log.info("CLOSE request#{}", requestId);
        // TODO the CLOSE request
        return REQUEST_HISTORY_MAPPER.entityToDto(requestHistoryService.closeRequest(requestId));
    }

    @GetMapping("/histories")
    @PreAuthorize("hasAnyRole('REPAIRER')")
    public List<RequestHistoryDto> searchBy(
            @RequestParam(value = "requestId", required = false) List<Long> requestIds,
            Authentication auth) {

        log.info("GET all history of request#{}", requestIds);
        return requestHistoryService.getRequestHistory(requestIds, auth.getName())
                .stream().map(REQUEST_HISTORY_MAPPER::entityToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/histories/feedback")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public List<RequestDto> getFeedBackUser(Authentication auth) {
        log.info("GET feedback of user@{}", auth.getName());
        return REQUEST_MAPPER.entityToDto(requestHistoryService.getFeedback(auth.getName()));
    }

    private boolean isCustomer(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(RoleName.ROLE_CUSTOMER.name()));
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(RoleName.ROLE_ADMIN.name()));
    }

    @PutMapping("/histories/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public RequestHistoryDto hideHistory(@PathVariable("id") Long historyId, @RequestParam boolean hide) {
        return REQUEST_HISTORY_MAPPER.entityToDto(requestHistoryService.hide(historyId, hide));
    }

}
