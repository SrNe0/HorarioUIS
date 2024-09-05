package uis.horariouis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uis.horariouis.service.HorarioGen;

@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

    @Autowired
    private HorarioGen horarioGen;

    @GetMapping("/generar")
    public ResponseEntity<String> generarHorario() {
        horarioGen.generarHorario();
        return ResponseEntity.ok("Los horarios se han generado correctamente.");
    }
}

