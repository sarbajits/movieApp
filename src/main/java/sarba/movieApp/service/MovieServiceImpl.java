package sarba.movieApp.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sarba.movieApp.dto.MovieDto;
import sarba.movieApp.dto.MoviePageResponse;
import sarba.movieApp.entities.Movie;
import sarba.movieApp.exceptions.MovieNotFoundException;
import sarba.movieApp.repositories.MovieRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    private FileService fileService;

    @Autowired
    private MovieRepository movieRepository;

    @Value("${project.fileLocation}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {
        Movie movie;

        // Upload the file and set the URL
        String uploadedFileName = fileService.uploadFile(path, file);
        String fileUrl = "/file/" + uploadedFileName;
        movieDto.setPosterUrl(fileUrl);

        // Check if this is an update operation

        Integer id = movieDto.getMovieId();
        if (id != null) {
            movie = movieRepository.findById(id)
                    .map(existingMovie -> {
                        existingMovie.setTitle(movieDto.getTitle());
                        existingMovie.setDirector(movieDto.getDirector());
                        existingMovie.setStudio(movieDto.getStudio());
                        existingMovie.setProducer(movieDto.getProducer());
                        existingMovie.setMovieCast(movieDto.getMovieCast());
                        existingMovie.setReleaseYear(movieDto.getReleaseYear());
                        existingMovie.setPosterUrl(movieDto.getPosterUrl());
                        return existingMovie;
                    })
                    .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + id));
        } else {
            movie = new Movie(
                    null,
                    movieDto.getTitle(),
                    movieDto.getDirector(),
                    movieDto.getStudio(),
                    movieDto.getProducer(),
                    movieDto.getMovieCast(),
                    movieDto.getReleaseYear(),
                    movieDto.getPosterUrl()
            );
        }

        // Save either a new or updated entity
        Movie savedMovie = movieRepository.save(movie);

        return new MovieDto(
                savedMovie.getMovieId(),
                savedMovie.getTitle(),
                savedMovie.getDirector(),
                savedMovie.getStudio(),
                savedMovie.getProducer(),
                savedMovie.getMovieCast(),
                savedMovie.getReleaseYear(),
                savedMovie.getPosterUrl()
        );
    }

    @Override
    public MovieDto getMovieById(Integer id) {

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + id));

        return new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getProducer(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPosterUrl()
        );
    }

    @Override
    public boolean deleteMovieById(Integer id) {
        if (movieRepository.existsById(id)) {
            movieRepository.deleteById(id);
            return true; // deletion successful
        } else {
            throw new MovieNotFoundException("Movie with ID " + id + " not found");
        }
    }

    @Override
    public List<MovieDto> getAllMovies() {
        return movieRepository.findAll()
                .stream()
                .map(movie -> new MovieDto(
                        movie.getMovieId(),
                        movie.getTitle(),
                        movie.getDirector(),
                        movie.getStudio(),
                        movie.getProducer(),
                        movie.getMovieCast(),
                        movie.getReleaseYear(),
                        movie.getPosterUrl()
                ))
                .collect(Collectors.toList());
    }

    public MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber-1, pageSize);

        Page<Movie> moviePages = movieRepository.findAll(pageable);
        List<Movie> movies = moviePages.getContent();

        List<MovieDto> movieDtos = movies.stream()
                .map(movie -> new MovieDto(
                        movie.getMovieId(),
                        movie.getTitle(),
                        movie.getDirector(),
                        movie.getStudio(),
                        movie.getProducer(),
                        movie.getMovieCast(),
                        movie.getReleaseYear(),
                        movie.getPosterUrl()
                ))
                .collect(Collectors.toList());

        return new MoviePageResponse(movieDtos, moviePages.getNumber()+1, moviePages.getSize(), moviePages.getTotalElements(), moviePages.getTotalPages(), moviePages.isLast());
    }


    @Override
    public MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber-1, pageSize, sort);

        Page<Movie> moviePages = movieRepository.findAll(pageable);
        List<Movie> movies = moviePages.getContent();

        List<MovieDto> movieDtos = movies.stream()
                .map(movie -> new MovieDto(
                        movie.getMovieId(),
                        movie.getTitle(),
                        movie.getDirector(),
                        movie.getStudio(),
                        movie.getProducer(),
                        movie.getMovieCast(),
                        movie.getReleaseYear(),
                        movie.getPosterUrl()
                ))
                .collect(Collectors.toList());

        return new MoviePageResponse(movieDtos, moviePages.getNumber()+1, moviePages.getSize(), moviePages.getTotalElements(), moviePages.getTotalPages(), moviePages.isLast());
    }

}
