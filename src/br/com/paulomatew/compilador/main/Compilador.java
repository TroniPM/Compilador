/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.paulomatew.compilador.main;

import br.com.paulomatew.compilador.analyzer.lexical.Lexical;
import br.com.paulomatew.compilador.analyzer.semantic.Semantic;
import br.com.paulomatew.compilador.analyzer.sintatic.Sintatic;
import br.com.paulomatew.compilador.analyzer.lexical.LexicalToken;
import br.com.paulomatew.compilador.exceptions.LexicalException;
import br.com.paulomatew.compilador.exceptions.SintaticException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author matt
 */
public class Compilador {

    public Lexical analizadorLexico = null;
    public Semantic analizadorSemantico = null;
    public Sintatic analizadorSintatico = null;

    public static ArrayList<String> RESERVED_WORDS_AND_OPERATORS = null;
    public static ArrayList<String> RESERVED_WORDS = null;
    public static ArrayList<String> RESERVED_WORDS_TOKEN = null;

    public String sourceCode = null;

    public String errorConsole = "";

    private void creating() {
        analizadorLexico = new Lexical();
        analizadorSintatico = new Sintatic();
        analizadorSemantico = new Semantic();

        String[] t1 = new String[]{"main", "(", ")", "{", "}", ";", ",", "=", "+", "-"/*10*/, "*", "/", "void", "int", "boolean", "break", "continue", "return", "if", "else"/*20*/, "while", "function", "true", "false", "print", "<", ">", "<=", ">=", "=="/*30*/, "!=", "&&", "||", "call"};
        RESERVED_WORDS_AND_OPERATORS = new ArrayList(Arrays.asList(t1));

        //utilizar esses indices para o LexicalObject type no array do analisador lexico
        String[] t2 = new String[]{"constante", "identificador", "token desconhecido", "principal", "ABRE_P", "FECHA_P", "ABRE_C", "FECHA_C", "END_COMMAND", "comando vírgula", "comando de atribuição", "operador adição", "operador subtração", "operador multiplicação", "operador divisão", "declaração de void", "declaração de variável int", "declaração de variável bool", "desvio incondicional", "desvio incondicional", "desvio incondicional", "desvio condicional", "desvio condicional", "desvio loop", "declaração de função", "comando true", "comando false", "função print", "relação MENOR", "relação MAIOR", "relação MENOR IGUAL", "relação MAIOR IGUAL", "relação IGUAL", "relação DIFERENTE", "relação AND", "relação OR", "chamada de  função"};
        RESERVED_WORDS_TOKEN = new ArrayList(Arrays.asList(t2));

        //Usado para mudar a cor das palavras na gui
        String[] t3 = new String[]{"main", "void", "int", "boolean", "break", "continue", "return", "if", "else", "while", "function", "true", "false", "print", "call"};
        RESERVED_WORDS = new ArrayList(Arrays.asList(t3));
    }

    public Compilador() {
        creating();
    }

    public static String getLexeme(int i) {
        return RESERVED_WORDS_AND_OPERATORS.get(i);
    }

    public static String getToken(int i) {
        return RESERVED_WORDS_TOKEN.get(i);
    }

    public Compilador(String codigo) {
        creating();
        start(codigo);
    }

    public void init(String sourceCode) {
        start(sourceCode);
    }

    /**
     * Operações vão acontecer aqui.
     *
     * @param txt
     */
    private void start(String txt) {
        try {
            analizadorLexico.init(txt);
        } catch (LexicalException ex) {
            Logger.getLogger(Lexical.class.getName()).log(Level.SEVERE, null, ex);

            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));

            errorConsole += "\n" + errors.toString().split("\n")[0];
            errorConsole = errorConsole.trim();
        }

        try {
            analizadorSintatico.init(analizadorLexico.tokenArray);
        } catch (SintaticException ex) {
            Logger.getLogger(Sintatic.class.getName()).log(Level.SEVERE, null, ex);

            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));

            errorConsole += "\n" + errors.toString().split("\n")[0];
            errorConsole = errorConsole.trim();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        String palavra
                = "mainn(){\n"
                + "	int function somar(int a1,int a2){\n"
                + "		return a1+a2;\n"
                + "	}\n"
                + "\n"
                + "	int inteiro1;\n"
                + "	boolean boo1;\n"
                + "	int inteiro2;\n"
                + "	boolean boo2;\n"
                + "	\n"
                + "	inteiro2=20;\n"
                + "	inteiro1 = inteiro2+2;\n"
                + "	\n"
                + "	boo1=false;\n"
                + "	boo2 = boo1&&true;\n"
                + "	\n"
                + "	if(boo1==true&&1<=2&&1>=2||1!=2||1>2&&1<2){\n"
                + "	} else {\n"
                + "	}\n"
                + "	\n"
                + "	somar(inteiro1,inteiro2);\n"
                + "	\n"
                + "	print(20);\n"
                + "}";

        Compilador c = new Compilador();
        c.init(palavra);
        //System.out.println(c.analizadorLexico.getTokenListAsTable());
        /*for (LexicalObject in : c.analizadorLexico.tokenArray) {
            in.print();
            }*/

    }
}
