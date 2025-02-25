package com.openclassrooms.starterjwt.unit.controller;

import com.openclassrooms.starterjwt.controllers.UserController;
import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private UserController userController;

    private User user;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@yoga.com");

        userDto = new UserDto();
    }

    @Test
    public void shouldFindUserById() {
        when(userService.findById(1L)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        ResponseEntity<?> response = userController.findById("1");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(userDto);

        verify(userService, times(1)).findById(1L);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    public void shouldReturnNotFoundWhenUserDoesNotExist() {
        when(userService.findById(1L)).thenReturn(null);

        ResponseEntity<?> response = userController.findById("1");

        assertThat(response.getStatusCodeValue()).isEqualTo(404);

        verify(userService, times(1)).findById(1L);
        verifyNoInteractions(userMapper);
    }

    @Test
    public void shouldReturnBadRequestForInvalidUserId() {
        ResponseEntity<?> response = userController.findById("invalid");

        assertThat(response.getStatusCodeValue()).isEqualTo(400);

        verifyNoInteractions(userService, userMapper);
    }

    @Test
    public void shouldDeleteUserSuccessfully() {
        when(userService.findById(1L)).thenReturn(user);
        doNothing().when(userService).delete(1L);

        mockSecurityContext("test@yoga.com");

        ResponseEntity<?> response = userController.save("1");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        verify(userService, times(1)).findById(1L);
        verify(userService, times(1)).delete(1L);
    }

    @Test
    public void shouldReturnNotFoundIfUserToDeleteDoesNotExist() {
        when(userService.findById(1L)).thenReturn(null);

        ResponseEntity<?> response = userController.save("1");

        assertThat(response.getStatusCodeValue()).isEqualTo(404);

        verify(userService, times(1)).findById(1L);
        verify(userService, never()).delete(anyLong());
    }

    @Test
    public void shouldReturnBadRequestWhenDeletingWithInvalidId() {
        ResponseEntity<?> response = userController.save("invalid");

        assertThat(response.getStatusCodeValue()).isEqualTo(400);

        verifyNoInteractions(userService);
    }

    @Test
    public void shouldReturnUnauthorizedWhenDeletingAnotherUser() {
        when(userService.findById(1L)).thenReturn(user);

        mockSecurityContext("other@yoga.com"); // 🔥 Utilisateur différent

        ResponseEntity<?> response = userController.save("1");

        assertThat(response.getStatusCodeValue()).isEqualTo(401);

        verify(userService, times(1)).findById(1L);
        verify(userService, never()).delete(anyLong());
    }

    private void mockSecurityContext(String email) {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(email);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
