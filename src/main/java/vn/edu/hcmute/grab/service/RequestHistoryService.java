package vn.edu.hcmute.grab.service;

import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vn.edu.hcmute.grab.constant.ActionStatus;
import vn.edu.hcmute.grab.constant.RequestStatus;
import vn.edu.hcmute.grab.dto.HistoryDto;
import vn.edu.hcmute.grab.dto.JoinedRepairerDto;
import vn.edu.hcmute.grab.dto.NotificationDto;
import vn.edu.hcmute.grab.entity.Repairer;
import vn.edu.hcmute.grab.entity.Request;
import vn.edu.hcmute.grab.entity.RequestHistory;
import vn.edu.hcmute.grab.repository.RepairerRepository;
import vn.edu.hcmute.grab.repository.RequestHistoryRepository;
import vn.edu.hcmute.grab.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static vn.edu.hcmute.grab.mapper.JoinedRepairerMapper.JOINED_REPAIRER_MAPPER;

@Service
public class RequestHistoryService {

    private final RepairerRepository repairerRepository;

    private final RequestRepository requestRepository;

    private final RequestHistoryRepository requestHistoryRepository;

    private final NotificationService notificationService;

    @Autowired
    public RequestHistoryService(RepairerRepository repairerRepository, RequestRepository requestRepository, RequestHistoryRepository requestHistoryRepository, NotificationService notificationService) {
        this.repairerRepository = repairerRepository;
        this.requestRepository = requestRepository;
        this.requestHistoryRepository = requestHistoryRepository;
        this.notificationService = notificationService;
    }


    public List<JoinedRepairerDto> getRepairerJoinedRequest(Long requestId, List<ActionStatus> actions, String usernameRepairer) {
        List<RequestHistory> requestHistories;
        if (usernameRepairer == null)
            requestHistories = requestHistoryRepository.findAllByRequestIdAndStatusIsInOrderByCreateAtDesc(requestId, actions);
        else
            requestHistories = requestHistoryRepository.findAllByRequestIdAndStatusIsInAndRepairerUserUsernameOrderByCreateAtDesc(requestId, actions, usernameRepairer);

        return requestHistories.stream()
                .map(JOINED_REPAIRER_MAPPER::entityToDtoWithRole)
                .collect(Collectors.toList());
    }

    public RequestHistory getRequestHistory(Long requestId, Long repairerId, ActionStatus status) {
        return requestHistoryRepository.findByRequestIdAndRepairerUserIdAndStatus(requestId, repairerId, status).
                orElseThrow(() -> new RuntimeException("Repairer didn't joined in request"));
    }


    public List<RequestHistory> getRequestHistory(List<Long> requestId, Long repairerId) {
        return requestHistoryRepository.findByRequestIdInAndRepairerId(requestId, repairerId);
    }

    public RequestHistory addRequestHistory(HistoryDto historyDto) {
        Request request = requestRepository.findById(historyDto.getRequestId())
                .orElseThrow(() -> new ObjectNotFoundException(historyDto.getRequestId(), Request.class.getSimpleName()));

        Repairer repairer = repairerRepository.findByUserId(historyDto.getRepairerId())
                .orElseThrow(() -> new ObjectNotFoundException(historyDto.getRepairerId(), Repairer.class.getSimpleName()));

        if (historyDto.getAction() == ActionStatus.RECEIVE) {
            return receiveRequest(historyDto, request, repairer);
        } else if (historyDto.getAction() == ActionStatus.QUOTE) {
            return quoteRequest(historyDto, request, repairer);
        } else if (historyDto.getAction() == ActionStatus.COMPLETE) {
            return closeRequest(historyDto, request, repairer);
        }
        return null;
    }

    public RequestHistory quoteRequest(HistoryDto historyDto, Request request, Repairer repairer) {

        RequestHistory history = new RequestHistory();
        history.setCreateAt(LocalDateTime.now());
        history.setPoint(historyDto.getPoint());
        history.setStatus(ActionStatus.QUOTE);
        history.setRepairer(repairer);
        history.setRequest(request);

        request.setNoQuote(request.getNoQuote() + 1);
        // update status for request
        if (request.getStatus() == RequestStatus.POSTED || request.getStatus() == RequestStatus.RECEIVED) {
            request.setStatus(RequestStatus.QUOTED);
        }

        request = requestRepository.save(request);

        // add notification
        String thumbnail = ServletUriComponentsBuilder.fromCurrentContextPath().path("/requests/description-images/")
                .path(request.getImagesDescription()[0]).toUriString();
        NotificationDto notification = NotificationDto.builder()
                .seen(false)
                .sendAt(new Date().getTime())
                .message(String.format("%s đã báo giá %d cho yêu cầu của bạn", repairer.getUser().getFullName(), historyDto.getPoint()))
                .requestId(request.getId())
                .action(ActionStatus.QUOTE)
                .thumbnail(thumbnail)
                .build();
        notificationService.saveNotification(request.getUser().getUsername(), notification);

        return requestHistoryRepository.save(history);
    }

    public RequestHistory receiveRequest(HistoryDto historyDto, Request request, Repairer repairer) {
        RequestHistory history = new RequestHistory();
        history.setCreateAt(LocalDateTime.now());
        history.setPoint(historyDto.getPoint());
        history.setStatus(ActionStatus.RECEIVE);
        history.setRepairer(repairer);
        history.setRequest(request);

        request.setNoReceiver(request.getNoReceiver() + 1);
        // update status for request
        if (request.getStatus() == RequestStatus.POSTED) {
            request.setStatus(RequestStatus.RECEIVED);
        }

        requestRepository.save(request);
        return requestHistoryRepository.save(history);
    }

    public RequestHistory closeRequest(HistoryDto historyDto, Request request, Repairer repairer) {
        RequestHistory history = new RequestHistory();
        history.setCreateAt(LocalDateTime.now());
        history.setPoint(historyDto.getPoint());
        history.setStatus(ActionStatus.COMPLETE);
        history.setRepairer(repairer);
        history.setRequest(request);

        request.setStatus(RequestStatus.COMPLETED);
        requestRepository.save(request);

        repairer.setCompletedJob(repairer.getCompletedJob() + 1l);
        repairerRepository.save(repairer);

        return requestHistoryRepository.save(history);
    }

    public List<RequestHistory> getRequestHistory(List<Long> requestId, String username) {
        Repairer repairer = repairerRepository.findByUserUsername(username)
                .orElseThrow(() -> new ObjectNotFoundException(username, Repairer.class.getSimpleName()));
        return getRequestHistory(requestId, repairer.getId());
    }
}
