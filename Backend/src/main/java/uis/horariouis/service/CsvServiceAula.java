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
import uis.horariouis.model.Edificio;
import uis.horariouis.repository.AulaRepository;
import uis.horariouis.repository.EdificioRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CsvServiceAula {
    private static final Logger log = LoggerFactory.getLogger(CsvServiceAula.class);

    @Autowired
    private AulaRepository aulaRepository;

    @Autowired
    private EdificioRepository edificioRepository;

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
                saveOrUpdateAula(aulaDTO);
            }
            log.info("Todas las aulas han sido guardadas.");
        } catch (Exception e) {
            log.error("Error al procesar el archivo CSV", e);
            throw new RuntimeException("Error al procesar el archivo CSV: " + e.getMessage());
        }
    }

    private AulaDTO parseAula(String[] csvData) {
        if (csvData.length < 5 || csvData[0].isEmpty() || csvData[1].isEmpty()) {
            throw new IllegalArgumentException("Datos incompletos o inválidos para el aula.");
        }
        AulaDTO aulaDTO = new AulaDTO();
        aulaDTO.setCodigo(csvData[0]);
        aulaDTO.setDescripcion(csvData[1]);
        aulaDTO.setCapacidad(Integer.parseInt(csvData[2]));
        aulaDTO.setNombreEdificio(csvData[3]);
        aulaDTO.setTieneComputadores(Boolean.parseBoolean(csvData[4]));
        return aulaDTO;
    }

    private void saveOrUpdateAula(AulaDTO aulaDTO) {
        Edificio edificio = edificioRepository.findByNombre(aulaDTO.getNombreEdificio());
        if (edificio == null) {
            log.error("Edificio no encontrado: {}", aulaDTO.getNombreEdificio());
            return;
        }
        Optional<Aula> existingAulaOpt = aulaRepository.findByCodigoAndEdificio(aulaDTO.getCodigo(), edificio);
        if (existingAulaOpt.isPresent()) {
            Aula existingAula = existingAulaOpt.get();
            existingAula.setDescripcion(aulaDTO.getDescripcion());
            existingAula.setCapacidad(aulaDTO.getCapacidad());
            existingAula.setTieneComputadores(aulaDTO.isTieneComputadores());
            existingAula.setEdificio(edificio);
            aulaRepository.save(existingAula);
            log.info("Aula actualizada: {} en el edificio {}", aulaDTO.getCodigo(), aulaDTO.getNombreEdificio());
        } else {
            Aula newAula = new Aula();
            newAula.setCodigo(aulaDTO.getCodigo());
            newAula.setDescripcion(aulaDTO.getDescripcion());
            newAula.setCapacidad(aulaDTO.getCapacidad());
            newAula.setTieneComputadores(aulaDTO.isTieneComputadores());
            newAula.setEdificio(edificio);
            aulaRepository.save(newAula);
            log.info("Aula creada: {} en el edificio {}", aulaDTO.getCodigo(), aulaDTO.getNombreEdificio());
        }
    }

    public void exportAulasToCsv(HttpServletResponse response) {
        try {
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=Aulas.csv");
            PrintWriter writer = response.getWriter();

            // Obtener todas las aulas
            List<Aula> aulas = aulaRepository.findAll();

            // Especificar el orden de las columnas
            String[] header = {"idAula", "codigo", "edificio", "descripcion", "capacidad", "tieneComputadores"};

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
                aula.getEdificio().getNombre(), // Usamos el nombre del edificio aquí
                aula.getDescripcion(),
                String.valueOf(aula.getCapacidad()),
                String.valueOf(aula.getTieneComputadores())
        );
    }
}
