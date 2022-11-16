package br.edu.ifpb.biblioteca.resource.exception;

import br.edu.ifpb.biblioteca.service.exception.AutorException;
import br.edu.ifpb.biblioteca.service.exception.LivroException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ResourceExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({AutorException.class, LivroException.class})
    public StandartError autorLivroNotFound(RuntimeException exception) {
        return new StandartError(HttpStatus.NOT_FOUND.value(), exception.getMessage(), System.currentTimeMillis());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ValidationError dataValidation(MethodArgumentNotValidException exception) {
        ValidationError error = new ValidationError(
                HttpStatus.BAD_REQUEST.value(), "Erro de validação", System.currentTimeMillis());
        for (FieldError fieldError : exception.getFieldErrors()) {
            error.addError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return error;
    }
}
