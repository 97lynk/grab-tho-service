package vn.edu.hcmute.grab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.hcmute.grab.dto.RepairerDto;
import vn.edu.hcmute.grab.service.RepairerService;

@RestController
@RequestMapping("/repairers")
public class RepairerController {

    private final RepairerService repairerService;

    @Autowired
    public RepairerController(RepairerService repairerService) {
        this.repairerService = repairerService;
    }


    @GetMapping("/{id}")
    public RepairerDto selecteRepairerById(@PathVariable("id") Long id){
        return repairerService.getRepairerById(id);
    }
}
