package vn.edu.hcmute.grab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.hcmute.grab.dto.RepairerDto;
import vn.edu.hcmute.grab.service.RepairerService;
import vn.edu.hcmute.grab.service.RequestService;

@RestController
@RequestMapping("/repairers")
public class RepairerController {

    private final RepairerService repairerService;

    private final RequestService requestService;

    @Autowired
    public RepairerController(RepairerService repairerService, RequestService requestService) {
        this.repairerService = repairerService;
        this.requestService = requestService;
    }


    @GetMapping("/{id}")
    public RepairerDto selectRepairerById(@PathVariable("id") Long id) {
        return repairerService.getRepairerById(id);
    }
}
