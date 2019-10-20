package vn.edu.hcmute.grab.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.edu.hcmute.grab.constant.RequestStatus;
import vn.edu.hcmute.grab.dto.AddRequestDto;
import vn.edu.hcmute.grab.dto.RequestDto;
import vn.edu.hcmute.grab.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
@Slf4j
public class RequestController {

    private final RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    public Page<RequestDto> getAllRequestOfUser(@PageableDefault Pageable pageable,
                                                @RequestParam(value = "status", defaultValue = "[]") List<RequestStatus> statuses,
                                                Authentication auth) {
        log.info("Get a page of request, user {}, filter status={}", auth.getName(), statuses);
        if (statuses.isEmpty())
            return requestService.getPageRequestOfUser(pageable, auth.getName());
        else
            return filterRequest(pageable, statuses, auth);
    }

    @PostMapping
    public RequestDto newRequest(@RequestBody AddRequestDto requestDto, Authentication auth) {
        log.info("Add new request, user " + auth.getName());
        return requestService.addNewRequest(requestDto, auth.getName());
    }

    public Page<RequestDto> filterRequest(Pageable pageable, List<RequestStatus> statuses, Authentication auth) {
        return requestService.getPageRequestOfUserAndFilterByStatus(pageable, auth.getName(), statuses);
    }
}
