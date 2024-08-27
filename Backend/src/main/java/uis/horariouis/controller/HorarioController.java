package uis.horariouis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uis.horariouis.model.Horario;
import uis.horariouis.service.AlgoritmoGeneticoService;
import uis.horariouis.service.HorarioService;

import java.util.List;

@RestController
@RequestMapping("/horarios")
public class HorarioController {

    @Autowired
    private AlgoritmoGeneticoService algoritmoGeneticoService;

    @Autowired
    private HorarioService horarioService;  // Inyectamos HorarioService

    // Endpoint para ejecutar el algoritmo genético y obtener la mejor asignación de horarios
    @GetMapping("/generar")
    public ResponseEntity<String> generarHorarios() {
        try {
            algoritmoGeneticoService.ejecutarAlgoritmoGenetico();
            return ResponseEntity.ok("Horarios generados con éxito utilizando el algoritmo genético.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al generar los horarios: " + e.getMessage());
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Horario>> listarHorarios() {
        try {
            List<Horario> horarios = horarioService.getAllHorarios();  // Usamos la instancia inyectada de HorarioService
            return ResponseEntity.ok(horarios);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
