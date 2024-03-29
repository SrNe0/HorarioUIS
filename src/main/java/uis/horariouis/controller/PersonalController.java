package uis.horariouis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uis.horariouis.model.Personal;
import uis.horariouis.service.PersonalService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/personal")
public class PersonalController {

    @Autowired
    private PersonalService personalService;

    @GetMapping("/")
    public List<Personal> getAllPersonal() {
        return personalService.getAllPersonal();
    }

    @GetMapping("/{id}")
    public Optional<Personal> getPersonalById(@PathVariable Long id) {
        return personalService.getPersonalById(id);
    }

    @PostMapping("/")
    public Personal saveOrUpdatePersonal(@RequestBody Personal personal) {
        return personalService.saveOrUpdatePersonal(personal);
    }

    @DeleteMapping("/{id}")
    public void deletePersonal(@PathVariable Long id) {
        personalService.deletePersonal(id);
    }
}
