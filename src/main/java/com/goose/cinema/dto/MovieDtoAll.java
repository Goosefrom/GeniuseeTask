package com.goose.cinema.dto;

import com.goose.cinema.dto.OrderDto;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
public class MovieDtoAll {
    private Long id;
    private String name;
    private LocalDate releaseDate;
    private Integer cost;
    private List<OrderDto> orders;
}
