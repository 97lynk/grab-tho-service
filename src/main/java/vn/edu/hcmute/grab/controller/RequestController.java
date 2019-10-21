package vn.edu.hcmute.grab.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.hcmute.grab.constant.RequestStatus;
import vn.edu.hcmute.grab.dto.AddRequestDto;
import vn.edu.hcmute.grab.dto.RequestDto;
import vn.edu.hcmute.grab.service.FileStorageService;
import vn.edu.hcmute.grab.service.RequestService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/requests")
@Slf4j
public class RequestController {

    private final RequestService requestService;

    private final FileStorageService fileStorageService;

    @Autowired
    public RequestController(RequestService requestService, FileStorageService fileStorageService) {
        this.requestService = requestService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public Page<?> getAllRequestOfUser(@PageableDefault Pageable pageable,
                                                      @RequestParam(value = "status", defaultValue = "") List<RequestStatus> statuses,
                                                      Authentication auth) {
        log.info("Get a page of request, user {}, filter status={}", auth.getName(), statuses);
        if (statuses.isEmpty())
            return requestService.getPageRequestOfUser(pageable, auth.getName());
        else
            return filterRequest(pageable, statuses, auth);
    }

    @GetMapping("/{id}")
    public RequestDto getRequestById(@PathVariable("id") Long id, Authentication auth) {
        log.info("Get a request by id " + id);
        return requestService.getRequest(id, auth.getName());
    }

    @PostMapping
    public RequestDto newRequest(@RequestBody AddRequestDto requestDto, Authentication auth) {
        log.info("Add new request, user " + auth.getName());
        return requestService.addNewRequest(requestDto, auth.getName());
    }

    public Page<?> filterRequest(Pageable pageable, List<RequestStatus> statuses, Authentication auth) {
        return requestService.getPageRequestOfUserAndFilterByStatus(pageable, auth.getName(), statuses);
    }

    @PostMapping("/description-images")
    public ResponseEntity<?> uploadFile(@RequestParam(value = "images", defaultValue = "[]") List<MultipartFile> files) {
        List<String> urlImages = files.stream()
                .map(fileStorageService::storeFileImage)
                .collect(Collectors.toList());

        return ResponseEntity.ok(urlImages);
    }

    @GetMapping("/description-images/{fileName}")
    public ResponseEntity<?> downloadFile(@PathVariable(value = "fileName") String fileName) {

        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileStorageService.restoreFileImage(fileName).toURI());

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
