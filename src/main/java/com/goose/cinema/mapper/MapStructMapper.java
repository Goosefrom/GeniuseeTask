package com.goose.cinema.mapper;

import com.goose.cinema.dto.MovieDtoAll;
import com.goose.cinema.dto.OrderDto;
import com.goose.cinema.persistance.Movie;
import com.goose.cinema.persistance.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MapStructMapper {
    OrderDto orderToOrderDto(Order order);

    MovieDtoAll movieToMovieDtoAll(Movie movie);
}
