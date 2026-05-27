package com.dutchvocabulary;

import com.dutchvocabulary.config.TestConfig;
import com.dutchvocabulary.dto.*;
import com.dutchvocabulary.model.Difficulty;
import com.dutchvocabulary.model.VocabularyWord;
import com.dutchvocabulary.repository.VocabularyWordRepository;
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
 * Integration tests for Vocabulary API endpoints.
 * Tests the full stack: Controller -> Service -> Repository -> Database
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Vocabulary API Integration Tests")
class VocabularyIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VocabularyWordRepository wordRepository;

    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        // Register and login to get auth token
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser_" + System.currentTimeMillis());
        registerRequest.setPassword("password123");
        registerRequest.setDisplayName("Test User");

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), AuthResponse.class);
        authToken = authResponse.getToken();
    }

    @Test
    @Order(1)
    @DisplayName("GET /api/words - Should return all vocabulary words")
    void getAllWords_ShouldReturnWords() throws Exception {
        mockMvc.perform(get("/api/words")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].dutch", notNullValue()))
                .andExpect(jsonPath("$[0].english", notNullValue()));
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/words/{id} - Should return specific word")
    void getWordById_ShouldReturnWord() throws Exception {
        // Get first word from database
        VocabularyWord word = wordRepository.findAll().get(0);

        mockMvc.perform(get("/api/words/" + word.getId())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(word.getId().intValue())))
                .andExpect(jsonPath("$.dutch", is(word.getDutch())))
                .andExpect(jsonPath("$.english", is(word.getEnglish())));
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/words/{id} - Should return 404 for non-existent word")
    void getWordById_NotFound_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/words/99999")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    @DisplayName("GET /api/words/category/{category} - Should filter by category")
    void getWordsByCategory_ShouldReturnFilteredWords() throws Exception {
        mockMvc.perform(get("/api/words/category/greetings")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[*].category", everyItem(is("greetings"))));
    }

    @Test
    @Order(5)
    @DisplayName("GET /api/words/difficulty/{difficulty} - Should filter by difficulty")
    void getWordsByDifficulty_ShouldReturnFilteredWords() throws Exception {
        mockMvc.perform(get("/api/words/difficulty/EASY")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[*].difficulty", everyItem(is("EASY"))));
    }

    @Test
    @Order(6)
    @DisplayName("GET /api/words/categories - Should return all categories")
    void getAllCategories_ShouldReturnCategories() throws Exception {
        mockMvc.perform(get("/api/words/categories")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$", hasItem("greetings")))
                .andExpect(jsonPath("$", hasItem("verbs")));
    }

    @Test
    @Order(7)
    @DisplayName("GET /api/words/search - Should search words")
    void searchWords_ShouldReturnMatchingWords() throws Exception {
        mockMvc.perform(get("/api/words/search")
                        .param("q", "hallo")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].dutch", containsStringIgnoringCase("hallo")));
    }

    @Test
    @Order(8)
    @DisplayName("POST /api/words - Should create new word")
    void createWord_ShouldReturnCreatedWord() throws Exception {
        VocabularyWordDTO newWord = VocabularyWordDTO.builder()
                .dutch("test_woord")
                .english("test_word")
                .category("test")
                .difficulty(Difficulty.EASY)
                .example("Dit is een test woord.")
                .exampleTranslation("This is a test word.")
                .build();

        mockMvc.perform(post("/api/words")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newWord)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.dutch", is("test_woord")))
                .andExpect(jsonPath("$.english", is("test_word")));

        // Verify it was saved
        assertThat(wordRepository.searchWords("test_woord")).hasSize(1);
    }

    @Test
    @Order(9)
    @DisplayName("PUT /api/words/{id} - Should update existing word")
    void updateWord_ShouldReturnUpdatedWord() throws Exception {
        // Create a word first
        VocabularyWord word = wordRepository.save(VocabularyWord.builder()
                .dutch("update_test")
                .english("update_test")
                .category("test")
                .difficulty(Difficulty.EASY)
                .build());

        VocabularyWordDTO updateDto = VocabularyWordDTO.builder()
                .dutch("updated_dutch")
                .english("updated_english")
                .category("updated_category")
                .difficulty(Difficulty.HARD)
                .build();

        mockMvc.perform(put("/api/words/" + word.getId())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dutch", is("updated_dutch")))
                .andExpect(jsonPath("$.english", is("updated_english")))
                .andExpect(jsonPath("$.difficulty", is("HARD")));
    }

    @Test
    @Order(10)
    @DisplayName("DELETE /api/words/{id} - Should delete word")
    void deleteWord_ShouldReturn204() throws Exception {
        // Create a word to delete
        VocabularyWord word = wordRepository.save(VocabularyWord.builder()
                .dutch("delete_test")
                .english("delete_test")
                .category("test")
                .difficulty(Difficulty.EASY)
                .build());

        Long wordId = word.getId();

        mockMvc.perform(delete("/api/words/" + wordId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNoContent());

        // Verify it was deleted
        assertThat(wordRepository.findById(wordId)).isEmpty();
    }

    @Test
    @Order(11)
    @DisplayName("Unauthorized access to protected quiz endpoint should return 401 or 403")
    void unauthorizedAccess_ShouldReturn401Or403() throws Exception {
        // Note: /api/words is public, so we test with /api/quiz which requires auth
        // Spring Security may return 401 or 403 depending on configuration
        mockMvc.perform(get("/api/quiz"))
                .andExpect(status().is4xxClientError());
    }
}

