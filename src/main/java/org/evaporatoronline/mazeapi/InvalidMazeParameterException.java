package org.evaporatoronline.mazeapi;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ResponseStatus;

@CrossOrigin(origins = {"https://www.evaporatoronline.org",
        "http://localhost:3000",
        "http://docker:808"})

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidMazeParameterException extends RuntimeException {
    public InvalidMazeParameterException(String message) {
        super(message);
    }
}
