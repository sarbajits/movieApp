package sarba.movieApp.service;

import org.springframework.web.multipart.MultipartFile;
import sarba.movieApp.dto.MovieDto;
import sarba.movieApp.dto.MoviePageResponse;

import java.io.IOException;
import java.util.List;

public interface MovieService {

    MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException;

    List<MovieDto> getAllMovies();

    MovieDto getMovieById(Integer id);

    boolean deleteMovieById(Integer id);

    MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize);
    MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize,String sortBy,String sortOrder);
}
