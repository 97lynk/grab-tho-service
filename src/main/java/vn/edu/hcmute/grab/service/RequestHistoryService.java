package vn.edu.hcmute.grab.service;

import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.hcmute.grab.constant.ActionStatus;
import vn.edu.hcmute.grab.dto.JoinedRepairerDto;
import vn.edu.hcmute.grab.entity.RequestHistory;
import vn.edu.hcmute.grab.repository.RepairerRepository;
import vn.edu.hcmute.grab.repository.RequestHistoryRepository;

import java.util.List;
import java.util.stream.Collectors;

import static vn.edu.hcmute.grab.mapper.JoinedRepairerMapper.JOINED_REPAIRER_MAPPER;

@Service
public class RequestHistoryService {

    private final RepairerRepository repairerRepository;

    private final RequestHistoryRepository requestHistoryRepository;

    @Autowired
    public RequestHistoryService(RepairerRepository repairerRepository, RequestHistoryRepository requestHistoryRepository) {
        this.repairerRepository = repairerRepository;
        this.requestHistoryRepository = requestHistoryRepository;
    }


    public List<JoinedRepairerDto> getRepairerJoinedRequest(Long requestId, List<ActionStatus> actions) {
        List<RequestHistory> requestHistories = requestHistoryRepository.findAllByRequestIdAndStatusIsIn(requestId, actions);
        return requestHistories.stream()
                .map(JOINED_REPAIRER_MAPPER::entityToDtoWithRole)
                .collect(Collectors.toList());
    }

    public RequestHistory getRequestHistory(Long requestId, Long repairerId, ActionStatus status){
        return requestHistoryRepository.findByRequestIdAndRepairerIdAndStatus(requestId, repairerId, status).
                orElseThrow(() -> new RuntimeException("Repairer didn't joined in request"));
    }


    public List<RequestHistory> getRequestHistory(Long requestId, Long repairerId){
        return requestHistoryRepository.findByRequestIdAndRepairerId(requestId, repairerId);
    }
}
