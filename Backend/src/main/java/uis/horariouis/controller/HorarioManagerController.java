package uis.horariouis.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uis.horariouis.model.Horario;
import uis.horariouis.service.HorarioManagerService;

import java.util.List;

@RestController
@RequestMapping("/api/horario-manager")
@Tag(name = "ASIGNACION AUTOMATICA", description = "ASIGNACION AUTOMATICA DE HORARIOS")
public class HorarioManagerController {

    @Autowired
    private HorarioManagerService horarioManagerService;

    @GetMapping("/asignar")
    public ResponseEntity<List<Horario>> asignarHorarios() {
        List<Horario> horarios = horarioManagerService.asignarHorarios();
        return ResponseEntity.ok(horarios);
    }
}
