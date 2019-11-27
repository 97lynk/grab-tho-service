package vn.edu.hcmute.grab.service;

import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vn.edu.hcmute.grab.constant.ActionStatus;
import vn.edu.hcmute.grab.constant.RequestStatus;
import vn.edu.hcmute.grab.dto.*;
import vn.edu.hcmute.grab.entity.*;
import vn.edu.hcmute.grab.repository.RepairerRepository;
import vn.edu.hcmute.grab.repository.RequestHistoryRepository;
import vn.edu.hcmute.grab.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
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

    private final NotificationService notificationService;

    private final WalletService walletService;

    private final List<RequestStatus> RECENT_STATUSES
            = Arrays.asList(RequestStatus.POSTED, RequestStatus.RECEIVED, RequestStatus.QUOTED);
    private final List<RequestStatus> ACCEPTED_STATUSES
            = Arrays.asList(RequestStatus.ACCEPTED, RequestStatus.WAITING);
    private final List<RequestStatus> COMPLETED_STATUSES
            = Arrays.asList(RequestStatus.COMPLETED, RequestStatus.FEEDBACK, RequestStatus.CLOSED);

    private final List<RequestStatus> STATUSES = new ArrayList<>();

    @Autowired
    public RequestService(RequestRepository requestRepository, RepairerRepository repairerRepository, RequestHistoryService requestHistoryService, RequestHistoryRepository requestHistoryRepository, UserService userService, NotificationService notificationService, WalletService walletService) {
        this.requestRepository = requestRepository;
        this.repairerRepository = repairerRepository;
        this.requestHistoryService = requestHistoryService;
        this.requestHistoryRepository = requestHistoryRepository;
        this.userService = userService;
        this.notificationService = notificationService;
        this.walletService = walletService;

        this.STATUSES.addAll(RECENT_STATUSES);
        this.STATUSES.addAll(ACCEPTED_STATUSES);
        this.STATUSES.addAll(COMPLETED_STATUSES);
    }

    public Page<?> getPageRequestOfUser(Pageable pageable, String username) {
        userService.selectUserByUsername(username);
        return requestRepository.findAllByUserUsername(pageable, username)
                .map(REQUEST_MAPPER::entityToRecentDto);
    }

    private Request getRequestById(Long id, String username) {
        userService.selectUserByUsername(username);
        return requestRepository.findByIdAndUserUsername(id, username)
                .orElseThrow(() -> new ObjectNotFoundException(id, Request.class.getSimpleName()));
    }

    public RequestDto getRequest(Long id, String username) {
        return REQUEST_MAPPER.entityToDto(getRequestById(id, username));
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

    public Page<?> getPagePrivateRequestAndFilterByStatus(Pageable pageable, List<RequestStatus> statuses, Long userId) {
        Page<Request> requests = requestRepository.findAllByStatusInAndRepairerUserId(pageable, statuses, userId);
        return requestMappingByStatus(requests, statuses);
    }

    private Page<?> requestMappingByStatus(Page<Request> requests, List<RequestStatus> statuses) {

        if (statuses.containsAll(RECENT_STATUSES))
            return requests.map(REQUEST_MAPPER::entityToRecentDto);
        else if (statuses.containsAll(ACCEPTED_STATUSES))
            return requests.map(REQUEST_MAPPER::entityToAcceptedDto);
        else if (statuses.containsAll(COMPLETED_STATUSES))
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

        List<String> receivers = repairerRepository.findAll().stream()
                .map(Repairer::getUser)
                .map(User::getUsername)
                .collect(Collectors.toList());

        // add notification
        String message = String.format("%s vừa mới gửi yêu cầu mới", user.getFullName());
        Date now = new Date();
        NotificationDto notification = NotificationDto.builder()
                .seen(false)
                .sendAt(now.getTime())
                .message(message)
                .requestId(request.getId())
                .action(ActionStatus.POST)
                .thumbnail(user.getAvatar())
                .build();

        receivers.forEach(u -> notificationService.saveNotification(u, notification));

        // push notification
        Notification noti = Notification.builder()
                .setImage(user.getAvatar())
                .setTitle("Yêu cầu mới")
                .setBody(message)
                .build();
        notificationService.pushNotification(receivers, noti, request);

        return REQUEST_MAPPER.entityToDto(request);
    }

    public RequestDto acceptRepairer(Long requestId, Long repairerId, String username) {

        RequestHistory quoteRequestHistory = requestHistoryService.getRequestHistory(requestId, repairerId, ActionStatus.QUOTE);

        Request request = getRequestById(requestId, username);

        Repairer repairer = repairerRepository.findByUserId(repairerId)
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

        // add notification
        String thumbnail = ServletUriComponentsBuilder.fromCurrentContextPath().path("/requests/description-images/")
                .path(request.getImagesDescription()[0]).toUriString();
        String message = String.format("%s đã chấp nhận báo giá %d của bạn", request.getUser().getFullName(), quoteRequestHistory.getPoint());
        NotificationDto notification = NotificationDto.builder()
                .seen(false)
                .sendAt(new Date().getTime())
                .message(message)
                .requestId(request.getId())
                .action(ActionStatus.ACCEPT)
                .thumbnail(thumbnail)
                .build();
        notificationService.saveNotification(repairer.getUser().getUsername(), notification);

        // push notification
        Notification noti = Notification.builder()
                .setImage(thumbnail)
                .setTitle("Chấp nhận báo giá")
                .setBody(message)
                .build();
        notificationService.pushNotification(Arrays.asList(repairer.getUser().getUsername()), noti, request);

        // refund to quoted repairers
        requestHistoryRepository.findAllByRequestIdAndStatusIsInOrderByCreateAtDesc(requestId, Arrays.asList(ActionStatus.QUOTE))
                .stream()
                .filter(h -> !repairerId.equals(h.getRepairer().getUser().getId()))
                .forEach(h -> {
            WalletHistory walletHistory = WalletHistory.builder()
                    .action(WalletHistory.WalletAction.REFUND_QUOTE)
                    .createAt(LocalDateTime.now())
                    .xeng(20l)
                    .note(h.getId().toString())
                    .wallet(h.getRepairer().getWallet())
                    .build();
            walletService.transaction(walletHistory, repairer.getWallet());
        });

        return REQUEST_MAPPER.entityToDto(requestRepository.save(request));
    }

    public RequestDto feedBack(Long requestId, String posterUsername, FeedBackDto feedBackDto) {

        // update status/comment/rating for request
        Request request = getRequestById(requestId, posterUsername);
        request.setStatus(RequestStatus.FEEDBACK);
        request.setComment(feedBackDto.getComment());
        request.setRate(feedBackDto.getRate());
        request.setFeedBack(true);

        // update review/rate for repairer
        Repairer repairer = request.getRepairer();
        long oldReview = repairer.getReviews();
        float oldRate = repairer.getRating();
        double total = oldRate * oldReview + feedBackDto.getRate();
        repairer.setRating((float) (total / (oldReview + 1)));
        repairer.setReviews(oldReview + 1);
        repairerRepository.save(repairer);

        // save a new history
        RequestHistory acceptRequestHistory = new RequestHistory();
        acceptRequestHistory.setCreateAt(LocalDateTime.now());
        acceptRequestHistory.setPoint(0l);
        acceptRequestHistory.setRepairer(request.getRepairer());
        acceptRequestHistory.setRequest(request);
        acceptRequestHistory.setStatus(ActionStatus.FEEDBACK);
        requestHistoryRepository.save(acceptRequestHistory);

        // add notification
        String thumbnail = ServletUriComponentsBuilder.fromCurrentContextPath().path("/requests/description-images/")
                .path(request.getImagesDescription()[0]).toUriString();
        String message = String.format("%s đã đánh giá yêu cầu", request.getUser().getFullName());
        NotificationDto notification = NotificationDto.builder()
                .seen(false)
                .sendAt(new Date().getTime())
                .message(message)
                .requestId(request.getId())
                .action(ActionStatus.FEEDBACK)
                .thumbnail(thumbnail)
                .build();
        notificationService.saveNotification(repairer.getUser().getUsername(), notification);

        // push notification
        Notification noti = Notification.builder()
                .setImage(thumbnail)
                .setTitle("Đánh giá")
                .setBody(message)
                .build();
        notificationService.pushNotification(Arrays.asList(repairer.getUser().getUsername()), noti, request);

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

    public List<RequestDto> getJoinedRequestByRepairer(List<ActionStatus> actions, String repairerUsername) {
        List<RequestHistory> histories = requestHistoryRepository.findAllByRepairerUserUsernameAndStatusIsIn(repairerUsername, actions);
        List<RequestStatus> statuses = actions.stream().map(this::convertActionToStatus).collect(Collectors.toList());
        return histories.stream().map(RequestHistory::getRequest)
                .filter(distinctByKey(Request::getId))
                .filter(r -> statuses.contains(r.getStatus()))
                .map(REQUEST_MAPPER::entityToDto)
                .collect(Collectors.toList());
    }

    private RequestStatus convertActionToStatus(ActionStatus action) {
        switch (action) {
            case FEEDBACK:
                return RequestStatus.FEEDBACK;
            case QUOTE:
                return RequestStatus.QUOTED;
            case ACCEPT:
                return RequestStatus.ACCEPTED;
            case COMPLETE:
                return RequestStatus.COMPLETED;
            default:
                return RequestStatus.RECEIVED;
        }
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

}
