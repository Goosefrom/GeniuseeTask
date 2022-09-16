package com.goose.cinema.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class OrderDto implements Serializable {
    private Long id;
    private Long movieId;
    private LocalDate orderTime;
    private Integer participants;
}
