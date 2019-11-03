package vn.edu.hcmute.grab.service;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.edu.hcmute.grab.constant.ActionStatus;
import vn.edu.hcmute.grab.constant.RequestStatus;
import vn.edu.hcmute.grab.dto.*;
import vn.edu.hcmute.grab.entity.Repairer;
import vn.edu.hcmute.grab.entity.Request;
import vn.edu.hcmute.grab.entity.RequestHistory;
import vn.edu.hcmute.grab.entity.User;
import vn.edu.hcmute.grab.repository.RepairerRepository;
import vn.edu.hcmute.grab.repository.RequestHistoryRepository;
import vn.edu.hcmute.grab.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static vn.edu.hcmute.grab.mapper.RequestMapper.REQUEST_MAPPER;


@Service
@Slf4j
public class RequestService {

    private final RequestRepository requestRepository;

    private final RepairerRepository repairerRepository;

    private final RequestHistoryService requestHistoryService;

    private final RequestHistoryRepository requestHistoryRepository;

    private final UserService userService;

    @Autowired
    public RequestService(RequestRepository requestRepository, RepairerRepository repairerRepository, RequestHistoryService requestHistoryService, RequestHistoryRepository requestHistoryRepository, UserService userService) {
        this.requestRepository = requestRepository;
        this.repairerRepository = repairerRepository;
        this.requestHistoryService = requestHistoryService;
        this.requestHistoryRepository = requestHistoryRepository;
        this.userService = userService;
    }

    public Page<?> getPageRequestOfUser(Pageable pageable, String username) {
        userService.selectUserByUsername(username);
        return requestRepository.findAllByUserUsername(pageable, username)
                .map(REQUEST_MAPPER::entityToRecentDto);
    }

    public RequestDto getRequest(Long id, String username) {
        userService.selectUserByUsername(username);
        Request request = requestRepository.findByIdAndUserUsername(id, username)
                .orElseThrow(() -> new ObjectNotFoundException(id, Request.class.getSimpleName()));

        return REQUEST_MAPPER.entityToDto(request);
    }

    public RequestDto getRequest(Long id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(id, Request.class.getSimpleName()));

        return REQUEST_MAPPER.entityToDto(request);
    }

    public Page<?> getPageRequestOfUserAndFilterByStatus(Pageable pageable, String username, List<RequestStatus> statuses) {
        userService.selectUserByUsername(username);
        Page<Request> requests = requestRepository.findAllByUserUsernameAndStatusIn(pageable, username, statuses);
        return requestMappingByStatus(requests, statuses);
    }

    public Page<?> getPageRequestAndFilterByStatus(Pageable pageable, List<RequestStatus> statuses) {
        Page<Request> requests = requestRepository.findAllByStatusIn(pageable, statuses);
        return requestMappingByStatus(requests, statuses);
    }

    private Page<?> requestMappingByStatus(Page<Request> requests, List<RequestStatus> statuses) {

        if (statuses.containsAll(Arrays.asList(RequestStatus.POSTED, RequestStatus.RECEIVED, RequestStatus.QUOTED)))
            return requests.map(REQUEST_MAPPER::entityToRecentDto);
        else if (statuses.containsAll(Arrays.asList(RequestStatus.ACCEPTED, RequestStatus.WAITING)))
            return requests.map(REQUEST_MAPPER::entityToAcceptedDto);
        else if (statuses.containsAll(Arrays.asList(RequestStatus.COMPLETED, RequestStatus.FEEDBACK, RequestStatus.CLOSED)))
            return requests.map(REQUEST_MAPPER::entityToCompletedDto);

        return requests.map(REQUEST_MAPPER::entityToDto);
    }

    public RequestDto addNewRequest(AddRequestDto requestDto, String username) {
        User user = userService.selectUserByUsername(username);
        Request request = REQUEST_MAPPER.dtoToEntity(requestDto);
        request.setUser(user);
        request.setCreateAt(LocalDateTime.now());
        request.setStatus(RequestStatus.POSTED);
        request.setNoReceiver(0);
        request.setNoQuote(0);
        request.setPoint(0l);

        request = requestRepository.save(request);
        return REQUEST_MAPPER.entityToDto(request);
    }

    public RequestDto acceptRepairer(Long requestId, Long repairerId, String username) {

        RequestHistory quoteRequestHistory = requestHistoryService.getRequestHistory(requestId, repairerId, ActionStatus.QUOTE);

        Request request = requestRepository.findByIdAndUserUsername(requestId, username)
                .orElseThrow(() -> new ObjectNotFoundException(requestId, Request.class.getSimpleName()));

        Repairer repairer = repairerRepository.findById(repairerId)
                .orElseThrow(() -> new ObjectNotFoundException(repairerId, Repairer.class.getSimpleName()));

        request.setRepairer(repairer);
        request.setPoint(quoteRequestHistory.getPoint());
        request.setStatus(RequestStatus.ACCEPTED);

        RequestHistory acceptRequestHistory = new RequestHistory();
        acceptRequestHistory.setCreateAt(LocalDateTime.now());
        acceptRequestHistory.setPoint(quoteRequestHistory.getPoint());
        acceptRequestHistory.setRepairer(repairer);
        acceptRequestHistory.setRequest(request);
        acceptRequestHistory.setStatus(ActionStatus.ACCEPT);
        requestHistoryRepository.save(acceptRequestHistory);

        return REQUEST_MAPPER.entityToDto(requestRepository.save(request));
    }

    public List<AcceptedRequestDto> getAcceptedRequestOfRepairer(String usernameOfRepairer) {
        return requestRepository.findByRepairerUserUsernameAndStatusIn(usernameOfRepairer, Arrays.asList(RequestStatus.ACCEPTED))
                .stream().map(REQUEST_MAPPER::entityToAcceptedDto)
                .collect(Collectors.toList());
    }

    public void receiveRequest(Long requestId, String repairerUsername) {
        Repairer repairer = repairerRepository.findByUserUsername(repairerUsername)
                .orElseThrow(() -> new ObjectNotFoundException(repairerUsername, Repairer.class.getSimpleName()));

        boolean alreadyReceived = requestHistoryService.getRequestHistory(Arrays.asList(requestId), repairer.getId()).stream()
                .anyMatch(rh -> rh.getStatus() == ActionStatus.RECEIVE);

        if (alreadyReceived) return;

        HistoryDto history = new HistoryDto();
        history.setAction(ActionStatus.RECEIVE);
        history.setRequestId(requestId);
        history.setRepairerId(repairer.getUser().getId());
        requestHistoryService.addRequestHistory(history);
    }
}
