package com.goose.cinema.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.goose.cinema.dto.MovieDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
@SpringBootTest
class MovieControllerTest {
    private static final String ID = "0";
    private static final String NAME = "testMovie1";
    private static final LocalDate RELEASE_DATE = LocalDate.of(2022, 9, 16);
    private static final Integer COST = 1;
    private static final String HOME_URL = "/api/movies";
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    static {
        JSON_MAPPER.registerModule(new JavaTimeModule());
    }


    @Autowired
    private MockMvc mockMvc;

    @Test
    void createTest() throws Exception {
        String content = JSON_MAPPER.writeValueAsString(createMovieDto());

        mockMvc.perform(post(HOME_URL).content(content)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo(NAME)));
    }

    @Test
    @Sql(value = "classpath:sql/movieCreate.sql", executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findByIdTest() throws Exception {
        mockMvc.perform(get(HOME_URL + "/" + ID).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(Integer.valueOf(ID))));
    }

    @Test
    @Sql(value = "classpath:sql/movieCreate.sql", executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void searchMoviesTest() throws Exception {
        String content = JSON_MAPPER.writeValueAsString(createMovieDto());

        mockMvc.perform(post(HOME_URL + "/all?page=0&size=1").content(content)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name", equalTo(NAME)));
    }

    @Test
    @Sql(value = "classpath:sql/movieCreate.sql", executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateTest() throws Exception {
        String content = JSON_MAPPER.writeValueAsString(createMovieDto());

        mockMvc.perform(patch(HOME_URL).content(content)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo(NAME)));
    }

    @Test
    @Sql(value = "classpath:sql/movieCreate.sql", executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deleteTest() throws Exception {
        mockMvc.perform(delete(HOME_URL + "/" + ID).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", equalTo("ok")));
    }

    private MovieDto createMovieDto() {
        MovieDto movieDto = new MovieDto();
        movieDto.setId(Long.valueOf(ID));
        movieDto.setName(NAME);
        movieDto.setReleaseDate(RELEASE_DATE);
        movieDto.setCost(COST);
        return movieDto;
    }

}
