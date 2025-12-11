package br.com.instadata.accessmanager.exception;

/**
 * Exceção lançada quando há um conflito de dados, como duplicação de registros.
 * Resulta em resposta HTTP 409 (Conflict).
 */
public class ConflictException extends RuntimeException {
    
    public ConflictException(String message) {
        super(message);
    }
    
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
