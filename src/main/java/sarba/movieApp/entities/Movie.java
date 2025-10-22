package sarba.movieApp.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer movieId;

    @Column(nullable = false,length = 200)
    private String title;

    @NotBlank(message = "please provide director")
    private String director;

    @NotBlank(message = "please provide studio")
    private String studio;

    @NotBlank(message = "please provide producer")
    private String producer;

    @ElementCollection
    @CollectionTable(name = "movie_cast")
    private Set<String> movieCast;

    private Integer releaseYear;

    @NotBlank(message = "please provide poster url")
    private String posterUrl;
}