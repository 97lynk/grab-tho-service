package vn.edu.hcmute.grab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.hcmute.grab.constant.ActionStatus;
import vn.edu.hcmute.grab.constant.RequestStatus;
import vn.edu.hcmute.grab.constant.RoleName;
import vn.edu.hcmute.grab.dto.RequestDto;
import vn.edu.hcmute.grab.dto.UserDto;
import vn.edu.hcmute.grab.mapper.RequestHistoryMapper;
import vn.edu.hcmute.grab.service.RequestHistoryService;
import vn.edu.hcmute.grab.service.RequestService;
import vn.edu.hcmute.grab.service.UserServiceImpl;

import java.util.Arrays;
import java.util.List;

import static vn.edu.hcmute.grab.mapper.RequestMapper.REQUEST_MAPPER;
import static vn.edu.hcmute.grab.mapper.UserMapper.USER_MAPPER;

@RestController
@RequestMapping("/posts")
@PreAuthorize("hasAnyRole('ADMIN', 'MOD')")
public class RequestManagementController {

    private final RequestService requestService;

    private final UserServiceImpl userService;

    private final RequestHistoryService requestHistoryService;


    @Autowired
    public RequestManagementController(RequestService requestService, UserServiceImpl userService, RequestHistoryService requestHistoryService) {
        this.requestService = requestService;
        this.userService = userService;
        this.requestHistoryService = requestHistoryService;
    }

    @GetMapping("/{id}")
    public RequestDto getRequestById(@PathVariable("id") Long requestId) {
        return requestService.getRequest(requestId);
    }

    @GetMapping
    public Page<?> getAllRequest(@PageableDefault(sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable,
                                 @RequestParam(value = "status", defaultValue = "") List<RequestStatus> statuses) {
        return requestService.getPageRequestAndFilterByStatus(pageable, statuses);
    }

    @GetMapping("/users/{id}")
    public Page<?> getAllRequest(@PageableDefault(sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable,
                                 @RequestParam(value = "status", defaultValue = "") List<RequestStatus> statuses,
                                 @PathVariable("id") Long userId) {
        UserDto user = USER_MAPPER.entityToDTOWithRoles(userService.selectUserById(userId));
        if (user.getRoles().contains(RoleName.ROLE_CUSTOMER)) {
            if (statuses.isEmpty())
                return requestService.getPageRequestOfUser(pageable, user.getUsername());
            else
                return requestService.getPageRequestOfUserAndFilterByStatus(pageable, user.getUsername(), statuses);
        } else {
            return requestService.getPageRequestOfRepairer(pageable, user.getUsername());
        }
    }

    @GetMapping("/users/{id}/histories")
    public Page<?> getAllHistoryRequest(@PageableDefault(sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable,
                                        @RequestParam(value = "action", defaultValue = "") List<ActionStatus> statuses,
                                        @PathVariable("id") Long userId,
                                        @RequestParam("repairer") boolean isRepairer) {

        if (isRepairer)
            return this.requestHistoryService.getRepairerHistory(userId, statuses, pageable)
                    .map(RequestHistoryMapper.REQUEST_HISTORY_MAPPER::entityToDto);
        else
            return this.requestHistoryService.getUserHistory(userId, statuses, pageable)
                    .map(RequestHistoryMapper.REQUEST_HISTORY_MAPPER::entityToDto);

    }


    @GetMapping("/users/{id}/feedback")
    public List<?> getFeedback(@PathVariable("id") Long userId) {
        UserDto user = USER_MAPPER.entityToDTOWithRoles(userService.selectUserById(userId));
        if (user.getRoles().contains(RoleName.ROLE_CUSTOMER)) {
            return REQUEST_MAPPER.entityToDto(requestHistoryService.getFeedback(userService.selectUserById(userId).getUsername()));
        } else {
            return requestService.getPagePrivateRequestAndFilterByStatus(PageRequest.of(0, 1000), Arrays.asList(RequestStatus.FEEDBACK), userId)
                    .getContent();
        }
    }


}
