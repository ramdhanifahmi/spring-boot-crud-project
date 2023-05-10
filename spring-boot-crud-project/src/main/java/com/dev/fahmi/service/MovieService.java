package com.dev.fahmi.service;

import com.dev.fahmi.domain.Movie;

import java.util.List;

public interface MovieService {
    List<Movie> getAllMovies();

    Movie getMovieById(Long id);

    Movie createMovie(Movie movie);

    Movie updateMovie(Long id, Movie movie);

    void deleteMovie(Long id);

    List<Movie> findMoviesByTitle(String keyword);
}
