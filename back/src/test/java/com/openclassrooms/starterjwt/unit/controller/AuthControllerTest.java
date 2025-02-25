package com.openclassrooms.starterjwt.unit.controller;

import com.openclassrooms.starterjwt.controllers.AuthController;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthController authController;

    private Authentication authentication;

    @BeforeEach
    public void setUp() {
        User user = new User("test@yoga.com", "Doe", "John", "password", false);
        user.setId(1L);

        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@yoga.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .admin(false)
                .build();

        authentication = mock(Authentication.class);
    }

    @Test
    public void shouldAuthenticateUserAsAdmin() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@yoga.com");
        loginRequest.setPassword("password");

        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setEmail("admin@yoga.com");
        adminUser.setPassword("password");
        adminUser.setAdmin(true);

        UserDetailsImpl userDetailsAdmin = UserDetailsImpl.builder()
                .id(1L)
                .username("admin@yoga.com")
                .password("password")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetailsAdmin);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("valid-jwt-token");
        doReturn(Optional.of(adminUser)).when(userRepository).findByEmail("admin@yoga.com");

        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isInstanceOf(JwtResponse.class);

        JwtResponse jwtResponse = (JwtResponse) response.getBody();

        assertThat(jwtResponse).isNotNull();
        assertThat(jwtResponse.getToken()).isEqualTo("valid-jwt-token");
        assertThat(jwtResponse.getUsername()).isEqualTo("admin@yoga.com");
        assertThat(jwtResponse.getAdmin()).isTrue();

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, times(1)).generateJwtToken(authentication);
        verify(userRepository, times(1)).findByEmail("admin@yoga.com");
    }


    @Test
    public void shouldAuthenticateUserButUserNotInRepository() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("unknown@yoga.com");
        loginRequest.setPassword("password");

        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(2L)
                .username("unknown@yoga.com")
                .password("password")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("valid-jwt-token");
        doReturn(Optional.empty()).when(userRepository).findByEmail("unknown@yoga.com");

        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isInstanceOf(JwtResponse.class);

        JwtResponse jwtResponse = (JwtResponse) response.getBody();

        assertThat(jwtResponse).isNotNull();
        assertThat(jwtResponse.getToken()).isEqualTo("valid-jwt-token");
        assertThat(jwtResponse.getUsername()).isEqualTo("unknown@yoga.com");
        assertThat(jwtResponse.getAdmin()).isFalse();

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, times(1)).generateJwtToken(authentication);
        verify(userRepository, times(1)).findByEmail("unknown@yoga.com");
    }


    @Test
    public void shouldFailAuthenticationWithInvalidCredentials() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("invalid@yoga.com");
        loginRequest.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Bad credentials"));

        assertThatThrownBy(() -> authController.authenticateUser(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Bad credentials");

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtUtils, userRepository);
    }

    @Test
    public void shouldRegisterUserSuccessfully() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("new@yoga.com");
        signupRequest.setPassword("password");
        signupRequest.setFirstName("Jane");
        signupRequest.setLastName("Doe");

        when(userRepository.existsByEmail("new@yoga.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded-password");

        ResponseEntity<?> response = authController.registerUser(signupRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isInstanceOf(MessageResponse.class);
        assertThat(((MessageResponse) response.getBody()).getMessage()).isEqualTo("User registered successfully!");

        verify(userRepository, times(1)).existsByEmail("new@yoga.com");
        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void shouldFailRegistrationIfEmailAlreadyExists() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@yoga.com");
        signupRequest.setPassword("password");

        when(userRepository.existsByEmail("test@yoga.com")).thenReturn(true);

        ResponseEntity<?> response = authController.registerUser(signupRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody()).isInstanceOf(MessageResponse.class);
        assertThat(((MessageResponse) Objects.requireNonNull(response.getBody())).getMessage()).isEqualTo("Error: Email is already taken!");

        verify(userRepository, times(1)).existsByEmail("test@yoga.com");
        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any(User.class));
    }
}
