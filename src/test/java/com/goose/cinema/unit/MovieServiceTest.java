package com.goose.cinema.unit;

import com.goose.cinema.dto.MovieDto;
import com.goose.cinema.dto.MovieDtoAll;
import com.goose.cinema.exception.CinemaException;
import com.goose.cinema.exception.ErrorType;
import com.goose.cinema.persistance.Movie;
import com.goose.cinema.repository.MovieRepository;
import com.goose.cinema.service.MovieService;
import com.goose.cinema.service.MovieServiceI;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MovieServiceTest {

    @Autowired
    private MovieService movieService;

    @MockBean
    private MovieRepository movieRepository;

    @Test
    public void createTest() {
        Movie movie = createMovie();
        MovieDto movieDto = createMovieDto(movie);

        when(movieRepository.save((any(Movie.class)))).thenReturn(movie);

        MovieDtoAll result = movieService.create(movieDto);

        verify(movieRepository).save(any(Movie.class));
        assertThat(result.getName()).isEqualTo(movie.getName());
    }

    @Test
    public void createIfFieldsNullTest() {
        MovieDto movie = new MovieDto();
        movie.setName("some movie");

        assertThatThrownBy(() -> movieService.create(movie))
                .isInstanceOf(CinemaException.class)
                .matches((error) -> ((CinemaException) error).getErrorType() == ErrorType.INTERNAL_ERROR);
    }

    @Test
    public void findByIdTest() {
        Movie movie = createMovie();

        when(movieRepository.findById(movie.getId())).thenReturn(Optional.of(movie));

        MovieDtoAll result = movieService.findById(movie.getId());

        verify(movieRepository).findById(movie.getId());
        assertThat(result.getId()).isEqualTo(movie.getId());
        assertThat(result.getName()).isEqualTo(movie.getName());
    }

    @Test
    public void searchMoviesTest() {
        Movie movie = createMovie();
        MovieDto movieInputDto = createMovieDto(movie);
        PageRequest pageRequest = PageRequest.of(0, 1);

        when(movieRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(movie)));

        Page<MovieDtoAll> page = movieService.searchAll(movieInputDto, pageRequest);

        verify(movieRepository).findAll(any(Specification.class), any(Pageable.class));

        Optional<MovieDtoAll> first = page.get().findFirst();

        assertThat(first).isPresent();
        assertThat(first.get().getName()).isEqualTo(movie.getName());
    }

    @Test
    public void updateTest() {
        Movie movie = createMovie();
        MovieDto movieDto = createMovieDto(movie);
        Movie returnMovie = new Movie();

        returnMovie.setId(movie.getId());
        returnMovie.setName("some movie");
        returnMovie.setReleaseDate(LocalDate.now().minusDays(1));
        returnMovie.setCost(111);

        when(movieRepository.findById(movieDto.getId())).thenReturn(Optional.of(returnMovie));
        when(movieRepository.save((any(Movie.class)))).thenReturn(returnMovie);

        MovieDtoAll result = movieService.update(movieDto);

        verify(movieRepository).findById(movieDto.getId());
        assertThat(result.getId()).isEqualTo(movie.getId());
        assertThat(result.getName()).isEqualTo(movie.getName());
        assertThat(result.getReleaseDate()).isEqualTo(movie.getReleaseDate());
        assertThat(result.getCost()).isEqualTo(movie.getCost());
    }

    @Test
    public void updateIfEmptyTest() {
        Movie movie = createMovie();
        MovieDto movieDto = createMovieDto(movie);

        when(movieRepository.findById(movieDto.getId())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> movieService.update(movieDto))
                .isInstanceOf(CinemaException.class)
                .matches((error) -> ((CinemaException) error).getErrorType() == ErrorType.NOT_FOUND);
    }

    @Test
    public void updateIfNameDuplicateTest() {
        Movie movie = createMovie();
        MovieDto movieDto = createMovieDto(movie);
        movieDto.setName("test");

        when(movieRepository.findById(movieDto.getId())).thenReturn(Optional.of(movie));
        when(movieRepository.existsByName(movieDto.getName())).thenReturn(true);
        assertThatThrownBy(() -> movieService.update(movieDto))
                .isInstanceOf(CinemaException.class)
                .matches((error) -> ((CinemaException) error).getErrorType() == ErrorType.ALREADY_OCCUPIED);
    }

    @Test
    public void updateIfNameDifferentTest() {
        Movie movie = createMovie();
        MovieDto movieDto = createMovieDto(movie);
        movieDto.setName("test");

        when(movieRepository.findById(movieDto.getId())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> movieService.update(movieDto))
                .isInstanceOf(CinemaException.class)
                .matches((error) -> ((CinemaException) error).getErrorType() == ErrorType.NOT_FOUND);
    }

    @Test
    public void updateIfCostLessThanNullTest() {
        Movie movie = createMovie();
        MovieDto movieDto = createMovieDto(movie);
        movieDto.setCost(0);

        when(movieRepository.findById(movieDto.getId())).thenReturn(Optional.of(movie));
        assertThatThrownBy(() -> movieService.update(movieDto))
                .isInstanceOf(CinemaException.class)
                .matches((error) -> ((CinemaException) error).getErrorType() == ErrorType.INTERNAL_ERROR);
    }

    @Test
    public void deleteTest() {
        Movie movie = createMovie();

        when(movieRepository.existsById(movie.getId())).thenReturn(true);

        movieService.delete(movie.getId());

        verify(movieRepository).deleteById(movie.getId());
    }

    @Test
    public void deleteIfEmptyTest() {
        Movie movie = createMovie();

        when(movieRepository.findById(movie.getId())).thenReturn(Optional.empty());

        Long id = movie.getId();

        assertThatThrownBy(() -> movieService.delete(id))
                .isInstanceOf(CinemaException.class)
                .matches((error) -> ((CinemaException) error).getErrorType() == ErrorType.NOT_FOUND);
    }

    private Movie createMovie() {
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setName("name");
        movie.setReleaseDate(LocalDate.now());
        movie.setCost(5);
        return movie;
    }

    private MovieDto createMovieDto(Movie movie) {
        MovieDto result = new MovieDto();
        result.setId(movie.getId());
        result.setName(movie.getName());
        result.setReleaseDate(movie.getReleaseDate());
        result.setCost(movie.getCost());
        return result;
    }

}
