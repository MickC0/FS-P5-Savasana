package com.openclassrooms.starterjwt.unit;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {

    @Mock
    SessionRepository sessionRepository;
    @Mock
    UserRepository userRepository;

    @InjectMocks
    SessionService sessionService;

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
                .users(new ArrayList<>())
                .build();
    }

    @Test
    public void shouldCreateSession() {
        when(sessionRepository.save(session)).thenReturn(session);
        Session createdSession = sessionService.create(session);
        verify(sessionRepository, times(1)).save(session);
        assertThat(createdSession).isNotNull();
        assertThat(createdSession).isEqualTo(session);
    }

    @Test
    public void shouldRetrieveAllSessions() {
        List<Session> sessions = new ArrayList<>();
        sessions.add(session);
        when(sessionRepository.findAll()).thenReturn(sessions);

        List<Session> returnedSessions = sessionService.findAll();
        verify(sessionRepository, times(1)).findAll();
        assertThat(returnedSessions).isNotNull();
        assertThat(returnedSessions).hasSize(1);
        assertThat(returnedSessions.get(0)).isEqualTo(session);
        assertThat(returnedSessions).isEqualTo(sessions);
    }gi
}
