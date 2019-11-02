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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.hcmute.grab.constant.RequestStatus;
import vn.edu.hcmute.grab.constant.RoleName;
import vn.edu.hcmute.grab.dto.AcceptedRequestDto;
import vn.edu.hcmute.grab.dto.AddRequestDto;
import vn.edu.hcmute.grab.dto.RequestDto;
import vn.edu.hcmute.grab.entity.Role;
import vn.edu.hcmute.grab.service.FileStorageService;
import vn.edu.hcmute.grab.service.RequestHistoryService;
import vn.edu.hcmute.grab.service.RequestService;

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
    public RequestController(RequestService requestService, RequestHistoryService requestHistoryService, FileStorageService fileStorageService) {
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
        log.info("Get a page of request, user {}, filter status={}", auth.getName(), statuses);

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
        log.info("Get a request by id " + id);
        if (isCustomer(auth))
            return requestService.getRequest(id, auth.getName());
        else {
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
        log.info("Add new request, user " + auth.getName());
        return requestService.addNewRequest(requestDto, auth.getName());
    }

    /**
     * upload image description
     *
     * @param files
     * @return
     */
    @PostMapping("/description-images")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'REPAIRER')")
    public ResponseEntity<?> uploadFile(@RequestParam(value = "images", defaultValue = "[]") List<MultipartFile> files) {
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
    public ResponseEntity<?> downloadFile(@PathVariable(value = "fileName") String fileName) {

        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileStorageService.restoreFileImage(fileName).toURI());

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
        return requestService.acceptRepairer(requestId, repairerId, auth.getName());
    }

    @GetMapping("/accepted")
    @PreAuthorize("hasAnyRole('REPAIRER')")
    public List<AcceptedRequestDto> getAcceptedRequest(Authentication auth) {
        return requestService.getAcceptedRequestOfRepairer(auth.getName());
    }

    private boolean isCustomer(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(RoleName.ROLE_CUSTOMER.name()));
    }

    public Page<?> filterRequest(Pageable pageable, List<RequestStatus> statuses, Authentication auth) {
        return requestService.getPageRequestOfUserAndFilterByStatus(pageable, auth.getName(), statuses);
    }

}
