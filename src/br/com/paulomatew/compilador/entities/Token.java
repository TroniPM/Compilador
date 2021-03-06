package br.com.paulomatew.compilador.entities;

/**
 * Created by Paulo Mateus on 26/06/2017. For project Compiladores
 * (https://github.com/TroniPM/Compilador) Contact: <paulomatew@gmail.com>
 */
public class Token extends Objeto {

    public int type = -99;
    public String lexeme = null;
    public String description1 = null;
    public String description2 = null;
    public String regra = null;
    public int line = 0;
    public int position = 0;
    public String scope = null;
    public String other = null;

    public boolean wasMapped = false;

    @Override
    public Token clone() {
        Token t = new Token();
        t.type = type;
        t.lexeme = lexeme;
        t.description1 = description1;
        t.description2 = description2;
        t.regra = regra;
        t.line = line;
        t.position = position;
        t.scope = scope;
        t.other = other;

        return t;
    }

    public Token() {
    }

    public Token(int type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    public Token(int type, String lexeme, String description) {
        this.type = type;
        this.lexeme = lexeme;
        this.description1 = description;
    }

    public Token(int type, String lexeme, String description, String other) {
        this.type = type;
        this.lexeme = lexeme;
        this.description1 = description;
        this.other = other;
    }

    public void print() {
        System.out.println("TOKEN: " + type);
        System.out.println("LEXEMA: " + lexeme);
        System.out.println("DESCRIÇÃO: " + description1);
        System.out.println("LINHA: " + line);
        System.out.println("POSIÇÃO: " + position);
        System.out.println("ESCOPO: " + scope);
        System.out.println("REGRA: " + regra);
        System.out.println("--------------------------------");
    }

    public String getData() {
        String a = "";
        a += ("\nTOKEN: " + type);
        a += ("\nLEXEMA: " + lexeme);
        a += ("\nDESCRIÇÃO: " + description1);
        a += ("\nLINHA: " + line);
        a += ("\nPOSIÇÃO: " + position);
        a += ("\nESCOPO: " + scope);
        a += ("\nREGRA: " + regra);
        a += ("\n--------------------------------");

        return a;
    }
}
