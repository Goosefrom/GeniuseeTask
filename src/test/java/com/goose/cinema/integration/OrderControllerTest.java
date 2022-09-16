package com.goose.cinema.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.goose.cinema.dto.OrderDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Sql(value = "classpath:sql/movieCreate.sql", executionPhase =
        Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
@SpringBootTest

public class OrderControllerTest {
    private static final Long ID = 1L;

    private static final Long MOVIE_ID = 1L;
    private static final String ORDER_TIME = "2022-09-16";

    private static final Integer PARTICIPANTS = 3;
    private static final String HOME_URL = "/api/orders";
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    static {
        JSON_MAPPER.registerModule(new JavaTimeModule());
    }


    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql(value = "classpath:sql/movieCreate.sql", executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void createTest() throws Exception {
        String content = JSON_MAPPER.writeValueAsString(createOrdersInputDto());

        mockMvc.perform(post(HOME_URL).content(content)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderTime", equalTo(ORDER_TIME)));
    }

    @Test
    @Sql(value = "classpath:sql/movieCreate.sql", executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:sql/orderCreate.sql", executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findByIdTest() throws Exception {
        mockMvc.perform(get(HOME_URL + "/" + ID).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(ID)));
    }

    @Test
    @Sql(value = "classpath:sql/movieCreate.sql", executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:sql/orderCreate.sql", executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void searchOrderssTest() throws Exception {
        String content = JSON_MAPPER.writeValueAsString(createOrdersInputDto());

        mockMvc.perform(post(HOME_URL + "/find?page=0&size=1").content(content)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", equalTo(ID)));
    }

    @Test
    @Sql(value = "classpath:sql/movieCreate.sql", executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:sql/orderCreate.sql", executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateTest() throws Exception {
        String content = JSON_MAPPER.writeValueAsString(createOrdersInputDto());

        mockMvc.perform(patch(HOME_URL).content(content)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(ID)));
    }

    @Test
    @Sql(value = "classpath:sql/movieCreate.sql", executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:sql/orderCreate.sql", executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deleteTest() throws Exception {
        mockMvc.perform(delete(HOME_URL + "/" + ID).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", equalTo("ok")));
    }

    private OrderDto createOrdersInputDto() {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(ID);
        orderDto.setMovieId(MOVIE_ID);
        orderDto.setOrderTime(LocalDate.parse(ORDER_TIME));
        orderDto.setParticipants(PARTICIPANTS);
        return orderDto;
    }

}
