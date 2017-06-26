package br.com.paulomatew.compilador.exceptions;

/**
 * Created by Paulo Mateus on 26/06/2017. For project Compiladores
 * (https://github.com/TroniPM/Compilador) Contact: <paulomatew@gmail.com>
 */
public class SemanticException extends Exception {

    public SemanticException() {
        // TODO Auto-generated constructor stub
    }

    public SemanticException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public SemanticException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public SemanticException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
}
