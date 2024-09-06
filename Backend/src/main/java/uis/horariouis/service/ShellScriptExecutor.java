package uis.horariouis.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ShellScriptExecutor {

    // Crea un logger para registrar información
    private static final Logger logger = Logger.getLogger(ShellScriptExecutor.class.getName());

    // Ejecuta un script y agrega el puerto a las IPs obtenidas
    public static List<String> getIPAddressesWithPort(String scriptPath, String port) {
        List<String> ipAddresses = new ArrayList<>();
        try {
            logger.log(Level.INFO, "Ejecutando script: " + scriptPath);
            // Ejecuta el script
            Process process = Runtime.getRuntime().exec(scriptPath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            // Lee cada línea (IP) generada por el script
            while ((line = reader.readLine()) != null) {
                String ipWithPort = "http://" + line + ":" + port;
                ipAddresses.add(ipWithPort); // Agrega "http://" y el puerto a la IP
                logger.log(Level.INFO, "IP obtenida: " + ipWithPort);
            }
            int exitCode = process.waitFor(); // Espera a que el proceso del script termine
            if (exitCode == 0) {
                logger.log(Level.INFO, "El script " + scriptPath + " se ejecutó correctamente.");
            } else {
                logger.log(Level.WARNING, "El script " + scriptPath + " terminó con un código de salida no esperado: " + exitCode);
            }
        } catch (Exception e) {
            // Manejo de errores
            logger.log(Level.SEVERE, "Error ejecutando el script " + scriptPath, e);
        }
        return ipAddresses;
    }

    // Ejecuta dos scripts, obtiene IPs y las combina
    public static List<String> getCombinedIPAddressesWithPort(String script1, String script2, String port) {
        logger.log(Level.INFO, "Comenzando ejecución de los dos scripts.");
        List<String> combinedIPs = new ArrayList<>();
        // Ejecuta el primer script y agrega las IPs obtenidas
        combinedIPs.addAll(getIPAddressesWithPort(script1, port));
        // Ejecuta el segundo script y agrega las IPs obtenidas
        combinedIPs.addAll(getIPAddressesWithPort(script2, port));
        logger.log(Level.INFO, "Número total de IPs obtenidas: " + combinedIPs.size());
        return combinedIPs; // Retorna la lista de IPs combinadas
    }
}
