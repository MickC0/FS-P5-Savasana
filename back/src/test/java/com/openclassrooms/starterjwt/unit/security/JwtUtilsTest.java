package com.openclassrooms.starterjwt.unit.security;

import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class JwtUtilsTest {

    @Autowired
    private JwtUtils jwtUtils;

    private String token;

    @BeforeEach
    public void setUp() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@yoga.com")
                .password("password")
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
        token = jwtUtils.generateJwtToken(authentication);
    }

    @Test
    public void shouldGenerateValidJwtToken() {
        assertThat(token).isNotNull();
        assertThat(jwtUtils.getUserNameFromJwtToken(token)).isEqualTo("test@yoga.com");
        assertThat(jwtUtils.validateJwtToken(token)).isTrue();
    }

    @Test
    public void shouldDetectExpiredJwtToken() throws InterruptedException {
        Thread.sleep(2000);
        assertThat(jwtUtils.validateJwtToken(token)).isFalse();
    }

    @Test
    public void shouldCatchSignatureException() {
        String invalidSignatureToken = token + "tampered";
        assertThat(jwtUtils.validateJwtToken(invalidSignatureToken)).isFalse();
    }

    @Test
    public void shouldCatchMalformedJwtException() {
        String malformedToken = "abc.def.ghi";
        assertThat(jwtUtils.validateJwtToken(malformedToken)).isFalse();
    }

    @Test
    public void shouldCatchUnsupportedJwtException() {
        String unsupportedToken = Jwts.builder()
                .setSubject("test@yoga.com")
                .claim("custom", "data")
                .compact();

        assertThat(jwtUtils.validateJwtToken(unsupportedToken)).isFalse();
    }

    @Test
    public void shouldCatchIllegalArgumentException() {
        assertThat(jwtUtils.validateJwtToken("")).isFalse();
        assertThat(jwtUtils.validateJwtToken(null)).isFalse();
    }
}
