package uis.horariouis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import uis.horariouis.security.CustomUserDetailsService;
import uis.horariouis.security.JwtUtil;
import uis.horariouis.model.AuthenticationRequest;
import uis.horariouis.model.AuthenticationResponse;
import uis.horariouis.model.Usuario;
import uis.horariouis.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@CrossOrigin
@RequestMapping("/api/security")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            logger.info("Attempting to authenticate user: {}", authenticationRequest.getUsername());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            // Si la autenticación falla, verifique si la contraseña está encriptada
            Usuario usuario = customUserDetailsService.loadUsuarioByUsername(authenticationRequest.getUsername());
            if (usuario != null && !passwordEncoder.matches(authenticationRequest.getPassword(), usuario.getContrasena())) {
                logger.info("Password not encrypted, updating password for user: {}", authenticationRequest.getUsername());
                usuario.setContrasena(passwordEncoder.encode(authenticationRequest.getPassword()));
                usuarioService.saveUsuario(usuario);
                // Intente autenticar de nuevo con la contraseña actualizada
                try {
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
                    );
                } catch (BadCredentialsException ex) {
                    logger.error("Invalid credentials for user: {}", authenticationRequest.getUsername());
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
                }
            } else {
                logger.error("Invalid credentials for user: {}", authenticationRequest.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (Exception e) {
            logger.error("Authentication error for user: {}", authenticationRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during authentication");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);
        logger.info("Generated JWT for user: {}", authenticationRequest.getUsername());

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
}
