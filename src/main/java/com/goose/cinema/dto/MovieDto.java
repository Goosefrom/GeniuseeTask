package com.goose.cinema.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MovieDto {
    private Long id;
    private String name;
    private LocalDate releaseDate;
    private Integer cost;
}
