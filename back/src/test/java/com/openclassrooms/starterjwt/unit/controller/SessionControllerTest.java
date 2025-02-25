package com.openclassrooms.starterjwt.unit.controller;

import com.openclassrooms.starterjwt.controllers.SessionController;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SessionControllerTest {

    @Mock
    private SessionService sessionService;

    @Mock
    private SessionMapper sessionMapper;

    @InjectMocks
    private SessionController sessionController;

    private Session session;
    private SessionDto sessionDto;

    @BeforeEach
    public void setUp() {
        session = new Session();
        session.setId(1L);
        session.setName("Yoga Session");

        sessionDto = new SessionDto();
        sessionDto.setId(1L);
        sessionDto.setName("Yoga Session");
    }

    @Test
    public void shouldFindSessionById() {
        when(sessionService.getById(1L)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        ResponseEntity<?> response = sessionController.findById("1");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(sessionDto);

        verify(sessionService, times(1)).getById(1L);
        verify(sessionMapper, times(1)).toDto(session);
    }

    @Test
    public void shouldReturnNotFoundWhenSessionDoesNotExist() {
        when(sessionService.getById(1L)).thenReturn(null);

        ResponseEntity<?> response = sessionController.findById("1");

        assertThat(response.getStatusCodeValue()).isEqualTo(404);

        verify(sessionService, times(1)).getById(1L);
        verifyNoInteractions(sessionMapper);
    }

    @Test
    public void shouldReturnBadRequestForInvalidSessionId() {
        ResponseEntity<?> response = sessionController.findById("abc");

        assertThat(response.getStatusCodeValue()).isEqualTo(400);

        verifyNoInteractions(sessionService, sessionMapper);
    }

    @Test
    public void shouldReturnAllSessions() {
        when(sessionService.findAll()).thenReturn(List.of(session));
        when(sessionMapper.toDto(List.of(session))).thenReturn(List.of(sessionDto));

        ResponseEntity<?> response = sessionController.findAll();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(List.of(sessionDto));

        verify(sessionService, times(1)).findAll();
        verify(sessionMapper, times(1)).toDto(List.of(session));
    }

    @Test
    public void shouldReturnEmptyListIfNoSessionsExist() {
        when(sessionService.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = sessionController.findAll();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(Collections.emptyList());

        verify(sessionService, times(1)).findAll();
        verify(sessionMapper, times(1)).toDto(Collections.emptyList());
    }

    @Test
    public void shouldCreateSession() {
        when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
        when(sessionService.create(session)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        ResponseEntity<?> response = sessionController.create(sessionDto);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(sessionDto);

        verify(sessionService, times(1)).create(session);
        verify(sessionMapper, times(1)).toEntity(sessionDto);
        verify(sessionMapper, times(1)).toDto(session);
    }

    @Test
    public void shouldUpdateSession() {
        when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
        when(sessionService.update(1L, session)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        ResponseEntity<?> response = sessionController.update("1", sessionDto);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(sessionDto);

        verify(sessionService, times(1)).update(1L, session);
        verify(sessionMapper, times(1)).toEntity(sessionDto);
        verify(sessionMapper, times(1)).toDto(session);
    }

    @Test
    public void shouldReturnBadRequestWhenUpdatingWithInvalidId() {
        ResponseEntity<?> response = sessionController.update("invalid", sessionDto);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);

        verifyNoInteractions(sessionService, sessionMapper);
    }

    @Test
    public void shouldDeleteSession() {
        when(sessionService.getById(1L)).thenReturn(session);

        ResponseEntity<?> response = sessionController.save("1");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        verify(sessionService, times(1)).getById(1L);
        verify(sessionService, times(1)).delete(1L);
    }

    @Test
    public void shouldReturnNotFoundIfSessionToDeleteDoesNotExist() {
        when(sessionService.getById(1L)).thenReturn(null);

        ResponseEntity<?> response = sessionController.save("1");

        assertThat(response.getStatusCodeValue()).isEqualTo(404);

        verify(sessionService, times(1)).getById(1L);
        verify(sessionService, never()).delete(anyLong());
    }

    @Test
    public void shouldReturnBadRequestWhenDeletingWithInvalidId() {
        ResponseEntity<?> response = sessionController.save("invalid");

        assertThat(response.getStatusCodeValue()).isEqualTo(400);

        verifyNoInteractions(sessionService);
    }

    @Test
    public void shouldParticipateInSession() {
        ResponseEntity<?> response = sessionController.participate("1", "2");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        verify(sessionService, times(1)).participate(1L, 2L);
    }

    @Test
    public void shouldReturnBadRequestForInvalidParticipationIds() {
        ResponseEntity<?> response = sessionController.participate("abc", "xyz");

        assertThat(response.getStatusCodeValue()).isEqualTo(400);

        verifyNoInteractions(sessionService);
    }

    @Test
    public void shouldCancelParticipationInSession() {
        ResponseEntity<?> response = sessionController.noLongerParticipate("1", "2");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        verify(sessionService, times(1)).noLongerParticipate(1L, 2L);
    }

    @Test
    public void shouldReturnBadRequestWhenNoLongerParticipatingWithInvalidId() {
        ResponseEntity<?> response = sessionController.noLongerParticipate("invalid", "2");

        assertThat(response.getStatusCodeValue()).isEqualTo(400);

        verifyNoInteractions(sessionService);
    }
}
