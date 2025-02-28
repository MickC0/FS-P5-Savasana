package com.openclassrooms.starterjwt.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class TeacherControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TeacherRepository teacherRepository;

    private Teacher testTeacher;
    private String token;

    @BeforeEach
    void setup() throws Exception {
        testTeacher = new Teacher();
        testTeacher.setFirstName("John");
        testTeacher.setLastName("Doe");
        testTeacher = teacherRepository.save(testTeacher);
        token = authenticateAndGetToken();
    }

    private String authenticateAndGetToken() throws Exception {
        String loginPayload = "{ \"email\": \"yoga@studio.com\", \"password\": \"test!1234\" }";

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseJson).get("token").asText();

        return "Bearer " + token;
    }

    @Test
    void testFindById_ShouldReturnTeacher_WhenTeacherExists() throws Exception {
        mockMvc.perform(get("/api/teacher/" + testTeacher.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }


    @Test
    void testFindById_ShouldReturnNotFound_WhenTeacherDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/teacher/9999")
                        .header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFindById_ShouldReturnBadRequest_WhenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/teacher/invalid")
                        .header("Authorization", token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFindAll_ShouldReturnListOfTeachers() throws Exception {
        mockMvc.perform(get("/api/teacher")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").isNumber());
    }

}
