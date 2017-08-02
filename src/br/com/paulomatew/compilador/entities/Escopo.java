package br.com.paulomatew.compilador.entities;

import java.util.ArrayList;

/**
 * Created by Paulo Mateus on 26/06/2017. For project Compiladores
 * (https://github.com/TroniPM/Compilador) Contact: <paulomatew@gmail.com>
 */
public class Escopo {

    public Escopo pai = null;
    public String label = null;

    public ArrayList<Escopo> filhos = new ArrayList<>();

    public Escopo() {
    }

    public Escopo(String label) {
        this.label = label;
    }

    public void print() {
        System.out.println("Escopo: " + label);
        System.out.println("Pai: " + (pai == null ? "null" : pai.label));
        System.out.println("Filho(s): ");
        for (Escopo in : filhos) {
            System.out.println("\t" + in.label);
        }
        System.out.println("-------------------------------------------------");
    }

    public String getData() {
        String a = "";
        a = ("Escopo: " + label) + "\n";
        a += ("Pai: " + (pai == null ? "null" : pai.label)) + "\n";
        a += ("Filho(s): ") + "\n";
        for (Escopo in : filhos) {
            a += ("\t" + in.label) + "\n";
        }
        a += ("-------------------------------------------------" + "\n");

        return a;
    }
}
