package com.goose.cinema.specification;

import com.goose.cinema.dto.MovieDto;
import com.goose.cinema.persistance.Movie;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MovieSpecification {

    public static Specification<Movie> movieSpecification(MovieDto movieDto) {
        return Specification.where(likeName(movieDto.getName()))
                .and(equalCost(movieDto.getCost()))
                .and(equalReleaseDate(movieDto.getReleaseDate()));
    }

    private static Specification<Movie> likeName(String name) {
        if (name == null) return null;

        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    private static Specification<Movie> equalCost(Integer cost) {
        if (cost == null) return null;

        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("cost"), cost);
    }

    private static Specification<Movie> equalReleaseDate(LocalDate releaseDate) {
        if (releaseDate == null) return null;

        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("releaseDate"), releaseDate);
    }
}
