package uis.horariouis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import uis.horariouis.security.JwtUtil;
import uis.horariouis.model.AuthenticationRequest;
import uis.horariouis.model.AuthenticationResponse;
import uis.horariouis.model.Usuario;
import uis.horariouis.model.Rol; // Asegúrate de tener esta importación
import uis.horariouis.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController // Marca esta clase como un controlador REST
@CrossOrigin // Habilita solicitudes CORS (Cross-Origin Resource Sharing)
@RequestMapping("/api/security") // Define la ruta base para todas las solicitudes en este controlador
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class); // Logger para registrar mensajes

    @Autowired
    private AuthenticationManager authenticationManager; // Gestor de autenticación de Spring

    @Autowired
    private JwtUtil jwtUtil; // Utilidad para manejar operaciones con JWT

    @Autowired
    private UserDetailsService userDetailsService; // Servicio para cargar detalles del usuario

    @Autowired
    private UsuarioService usuarioService; // Servicio para manejar operaciones con usuarios

    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // Codificador de contraseñas

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        // Declarar la variable `usuario` sin inicializarla como `null`
        Usuario usuario;

        try {
            logger.info("Attempting to authenticate user: {}", authenticationRequest.getUsername());

            // Cargar el usuario desde la base de datos
            usuario = usuarioService.findByNombreUsuario(authenticationRequest.getUsername());
            if (usuario == null) {
                logger.error("User not found: {}", authenticationRequest.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }

            // Verificar si la contraseña proporcionada coincide con la encriptada
            if (!passwordEncoder.matches(authenticationRequest.getPassword(), usuario.getContrasena())) {
                logger.error("Invalid password for user: {}", authenticationRequest.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }

            // Intentar autenticar al usuario con las credenciales proporcionadas
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );

        } catch (BadCredentialsException e) {
            logger.error("Authentication failed for user: {} - Bad credentials", authenticationRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (Exception e) {
            logger.error("An error occurred during authentication for user: {}", authenticationRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during authentication");
        }

        // Cargar los detalles del usuario y generar el token JWT
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);
        logger.info("Generated JWT for user: {}", authenticationRequest.getUsername());

        // Obtener el rol del usuario
        Rol rol = usuario.getRol();  // Ahora `usuario` es accesible aquí

        // Determinar si el token tiene expiración y cuánto tiempo durará
        boolean expires = true;  // Suponiendo que el token tiene expiración
        int timeout = 3600; // Tiempo de expiración del token en segundos (1 hora)

        // Retornar la respuesta con el token, el rol y los nuevos campos
        return ResponseEntity.ok(new AuthenticationResponse(jwt, rol, expires, timeout));
    }
}
