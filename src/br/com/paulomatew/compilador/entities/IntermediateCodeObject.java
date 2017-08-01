package br.com.paulomatew.compilador.entities;

/**
 * Created by Paulo Mateus on 31/07/2017. For project Compiladores
 * (https://github.com/TroniPM/Compilador) Contact: <paulomatew@gmail.com>
 */
public class IntermediateCodeObject {

    //public int label = 0;
    public String txt = "";

    public String parte1 = "";
    public String operacao1 = "";
    public String parte2 = "";
    public String operacao2 = "";
    public String parte3 = "";
    public String operacao3 = "";

    public String getData() {
        return (parte1 + " " + operacao1 + " " + parte2 + " " + operacao2 + " " + parte3 + " " + operacao3);
    }

    public void print() {
        System.out.println(parte1 + " " + operacao1 + " " + parte2 + " " + operacao2 + " " + parte3 + " " + operacao3);
    }

    public IntermediateCodeObject() {
    }
}
