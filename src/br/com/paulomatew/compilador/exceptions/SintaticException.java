package br.com.paulomatew.compilador.exceptions;

/**
 * Created by Paulo Mateus on 26/06/2017. For project Compiladores
 * (https://github.com/TroniPM/Compilador) Contact: <paulomatew@gmail.com>
 */
public class SintaticException extends Exception {

    public SintaticException() {
        // TODO Auto-generated constructor stub
    }

    public SintaticException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public SintaticException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public SintaticException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
}
