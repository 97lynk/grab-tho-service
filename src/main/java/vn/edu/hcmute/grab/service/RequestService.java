package vn.edu.hcmute.grab.service;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.edu.hcmute.grab.constant.RequestStatus;
import vn.edu.hcmute.grab.dto.AddRequestDto;
import vn.edu.hcmute.grab.dto.RequestDto;
import vn.edu.hcmute.grab.entity.Request;
import vn.edu.hcmute.grab.entity.User;
import vn.edu.hcmute.grab.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static vn.edu.hcmute.grab.mapper.RequestMapper.REQUEST_MAPPER;


@Service
@Slf4j
public class RequestService {

    private final RequestRepository requestRepository;

    private final UserService userService;

    @Autowired
    public RequestService(RequestRepository requestRepository, UserService userService) {
        this.requestRepository = requestRepository;
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

    public Page<?> getPageRequestOfUserAndFilterByStatus(Pageable pageable, String username, List<RequestStatus> statuses) {
        userService.selectUserByUsername(username);
        Page<Request> requests = requestRepository.findAllByUserUsernameAndStatusIn(pageable, username, statuses);
        if (statuses.containsAll(Arrays.asList(RequestStatus.POSTED, RequestStatus.RECEIVED, RequestStatus.QUOTED)))
            return requests.map(REQUEST_MAPPER::entityToRecentDto);
        else if(statuses.containsAll(Arrays.asList(RequestStatus.ACCEPTED, RequestStatus.WAITING)))
            return requests.map(REQUEST_MAPPER::entityToAcceptedDto);

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
}
