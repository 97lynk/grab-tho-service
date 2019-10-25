package vn.edu.hcmute.grab.service;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.hcmute.grab.dto.RepairerDto;
import vn.edu.hcmute.grab.entity.Repairer;
import vn.edu.hcmute.grab.repository.RepairerRepository;

import static vn.edu.hcmute.grab.mapper.RepairerMapper.REPAIRER_MAPPER;

@Service
@Slf4j
public class RepairerService {

    private final RepairerRepository repairerRepository;

    @Autowired
    public RepairerService(RepairerRepository repairerRepository) {
        this.repairerRepository = repairerRepository;
    }

    public RepairerDto getRepairerById(Long id) {
        return REPAIRER_MAPPER.entityToDTO(
          repairerRepository.findById(id)
                  .orElseThrow(() -> new ObjectNotFoundException(id, Repairer.class.getSimpleName()))
        );
    }
}
