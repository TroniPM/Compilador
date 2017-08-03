/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.paulomatew.compilador.entities;

/**
 * Created by Paulo Mateus on 02/08/2017. For project Compiladores
 * (https://github.com/TroniPM/Compilador) Contact: <paulomatew@gmail.com>
 */
public class GotoLabel {

    public String label = null, scope = null;

    public void print() {
        System.out.println("LABEL: " + label);
        System.out.println("SCOPE: " + scope);
    }
}
