package sarba.movieApp.exceptions;

import jakarta.persistence.EntityNotFoundException;

public class MovieNotFoundException extends EntityNotFoundException {
    public MovieNotFoundException(String message){
        super(message);
    }
}
