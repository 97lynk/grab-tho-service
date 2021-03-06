package vn.edu.hcmute.grab.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.hcmute.grab.constant.ActionStatus;
import vn.edu.hcmute.grab.constant.RequestStatus;
import vn.edu.hcmute.grab.constant.RoleName;
import vn.edu.hcmute.grab.dto.AcceptedRequestDto;
import vn.edu.hcmute.grab.dto.AddRequestDto;
import vn.edu.hcmute.grab.dto.FeedBackDto;
import vn.edu.hcmute.grab.dto.RequestDto;
import vn.edu.hcmute.grab.service.FileStorageService;
import vn.edu.hcmute.grab.service.RequestHistoryService;
import vn.edu.hcmute.grab.service.RequestService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/requests")
@Slf4j
public class RequestController {

    private final RequestService requestService;

    private final RequestHistoryService requestHistoryService;

    private final FileStorageService fileStorageService;

    @Autowired
    public RequestController(RequestService requestService, RequestHistoryService requestHistoryService,
                             FileStorageService fileStorageService) {
        this.requestService = requestService;
        this.requestHistoryService = requestHistoryService;
        this.fileStorageService = fileStorageService;
    }

    /**
     * get list request by status for customer and repairer
     *
     * @param pageable
     * @param statuses
     * @param auth
     * @return
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'REPAIRER')")
    public Page<?> getAllRequestOfUser(@PageableDefault(sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable,
                                       @RequestParam(value = "status", defaultValue = "") List<RequestStatus> statuses,
                                       Authentication auth) {
        log.info("GET a page of request, user {}, filter status={}", auth.getName(), statuses);

        if (isCustomer(auth)) {
            if (statuses.isEmpty())
                return requestService.getPageRequestOfUser(pageable, auth.getName());
            else
                return filterRequest(pageable, statuses, auth);
        } else {
            return requestService.getPageRequestAndFilterByStatus(pageable, statuses);
        }
    }

    /**
     * get a request
     *
     * @param id
     * @param auth
     * @return
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'REPAIRER')")
    public RequestDto getRequestById(@PathVariable("id") Long id, Authentication auth) {
        log.info("GET request#{}", id);
        if (isCustomer(auth))
            return requestService.getRequest(id, auth.getName());
        else {
            // TODO the repairer RECEIVE a request
            requestService.receiveRequest(id, auth.getName());
            return requestService.getRequest(id);
        }
    }

    /**
     * post a request by customer
     *
     * @param requestDto
     * @param auth
     * @return
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public RequestDto newRequest(@RequestBody AddRequestDto requestDto, Authentication auth) {
        // TODO POST a new request
        log.info("POST new request, user=#{}", auth.getName());
        return requestService.addNewRequest(requestDto, auth.getName());
    }

    /**
     * upload image description
     *
     * @param files
     * @return
     */
    @PostMapping("/description-images")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'REPAIRER', 'ADMIN', 'MOD')")
    public ResponseEntity<?> uploadFile(@RequestParam(value = "images", defaultValue = "[]") List<MultipartFile> files) {
        log.info("UPLOAD {} images", files.size());
        List<String> urlImages = files.stream()
                .map(fileStorageService::storeFileImage)
                .collect(Collectors.toList());

        return ResponseEntity.ok(urlImages);
    }

    /**
     * get image description
     *
     * @param fileName
     * @return
     */
    @GetMapping("/description-images/{fileName}")
//    @PreAuthorize("hasAnyRole('CUSTOMER', 'REPAIRER')")
    public ResponseEntity<?> downloadFile(@PathVariable(value = "fileName") String fileName,
                                          @RequestParam(name = "q", defaultValue = "1.0") float quality) throws IOException {

        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileStorageService.restoreFileImage(fileName, quality).toURI());

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * accept repairer
     *
     * @param requestId
     * @param repairerId
     * @param auth
     * @return
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public RequestDto acceptRepairer(@PathVariable("id") Long requestId,
                                     @RequestParam("repairer_id") Long repairerId,
                                     OAuth2Authentication auth) {
        log.info("ACCEPT {} repairer#{} for request#{}", auth.getName(), repairerId, requestId);
        // TODO the poster ACCEPT repairer
        return requestService.acceptRepairer(requestId, repairerId, auth.getName());
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public RequestDto feedBack(@PathVariable("id") Long requestId, @RequestBody FeedBackDto feedBack,
                               OAuth2Authentication auth) {
        log.info("FEEDBACK {} {} for request#{}", feedBack.getRate(), feedBack.getComment(), auth.getName());
        // TODO the poster FEEDBACK
        return requestService.feedBack(requestId, auth.getName(), feedBack);
    }


    /**
     * get accepted requests
     *
     * @param auth
     * @return
     */
    @GetMapping("/accepted")
    @PreAuthorize("hasAnyRole('REPAIRER')")
    public List<AcceptedRequestDto> getAcceptedRequest(Authentication auth) {
        log.info("GET accepted request of repairer {}", auth.getName());
        return requestService.getAcceptedRequestOfRepairer(auth.getName());
    }

    private boolean isCustomer(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(RoleName.ROLE_CUSTOMER.name()));
    }

    public Page<?> filterRequest(Pageable pageable, List<RequestStatus> statuses, Authentication auth) {
        return requestService.getPageRequestOfUserAndFilterByStatus(pageable, auth.getName(), statuses);
    }

    @GetMapping("/private")
    @PreAuthorize("hasAnyRole('REPAIRER')")
    public List<RequestDto> getJoinedRequestByRepairer(Authentication auth,
                                                       @RequestParam(value = "action", defaultValue = "") List<ActionStatus> actions) {
        log.info("GET joined request by repairer {}", auth.getName());
        return requestService.getJoinedRequestByRepairer(actions, auth.getName());
    }
}
