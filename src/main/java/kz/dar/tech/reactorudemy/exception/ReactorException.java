package kz.dar.tech.reactorudemy.exception;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class ReactorException extends Throwable {
    private Throwable exception;
    private String message;
}
