package uis.horariouis.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@Data
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private int status;
    private Map<String, Object> details; // Nuevo campo para detalles
    public ErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }

}
