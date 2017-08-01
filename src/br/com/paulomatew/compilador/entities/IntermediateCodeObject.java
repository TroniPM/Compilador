package br.com.paulomatew.compilador.entities;

/**
 * Created by Paulo Mateus on 31/07/2017. For project Compiladores
 * (https://github.com/TroniPM/Compilador) Contact: <paulomatew@gmail.com>
 */
public class IntermediateCodeObject {

    public int label = 0;
    public String txt = "";

    public String getData() {
        return String.valueOf(label) + ": " + txt;
    }

    public void print() {
        System.out.println(String.valueOf(label) + ": " + txt);
    }

    public IntermediateCodeObject() {
    }

}
