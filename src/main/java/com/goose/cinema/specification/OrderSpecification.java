package com.goose.cinema.specification;

import com.goose.cinema.dto.OrderDto;
import com.goose.cinema.persistance.Order;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderSpecification {

    public static Specification<Order> orderSpecification(OrderDto orderDto) {
        return Specification.where(equalMovieid(orderDto.getMovieId()))
                .and(equalOrderTime(orderDto.getOrderTime()))
                .and(equalParticipants(orderDto.getParticipants()));
    }

    public static Specification<Order> equalMovieid(Long movieId) {
        if (movieId == null) return null;

        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("movieId"), movieId);
    }

    public static Specification<Order> equalOrderTime(LocalDate orderTime) {
        if (orderTime == null) return null;

        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("orderTime"), orderTime);
    }

    public static Specification<Order> equalParticipants(Integer participans) {
        if (participans == null) return null;

        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("participants"), participans);
    }
}
