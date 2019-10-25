package vn.edu.hcmute.grab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.hcmute.grab.constant.ActionStatus;
import vn.edu.hcmute.grab.dto.JoinedRepairerDto;
import vn.edu.hcmute.grab.entity.RequestHistory;
import vn.edu.hcmute.grab.service.RequestHistoryService;

import java.util.*;
import java.util.stream.Collectors;

import static vn.edu.hcmute.grab.mapper.JoinedRepairerMapper.JOINED_REPAIRER_MAPPER;
import static vn.edu.hcmute.grab.mapper.RequestHistoryMapper.REQUEST_HISTORY_MAPPER;

@RestController
public class RequestHistoryController {

    private final RequestHistoryService requestHistoryService;

    @Autowired
    public RequestHistoryController(RequestHistoryService requestHistoryService) {
        this.requestHistoryService = requestHistoryService;
    }

    @GetMapping("/requests/{id}/histories/repairers")
    public List<JoinedRepairerDto> getAllRepairersReceivedRequest(
            @PathVariable("id") Long requestId,
            @RequestParam(value = "actions", defaultValue = "") List<ActionStatus> actions) {

        return requestHistoryService.getRepairerJoinedRequest(requestId, actions);
    }

    @GetMapping("/requests/{requestId}/histories/repairers/{repairerId}")
    public ResponseEntity<?> getRepairersReceivedRequest(@PathVariable("requestId") Long requestId, @PathVariable("repairerId") Long repairerId) {

        List<RequestHistory> requestHistory = requestHistoryService.getRequestHistory(requestId, repairerId);
//                .stream().map(REQUEST_HISTORY_MAPPER::entityToDto)
//                .collect(Collectors.toList());

        Map<String, Object> data = new LinkedHashMap<>();
        if (!requestHistory.isEmpty()) {
            data.put("repairer", JOINED_REPAIRER_MAPPER.entityToDto(requestHistory.get(0)));
            data.put("histories", requestHistory.stream().map(REQUEST_HISTORY_MAPPER::entityToDto)
                    .collect(Collectors.toList()));
        }else {
            data.put("repairer", null);
            data.put("histories", Collections.emptyList());
        }
        return ResponseEntity.ok(data);
    }
}
