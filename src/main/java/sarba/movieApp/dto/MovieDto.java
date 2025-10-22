package sarba.movieApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieDto {
    private Integer movieId;
    private String title;
    private String director;
    private String studio;
    private String producer;
    private Set<String> movieCast;
    private Integer releaseYear;
    private String posterUrl;
}
