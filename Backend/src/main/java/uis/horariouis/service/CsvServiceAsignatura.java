package uis.horariouis.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uis.horariouis.model.Asignatura;
import uis.horariouis.repository.AsignaturaRepository;
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
public class CsvServiceAsignatura {
    private static final Logger log = LoggerFactory.getLogger(CsvServiceAsignatura.class);

    @Autowired
    private AsignaturaRepository asignaturaRepository;

    public void importAsignaturasFromCsv(MultipartFile file) {
        log.info("Iniciando importación de archivo CSV para Asignaturas");
        List<Asignatura> asignaturas = new ArrayList<>();
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                try {
                    Asignatura asignatura = parseAsignatura(nextRecord);
                    asignaturas.add(asignatura);
                    log.info("Asignatura procesada: {}", asignatura.getNombre());
                } catch (IllegalArgumentException e) {
                    log.error("Error al parsear la asignatura: {}", e.getMessage());
                }
            }
            asignaturaRepository.saveAll(asignaturas);
            log.info("Todas las asignaturas han sido guardadas.");
        } catch (Exception e) {
            log.error("Error al procesar el archivo CSV", e);
            throw new RuntimeException("Error al procesar el archivo CSV: " + e.getMessage());
        }
    }

    private Asignatura parseAsignatura(String[] csvData) {
        if (csvData.length < 4 || csvData[1].isEmpty() || csvData[2].isEmpty()) {
            throw new IllegalArgumentException("Datos incompletos o inválidos para la asignatura.");
        }
        Asignatura asignatura = new Asignatura();
        asignatura.setCodigo(csvData[1]);
        asignatura.setNombre(csvData[2]);
        asignatura.setHorasTeoria(Integer.parseInt(csvData[3]));
        asignatura.setHorasPractica(Integer.parseInt(csvData[4]));
        return asignatura;
    }

    public void exportAsignaturasToCsv(HttpServletResponse response) {
        try {
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=asignaturas.csv");
            PrintWriter writer = response.getWriter();
            StatefulBeanToCsv<Asignatura> beanToCsv = new StatefulBeanToCsvBuilder<Asignatura>(writer).build();
            List<Asignatura> asignaturas = asignaturaRepository.findAll(); // Asume que este método ya existe en el repositorio
            beanToCsv.write(asignaturas);
            writer.close();
        } catch (Exception e) {
            log.error("Error al exportar los datos a CSV", e);
            throw new RuntimeException("Error al exportar los datos a CSV: " + e.getMessage());
        }
    }
}
