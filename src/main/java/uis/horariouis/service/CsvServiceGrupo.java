package uis.horariouis.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uis.horariouis.dto.GrupoDTO;
import uis.horariouis.model.Grupo;
import uis.horariouis.repository.GrupoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvServiceGrupo {
    private static final Logger log = LoggerFactory.getLogger(CsvServiceGrupo.class);

    @Autowired
    private GrupoService grupoService;
    @Autowired
    private GrupoRepository grupoRepository;

    public void importGruposFromCsv(MultipartFile file) {
        log.info("Iniciando importación de archivo CSV para Grupos");
        List<GrupoDTO> grupoDTOList = new ArrayList<>();
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                try {
                    GrupoDTO grupoDTO = parseGrupo(nextRecord);
                    grupoDTOList.add(grupoDTO);
                    log.info("Grupo procesado: {}", grupoDTO.getNombre());
                } catch (IllegalArgumentException e) {
                    log.error("Error al parsear el grupo: {}", e.getMessage());
                }
            }
            for (GrupoDTO grupoDTO : grupoDTOList) {
                grupoService.createGrupoFromDTO(grupoDTO);
            }
            log.info("Todos los grupos han sido guardados.");
        } catch (Exception e) {
            log.error("Error al procesar el archivo CSV", e);
            throw new RuntimeException("Error al procesar el archivo CSV: " + e.getMessage());
        }
    }

    private GrupoDTO parseGrupo(String[] csvData) {
        if (csvData.length < 3 || csvData[0].isEmpty() || csvData[1].isEmpty()) {
            throw new IllegalArgumentException("Datos incompletos o inválidos para el grupo.");
        }
        GrupoDTO grupoDTO = new GrupoDTO();
        grupoDTO.setCodigoAsignatura(csvData[0]);
        grupoDTO.setNombre(csvData[1]);
        grupoDTO.setCupo(Integer.parseInt(csvData[2]));
        return grupoDTO;
    }
    public void exportGruposToCsv(HttpServletResponse response) {
        try {
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=Grupos.csv");
            PrintWriter writer = response.getWriter();

            // Obtener todos los grupos
            List<Grupo> grupos = grupoRepository.findAll();

            // Especificar el orden de las columnas
            String[] header = {"IDGRUPO", "ASIGNATURA", "NOMBREGRUPO", "CUPO"};

            // Escribir el encabezado al archivo CSV
            writer.println(String.join(",", header));

            // Escribir los datos de los grupos en el archivo CSV
            for (Grupo grupo : grupos) {
                writer.println(String.join(",", formatGrupo(grupo)));
            }

            writer.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al exportar los datos a CSV: " + e.getMessage());
        }
    }

    // Método para formatear un grupo como una lista de valores de cadena
    private List<String> formatGrupo(Grupo grupo) {
        return List.of(
                String.valueOf(grupo.getIdGrupo()),
                String.valueOf(grupo.getAsignatura()),
                grupo.getNombreGrupo(),
                String.valueOf(grupo.getCupo())
        );
    }
}



