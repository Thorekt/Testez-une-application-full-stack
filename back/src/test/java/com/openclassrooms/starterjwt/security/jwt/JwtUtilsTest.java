package com.openclassrooms.starterjwt.security.jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.UnsupportedJwtException;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Tests pour {@link JwtUtils}
 * Chaque test utilise des commentaires Gherkin (Given / When / Then) en
 * français.
 */
public class JwtUtilsTest {

    private JwtUtils jwtUtils;

    private final String secret = "testSecretKeyForJwt";

    @BeforeEach
    public void setup() throws Exception {
        jwtUtils = new JwtUtils();

        // injecter les valeurs privées annotées @Value via reflection
        Field secretField = JwtUtils.class.getDeclaredField("jwtSecret");
        secretField.setAccessible(true);
        secretField.set(jwtUtils, secret);

        Field expField = JwtUtils.class.getDeclaredField("jwtExpirationMs");
        expField.setAccessible(true);
        // 1 heure
        expField.set(jwtUtils, 3600000);
    }

    @Test
    public void testGenerateJwtTokenAndGetUserNameFromJwtToken() {
        // Given: un Authentication contenant un UserDetailsImpl
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("user@example.com")
                .firstName("Jean")
                .lastName("Dupont")
                .password("pwd")
                .admin(false)
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // When: génération du token
        String token = jwtUtils.generateJwtToken(auth);

        // Then: token non null et subject égal au username
        assertNotNull(token);
        String username = jwtUtils.getUserNameFromJwtToken(token);
        assertEquals("user@example.com", username);
    }

    @Test
    public void testValidateJwtTokenValidTokenReturnsTrue() {
        // Given: un token JWT correctement signé
        String token = Jwts.builder()
                .setSubject("alice@example.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + 60000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

        // When
        boolean valid = jwtUtils.validateJwtToken(token);

        // Then
        assertTrue(valid);
    }

    @Test
    public void testValidateJwtTokenInvalidSignatureReturnsFalse() {
        // Given: token signé avec une autre clé
        String otherSecret = "otherSecret";
        String token = Jwts.builder()
                .setSubject("bob@example.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + 60000))
                .signWith(SignatureAlgorithm.HS512, otherSecret)
                .compact();

        // When
        boolean valid = jwtUtils.validateJwtToken(token);

        // Then
        assertFalse(valid);
    }

    @Test
    public void testValidateJwtTokenMalformedReturnsFalse() {
        // Given: chaîne non JWT
        String token = "not.a.jwt";

        // When
        boolean valid = jwtUtils.validateJwtToken(token);

        // Then
        assertFalse(valid);
    }

    @Test
    public void testValidateJwtTokenExpiredReturnsFalse() {
        // Given: token expiré
        String token = Jwts.builder()
                .setSubject("carol@example.com")
                .setIssuedAt(new Date((new Date()).getTime() - 100000))
                .setExpiration(new Date((new Date()).getTime() - 50000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

        // When
        boolean valid = jwtUtils.validateJwtToken(token);

        // Then
        assertFalse(valid);
    }

    @Test
    public void testValidateJwtTokenEmptyReturnsFalse() {
        // Given
        String token = "";

        // When
        boolean valid = jwtUtils.validateJwtToken(token);

        // Then
        assertFalse(valid);
    }

    @Test
    public void testValidateJwtTokenUnsupportedReturnsFalse() {
        // Given: mock static Jwts.parser() to throw UnsupportedJwtException
        try (MockedStatic<io.jsonwebtoken.Jwts> mocked = Mockito.mockStatic(Jwts.class)) {
            JwtParser parser = mock(JwtParser.class);
            mocked.when(Jwts::parser).thenReturn(parser);
            when(parser.setSigningKey(anyString())).thenReturn(parser);
            when(parser.parseClaimsJws(anyString())).thenThrow(new UnsupportedJwtException("unsupported"));

            // When
            boolean valid = jwtUtils.validateJwtToken("any-token");

            // Then
            assertFalse(valid);
        }
    }

}
