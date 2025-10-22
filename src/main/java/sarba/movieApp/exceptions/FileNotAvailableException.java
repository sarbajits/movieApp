package sarba.movieApp.exceptions;

import java.io.FileNotFoundException;

public class FileNotAvailableException extends FileNotFoundException {
    public FileNotAvailableException(String message){
        super(message);
    }
}
