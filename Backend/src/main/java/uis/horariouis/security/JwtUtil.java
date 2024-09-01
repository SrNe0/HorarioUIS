package uis.horariouis.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service // Marca esta clase como un servicio gestionado por Spring
public class JwtUtil {

    // Clave secreta para firmar el token JWT. En un entorno de producción, se debe almacenar de manera segura.
    private final String SECRET_KEY = "secret";

    // Extrae el nombre de usuario del token JWT
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extrae la fecha de expiración del token JWT
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extrae un reclamo específico del token JWT utilizando una función de resolución de reclamos
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extrae todos los reclamos del token JWT
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY) // Configura la clave secreta para verificar la firma
                .parseClaimsJws(token)
                .getBody();
    }

    // Verifica si el token JWT ha expirado
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Genera un token JWT para el usuario dado
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    // Crea el token JWT configurando los reclamos, el sujeto, la fecha de emisión, la fecha de expiración y firmando con el algoritmo HS256
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // Establece el sujeto del token (nombre de usuario)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Establece la fecha de emisión del token
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Establece la fecha de expiración del token (10 horas)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // Firma el token con el algoritmo HS256 y la clave secreta
                .compact();
    }

    // Válida el token JWT verificando el nombre de usuario y si el token ha expirado
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
