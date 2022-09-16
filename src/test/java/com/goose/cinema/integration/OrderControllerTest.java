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

class OrderControllerTest {
    private static final String ID = "0";

    private static final String MOVIE_ID = "1";
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
                .andExpect(jsonPath("$.id", is(Integer.valueOf(ID))));
    }

    @Test
    @Sql(value = "classpath:sql/movieCreate.sql", executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "classpath:sql/orderCreate.sql", executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void searchOrdersTest() throws Exception {
        String content = JSON_MAPPER.writeValueAsString(createOrdersInputDto());

        mockMvc.perform(post(HOME_URL + "/all?page=0&size=1").content(content)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", equalTo(Integer.valueOf(ID))));
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
                .andExpect(jsonPath("$.id", equalTo(Integer.valueOf(ID))));
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
        orderDto.setId(Long.valueOf(ID));
        orderDto.setMovieId(Long.valueOf(MOVIE_ID));
        orderDto.setOrderTime(LocalDate.parse(ORDER_TIME));
        orderDto.setParticipants(PARTICIPANTS);
        return orderDto;
    }

}
