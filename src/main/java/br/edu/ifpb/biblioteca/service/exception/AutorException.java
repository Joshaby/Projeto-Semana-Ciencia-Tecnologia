package br.edu.ifpb.biblioteca.service.exception;

public class AutorException extends RuntimeException {

    public AutorException(String message) {
        super(message);
    }

    public AutorException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
