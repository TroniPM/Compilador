package br.com.paulomatew.compilador.entities;

/**
 * Created by Paulo Mateus on 26/06/2017. For project Compiladores
 * (https://github.com/TroniPM/Compilador) Contact: <paulomatew@gmail.com>
 */
public class LexicalToken extends Objeto {

    public int type = -99;
    public String lexeme = null;
    public String description = null;
    public String regra = null;
    public int line = 0;
    public int position = 0;
    public String scope = null;

    public LexicalToken() {
    }

    public LexicalToken(int type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
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
        System.out.println("POSIÇÃO: " + position);
        System.out.println("ESCOPO: " + scope);
        System.out.println("REGRA: " + regra);
        System.out.println("--------------------------------");
    }

    public String getData() {
        String a = "";
        a += ("\nTOKEN: " + type);
        a += ("\nLEXEMA: " + lexeme);
        a += ("\nDESCRIÇÃO: " + description);
        a += ("\nLINHA: " + line);
        a += ("\nPOSIÇÃO: " + position);
        a += ("\nESCOPO: " + scope);
        a += ("\nREGRA: " + regra);
        a += ("\n--------------------------------");

        return a;
    }
}
