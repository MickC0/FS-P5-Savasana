package com.openclassrooms.starterjwt.integration;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class SessionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SessionRepository sessionRepository;

    private Session testSession;
    private String token;

    @BeforeEach
    void setup() throws Exception {
        testSession = new Session();
        testSession.setName("Yoga Session");
        testSession.setDescription("A relaxing yoga session");
        testSession.setDate(new Date());
        testSession.setUsers(new ArrayList<>());
        testSession = sessionRepository.save(testSession);
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
    void testFindById_ShouldReturnSession_WhenSessionExists() throws Exception {
        mockMvc.perform(get("/api/session/" + testSession.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yoga Session"))
                .andExpect(jsonPath("$.description").value("A relaxing yoga session"));
    }

    @Test
    void testFindById_ShouldReturnNotFound_WhenSessionDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/session/9999")
                        .header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFindById_ShouldReturnBadRequest_WhenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/session/invalid")
                        .header("Authorization", token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFindAll_ShouldReturnListOfSessions() throws Exception {
        mockMvc.perform(get("/api/session")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").isNumber());
    }

    @Test
    void testCreateSession_ShouldReturnCreatedSession() throws Exception {
        SessionDto newSession = new SessionDto();
        newSession.setName("Meditation Session");
        newSession.setDescription("A guided meditation session");
        newSession.setDate(new Date());
        newSession.setTeacher_id(1L);

        mockMvc.perform(post("/api/session")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSession)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Meditation Session"))
                .andExpect(jsonPath("$.description").value("A guided meditation session"));
    }

    @Test
    void testUpdateSession_ShouldReturnUpdatedSession() throws Exception {
        SessionDto updatedSession = new SessionDto();
        updatedSession.setName("Updated Yoga Session");
        updatedSession.setDescription("An updated description");
        updatedSession.setDate(new Date());
        updatedSession.setTeacher_id(1L);

        mockMvc.perform(put("/api/session/" + testSession.getId())
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSession)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Yoga Session"))
                .andExpect(jsonPath("$.description").value("An updated description"));
    }

    @Test
    void testDeleteSession_ShouldRemoveSession() throws Exception {
        mockMvc.perform(delete("/api/session/" + testSession.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk());

        Optional<Session> deletedSession = sessionRepository.findById(testSession.getId());
        assertThat(deletedSession).isEmpty();
    }

    @Test
    void testParticipate_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/api/session/" + testSession.getId() + "/participate/1")
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    void testNoLongerParticipate_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/api/session/" + testSession.getId() + "/participate/1")
                        .header("Authorization", token))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/session/" + testSession.getId() + "/participate/1")
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateSession_ShouldReturnBadRequest_WhenIdIsInvalid() throws Exception {
        SessionDto updatedSession = new SessionDto();
        updatedSession.setName("Updated Yoga Session");
        updatedSession.setDescription("An updated description");
        updatedSession.setDate(new Date());
        updatedSession.setTeacher_id(1L);

        mockMvc.perform(put("/api/session/invalid")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSession)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteSession_ShouldReturnBadRequest_WhenIdIsInvalid() throws Exception {
        mockMvc.perform(delete("/api/session/invalid")
                        .header("Authorization", token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteSession_ShouldReturnNotFound_WhenSessionDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/session/9999")
                        .header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    @Test
    void testParticipate_ShouldReturnBadRequest_WhenIdIsInvalid() throws Exception {
        mockMvc.perform(post("/api/session/invalid/participate/1")
                        .header("Authorization", token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testParticipate_ShouldReturnBadRequest_WhenUserIdIsInvalid() throws Exception {
        mockMvc.perform(post("/api/session/" + testSession.getId() + "/participate/invalid")
                        .header("Authorization", token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testNoLongerParticipate_ShouldReturnBadRequest_WhenIdIsInvalid() throws Exception {
        mockMvc.perform(delete("/api/session/invalid/participate/1")
                        .header("Authorization", token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testNoLongerParticipate_ShouldReturnBadRequest_WhenUserIdIsInvalid() throws Exception {
        mockMvc.perform(delete("/api/session/" + testSession.getId() + "/participate/invalid")
                        .header("Authorization", token))
                .andExpect(status().isBadRequest());
    }


}
