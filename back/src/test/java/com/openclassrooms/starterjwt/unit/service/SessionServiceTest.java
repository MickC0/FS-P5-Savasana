package com.openclassrooms.starterjwt.unit.service;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
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
    private Teacher teacher;
    private LocalDateTime updateAtValue;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1L)
                .email("john.doe@yoga.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .createdAt(LocalDateTime.of(2025, 3, 3, 10, 0))
                .updatedAt(LocalDateTime.of(2025, 3, 3, 10, 30))
                .admin(false)
                .build();

        teacher = Teacher.builder()
                .id(1L)
                .firstName("Jolie")
                .lastName("Rose")
                .createdAt(LocalDateTime.of(2025, 3, 3, 10, 0))
                .updatedAt(LocalDateTime.of(2025, 3, 3, 10, 30))
                .build();

        session = Session.builder()
                .id(1L)
                .date(Date.from(Instant.parse("2025-03-03T10:00:00Z")))
                .name("Session")
                .teacher(teacher)
                .createdAt(LocalDateTime.of(2025, 3, 3, 10, 0))
                .updatedAt(LocalDateTime.of(2025, 3, 3, 10, 30))
                .users(new ArrayList<>())
                .build();

        updateAtValue =  LocalDateTime.of(2025, 3, 3, 11, 0);
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
    }

    @Test
    public void shouldRetrieveSessionById() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        Session returnedSession = sessionService.getById(1L);
        verify(sessionRepository, times(1)).findById(1L);
        assertThat(returnedSession).isNotNull();
        assertThat(returnedSession).isEqualTo(session);
    }

    @Test
    public void shouldDeleteSessionById() {
        doNothing().when(sessionRepository).deleteById(1L);
        sessionService.delete(1L);
        verify(sessionRepository, times(1)).deleteById(1L);
    }

    @Test
    public void shouldUpdateSession() {
        Session updateSession = Session.builder()
                .id(1L)
                .date(Date.from(Instant.parse("2025-03-03T10:00:00Z")))
                .name("Session maj")
                .teacher(teacher)
                .createdAt(LocalDateTime.of(2025, 3, 3, 10, 0))
                .updatedAt(updateAtValue)
                .users(new ArrayList<>())
                .build();
        when(sessionRepository.save(any(Session.class))).thenReturn(updateSession);
        Session updatedSession = sessionService.update(1L, updateSession);
        verify(sessionRepository, times(1)).save(updatedSession);
        assertThat(updatedSession).isNotNull();
        assertThat(updatedSession.getName()).isEqualTo("Session maj");
        assertThat(updatedSession.getUpdatedAt()).isEqualTo(updateAtValue);
    }

    @Test
    public void shouldAddUserToSession() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(sessionRepository.save(session)).thenReturn(session);
        sessionService.participate(1L,1L);
        verify(sessionRepository, times(1)).save(session);
        assertThat(session.getUsers()).contains(user);
    }

    @Test
    public void shouldThrowNotFoundWhenSessionOrUserNotFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> sessionService.participate(1L, 1L))
                .isInstanceOf(NotFoundException.class);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.participate(1L, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void shouldThrowBadRequestIfUserAlreadyParticipates() {
        session.getUsers().add(user);
        when(sessionRepository.findById(10L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> sessionService.participate(10L, 1L))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    public void shouldRemoveUserFromSession() {
        session.getUsers().add(user);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(sessionRepository.save(session)).thenReturn(session);
        assertThat(session.getUsers()).contains(user);
        sessionService.noLongerParticipate(1L, 1L);
        verify(sessionRepository, times(1)).findById(1L);
        assertThat(session.getUsers()).doesNotContain(user);
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    public void shouldThrowNotFoundIfSessionNotExistsForRemoval() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.noLongerParticipate(1L, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void shouldThrowBadRequestIfUserNotInSession() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        assertThatThrownBy(() -> sessionService.noLongerParticipate(1L, 1L))
                .isInstanceOf(BadRequestException.class);
    }
    @Test
    public void shouldRemoveOnlyTargetUserFromSession() {
        // 🔹 Ajoute plusieurs utilisateurs
        User otherUser = User.builder()
                .id(2L)
                .email("pasjohn.doe@yoga.com")
                .firstName("Pasjohn")
                .lastName("Doe")
                .password("password")
                .createdAt(LocalDateTime.of(2025, 3, 3, 10, 0))
                .updatedAt(LocalDateTime.of(2025, 3, 3, 10, 30))
                .admin(false)
                .build();
        session.getUsers().add(user);
        session.getUsers().add(otherUser);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(sessionRepository.save(session)).thenReturn(session);

        sessionService.noLongerParticipate(1L, 1L);

        assertThat(session.getUsers()).containsExactly(otherUser);

        verify(sessionRepository, times(1)).save(session);
    }

}
