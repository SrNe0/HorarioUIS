package uis.horariouis.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uis.horariouis.dto.ProfesorDTO;
import uis.horariouis.model.Profesor;
import uis.horariouis.repository.ProfesorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class CsvServiceProfesor {
    private static final Logger log = LoggerFactory.getLogger(CsvServiceProfesor.class);

    @Autowired
    private ProfesorRepository profesorRepository;

    @Autowired
    private ProfesorService profesorService;

    public void importProfesoresFromCsv(MultipartFile file) {
        log.info("Iniciando importación de archivo CSV para Profesores");
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {

            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                try {
                    ProfesorDTO profesorDTO = parseProfesor(nextRecord);
                    // Verificar si el profesor ya existe
                    Optional<Profesor> existingProfesor = profesorRepository.findByDocumentoIdentidad(profesorDTO.getDocumentoIdentidad());
                    if (existingProfesor.isPresent()) {
                        // Actualizar registro existente
                        Profesor existing = existingProfesor.get();
                        updateProfesor(existing, profesorDTO);
                        profesorRepository.save(existing);
                        log.info("Profesor actualizado: {}", profesorDTO.getNombre1() + " " + profesorDTO.getApellido1());
                    } else {
                        // Crear nuevo registro a través del servicio
                        profesorService.createProfesor(profesorDTO);
                        log.info("Profesor agregado: {}", profesorDTO.getNombre1() + " " + profesorDTO.getApellido1());
                    }
                } catch (IllegalArgumentException e) {
                    log.error("Error al parsear el profesor: {}. Datos: {}", e.getMessage(), Arrays.toString(nextRecord));
                }
            }
        } catch (Exception e) {
            log.error("Error al procesar el archivo CSV", e);
            throw new RuntimeException("Error al procesar el archivo CSV: " + e.getMessage());
        }
    }

    private ProfesorDTO parseProfesor(String[] csvData) {
        ProfesorDTO profesorDTO = new ProfesorDTO();

        profesorDTO.setDocumentoIdentidad(csvData.length > 0 ? csvData[0] : "");
        profesorDTO.setApellido1(csvData.length > 1 ? csvData[1] : "");
        profesorDTO.setApellido2(csvData.length > 2 ? csvData[2] : "");
        profesorDTO.setNombre1(csvData.length > 3 ? csvData[3] : "");
        profesorDTO.setNombre2(csvData.length > 4 ? csvData[4] : "");
        profesorDTO.setTelefono(csvData.length > 5 ? csvData[5] : "");
        profesorDTO.setCorreo(csvData.length > 6 ? csvData[6] : "");

        return profesorDTO;
    }

    private void updateProfesor(Profesor existing, ProfesorDTO updated) {
        existing.setApellido1(updated.getApellido1());
        existing.setApellido2(updated.getApellido2());
        existing.setNombre1(updated.getNombre1());
        existing.setNombre2(updated.getNombre2());
        existing.setTelefono(updated.getTelefono());
        existing.setCorreo(updated.getCorreo());
        // Actualiza otros campos necesarios
    }

    public void exportProfesoresToCsv(HttpServletResponse response) {
        try {
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=Profesores.csv");
            PrintWriter writer = response.getWriter();

            // Obtener todos los profesores
            List<Profesor> profesores = profesorRepository.findAll();

            // Especificar el orden de las columnas
            String[] header = {"idProfesor", "Documento", "Nombre1", "Nombre2", "Apellido1", "Apellido2", "Teléfono", "Correo"};

            // Escribir el encabezado al archivo CSV
            writer.println(String.join(",", header));

            // Escribir los datos de los profesores en el archivo CSV
            for (Profesor profesor : profesores) {
                writer.println(String.join(",", formatProfesor(profesor)));
            }

            writer.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al exportar los datos a CSV: " + e.getMessage());
        }
    }

    // Método para formatear un profesor como una lista de valores de cadena
    private List<String> formatProfesor(Profesor profesor) {
        return List.of(
                String.valueOf(profesor.getIdProfesor()),
                profesor.getDocumentoIdentidad(),
                profesor.getNombre1(),
                profesor.getNombre2(),
                profesor.getApellido1(),
                profesor.getApellido2(),
                profesor.getTelefono(),
                profesor.getCorreo()
        );
    }
}
