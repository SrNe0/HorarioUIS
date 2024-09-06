package uis.horariouis.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Component
public class IpSetupRunner implements CommandLineRunner {

    @Override
    public void run(String... args) {
        // Ejecuta ambos scripts y obtiene las IPs con el puerto
        List<String> ips = ShellScriptExecutor.getCombinedIPAddressesWithPort(
                "/home/dev/getIPAddr_Local.sh",
                "/home/dev/getIPAddr_tailScaled.sh",
                "4200"
        );

        // Remover el puerto si existe, dejando solo la IP
        String localApiUrl = ips.size() > 0 ? removePort(ips.get(0)) : "http://182.168.0.100";
        String tailScaleApiUrl = ips.size() > 1 ? removePort(ips.get(1)) : "http://101.112.128.60";

        // Escribe las IPs en el archivo config.json sin puerto
        try (FileWriter file = new FileWriter("/home/dev/HorarioUIS/Frontend/src/assets/Ips.json")) {
            file.write("{\"localApiUrl\": \"" + localApiUrl + "\", \"tailScaleApiUrl\": \"" + tailScaleApiUrl + "\"}");
            System.out.println("Configuración completada: " + localApiUrl + " y " + tailScaleApiUrl);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error configurando las IPs. Se usarán las IPs por defecto.");
        }
    }

    // Función para remover el puerto de una URL si existe
    private String removePort(String url) {
        // Remueve todo a partir del último ':' para quitar el puerto
        return url.replaceAll(":[0-9]+", "");
    }
}
