package br.com.paulomatew.compilador.main;

import br.com.paulomatew.compilador.core.IntermediateCodeGenerator;
import br.com.paulomatew.compilador.core.Lexical;
import br.com.paulomatew.compilador.core.Semantic;
import br.com.paulomatew.compilador.core.Sintatic;
import br.com.paulomatew.compilador.entities.Escopo;
import br.com.paulomatew.compilador.entities.Token;
import br.com.paulomatew.compilador.exceptions.IntermediateCodeGeneratorException;
import br.com.paulomatew.compilador.exceptions.LexicalException;
import br.com.paulomatew.compilador.exceptions.SemanticException;
import br.com.paulomatew.compilador.exceptions.SintaticException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Paulo Mateus on 26/06/2017. For project Compiladores
 * (https://github.com/TroniPM/Compilador) Contact: <paulomatew@gmail.com>
 */
public class Compilador {

    public Lexical analizadorLexico = null;
    public Semantic analizadorSemantico = null;
    public Sintatic analizadorSintatico = null;
    public IntermediateCodeGenerator generator = null;

    public static ArrayList<String> RESERVED_WORDS_AND_OPERATORS = null;
    public static ArrayList<String> RESERVED_WORDS = null;
    public static ArrayList<String> RESERVED_WORDS_TOKEN = null;

    public String sourceCode = null;

    public String errorConsole = "";
    public String codigoIntermediario = "";
    public boolean erro = false;

    public static void main(String[] args) {
        Compilador c = new Compilador();
        //c.creating();

        for (int i = 0; i < c.RESERVED_WORDS_TOKEN.size(); i++) {
            System.out.print(i + "\t" + c.RESERVED_WORDS_TOKEN.get(i));
            if (i >= 3) {
                System.out.print(" ( " + c.RESERVED_WORDS_AND_OPERATORS.get(i - 3) + " )");
            }
            System.out.println("");
        }
    }

    private void creating() {
        analizadorLexico = new Lexical();
        analizadorSintatico = new Sintatic();
        analizadorSemantico = new Semantic();
        generator = new IntermediateCodeGenerator();

        String[] t1 = new String[]{/*+3*/"main", "(", ")"/*5*/, "{", "}", ";", ",", "="/*10*/, "+", "-", "*", "/", "void"/*15*/, "int", "boolean", "break", "continue", "return"/*20*/, "if", "else", "while", "function", "true"/*25*/, "false", "print", "<", ">", "<="/*30*/, ">=", "==", "!=", "&&", "||"/*35*/, "call", "[", "]"};
        RESERVED_WORDS_AND_OPERATORS = new ArrayList(Arrays.asList(t1));

        //utilizar esses indices para o LexicalObject type no array do analisador lexico
        String[] t2 = new String[]{"constante", "identificador", "token desconhecido", "principal", "ABRE_PARENT", "FECHA_PARENT", "ABRE_CHAVES", "FECHA_CHAVES", "END_COMMAND", "comando vírgula", "comando de atribuição", "operador adição", "operador subtração", "operador multiplicação", "operador divisão", "declaração de void", "declaração de variável int", "declaração de variável bool", "desvio incondicional", "desvio incondicional", "desvio incondicional", "desvio condicional", "desvio condicional", "desvio loop", "declaração de função", "comando true", "comando false", "função print", "relação MENOR", "relação MAIOR", "relação MENOR IGUAL", "relação MAIOR IGUAL", "relação IGUAL", "relação DIFERENTE", "relação AND", "relação OR", "chamada de função", "ABRE_COLCHETES", "FECHA_COLCHETES"};
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
        erro = false;

        try {
            analizadorLexico.init(txt);
        } catch (LexicalException ex) {
            Logger.getLogger(Lexical.class.getName()).log(Level.SEVERE, null, ex);

            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));

            errorConsole += "\n" + (errors.toString().split("\n")[0]).trim();
            //errorConsole = errorConsole.trim();

            erro = true;
        }

        if (!erro) {
            try {
                analizadorSintatico.init(analizadorLexico.tokenArray);
            } catch (SintaticException ex) {
                Logger.getLogger(Sintatic.class.getName()).log(Level.SEVERE, null, ex);

                StringWriter errors = new StringWriter();
                ex.printStackTrace(new PrintWriter(errors));

                errorConsole += "\n" + (errors.toString().split("\n")[0]).trim();
                //errorConsole = errorConsole.trim();

                erro = true;
            }

            if (!erro) {
                try {
                    analizadorSemantico.init(analizadorLexico.tokenArray, analizadorLexico.escoposArvore);
                    
                for (Escopo in : analizadorLexico.escoposArvore) {
                    in.getData();
                    in.print();
                }
                } catch (SemanticException ex) {
                    Logger.getLogger(Sintatic.class.getName()).log(Level.SEVERE, null, ex);

                    StringWriter errors = new StringWriter();
                    ex.printStackTrace(new PrintWriter(errors));

                    errorConsole += "\n" + (errors.toString().split("\n")[0]).trim();
                    //errorConsole = errorConsole.trim();

                    erro = true;
                }

                if (!erro) {
                    try {
                        codigoIntermediario = IntermediateCodeGenerator.gerarCode(generator.parser(analizadorLexico.tokenArray));
                    } catch (IntermediateCodeGeneratorException ex) {
                        Logger.getLogger(Sintatic.class.getName()).log(Level.SEVERE, null, ex);

                        StringWriter errors = new StringWriter();
                        ex.printStackTrace(new PrintWriter(errors));

                        errorConsole += "\n" + (errors.toString().split("\n")[0]).trim();
                        //errorConsole = errorConsole.trim();

                        erro = true;
                    }

                    if (!errorConsole.isEmpty()) {
                        errorConsole += "\n";
                    }
                    errorConsole += "\t" + "<<Compiled>>";
                }
            }
        }
    }

}
