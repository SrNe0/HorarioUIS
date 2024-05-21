package uis.horariouis.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uis.horariouis.dto.AulaDTO;
import uis.horariouis.model.Aula;
import uis.horariouis.repository.AulaRepository;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvServiceAula {
    private static final Logger log = LoggerFactory.getLogger(CsvServiceAula.class);

    @Autowired
    private AulaRepository aulaRepository;
    @Autowired
    private AulaService aulaService;


    public void importAulasFromCsv(MultipartFile file) {
        log.info("Iniciando importación de archivo CSV para Aulas");
        List<AulaDTO> aulaDTOList = new ArrayList<>();
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                try {
                    AulaDTO aulaDTO = parseAula(nextRecord);
                    aulaDTOList.add(aulaDTO);
                    log.info("Aula procesada: {}", aulaDTO.getDescripcion());
                } catch (IllegalArgumentException e) {
                    log.error("Error al parsear el aula: {}", e.getMessage());
                }
            }
            for (AulaDTO aulaDTO : aulaDTOList) {
                aulaService.createOrUpdateAula(aulaDTO);
            }
            log.info("Todas las aulas han sido guardadas.");
        } catch (Exception e) {
            log.error("Error al procesar el archivo CSV", e);
            throw new RuntimeException("Error al procesar el archivo CSV: " + e.getMessage());
        }
    }
    private AulaDTO parseAula(String[] csvData) {
        if (csvData.length < 4 || csvData[0].isEmpty() || csvData[1].isEmpty()) {
            throw new IllegalArgumentException("Datos incompletos o inválidos para el aula.");
        }
        AulaDTO aulaDTO = new AulaDTO();
        aulaDTO.setCodigo(csvData[0]);
        aulaDTO.setDescripcion(csvData[1]);
        aulaDTO.setCapacidad(Integer.parseInt(csvData[2]));
        aulaDTO.setNombreEdificio(csvData[3]);
        return aulaDTO;
    }
    public void exportAulasToCsv(HttpServletResponse response) {
        try {
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=Aulas.csv");
            PrintWriter writer = response.getWriter();

            // Obtener todas las aulas
            List<Aula> aulas = aulaRepository.findAll();

            // Especificar el orden de las columnas
            String[] header = {"idAula", "codigo", "edificio", "descripcion", "capacidad"};

            // Escribir el encabezado al archivo CSV
            writer.println(String.join(",", header));

            // Escribir los datos de las aulas en el archivo CSV
            for (Aula aula : aulas) {
                writer.println(String.join(",", formatAula(aula)));
            }

            writer.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al exportar los datos a CSV: " + e.getMessage());
        }
    }

    // Método para formatear una aula como una lista de valores de cadena
    private List<String> formatAula(Aula aula) {
        return List.of(
                String.valueOf(aula.getIdAula()),
                aula.getCodigo(),
                String.valueOf(aula.getEdificio()), // Aquí asumimos que Aula tiene una relación con Edificio
                aula.getDescripcion(),
                String.valueOf(aula.getCapacidad())
        );
    }

}