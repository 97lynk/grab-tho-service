package vn.edu.hcmute.grab.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.edu.hcmute.grab.constant.RequestStatus;
import vn.edu.hcmute.grab.dto.RepairerDto;
import vn.edu.hcmute.grab.service.RepairerService;
import vn.edu.hcmute.grab.service.RequestService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/repairers")
@Slf4j
public class RepairerController {

    private final RepairerService repairerService;

    private final RequestService requestService;

    @Autowired
    public RepairerController(RepairerService repairerService, RequestService requestService) {
        this.repairerService = repairerService;
        this.requestService = requestService;
    }

    /**
     * get a repairer by id
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public RepairerDto selectRepairerById(@PathVariable("id") Long id) {
        log.info("GET repairer#{}", id);
        return repairerService.getRepairerById(id);
    }

    @GetMapping("/{id}/rate")
    public Map<String, Long> getStatisticRate(@PathVariable("id") Long id) {
        log.info("GET rate of repairer#{}", id);
        return repairerService.getRateRepairer(id);
    }


    @GetMapping("/{id}/requests")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public Page<?> getAllRequestOfUser(@PathVariable("id") Long id,
                                       @PageableDefault(sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable,
                                       @RequestParam(value = "status", defaultValue = "") List<RequestStatus> statuses,
                                       Authentication auth) {
        log.info("GET a page request of repairer#{}, user {}, filter status={}", id, auth.getName(), statuses);

        return requestService.getPagePrivateRequestAndFilterByStatus(pageable, statuses, id);
    }

}
