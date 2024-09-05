package uis.horariouis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uis.horariouis.model.Horario;
import uis.horariouis.service.HorarioGen;
import uis.horariouis.service.HorarioService;

import java.util.List;

@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

    @Autowired
    private HorarioGen horarioGen;

    @Autowired
    private HorarioService horarioService;

    @GetMapping("")
    public List<Horario> getHorarios() {return horarioService.getAllHorarios();}

    @GetMapping("/generar")
    public ResponseEntity<String> generarHorario() {
        horarioGen.generarHorario();
        return ResponseEntity.ok("Los horarios se han generado correctamente.");
    }
}

