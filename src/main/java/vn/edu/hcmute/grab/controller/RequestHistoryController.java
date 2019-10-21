package vn.edu.hcmute.grab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.hcmute.grab.constant.ActionStatus;
import vn.edu.hcmute.grab.dto.JoinedRepairerDto;
import vn.edu.hcmute.grab.service.RequestHistoryService;

import java.util.List;

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
}
