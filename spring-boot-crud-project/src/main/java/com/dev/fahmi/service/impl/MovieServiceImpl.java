package com.dev.fahmi.service.impl;

import com.dev.fahmi.domain.Movie;
import com.dev.fahmi.repository.MovieRepository;
import com.dev.fahmi.service.MovieService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class MovieServiceImpl implements MovieService {
    @Autowired
    private MovieRepository movieRepository;

    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Movie not found with id: " + id));
    }

    @Override
    public Movie createMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    public Movie updateMovie(Long id, Movie movie) {
        Movie existingMovie = getMovieById(id);
        existingMovie.setTitle(movie.getTitle());
        existingMovie.setDescription(movie.getDescription());
        existingMovie.setRating(movie.getRating());
        return movieRepository.save(existingMovie);
    }

    @Override
    public void deleteMovie(Long id) {
        getMovieById(id);
        movieRepository.deleteById(id);
    }

    @Override
    public List<Movie> findMoviesByTitle(String keyword) {
        if (StringUtils.isEmpty(keyword)) {
            throw new IllegalArgumentException("Keyword cannot be empty or null");
        }
        return movieRepository.findByTitleContaining(keyword);
    }
}
