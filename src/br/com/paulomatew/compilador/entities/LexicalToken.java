/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.paulomatew.compilador.entities;

/**
 *
 * @author matt
 */
public class LexicalToken extends Objeto{

    public int type = -99;
    public String lexeme = null;
    public String description = null;
    public int line = 0;
    public int position = 0;

    public LexicalToken() {
    }

    public LexicalToken(int type, String lexeme/*, String description*/) {
        this.type = type;
        this.lexeme = lexeme;
        //this.description = description;
    }

    public LexicalToken(int type, String lexeme, String description) {
        this.type = type;
        this.lexeme = lexeme;
        this.description = description;
    }

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
