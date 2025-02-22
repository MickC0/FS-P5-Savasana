package com.openclassrooms.starterjwt.unit;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private Session session;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1L)
                .email("john.doe@yoga.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .admin(false)
                .build();

        Teacher teacher = Teacher.builder()
                .id(1L)
                .firstName("Jolie")
                .lastName("Rose")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        session = Session.builder()
                .id(1L)
                .date(Date.from(Instant.parse("2025-03-03T10:00:00Z")))
                .name("Session")
                .teacher(teacher)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .users(new ArrayList<>(List.of(user)))
                .build();
    }

    @Test
    public void shouldRetrieveUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User returnedUser = userService.findById(1L);
        verify(userRepository, times(1)).findById(1L);
        assertThat(returnedUser).isNotNull();
        assertThat(returnedUser).isEqualTo(user);
    }

    @Test
    public void shouldReturnNullIfUserNotExists() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        User returnedUser = userService.findById(2L);
        verify(userRepository, times(1)).findById(2L);
        assertThat(returnedUser).isNull();
    }

    @Test
    public void shouldRemoveUserFromSessionAndDeleteUser() {
        List<Session> sessions = new ArrayList<>();
        sessions.add(session);
        when(sessionRepository.findAll()).thenReturn(sessions);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);
        assertThat(sessions.get(0).getUsers().isEmpty()).isTrue();
        verify(sessionRepository, times(1)).save(session);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    public void shouldReturnExceptionIfDeleteUserNotExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.delete(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User not found");
        verify(userRepository, never()).deleteById(anyLong());
        verify(sessionRepository, never()).save(any());
    }
}
