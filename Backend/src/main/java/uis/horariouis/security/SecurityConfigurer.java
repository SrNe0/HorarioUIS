package uis.horariouis.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration // Indica que esta clase es una configuración de Spring
@EnableWebSecurity // Habilita la seguridad web de Spring
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService; // Servicio para cargar detalles del usuario desde la base de datos

    @Autowired
    private JwtRequestFilter jwtRequestFilter; // Filtro JWT para validar tokens en cada solicitud

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // Configura el gestor de autenticación con el servicio de detalles de usuario y un codificador de contraseñas
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Configura la seguridad HTTP
        http.cors().and().csrf().disable() // Habilita CORS y deshabilita CSRF (Cross-Site Request Forgery)
                .authorizeRequests() // Permite la autorización de solicitudes
                .antMatchers("/api/security/authenticate").permitAll() // Permite el acceso público al endpoint de autenticación
                .antMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").authenticated() // Requiere autenticación para los documentos de API y Swagger UI
                .antMatchers(HttpMethod.GET, "/**").hasAnyRole("USER", "ADMIN") // Permite acceso GET a usuarios con roles USER y ADMIN
                .antMatchers(HttpMethod.POST, "/**").hasRole("ADMIN") // Permite acceso POST solo a usuarios con rol ADMIN
                .antMatchers(HttpMethod.PUT, "/**").hasRole("ADMIN") // Permite acceso PUT solo a usuarios con rol ADMIN
                .antMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN") // Permite acceso DELETE solo a usuarios con rol ADMIN
                .anyRequest().authenticated() // Requiere autenticación para cualquier otra solicitud
                .and().httpBasic() // Habilita la autenticación básica (útil para Swagger)
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Configura la política de sesión sin estado (stateless)
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler()); // Maneja excepciones de acceso denegado
        // Añade el filtro JWT antes del filtro de autenticación de nombre de usuario y contraseña
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        // Define el bean de BCryptPasswordEncoder para codificar las contraseñas
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        // Define el bean de AccessDeniedHandler para manejar accesos denegados
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpStatus.FORBIDDEN.value()); // Configura el estado de la respuesta como 403 (FORBIDDEN)
            response.setContentType("application/json"); // Configura el tipo de contenido de la respuesta como JSON
            response.getWriter().write("{\"message\": \"You do not have permission to access this resource\", \"status\": 403}"); // Escribe el mensaje de error en la respuesta
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // Configura las reglas de CORS (Cross-Origin Resource Sharing)
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200", "http://100.112.128.60:4200", "http://192.168.0.101:4200")); // Permite orígenes específicos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Permite métodos HTTP específicos
        configuration.setAllowedHeaders(List.of("*")); // Permite todos los encabezados
        configuration.setAllowCredentials(true); // Permite credenciales
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica la configuración a todas las rutas
        return source;
    }
}