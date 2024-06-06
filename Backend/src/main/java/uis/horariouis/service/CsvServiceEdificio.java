package uis.horariouis.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uis.horariouis.model.Edificio;
import uis.horariouis.repository.EdificioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvServiceEdificio {
    private static final Logger log = LoggerFactory.getLogger(CsvServiceEdificio.class);

    @Autowired
    private EdificioService EdificioService;
    @Autowired
    private EdificioRepository EdificioRepository;


    public void importEdificiosFromCsv(MultipartFile file) {
        log.info("Iniciando importación de archivo CSV");
        List<Edificio> edificios = new ArrayList<>();
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();  // Skip the header
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                try {
                    Edificio edificio = parseEdificio(nextRecord);
                    edificios.add(edificio);
                    log.info("Edificio procesado: {}", edificio.getNombre());
                } catch (IllegalArgumentException e) {
                    log.error("Error al parsear el edificio: {}", e.getMessage());
                }
            }
            EdificioRepository.saveAll(edificios);  // Bulk insert
            log.info("Todos los edificios han sido guardados.");
        } catch (Exception e) {
            log.error("Error al procesar el archivo CSV", e);
            throw new RuntimeException("Error al procesar el archivo CSV: " + e.getMessage());
        }
    }

    private Edificio parseEdificio(String[] csvData) {
        if (csvData.length < 2 || csvData[1].isEmpty()) {
            throw new IllegalArgumentException("Datos incompletos o inválidos para el edificio.");
        }
        Edificio edificio = new Edificio();
        edificio.setNombre(csvData[1]); // Asumiendo que el nombre es la segunda columna
        return edificio;
    }
    public void exportEdificiosToCsv(HttpServletResponse response) {
        try {
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=edificios.csv");
            PrintWriter writer = response.getWriter();
            StatefulBeanToCsv<Edificio> beanToCsv = new StatefulBeanToCsvBuilder<Edificio>(writer)
                    .withApplyQuotesToAll(false)  // Configura si quieres aplicar comillas a todos los campos
                    .build();
            List<Edificio> edificios = EdificioService.getAllEdificios();  // Asume que este método ya existe
            beanToCsv.write(edificios);
            writer.close();
        } catch (Exception e) {
            log.error("Error al exportar los datos a CSV", e);
            throw new RuntimeException("Error al exportar los datos a CSV: " + e.getMessage());
        }
    }

}
