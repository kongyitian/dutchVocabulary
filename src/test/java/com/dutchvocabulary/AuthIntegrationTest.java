package com.dutchvocabulary;

import com.dutchvocabulary.config.TestConfig;
import com.dutchvocabulary.dto.*;
import com.dutchvocabulary.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Authentication API endpoints.
 * Tests user registration, login, and token validation.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Authentication API Integration Tests")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private static String testUsername;
    private static String testPassword = "testPassword123";
    private static String authToken;

    @BeforeAll
    static void setUpClass() {
        testUsername = "authtest_" + System.currentTimeMillis();
    }

    @Test
    @Order(1)
    @DisplayName("POST /api/auth/register - Should register new user")
    void register_ShouldCreateUser() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(testUsername);
        request.setPassword(testPassword);
        request.setDisplayName("Auth Test User");

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.username", is(testUsername)))
                .andExpect(jsonPath("$.displayName", is("Auth Test User")))
                .andExpect(jsonPath("$.message", containsString("successful")))
                .andReturn();

        AuthResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), AuthResponse.class);
        authToken = response.getToken();

        // Verify user exists in database
        assertThat(userRepository.existsByUsername(testUsername)).isTrue();
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/auth/register - Should reject duplicate username")
    void register_DuplicateUsername_ShouldReturn400() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(testUsername); // Same username as previous test
        request.setPassword("anotherPassword");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("exists")));
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/auth/login - Should login existing user")
    void login_ValidCredentials_ShouldReturnToken() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername(testUsername);
        request.setPassword(testPassword);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.username", is(testUsername)))
                .andExpect(jsonPath("$.message", containsString("successful")));
    }

    @Test
    @Order(4)
    @DisplayName("POST /api/auth/login - Should reject invalid password")
    void login_InvalidPassword_ShouldReturn401() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername(testUsername);
        request.setPassword("wrongPassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", containsString("Invalid")));
    }

    @Test
    @Order(5)
    @DisplayName("POST /api/auth/login - Should reject non-existent user")
    void login_NonExistentUser_ShouldReturn401() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("nonexistent_user_xyz");
        request.setPassword("anyPassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(6)
    @DisplayName("GET /api/auth/me - Should return current user info")
    void getCurrentUser_WithValidToken_ShouldReturnUserInfo() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(testUsername)))
                .andExpect(jsonPath("$.displayName", is("Auth Test User")));
    }

    @Test
    @Order(7)
    @DisplayName("GET /api/auth/me - Should reject invalid token")
    void getCurrentUser_WithInvalidToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer invalid_token_xyz"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(8)
    @DisplayName("GET /api/auth/me - Should reject missing token")
    void getCurrentUser_WithoutToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(9)
    @DisplayName("Protected endpoint - Should work with valid token")
    void protectedEndpoint_WithValidToken_ShouldWork() throws Exception {
        mockMvc.perform(get("/api/words")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());
    }

    @Test
    @Order(10)
    @DisplayName("Full auth flow - Register, Login, Access Protected Resource")
    void fullAuthFlow_ShouldWork() throws Exception {
        String newUsername = "flowtest_" + System.currentTimeMillis();

        // 1. Register
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(newUsername);
        registerRequest.setPassword("flowPassword123");
        registerRequest.setDisplayName("Flow Test");

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // 2. Login with same credentials
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(newUsername);
        loginRequest.setPassword("flowPassword123");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse loginResponse = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(), AuthResponse.class);

        // 3. Access protected resource
        mockMvc.perform(get("/api/words")
                        .header("Authorization", "Bearer " + loginResponse.getToken()))
                .andExpect(status().isOk());

        // 4. Check user info
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + loginResponse.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(newUsername)));
    }
}

