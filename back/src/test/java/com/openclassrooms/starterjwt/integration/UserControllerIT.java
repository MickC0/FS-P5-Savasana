package com.openclassrooms.starterjwt.integration;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String token;
    private User testUser;
    private User anotherUser;

    @BeforeEach
    void setup() throws Exception {
        testUser = new User();
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@yoga.com");
        testUser.setPassword(passwordEncoder.encode("test!1234"));
        testUser = userRepository.save(testUser);

        anotherUser = new User();
        anotherUser.setFirstName("Jane");
        anotherUser.setLastName("Smith");
        anotherUser.setEmail("jane.smith@yoga.com");
        anotherUser.setPassword(passwordEncoder.encode("test!1234"));
        anotherUser = userRepository.save(anotherUser);

        token = authenticateAndGetToken();
    }

    private String authenticateAndGetToken() throws Exception {
        String loginPayload = "{ \"email\": \"john.doe@yoga.com\", \"password\": \"test!1234\" }";

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
    void testFindById_ShouldReturnUser_WhenUserExists() throws Exception {
        mockMvc.perform(get("/api/user/" + testUser.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@yoga.com"));
    }

    @Test
    void testFindById_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/user/9999")
                        .header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFindById_ShouldReturnBadRequest_WhenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/user/invalid")
                        .header("Authorization", token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteUser_ShouldReturnOk_WhenUserDeletesHimself() throws Exception {
        mockMvc.perform(delete("/api/user/" + testUser.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk());

        Optional<User> deletedUser = userRepository.findById(testUser.getId());
        assertThat(deletedUser).isEmpty();
    }

    @Test
    void testDeleteUser_ShouldReturnUnauthorized_WhenUserTriesToDeleteAnotherUser() throws Exception {
        mockMvc.perform(delete("/api/user/" + anotherUser.getId())
                        .header("Authorization", token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeleteUser_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/user/9999")
                        .header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUser_ShouldReturnBadRequest_WhenIdIsInvalid() throws Exception {
        mockMvc.perform(delete("/api/user/invalid")
                        .header("Authorization", token))
                .andExpect(status().isBadRequest());
    }
}
