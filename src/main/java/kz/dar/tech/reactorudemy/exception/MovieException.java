package kz.dar.tech.reactorudemy.exception;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class MovieException extends RuntimeException {
    String message;
}
