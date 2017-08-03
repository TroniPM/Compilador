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
    public String parte4 = "";
    public String operacao4 = "";

    public String getData() {
        return (txt + "\t" + parte1 + " " + operacao1 + " " + parte2 + " " + operacao2 + " " + parte3 + " " + operacao3 + " " + parte4 + " " + operacao4);
    }

    public void print() {
        System.out.println(txt + "\t" + parte1 + " " + operacao1 + " " + parte2 + " " + operacao2 + " " + parte3 + " " + operacao3 + " " + parte4 + " " + operacao4);
    }

    public IntermediateCodeObject() {
    }

    public IntermediateCodeObject(String txt) {
        this.txt = txt;
    }

    public IntermediateCodeObject(String parte1, String operacao1) {
        this.parte1 = parte1;
        this.operacao1 = operacao1;
    }

}
