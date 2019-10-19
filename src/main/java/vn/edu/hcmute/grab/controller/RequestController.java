package vn.edu.hcmute.grab.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.edu.hcmute.grab.dto.AddRequestDto;
import vn.edu.hcmute.grab.dto.RequestDto;
import vn.edu.hcmute.grab.service.RequestService;

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
    public Page<RequestDto> getAllRequestOfUser(@PageableDefault Pageable pageable, Authentication auth) {
        log.info("Get a page of request, user " + auth.getName());
        return requestService.getPageRequestOfUser(pageable, auth.getName());
    }

    @PostMapping
    public RequestDto newRequest(@RequestBody AddRequestDto requestDto, Authentication auth){
        log.info("Add new request, user " + auth.getName());
        return requestService.addNewRequest(requestDto, auth.getName());
    }
}
