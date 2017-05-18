/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.paulomatew.compilador.analyzer.lexical;

/**
 *
 * @author matt
 */
public class LexicalToken {

    public int type = -99;
    public String lexeme = null;
    public String description = null;
    public int line = 0;

    public void print() {
        System.out.println("TOKEN: " + type);
        System.out.println("LEXEMA: " + lexeme);
        System.out.println("DESCRIÇÃO: " + description);
        System.out.println("LINHA: " + line);
        System.out.println("--------------------------------");
    }

    public String getData() {
        String a = "";
        a += ("\nTOKEN: " + type);
        a += ("\nLEXEMA: " + lexeme);
        a += ("\nDESCRIÇÃO: " + description);
        a += ("\nLINHA: " + line);
        a += ("\n--------------------------------");

        return a;
    }
}
