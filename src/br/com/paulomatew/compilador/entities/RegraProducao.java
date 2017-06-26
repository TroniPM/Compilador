package br.com.paulomatew.compilador.entities;

/**
 * Created by Paulo Mateus on 26/06/2017. For project Compiladores
 * (https://github.com/TroniPM/Compilador) Contact: <paulomatew@gmail.com>
 */
public class RegraProducao extends Objeto {

    public String method = null;
    public boolean dontPrintException = false;

    public RegraProducao(String method) {
        this.method = method;

    }

    public RegraProducao(String condicao, boolean dontPrintException) {
        this.method = method;
        this.dontPrintException = dontPrintException;
    }

    public void print() {
        System.out.println("-----------------");
        System.out.println("Regra: <" + method + ">");
        System.out.println("-----------------");
    }
}
