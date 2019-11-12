package vn.edu.hcmute.grab.service;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.hcmute.grab.constant.RequestStatus;
import vn.edu.hcmute.grab.dto.RepairerDto;
import vn.edu.hcmute.grab.entity.Repairer;
import vn.edu.hcmute.grab.entity.Request;
import vn.edu.hcmute.grab.repository.RepairerRepository;
import vn.edu.hcmute.grab.repository.RequestRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static vn.edu.hcmute.grab.mapper.RepairerMapper.REPAIRER_MAPPER;

@Service
@Slf4j
public class RepairerService {

    private final RepairerRepository repairerRepository;

    private final RequestRepository requestRepository;

    @Autowired
    public RepairerService(RepairerRepository repairerRepository, RequestRepository requestRepository) {
        this.repairerRepository = repairerRepository;
        this.requestRepository = requestRepository;
    }

    public RepairerDto getRepairerById(Long id) {
        return REPAIRER_MAPPER.entityToDTO(
                repairerRepository.findByUserId(id)
                        .orElseThrow(() -> new ObjectNotFoundException(id, Repairer.class.getSimpleName()))
        );
    }

    public Map<String, Long> getRateRepairer(Long id) {
        List<Request> requests = requestRepository.findAllByRepairerId(id).stream()
                .filter(r -> r.getStatus() == RequestStatus.FEEDBACK)
                .collect(Collectors.toList());

        Map<String, Long> results = requests.stream().collect(Collectors.groupingBy(r -> String.valueOf(r.getRate()), Collectors.counting()));
        results.putIfAbsent("5.0", 0l);
        results.putIfAbsent("4.0", 0l);
        results.putIfAbsent("3.0", 0l);
        results.putIfAbsent("2.0", 0l);
        results.putIfAbsent("1.0", 0l);
        return results;
    }
}
