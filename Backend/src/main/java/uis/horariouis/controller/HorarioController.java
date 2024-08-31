package uis.horariouis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uis.horariouis.model.Horario;
import uis.horariouis.model.Grupo;
import uis.horariouis.service.AlgoritmoGeneticoService;
import uis.horariouis.service.HorarioService;
import uis.horariouis.service.GrupoService;
import uis.horariouis.verification.VerificadorRestricciones;

import java.util.List;

@RestController
@RequestMapping("/horarios")
public class HorarioController {

    @Autowired
    private AlgoritmoGeneticoService algoritmoGeneticoService;

    @Autowired
    private HorarioService horarioService;

    @Autowired
    private GrupoService grupoService;  // Inyectamos GrupoService

    @Autowired
    private VerificadorRestricciones verificadorRestricciones;  // Inyectamos VerificadorRestricciones

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
            List<Horario> horarios = horarioService.getAllHorarios();
            return ResponseEntity.ok(horarios);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Nuevo endpoint para verificar todas las restricciones

    @GetMapping("/verificarRestricciones")
    public ResponseEntity<String> verificarRestricciones() {
        try {
            List<Horario> horarios = horarioService.getAllHorarios();
            List<Grupo> grupos = grupoService.getAllGrupos();
            String resultadoVerificacion = verificadorRestricciones.verificarTodasLasRestricciones(horarios, grupos);
            return ResponseEntity.ok(resultadoVerificacion);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al verificar las restricciones: " + e.getMessage());
        }
    }

}
