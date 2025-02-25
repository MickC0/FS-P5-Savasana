package com.openclassrooms.starterjwt.unit.controller;

import com.openclassrooms.starterjwt.controllers.TeacherController;
import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeacherControllerTest {

    @Mock
    private TeacherService teacherService;

    @Mock
    private TeacherMapper teacherMapper;

    @InjectMocks
    private TeacherController teacherController;

    private Teacher teacher;
    private TeacherDto teacherDto;

    @BeforeEach
    public void setUp() {
        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");

        teacherDto = new TeacherDto();
    }

    @Test
    public void shouldFindTeacherById() {
        when(teacherService.findById(1L)).thenReturn(teacher);
        when(teacherMapper.toDto(teacher)).thenReturn(teacherDto);

        ResponseEntity<?> response = teacherController.findById("1");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(teacherDto);

        verify(teacherService, times(1)).findById(1L);
        verify(teacherMapper, times(1)).toDto(teacher);
    }

    @Test
    public void shouldReturnNotFoundWhenTeacherDoesNotExist() {
        when(teacherService.findById(1L)).thenReturn(null);

        ResponseEntity<?> response = teacherController.findById("1");

        assertThat(response.getStatusCodeValue()).isEqualTo(404);

        verify(teacherService, times(1)).findById(1L);
        verifyNoInteractions(teacherMapper);
    }

    @Test
    public void shouldReturnBadRequestForInvalidTeacherId() {
        ResponseEntity<?> response = teacherController.findById("abc");

        assertThat(response.getStatusCodeValue()).isEqualTo(400);

        verifyNoInteractions(teacherService, teacherMapper);
    }

    @Test
    public void shouldReturnAllTeachers() {
        when(teacherService.findAll()).thenReturn(List.of(teacher));
        when(teacherMapper.toDto(List.of(teacher))).thenReturn(List.of(teacherDto));

        ResponseEntity<?> response = teacherController.findAll();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(List.of(teacherDto));

        verify(teacherService, times(1)).findAll();
        verify(teacherMapper, times(1)).toDto(List.of(teacher));
    }

    @Test
    public void shouldReturnEmptyListIfNoTeachersExist() {
        when(teacherService.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = teacherController.findAll();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(Collections.emptyList());

        verify(teacherService, times(1)).findAll();
        verify(teacherMapper, times(1)).toDto(Collections.emptyList());
    }
}
