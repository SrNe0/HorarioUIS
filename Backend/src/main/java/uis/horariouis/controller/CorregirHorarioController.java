package uis.horariouis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import uis.horariouis.service.CorregirHorariosService;
import uis.horariouis.model.Grupo;
import uis.horariouis.repository.GrupoRepository;

import java.util.List;

@RestController
class CorregirHorarioController {

    @Autowired
    private CorregirHorariosService corregirHorariosService;

    @Autowired
    private GrupoRepository grupoRepository;

    @PostMapping("/corregirHorarios")
    public String corregirHorarios() {
        List<Grupo> grupos = grupoRepository.findAll();
        corregirHorariosService.corregirHorariosConflictivos(grupos);
        return "Horarios conflictivos corregidos.";
    }
}
