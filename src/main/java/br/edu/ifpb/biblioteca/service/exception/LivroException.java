package br.edu.ifpb.biblioteca.service.exception;

public class LivroException extends RuntimeException {

    public LivroException(String message) {
        super(message);
    }

    public LivroException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
