/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.paulomatew.compilador.entities;

import java.util.ArrayList;

/**
 *
 * @author Mateus
 */
public class Escopo {

    public Escopo pai = null;
    //public ArrayList<Escopo> filhos = new ArrayList<>();
    public String label = null;

    public Escopo() {
    }

    public Escopo(String label) {
        this.label = label;
    }
}
