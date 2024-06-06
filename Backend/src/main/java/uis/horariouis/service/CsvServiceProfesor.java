package uis.horariouis.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uis.horariouis.model.Profesor;
import uis.horariouis.repository.ProfesorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvServiceProfesor {
    private static final Logger log = LoggerFactory.getLogger(CsvServiceProfesor.class);

    @Autowired
    private ProfesorRepository profesorRepository;
    @Autowired
    private ProfesorService profesorService;

    public void importProfesoresFromCsv(MultipartFile file) {
        log.info("Iniciando importación de archivo CSV para Profesores");
        List<Profesor> profesores = new ArrayList<>();
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                try {
                    Profesor profesor = parseProfesor(nextRecord);
                    profesores.add(profesor);
                    log.info("Profesor procesado: {}", profesor.getNombre1() + " " + profesor.getApellido1());
                } catch (IllegalArgumentException e) {
                    log.error("Error al parsear el profesor: {}", e.getMessage());
                }
            }
            profesorRepository.saveAll(profesores);
            log.info("Todos los profesores han sido guardados.");
        } catch (Exception e) {
            log.error("Error al procesar el archivo CSV", e);
            throw new RuntimeException("Error al procesar el archivo CSV: " + e.getMessage());
        }
    }

    private Profesor parseProfesor(String[] csvData) {
        if (csvData.length < 7 || csvData[1].isEmpty() || csvData[2].isEmpty() ||
                csvData[3].isEmpty() || csvData[4].isEmpty() || csvData[5].isEmpty() || csvData[6].isEmpty()) {
            throw new IllegalArgumentException("Datos incompletos o inválidos para el profesor.");
        }
        Profesor profesor = new Profesor();
        profesor.setDocumentoIdentidad(csvData[0]);
        profesor.setApellido1(csvData[1]);
        profesor.setApellido2(csvData[2]);
        profesor.setNombre1(csvData[3]);
        profesor.setNombre2(csvData[4]);
        profesor.setTelefono(csvData[5]);
        profesor.setCorreo(csvData[6]);

        // La generación automática del usuario y la contraseña podría realizarse aqui
        profesor.setUsuario(profesorService.createProfesor(profesor).getUsuario());
        return profesor;
    }

    public void exportProfesoresToCsv(HttpServletResponse response) {
        try {
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=Profesores.csv");
            PrintWriter writer = response.getWriter();

            // Obtener todas las aulas
            List<Profesor> profesores = profesorRepository.findAll();

            // Especificar el orden de las columnas
            String[] header = {"idProfesor", "Documento", "Nombre1", "Nombre2", "Apellido1", "Apellido2", "Teléfono", "Correo"};

            // Escribir el encabezado al archivo CSV
            writer.println(String.join(",", header));

            // Escribir los datos de las aulas en el archivo CSV
            for (Profesor profesor : profesores) {
                writer.println(String.join(",", formatProfesor(profesor)));
            }

            writer.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al exportar los datos a CSV: " + e.getMessage());
        }
    }

    // Método para formatear una aula como una lista de valores de cadena
    private List<String> formatProfesor(Profesor profesor) {
        return List.of(
                String.valueOf(profesor.getIdProfesor()),
                profesor.getDocumentoIdentidad(),
                profesor.getNombre1(),
                profesor.getNombre2(),
                profesor.getApellido1(),
                profesor.getApellido2(),// Aquí asumimos que Aula tiene una relación con Edificio
                profesor.getTelefono(),
                profesor.getCorreo()
        );
    }
}
