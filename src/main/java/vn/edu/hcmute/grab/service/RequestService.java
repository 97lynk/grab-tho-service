package vn.edu.hcmute.grab.service;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.cert.ocsp.Req;
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

    public Page<RequestDto> getPageRequestOfUser(Pageable pageable, String username) {
        userService.selectUserByUsername(username);
        return requestRepository.findAllByUserUsername(pageable, username)
                .map(REQUEST_MAPPER::entityToDto);
    }

    public RequestDto addNewRequest(AddRequestDto requestDto, String username){
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
