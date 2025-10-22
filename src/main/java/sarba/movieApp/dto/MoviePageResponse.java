package sarba.movieApp.dto;

import java.util.List;

public record MoviePageResponse(
        List<MovieDto> MovieDtos,
        Integer pageNumber,
        Integer pageSize,
        Long totalElements,
        Integer totalPages,
        boolean isLast
) {

}
