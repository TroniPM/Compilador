package br.com.paulomatew.compilador.entities;

/**
 * Created by Paulo Mateus on 26/06/2017. For project Compiladores
 * (https://github.com/TroniPM/Compilador) Contact: <paulomatew@gmail.com>
 */
public class Escopo {

    public Escopo pai = null;
    public String label = null;

    public Escopo() {
    }

    public Escopo(String label) {
        this.label = label;
    }
}
