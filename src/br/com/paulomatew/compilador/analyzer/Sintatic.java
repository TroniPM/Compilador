/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.paulomatew.compilador.analyzer;

import br.com.paulomatew.compilador.entities.LexicalObject;
import br.com.paulomatew.compilador.exceptions.SintaticException;
import br.com.paulomatew.compilador.main.Compilador;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author matt
 */
public class Sintatic {

    public ArrayList<LexicalObject> tokenListFromLexical = null;

    public void init(ArrayList<LexicalObject> arr) throws SintaticException {
        if (arr == null || arr.isEmpty()) {
            throw new SintaticException("Nenhum token encontrado.");
        }

        this.tokenListFromLexical = arr;

        parser();
    }

    private void parser() throws SintaticException {
        //System.out.println("ENTROU NO PARSER DO SINTATICO");
        for (int i = 0; i < tokenListFromLexical.size(); i++) {
            LexicalObject lexObj = tokenListFromLexical.get(i);
            if (i == 0) {
                if (lexObj.type != 3) {
                    throw new SintaticException("Program starts with unexpected token '" + lexObj.lexeme + "' line " + lexObj.linha);
                }
            }

            //main
            if (lexObj.type == 3) {
                if (tokenListFromLexical.get(i + 1) != null) {
                    //System.out.println(tokenListFromLexical.get(i + 1).lexeme + "\t" + tokenListFromLexical.get(i + 1).type);
                    if (tokenListFromLexical.get(i + 1).type == 4) {
                        System.out.println("É UM PARENTESE");
                    } else {
                        throw new SintaticException("Unexpected token after '" + lexObj.lexeme + "' line " + lexObj.linha);
                    }
                } else {
                    throw new SintaticException("Unexpected EOF after '" + Compilador.getToken(lexObj.type) + "' line " + lexObj.linha);
                }
            }
        }
    }
}
