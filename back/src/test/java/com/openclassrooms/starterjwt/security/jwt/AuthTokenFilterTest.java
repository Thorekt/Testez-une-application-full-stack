package com.openclassrooms.starterjwt.security.jwt;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Tests complets pour {@link AuthTokenFilter}
 * Chaque test contient des commentaires Gherkin (Given / When / Then) en
 * français.
 *
 * On couvre : doFilterInternal (chemin nominal et cas particuliers) et parseJwt
 * (méthode privée)
 */
public class AuthTokenFilterTest {

    private AuthTokenFilter filter;
    private JwtUtils mockedJwtUtils;
    private UserDetailsServiceImpl mockedUserDetailsService;

    @BeforeEach
    public void setup() throws Exception {
        // Initialisation du filtre et injection des mocks dans les champs @Autowired
        // via reflection
        filter = new AuthTokenFilter();
        mockedJwtUtils = Mockito.mock(JwtUtils.class);
        mockedUserDetailsService = Mockito.mock(UserDetailsServiceImpl.class);

        Field jwtField = AuthTokenFilter.class.getDeclaredField("jwtUtils");
        jwtField.setAccessible(true);
        jwtField.set(filter, mockedJwtUtils);

        Field udsField = AuthTokenFilter.class.getDeclaredField("userDetailsService");
        udsField.setAccessible(true);
        udsField.set(filter, mockedUserDetailsService);
    }

    @AfterEach
    public void tearDown() {
        // Nettoyage du SecurityContext pour éviter les interférences entre tests
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testDoFilterInternalValidJwtSetsAuthentication() throws ServletException, IOException {
        // Given: un header Authorization valide contenant un JWT
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        FilterChain chain = Mockito.mock(FilterChain.class);

        String jwt = "valid.jwt";
        String username = "user@example.com";

        when(req.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(mockedJwtUtils.validateJwtToken(jwt)).thenReturn(true);
        when(mockedJwtUtils.getUserNameFromJwtToken(jwt)).thenReturn(username);

        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(42L)
                .username(username)
                .firstName("Test")
                .lastName("User")
                .password("pwd")
                .admin(false)
                .build();

        when(mockedUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        // When: exécution du filtre
        filter.doFilterInternal(req, resp, chain);

        // Then: l'authentication doit être positionnée et la requête doit continuer
        verify(chain).doFilter(req, resp);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(userDetails, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Test
    public void testDoFilterInternalNoHeaderNoAuth() throws ServletException, IOException {
        // Given: pas d'entête Authorization
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        FilterChain chain = Mockito.mock(FilterChain.class);

        when(req.getHeader("Authorization")).thenReturn(null);

        // When
        filter.doFilterInternal(req, resp, chain);

        // Then
        verify(chain).doFilter(req, resp);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testDoFilterInternalInvalidPrefixNoAuth() throws ServletException, IOException {
        // Given: header présent mais sans préfixe Bearer
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        FilterChain chain = Mockito.mock(FilterChain.class);

        when(req.getHeader("Authorization")).thenReturn("Token abcdef");

        // When
        filter.doFilterInternal(req, resp, chain);

        // Then
        verify(chain).doFilter(req, resp);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testDoFilterInternalInvalidJwtNoAuth() throws ServletException, IOException {
        // Given: header avec JWT invalide
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        FilterChain chain = Mockito.mock(FilterChain.class);

        String jwt = "invalid.jwt";
        when(req.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(mockedJwtUtils.validateJwtToken(jwt)).thenReturn(false);

        // When
        filter.doFilterInternal(req, resp, chain);

        // Then
        verify(chain).doFilter(req, resp);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testDoFilterInternalJwtUtilsThrowsNoAuthAndContinue() throws ServletException, IOException {
        // Given: jwtUtils lève une exception lors de la validation
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        FilterChain chain = Mockito.mock(FilterChain.class);

        String jwt = "bad.jwt";
        when(req.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(mockedJwtUtils.validateJwtToken(jwt)).thenThrow(new RuntimeException("boom"));

        // When
        filter.doFilterInternal(req, resp, chain);

        // Then: la requête continue malgré l'exception et pas d'auth set
        verify(chain).doFilter(req, resp);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testDoFilterInternalUserDetailsServiceThrowsNoAuthAndContinue() throws ServletException, IOException {
        // Given: validation ok mais loadUserByUsername lève une exception
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        FilterChain chain = Mockito.mock(FilterChain.class);

        String jwt = "valid.jwt";
        String username = "user@example.com";
        when(req.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(mockedJwtUtils.validateJwtToken(jwt)).thenReturn(true);
        when(mockedJwtUtils.getUserNameFromJwtToken(jwt)).thenReturn(username);
        when(mockedUserDetailsService.loadUserByUsername(username)).thenThrow(new RuntimeException("user load error"));

        // When
        filter.doFilterInternal(req, resp, chain);

        // Then
        verify(chain).doFilter(req, resp);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testDoFilterInternalFilterChainThrowsIOExceptionPropagated() throws ServletException, IOException {
        // Given: le FilterChain lance une IOException
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        FilterChain chain = Mockito.mock(FilterChain.class);

        when(req.getHeader("Authorization")).thenReturn(null);
        doThrow(new IOException("io error")).when(chain).doFilter(req, resp);

        // When / Then: l'IOException doit être propagée
        assertThrows(IOException.class, () -> filter.doFilterInternal(req, resp, chain));
    }

    // Tests pour la méthode privée parseJwt via reflection
    @Test
    public void testParseJwtNullHeaderReturnsNull() throws Exception {
        // Given
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        when(req.getHeader("Authorization")).thenReturn(null);

        // When
        Method m = AuthTokenFilter.class.getDeclaredMethod("parseJwt", HttpServletRequest.class);
        m.setAccessible(true);
        Object res = m.invoke(filter, req);

        // Then
        assertNull(res);
    }

    @Test
    public void testParseJwtBearerWithTokenReturnsToken() throws Exception {
        // Given
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        when(req.getHeader("Authorization")).thenReturn("Bearer mytoken123");

        // When
        Method m = AuthTokenFilter.class.getDeclaredMethod("parseJwt", HttpServletRequest.class);
        m.setAccessible(true);
        Object res = m.invoke(filter, req);

        // Then
        assertEquals("mytoken123", res);
    }

    @Test
    public void testParseJwtBearerSpaceReturnsEmptyString() throws Exception {
        // Given
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        when(req.getHeader("Authorization")).thenReturn("Bearer ");

        // When
        Method m = AuthTokenFilter.class.getDeclaredMethod("parseJwt", HttpServletRequest.class);
        m.setAccessible(true);
        Object res = m.invoke(filter, req);

        // Then: substring(7,7) -> chaîne vide
        assertEquals("", res);
    }

    @Test
    public void testParseJwtWrongPrefixReturnsNull() throws Exception {
        // Given
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        when(req.getHeader("Authorization")).thenReturn("Bear mytoken");

        // When
        Method m = AuthTokenFilter.class.getDeclaredMethod("parseJwt", HttpServletRequest.class);
        m.setAccessible(true);
        Object res = m.invoke(filter, req);

        // Then
        assertNull(res);
    }

}
