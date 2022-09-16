package com.goose.cinema.service;

import com.goose.cinema.dto.MovieDto;
import com.goose.cinema.dto.MovieDtoAll;
import com.goose.cinema.exception.CinemaException;
import com.goose.cinema.exception.ErrorType;
import com.goose.cinema.mapper.MapStructMapper;
import com.goose.cinema.persistance.Movie;
import com.goose.cinema.persistance.Order;
import com.goose.cinema.repository.MovieRepository;
import com.goose.cinema.specification.MovieSpecification;
import com.goose.cinema.specification.OrderSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class MovieServiceI implements MovieService {

    private final MovieRepository moviesRepository;
    private final MapStructMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public MovieDtoAll findById(Long id) {
        Movie byId = moviesRepository.findById(id)
                .orElseThrow(() -> new CinemaException(ErrorType.NOT_FOUND, "Movie not found"));

        return mapper.movieToMovieDtoAll(byId);
    }

    @Override
    public Page<MovieDtoAll> searchAll(MovieDto movieDto, Pageable pageable) {
        Page<Movie> ordersPage = moviesRepository.findAll(MovieSpecification.movieSpecification(movieDto), pageable);

        return new PageImpl<>(ordersPage.get().map(mapper::movieToMovieDtoAll).toList());
    }

    @Override
    public MovieDtoAll create(MovieDto movieDto) {
        checkForDuplicate(movieDto.getName());
        if(Objects.isNull(movieDto.getName())
                || Objects.isNull(movieDto.getReleaseDate())
                || Objects.isNull(movieDto.getCost())) {
            throw new CinemaException(ErrorType.INTERNAL_ERROR, "Not enough information");
        }
        checkCost(movieDto.getCost());

        Movie newMovie = new Movie();
        newMovie.setName(movieDto.getName());
        newMovie.setCost(movieDto.getCost());
        newMovie.setReleaseDate(movieDto.getReleaseDate());
        Movie save = moviesRepository.save(newMovie);

        return mapper.movieToMovieDtoAll(save);
    }

    @Override
    public MovieDtoAll update(MovieDto movieDto) {
        Movie updateMovie = moviesRepository.findById(movieDto.getId())
                .orElseThrow(() -> new CinemaException(ErrorType.NOT_FOUND, "Movie not found"));

        if (Objects.nonNull(movieDto.getName()) && !movieDto.getName().equals(updateMovie.getName())) {
            checkForDuplicate(movieDto.getName());
            updateMovie.setName(movieDto.getName());
        }

        if (Objects.nonNull(movieDto.getCost())) {
            checkCost(movieDto.getCost());
            updateMovie.setCost(movieDto.getCost());
        }

        if (Objects.nonNull(movieDto.getReleaseDate())) updateMovie.setReleaseDate(movieDto.getReleaseDate());

        return mapper.movieToMovieDtoAll(updateMovie);
    }

    @Override
    public void delete(Long id) {
        if(!moviesRepository.existsById(id)) {
            throw new CinemaException(ErrorType.NOT_FOUND, "Movie doesn't exists");
        }
        moviesRepository.deleteById(id);

    }

    private void checkForDuplicate(String name) {
        if(moviesRepository.existsByName(name)) {
            throw new CinemaException(ErrorType.ALREADY_OCCUPIED, "This name is already exist");
        }
    }

    private void checkCost(int cost) {
        if(cost <= 0) {
            throw new CinemaException(ErrorType.INTERNAL_ERROR, "Cost should be greater than 0");
        }
    }

}
