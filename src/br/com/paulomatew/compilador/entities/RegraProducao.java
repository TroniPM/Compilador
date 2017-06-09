/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.paulomatew.compilador.entities;

import br.com.paulomatew.compilador.entities.LexicalToken;

/**
 *
 * @author Mateus
 */
public class RegraProducao extends Objeto {

    public String method = null;

    public RegraProducao(String method) {
        this.method = method;

    }

}
