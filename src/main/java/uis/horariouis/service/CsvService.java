package uis.horariouis.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uis.horariouis.model.Edificio;
import uis.horariouis.repository.EdificioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.List;

@Service
public class CsvService {
    private static final Logger log = LoggerFactory.getLogger(CsvService.class);

    @Autowired
    private EdificioService EdificioService;
    @Autowired
    private EdificioRepository EdificioRepository;

    public void importEdificiosFromCsv(MultipartFile file) {
        log.info("Iniciando importación de archivo CSV");
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();  // Skip the first line
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                log.info("Procesando línea: {}", Arrays.toString(nextRecord));
                Edificio edificio = new Edificio();
                edificio.setNombre(nextRecord[1]); // Asumiendo que el nombre es la segunda columna
                EdificioRepository.save(edificio);
                log.info("Edificio guardado: {}", edificio.getNombre());
            }
        } catch (Exception e) {
            log.error("Error al procesar el archivo CSV", e);
            throw new RuntimeException("Error al procesar el archivo CSV: " + e.getMessage());
        }
    }
    public void exportEdificiosToCsv(HttpServletResponse response) {
        try (PrintWriter writer = response.getWriter()) {
            StatefulBeanToCsv<Edificio> beanToCsv = new StatefulBeanToCsvBuilder<Edificio>(writer).build();
            List<Edificio> edificios = EdificioService.getAllEdificios(); // Asume que este método ya existe
            beanToCsv.write(edificios);
        } catch (Exception e) {
            throw new RuntimeException("Error al exportar los datos a CSV: " + e.getMessage());
        }
    }
}
