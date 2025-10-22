package sarba.movieApp.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sarba.movieApp.dto.MovieDto;
import sarba.movieApp.dto.MoviePageResponse;
import sarba.movieApp.service.MovieService;
import sarba.movieApp.utils.AppConstants;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movie/")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @PostMapping("/add")
    public ResponseEntity<MovieDto> addMovie(@RequestPart MultipartFile file, @RequestPart String movieDtoObj) throws IOException {

        MovieDto movieDto = convertToDto(movieDtoObj);

        return new ResponseEntity<>(movieService.addMovie(movieDto, file), HttpStatus.CREATED);
    }

    @PostMapping("/edit")
    public ResponseEntity<MovieDto> editMovie(@RequestPart MultipartFile file, @RequestPart String movieDtoObj) throws IOException {

        MovieDto movieDto = convertToDto(movieDtoObj);

        return new ResponseEntity<>(movieService.addMovie(movieDto, file), HttpStatus.CREATED);
    }

    private MovieDto convertToDto(String obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(obj, MovieDto.class);
    }

    @GetMapping("/{id}")
    public MovieDto getMovieById(@PathVariable Integer id) {
        return movieService.getMovieById(id);
    }

    @GetMapping("/delete/{id}")
    public boolean deleteMovieById(@PathVariable Integer id) {
        return movieService.deleteMovieById(id);
    }

    @GetMapping("/all-old")
    public List<MovieDto> getALlMovies() {
        return movieService.getAllMovies();
    }

    @GetMapping("/allByPage")
    public ResponseEntity<MoviePageResponse> getALlMoviesWithPagination(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize
    ) {
        return ResponseEntity.ok(movieService.getAllMoviesWithPagination(pageNumber,pageSize));
    }

    @GetMapping("/all")
    public ResponseEntity<MoviePageResponse> getALlMoviesWithPaginationAndSorting(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder
    ) {
        return ResponseEntity.ok(movieService.getAllMoviesWithPaginationAndSorting(pageNumber,pageSize,sortBy,sortOrder));
    }

}
