package uis.horariouis.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uis.horariouis.service.ShellScriptExecutor;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MyConfiguration {

    // Definir un logger para esta clase
    private static final Logger logger = LoggerFactory.getLogger(MyConfiguration.class);

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // IPs por defecto con el puerto 4200
                List<String> defaultIPs = List.of(
                        "http://localhost:4200"
                );

                List<String> dynamicIPs = new ArrayList<>();
                try {
                    dynamicIPs = ShellScriptExecutor.getCombinedIPAddressesWithPort(
                            "/home/dev/getIPAddr_Local.sh",
                            "/home/dev/getIPAddr_tailScaled.sh",
                            "4200"
                    );
                } catch (Exception e) {
                    // Usa el logger para registrar advertencias y errores
                    logger.warn("No se pudieron ejecutar los scripts, continuando con las IPs por defecto.", e);
                }

                // Si no se obtuvieron IPs dinámicas, continúa solo con las IPs por defecto
                List<String> allowedIPs = new ArrayList<>(defaultIPs);
                if (!dynamicIPs.isEmpty()) {
                    allowedIPs.addAll(dynamicIPs); // Añade las IPs dinámicas si se encontraron
                }

                // Convertir la lista completa de IPs en un array
                String[] allowedOrigins = allowedIPs.toArray(new String[0]);

                // Configuración CORS con las IPs combinadas
                registry.addMapping("/**")
                        .allowedOrigins(allowedOrigins)  // Usa las IPs combinadas con el puerto
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
