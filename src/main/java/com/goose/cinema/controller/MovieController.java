package com.goose.cinema.controller;

import com.goose.cinema.dto.MovieDto;
import com.goose.cinema.dto.MovieDtoAll;
import com.goose.cinema.dto.OrderDto;
import com.goose.cinema.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movies")

public class MovieController {
    private final MovieService movieService;

    @GetMapping("/{id}")
    public MovieDtoAll findById(@PathVariable Long id) {return movieService.findById(id);}

    @PostMapping("/all")
    public Page<MovieDtoAll> findAll(@RequestBody(required = false) MovieDto movieDto,
                                     @RequestParam("page") Integer page,
                                     @RequestParam("size") Integer size) {
        return movieService.searchAll(movieDto, PageRequest.of(page, size));
    }

    @PostMapping
    public MovieDtoAll create(@RequestBody MovieDto movieDto) {return movieService.create(movieDto);}

    @PatchMapping
    public MovieDtoAll update(@RequestBody MovieDto movieDto) {return movieService.update(movieDto);}

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        movieService.delete(id);
        return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body("ok");
    }
}
