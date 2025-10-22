package sarba.movieApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sarba.movieApp.entities.Movie;

public interface MovieRepository extends JpaRepository<Movie,Integer> {

}
