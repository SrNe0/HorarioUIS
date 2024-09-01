package uis.horariouis.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component // Marca esta clase como un componente gestionado por Spring
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService; // Servicio para cargar detalles del usuario desde la base de datos

    @Autowired
    private JwtUtil jwtUtil; // Utilidad para manejar operaciones con JWT (extraer usuario, validar token, etc.)

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class); // Logger para registrar mensajes

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Obtiene el encabezado de autorización de la solicitud
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Verifica si el encabezado contiene un token JWT
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // Extrae el token JWT eliminando el prefijo "Bearer"
            try {
                username = jwtUtil.extractUsername(jwt); // Extrae el nombre de usuario del token JWT
            } catch (Exception e) {
                logger.warn("JWT token is invalid: {}", e.getMessage()); // Corrige la llamada al logger
            }
        }

        // Verifica si el nombre de usuario no es nulo y si no hay una autenticación existente en el contexto de seguridad
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Carga los detalles del usuario desde la base de datos
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Válida el token JWT con los detalles del usuario
            if (jwtUtil.validateToken(jwt, userDetails)) {

                // Crea un token de autenticación con los detalles del usuario y sus autorizaciones
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // Configura los detalles de autenticación
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken); // Establece la autenticación en el contexto de seguridad
            }
        }
        // Continúa con la cadena de filtros
        chain.doFilter(request, response);
    }
}
