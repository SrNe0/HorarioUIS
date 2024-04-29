package uis.horariouis.exception;

// Importaciones necesarias de Spring Framework y otros componentes.
import org.springframework.dao.DataIntegrityViolationException; // Excepción de violación de integridad de datos
import org.springframework.http.HttpStatus; // Enumeración de códigos de estado HTTP estándar
import org.springframework.http.ResponseEntity; // Clase para representar una respuesta HTTP
import org.springframework.web.bind.MethodArgumentNotValidException; // Excepción lanzada cuando falla la validación de argumentos de método
import org.springframework.web.bind.annotation.ExceptionHandler; // Anotación para manejar excepciones
import org.springframework.web.bind.annotation.RestControllerAdvice; // Anotación que marca una clase como consejero global para controladores REST
import uis.horariouis.model.ErrorResponse; // Modelo para representar mensajes de error personalizados

import java.time.LocalDateTime; // Clase para representar fechas y horas
import java.util.LinkedHashMap; // Implementación de Map que mantiene el orden de inserción
import java.util.List; // Interfaz para representar una colección ordenada de elementos
import java.util.Map; // Interfaz que asocia claves con valores únicos
import java.util.stream.Collectors; // Utilidades para operaciones de secuencia de Java Stream

@RestControllerAdvice // Anotación para la clase que proporciona manejo global de excepciones para controladores REST
public class GlobalExceptionHandler {

    // Manejador de excepciones para la violación de integridad de datos
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Data integrity violation occurred", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Manejador de excepciones para la validación de argumentos de método
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        // Creación del cuerpo de la respuesta de error
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now()); // Fecha y hora actual
        body.put("status", HttpStatus.BAD_REQUEST.value()); // Código de estado HTTP

        // Personalización de los mensajes de error de validación
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());

        // Agregar los mensajes de error al cuerpo de la respuesta
        body.put("message", "Validation error: " + String.join(", ", errors));

        // Crear el objeto de respuesta de error
        ErrorResponse errorResponse = new ErrorResponse("Validation error", HttpStatus.BAD_REQUEST.value());
        errorResponse.setDetails(body);

        // Devolver la respuesta de error junto con el código de estado HTTP correspondiente
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Manejador de excepciones para la excepción de recurso no encontrado
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // Manejador de excepciones para otras excepciones no manejadas específicamente
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
