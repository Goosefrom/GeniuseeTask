package com.goose.cinema.service;

import com.goose.cinema.dto.MovieDtoAll;
import com.goose.cinema.dto.MovieDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface MovieService {

    MovieDtoAll findById(Long id);

    Page<MovieDtoAll> searchAll(MovieDto movieDto, Pageable pageable);

    MovieDtoAll create(MovieDto movieDto);

    MovieDtoAll update(MovieDto movieDto);

    void delete(Long id);
}
