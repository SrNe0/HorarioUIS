package uis.horariouis.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import uis.horariouis.service.ShellScriptExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

    // Definir un logger para esta clase
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfigurer.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/security/authenticate").permitAll()
                .antMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").authenticated()
                .antMatchers(HttpMethod.GET, "/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.POST, "/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and().httpBasic()
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler());

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"You do not have permission to access this resource\", \"status\": 403}");
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
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

        List<String> allowedOrigins = new ArrayList<>(defaultIPs);
        if (!dynamicIPs.isEmpty()) {
            allowedOrigins.addAll(dynamicIPs);
        }

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
