package com.openclassrooms.starterjwt.unit.service;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    private Teacher teacher;

    @BeforeEach
    public void setUp() {
        teacher = Teacher.builder()
                .id(1L)
                .firstName("Jolie")
                .lastName("Rose")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

    }

    @Test
    public void shouldRetrieveAllTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        teachers.add(teacher);
        when(teacherRepository.findAll()).thenReturn(teachers);
        List<Teacher> returnedTeachers = teacherService.findAll();
        verify(teacherRepository, times(1)).findAll();
        assertThat(returnedTeachers).isNotNull();
        assertThat(returnedTeachers).hasSize(1);
        assertThat(returnedTeachers).isEqualTo(teachers);
    }

    @Test
    public void shouldRetrieveTeacherById() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        Teacher returnedTeacher = teacherService.findById(1L);
        verify(teacherRepository, times(1)).findById(1L);
        assertThat(returnedTeacher).isNotNull();
        assertThat(returnedTeacher).isEqualTo(teacher);
    }
}
