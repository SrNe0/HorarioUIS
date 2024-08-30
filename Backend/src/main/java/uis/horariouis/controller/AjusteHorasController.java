package uis.horariouis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uis.horariouis.service.AjusteHorasService;

@RestController
@RequestMapping("/api/ajuste-horas")
public class AjusteHorasController {

    @Autowired
    private AjusteHorasService ajusteHorasService;

    @PostMapping("/ajustar")
    public ResponseEntity<String> ajustarHorariosDe5Horas() {
        ajusteHorasService.ajustarHorariosDe5Horas();
        return ResponseEntity.ok("Ajuste de horarios de 5 horas completado exitosamente.");
    }
}
