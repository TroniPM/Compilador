/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.paulomatew.compilador.analyzer.sintatic;

import br.com.paulomatew.compilador.entities.RegraProducao;
import br.com.paulomatew.compilador.entities.LexicalToken;
import br.com.paulomatew.compilador.entities.Objeto;
import br.com.paulomatew.compilador.exceptions.SintaticException;
import br.com.paulomatew.compilador.main.Compilador;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author matt
 */
public class Sintatic {

    public ArrayList<LexicalToken> tokenListFromLexical = null;
    private ArrayList<Objeto> pilha = null;
    public String estadoDaPilha = null;
    private int interacao = 1;

    public void init(ArrayList<LexicalToken> arr) throws SintaticException {
        if (arr == null || arr.isEmpty()) {
            throw new SintaticException("Nenhum token encontrado.");
        }

        this.tokenListFromLexical = arr;

        parserNew();
    }

    private void printPilha() {
        for (int i = pilha.size() - 1; i >= 0; i--) {
            Objeto o = pilha.get(i);
            if (o instanceof LexicalToken) {
                System.out.println(((LexicalToken) o).lexeme);
            } else if (o instanceof RegraProducao) {
                System.out.println(((RegraProducao) o).method);
            }
        }
    }

    private void salvarEstadoDaPilha(String atual) {
        estadoDaPilha += (interacao++) + ") -------------- ( " + atual + " )\n";

        //TODO inverter posição da pilha
        for (int i = pilha.size() - 1; i >= 0; i--) {
            if (pilha.get(i) instanceof LexicalToken) {
                estadoDaPilha += ((LexicalToken) pilha.get(i)).lexeme + "\n";
            } else if (pilha.get(i) instanceof RegraProducao) {
                estadoDaPilha += ((RegraProducao) pilha.get(i)).method + "\n";
            } else {
                estadoDaPilha += "FIM DA PILHA\n";
            }
        }
        /*for (Objeto in : pilha) {
            if (in instanceof LexicalToken) {
                estadoDaPilha += ((LexicalToken) in).lexeme + "\n";
            } else {
                estadoDaPilha += "<" + ((RegraProducao) in).method + ">\n";
            }
        }*/
        estadoDaPilha += "------------------------------\n";
    }

    private void parserNew() throws SintaticException {
        ArrayList<LexicalToken> fitaSendoLida = new ArrayList<>();
        for (LexicalToken in : tokenListFromLexical) {
            fitaSendoLida.add(in);
        }

        pilha = new ArrayList<>();

        estadoDaPilha = "";
        interacao = 1;

        addNaPilha(new LexicalToken(99, "$", "FIM DO PROGRAMA ($)"));
        addNaPilha(new RegraProducao("programa"));
        //salvarEstadoDaPilha("programa");
        for (int i = 0; i < fitaSendoLida.size(); i++) {
            Objeto tokenDaPilha = getNaPilha();
            if (tokenDaPilha instanceof LexicalToken) {

                LexicalToken o1 = (LexicalToken) tokenDaPilha;
                salvarEstadoDaPilha(o1.lexeme);

                if (fitaSendoLida.get(i).type != o1.type) {
                    //o1.print();
                    //token.print();
                    throw new SintaticException("Unexpected token '" + (fitaSendoLida.get(i).lexeme)
                            + "' at line " + fitaSendoLida.get(i).line + ", position " + fitaSendoLida.get(i).position
                            + " (expected: '" + o1.description + "').");
                } else {
                    //throw new SintaticException("TOKEN DIFERENTE. COMPILADOR NÃO RECONHECE ESSA LINGUAGEM");
                }

            } else if (tokenDaPilha instanceof RegraProducao) {
                RegraProducao o1 = (RegraProducao) tokenDaPilha;

                salvarEstadoDaPilha(o1.method);

                if (o1.method.equals("programa")) {
                    //throw new SintaticException("CHAMOU PROGRAMA. NÃO DEVERIA");
                    if (programa(fitaSendoLida.get(i))) {
                        addNaPilha(new RegraProducao("declarar_func"));
                        addNaPilha(new LexicalToken(7, "}", "}"));
                        addNaPilha(new RegraProducao("escopo"));
                        addNaPilha(new LexicalToken(6, "{", "{"));
                        addNaPilha(new LexicalToken(5, ")", ")"));
                        addNaPilha(new LexicalToken(4, "(", "("));
                        addNaPilha(new LexicalToken(3, "main", "main"));

                        //i++;
                    } else {
                        throw new SintaticException("Unexpected token '" + (fitaSendoLida.get(i).lexeme)
                                + "' at line " + fitaSendoLida.get(i).line + " (expected: 'main').");
                    }
                } else if (o1.method.equals("escopo")) {
                    String spe = "int, boolean, identificador, print, call, if, while, break, continue";
                    if (escopo(fitaSendoLida.get(i))) {
                        //<escopo> sempre tem q ser a primeira chamada (pra ser a ultima na pila)
                        if (fitaSendoLida.get(i).type == 16) {//DECLARAÇÃO INT
                            addNaPilha(new RegraProducao("escopo"));
                            addNaPilha(new LexicalToken(8, ";", ";"));
                            addNaPilha(new LexicalToken(1, "<identificador>", "identificador"));
                            addNaPilha(new LexicalToken(16, "int", spe));
                        } else if (fitaSendoLida.get(i).type == 17) {//DECLARAÇÃO BOOLEAN
                            addNaPilha(new RegraProducao("escopo"));
                            addNaPilha(new LexicalToken(8, ";", ";"));
                            addNaPilha(new LexicalToken(1, "<identificador>", "identificador"));
                            addNaPilha(new LexicalToken(17, "boolean", spe));
                        } else if (fitaSendoLida.get(i).type == 1) {//Atribuição
                            addNaPilha(new RegraProducao("escopo"));
                            addNaPilha(new LexicalToken(8, ";", ";"));
                            addNaPilha(new RegraProducao("atrib"));
                            addNaPilha(new LexicalToken(10, "=", "="));
                            addNaPilha(new LexicalToken(1, "<identificador>", spe));

                            //throw new SintaticException("FAZER REGRAS DE <atrib>");
                        } else if (fitaSendoLida.get(i).type == 27) {//Print
                            addNaPilha(new RegraProducao("escopo"));
                            addNaPilha(new LexicalToken(8, ";", ";"));
                            addNaPilha(new LexicalToken(5, ")", "false, true, identificador, constante, )"));
                            addNaPilha(new RegraProducao("printar_sec"));
                            addNaPilha(new LexicalToken(4, "(", "("));
                            addNaPilha(new LexicalToken(27, "print", spe));
                        } else if (fitaSendoLida.get(i).type == 21) {//IF
                            addNaPilha(new RegraProducao("escopo"));
                            addNaPilha(new RegraProducao("bloco_else"));
                            addNaPilha(new LexicalToken(7, "}", "}"));
                            addNaPilha(new RegraProducao("escopo"));
                            addNaPilha(new LexicalToken(6, "{", "{"));
                            addNaPilha(new LexicalToken(5, ")", ")"));
                            addNaPilha(new RegraProducao("exp_logic"));
                            addNaPilha(new LexicalToken(4, "(", "("));
                            addNaPilha(new LexicalToken(21, "if", spe));
                        } else if (fitaSendoLida.get(i).type == 23) {//WHILE
                            addNaPilha(new RegraProducao("escopo"));
                            addNaPilha(new LexicalToken(7, "}", "}"));
                            addNaPilha(new RegraProducao("escopo"));
                            addNaPilha(new LexicalToken(6, "{", "{"));
                            addNaPilha(new LexicalToken(5, ")", ")"));
                            addNaPilha(new RegraProducao("exp_logic"));
                            addNaPilha(new LexicalToken(4, "(", "("));
                            addNaPilha(new LexicalToken(23, "while", spe));
                        } else if (fitaSendoLida.get(i).type == 36) {//CALL
                            addNaPilha(new RegraProducao("escopo"));
                            addNaPilha(new LexicalToken(8, ";", ";"));
                            addNaPilha(new RegraProducao("chamar_func"));

                        } else if (fitaSendoLida.get(i).type == 18) {//BREAK
                            addNaPilha(new RegraProducao("escopo"));
                            addNaPilha(new LexicalToken(8, ";", ";"));
                            addNaPilha(new LexicalToken(18, "break", spe));
                        } else if (fitaSendoLida.get(i).type == 19) {//CONTINUE
                            addNaPilha(new RegraProducao("escopo"));
                            addNaPilha(new LexicalToken(8, ";", ";"));
                            addNaPilha(new LexicalToken(19, "continue", spe));
                        }

                    } else {
                        //throw new SintaticException("NÃO É <escopo>");
                        throw new SintaticException("Unexpected token '" + (fitaSendoLida.get(i).lexeme)
                                + "' at line " + fitaSendoLida.get(i).line + " (expected: '" + spe + "').");
                    }
                } else if (o1.method.equals("atrib")) {
                    //UTILIZANDO LOOK AHEAD
                    if (atrib(fitaSendoLida.get(i))) {
                        if (fitaSendoLida.get(i).type == 1//IDENTIFICADOR e PONTOVIRGULA
                                && lookAhead(fitaSendoLida.get(i + 1), new LexicalToken(8, ";"))) {
                            addNaPilha(new LexicalToken(1, "<identificador>", "identificador"));
                        } else if (fitaSendoLida.get(i).type == 0//COSNTANTE e PONTOVIRGULA
                                && lookAhead(fitaSendoLida.get(i + 1), new LexicalToken(8, ";"))) {
                            addNaPilha(new LexicalToken(0, "<numero>", "cosntante"));
                        } else if (fitaSendoLida.get(i).type == 25 //TRUE
                                && lookAhead(fitaSendoLida.get(i + 1), new LexicalToken(8, ";"))) {
                            addNaPilha(new LexicalToken(25, "true", "true"));
                        } else if (fitaSendoLida.get(i).type == 26 //FALSE
                                && lookAhead(fitaSendoLida.get(i + 1), new LexicalToken(8, ";"))) {
                            addNaPilha(new LexicalToken(26, "false", "false"));
                        } else if (fitaSendoLida.get(i).type == 36) {//CALL
                            addNaPilha(new RegraProducao("chamar_func"));
                        } else if (fitaSendoLida.get(i).type == 37) { //[ exp_arit
                            addNaPilha(new LexicalToken(38, "]", "]"));
                            addNaPilha(new RegraProducao("exp_arit"));
                            addNaPilha(new LexicalToken(37, "[", "["));
                        } else {
                            addNaPilha(new RegraProducao("exp_logic"));
                        }
                        /*else if ((arr.get(i).type == 0 //CONSTANTE e EXPRESSAO_LOGICA
                                || arr.get(i).type == 1 //IDENTIFICADOR e EXPRESSAO_LOGICA
                                || arr.get(i).type == 25 //TRUE e EXPRESSAO_LOGICA
                                || arr.get(i).type == 26)//FALSE e EXPRESSAO_LOGICA
                                && (lookAhead(arr.get(i + 1), new LexicalToken(28, "<", "<")))
                                || lookAhead(arr.get(i + 1), new LexicalToken(29, ">", ">"))
                                || lookAhead(arr.get(i + 1), new LexicalToken(30, "<=", "<="))
                                || lookAhead(arr.get(i + 1), new LexicalToken(31, ">=", ">="))
                                || lookAhead(arr.get(i + 1), new LexicalToken(32, "==", "=="))
                                || lookAhead(arr.get(i + 1), new LexicalToken(33, "!=", "!="))) {
                            addNaPilha(new RegraProducao("condicao"));
                        } else if (arr.get(i).type == 4) {//ABRE_PARENTESES
                            addNaPilha(new LexicalToken(5, ")", ")"));
                            addNaPilha(new RegraProducao("exp_arit"));
                            addNaPilha(new LexicalToken(4, "(", "("));
                        }*/
                    } else {
                        throw new SintaticException("NÃO É <atrib>");
                    }
                } else if (o1.method.equals("chamar_func")) {
                    if (chamar_func(fitaSendoLida.get(i))) {
                        addNaPilha(new LexicalToken(5, ")", ")"));
                        addNaPilha(new RegraProducao("lista_arg"));
                        addNaPilha(new LexicalToken(4, "(", "("));
                        addNaPilha(new LexicalToken(1, "<identificador>", "identificador"));
                        addNaPilha(new LexicalToken(36, "call", "call"));
                    } else {
                        throw new SintaticException("Unexpected token '" + (fitaSendoLida.get(i).lexeme)
                                + "' at line " + fitaSendoLida.get(i).line + " (expected: 'call').");
                    }
                } else if (o1.method.equals("exp_arit")) {
                    if (exp_arit(fitaSendoLida.get(i))) {
                        if (fitaSendoLida.get(i).type == 1) {
                            addNaPilha(new RegraProducao("oper_arit"));
                            addNaPilha(new LexicalToken(1, "<identificador>", "identificador"));
                        } else if (fitaSendoLida.get(i).type == 0) {
                            addNaPilha(new RegraProducao("oper_arit"));
                            addNaPilha(new LexicalToken(0, "<numero>", "constante"));
                        } else if (fitaSendoLida.get(i).type == 4) {//(
                            addNaPilha(new RegraProducao("oper_arit"));
                            addNaPilha(new LexicalToken(5, ")", ")"));
                            addNaPilha(new RegraProducao("exp_arit"));
                            addNaPilha(new LexicalToken(4, "(", "("));
                        }
                    } else if (o1.dontPrintException) {
                        System.out.println("exp_arit >> o1.dontPrintException");
                    } else {
                        throw new SintaticException("Unexpected token '" + (fitaSendoLida.get(i).lexeme)
                                + "' at line " + fitaSendoLida.get(i).line + " (<exp_arit>).");
                    }
                } else if (o1.method.equals("oper_arit")) {
                    //PODE GERAR VAZIO
                    if (oper_arit(fitaSendoLida.get(i))) {
                        if (fitaSendoLida.get(i).type == 11) {
                            addNaPilha(new RegraProducao("exp_arit"));
                            addNaPilha(new LexicalToken(11, "+", "+"));
                        } else if (fitaSendoLida.get(i).type == 12) {
                            addNaPilha(new RegraProducao("exp_arit"));
                            addNaPilha(new LexicalToken(12, "-", "-"));
                        } else if (fitaSendoLida.get(i).type == 13) {
                            addNaPilha(new RegraProducao("exp_arit"));
                            addNaPilha(new LexicalToken(13, "*", "*"));
                        } else if (fitaSendoLida.get(i).type == 14) {
                            addNaPilha(new RegraProducao("exp_arit"));
                            addNaPilha(new LexicalToken(14, "/", "/"));
                        }

                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
                } else if (o1.method.equals("exp_logic")) {
                    if (exp_logic(fitaSendoLida.get(i))) {
                        if (fitaSendoLida.get(i).type == 1) {
                            addNaPilha(new RegraProducao("oper_logic"));
                            addNaPilha(new LexicalToken(1, "<identificador>", "identificador"));
                        } else if (fitaSendoLida.get(i).type == 0) {
                            addNaPilha(new RegraProducao("oper_logic"));
                            addNaPilha(new LexicalToken(0, "<numero>", "constante"));
                        } else if (fitaSendoLida.get(i).type == 25) {
                            addNaPilha(new RegraProducao("oper_logic"));
                            addNaPilha(new LexicalToken(25, "true", "true"));
                        } else if (fitaSendoLida.get(i).type == 26) {
                            addNaPilha(new RegraProducao("oper_logic"));
                            addNaPilha(new LexicalToken(26, "false", "false"));
                        } else if (fitaSendoLida.get(i).type == 4) {//(
                            addNaPilha(new RegraProducao("exp_logic_cont"));
                            addNaPilha(new RegraProducao("oper_logic"));
                            addNaPilha(new LexicalToken(5, ")", ")"));
                            addNaPilha(new RegraProducao("exp_logic"));
                            addNaPilha(new LexicalToken(4, "(", "("));
                        } else {
                            //Evitar que seja passada uma condição vazia.
                            throw new SintaticException("Unexpected token '" + (fitaSendoLida.get(i).lexeme)
                                    + "' at line " + fitaSendoLida.get(i).line + " (<exp_logic>).");
                        }

                    } else if (o1.dontPrintException) {
                        System.out.println("exp_logic >> o1.dontPrintException");

                    } else {
                        //throw new SintaticException("NÃO É <exp_logic>");

                        throw new SintaticException("Unexpected token '" + (fitaSendoLida.get(i).lexeme)
                                + "' at line " + fitaSendoLida.get(i).line + " (<exp_logic>).");
                    }

                } else if (o1.method.equals("oper_logic")) {
                    //PODE GERAR VAZIO
                    if (oper_logic(fitaSendoLida.get(i))) {
                        if (fitaSendoLida.get(i).type == 28) {
                            addNaPilha(new RegraProducao("exp_logic_cont"));
                            addNaPilha(new RegraProducao("exp_logic"));
                            addNaPilha(new LexicalToken(28, "<", "<"));
                        } else if (fitaSendoLida.get(i).type == 29) {
                            addNaPilha(new RegraProducao("exp_logic_cont"));
                            addNaPilha(new RegraProducao("exp_logic"));
                            addNaPilha(new LexicalToken(29, ">", ">"));
                        } else if (fitaSendoLida.get(i).type == 30) {
                            addNaPilha(new RegraProducao("exp_logic_cont"));
                            addNaPilha(new RegraProducao("exp_logic"));
                            addNaPilha(new LexicalToken(30, "<=", "<="));
                        } else if (fitaSendoLida.get(i).type == 31) {
                            addNaPilha(new RegraProducao("exp_logic_cont"));
                            addNaPilha(new RegraProducao("exp_logic"));
                            addNaPilha(new LexicalToken(31, ">=", ">="));
                        } else if (fitaSendoLida.get(i).type == 32) {
                            addNaPilha(new RegraProducao("exp_logic_cont"));
                            addNaPilha(new RegraProducao("exp_logic"));
                            addNaPilha(new LexicalToken(32, "==", "=="));
                        } else if (fitaSendoLida.get(i).type == 33) {
                            addNaPilha(new RegraProducao("exp_logic_cont"));
                            addNaPilha(new RegraProducao("exp_logic"));
                            addNaPilha(new LexicalToken(33, "!=", "!="));
                        }

                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
                } else if (o1.method.equals("exp_logic_cont")) {
                    //PODE GERAR VAZIO
                    if (oper_logic_cont(fitaSendoLida.get(i))) {
                        if (fitaSendoLida.get(i).type == 34) {
                            addNaPilha(new RegraProducao("exp_logic"));
                            addNaPilha(new LexicalToken(34, "&&", "&&"));
                        } else if (fitaSendoLida.get(i).type == 35) {
                            addNaPilha(new RegraProducao("exp_logic"));
                            addNaPilha(new LexicalToken(35, "||", "||"));
                        }
                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
                } else if (o1.method.equals("printar_sec")) {
                    //PODE GERAR VAZIO
                    if (printar_sec(fitaSendoLida.get(i))) {
                        String esp = "false, true, identificador, constante";
                        if (fitaSendoLida.get(i).type == 1) {//IDENTIFICADOR
                            addNaPilha(new LexicalToken(1, "<identificador>", esp));
                        } else if (fitaSendoLida.get(i).type == 0) {//CONSTANTE
                            addNaPilha(new LexicalToken(0, "<numero>", esp));
                        } else if (fitaSendoLida.get(i).type == 25) {//TRUE
                            addNaPilha(new LexicalToken(25, "true", esp));
                        } else if (fitaSendoLida.get(i).type == 26) {//FALSE
                            addNaPilha(new LexicalToken(26, "false", esp));
                        }
                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/

                } else if (o1.method.equals("bloco_else")) {
                    //PODE GERAR VAZIO
                    if (bloco_else(fitaSendoLida.get(i))) {
                        addNaPilha(new LexicalToken(7, "}", "}"));
                        addNaPilha(new RegraProducao("escopo"));
                        addNaPilha(new LexicalToken(6, "{", "{"));
                        addNaPilha(new LexicalToken(22, "else", "else"));

                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
                } else if (o1.method.equals("lista_arg")) {
                    //PODE GERAR VAZIO
                    if (lista_arg(fitaSendoLida.get(i))) {
                        if (fitaSendoLida.get(i).type == 0) {//CONSTANTE
                            addNaPilha(new RegraProducao("lista_arg_sec"));
                            addNaPilha(new LexicalToken(0, "<numero>", "constante"));
                        } else if (fitaSendoLida.get(i).type == 1) {//IDENTIFICADOR
                            addNaPilha(new RegraProducao("lista_arg_sec"));
                            addNaPilha(new LexicalToken(1, "<identificador>", "identificador"));
                        } else if (fitaSendoLida.get(i).type == 25) {//TRUE
                            addNaPilha(new RegraProducao("lista_arg_sec"));
                            addNaPilha(new LexicalToken(25, "true", "true"));
                        } else if (fitaSendoLida.get(i).type == 26) {//FALSE
                            addNaPilha(new RegraProducao("lista_arg_sec"));
                            addNaPilha(new LexicalToken(26, "false", "false"));
                        }
                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
                } else if (o1.method.equals("lista_arg_sec")) {
                    if (lista_arg_sec(fitaSendoLida.get(i))) {
                        if (fitaSendoLida.get(i).type == 9) {//VIRGULA
                            addNaPilha(new RegraProducao("lista_arg_sec"));
                            addNaPilha(new RegraProducao("lista_arg_ter"));
                            addNaPilha(new LexicalToken(9, ",", ","));
                        }
                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
                } else if (o1.method.equals("lista_arg_ter")) {
                    String esp = "false, true, identificador, constante";
                    if (lista_arg_ter(fitaSendoLida.get(i))) {
                        if (fitaSendoLida.get(i).type == 0) {//CONSTANTE
                            addNaPilha(new LexicalToken(0, "<numero>", esp));
                        } else if (fitaSendoLida.get(i).type == 1) {//IDENTIFICADOR
                            addNaPilha(new LexicalToken(1, "<identificador>", esp));
                        } else if (fitaSendoLida.get(i).type == 25) {//TRUE
                            addNaPilha(new LexicalToken(25, "true", esp));
                        } else if (fitaSendoLida.get(i).type == 26) {//FALSE
                            addNaPilha(new LexicalToken(26, "false", esp));
                        }
                    } else {
                        throw new SintaticException("Unexpected token '" + (fitaSendoLida.get(i).lexeme)
                                + "' at line " + fitaSendoLida.get(i).line + " (expected: '" + esp + "').");
                    }
                } else if (o1.method.equals("declarar_func")) {
                    //PODE GERAR VAZIO
                    if (declarar_func(fitaSendoLida.get(i))) {
                        addNaPilha(new RegraProducao("declarar_func"));
                        addNaPilha(new LexicalToken(7, "}", "}"));
                        addNaPilha(new RegraProducao("retorno_func"));
                        addNaPilha(new RegraProducao("escopo"));
                        addNaPilha(new LexicalToken(6, "{", "{"));
                        addNaPilha(new LexicalToken(5, ")", ")"));
                        addNaPilha(new RegraProducao("lista_param"));
                        addNaPilha(new LexicalToken(4, "(", "("));
                        addNaPilha(new LexicalToken(1, "<identificador>", "identificador"));
                        addNaPilha(new RegraProducao("func_tipo"));
                        addNaPilha(new LexicalToken(24, "function", "function"));

                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
                } else if (o1.method.equals("func_tipo")) {
                    String esp = "void, int, boolean";
                    if (func_tipo(fitaSendoLida.get(i))) {
                        if (fitaSendoLida.get(i).type == 15) {//VOID
                            addNaPilha(new LexicalToken(15, "void", esp));
                        } else if (fitaSendoLida.get(i).type == 16) {//INT
                            addNaPilha(new LexicalToken(16, "int", esp));
                        } else if (fitaSendoLida.get(i).type == 17) {//BOOELAN
                            addNaPilha(new LexicalToken(17, "boolean", esp));
                        }
                    } else {
                        throw new SintaticException("Unexpected token '" + (fitaSendoLida.get(i).lexeme)
                                + "' at line " + fitaSendoLida.get(i).line + " (expected: '" + esp + "').");
                    }
                } else if (o1.method.equals("lista_param")) {
                    //PODE GERAR VAZIO
                    String esp = "int, boolean";
                    if (lista_param(fitaSendoLida.get(i))) {
                        if (fitaSendoLida.get(i).type == 16) {//INT
                            addNaPilha(new RegraProducao("lista_param_sec"));
                            addNaPilha(new LexicalToken(1, "<identificador>", "identificador"));
                            addNaPilha(new LexicalToken(16, "int", esp));
                        } else if (fitaSendoLida.get(i).type == 17) {//BOOELAN
                            addNaPilha(new RegraProducao("lista_param_sec"));
                            addNaPilha(new LexicalToken(1, "<identificador>", "identificador"));
                            addNaPilha(new LexicalToken(17, "boolean", esp));
                        }
                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
                } else if (o1.method.equals("lista_param_sec")) {
                    //PODE GERAR VAZIO
                    if (lista_param_sec(fitaSendoLida.get(i))) {
                        addNaPilha(new RegraProducao("lista_param_sec"));
                        addNaPilha(new LexicalToken(1, "<identificador>", "identificador"));
                        //System.out.println(">>>>>>>>>>>>>>>>>> " + fitaSendoLida.get(i).type);
                        if (fitaSendoLida.get(i).type == 9//VIRGULA e INTEIRO
                                && lookAhead(fitaSendoLida.get(i + 1), new LexicalToken(16, "int", "int, boolean"))) {
                            addNaPilha(new LexicalToken(16, "int", "int, boolean"));
                        } else if (fitaSendoLida.get(i).type == 9//VIRGULA e BOLEAN
                                && lookAhead(fitaSendoLida.get(i + 1), new LexicalToken(17, "boolean", "int, boolean"))) {
                            addNaPilha(new LexicalToken(17, "int", "int, boolean"));
                        }

                        /*if (fitaSendoLida.get(i).type == 16) {//INT
                            System.out.println("ENTROU NO INT");
                            addNaPilha(new LexicalToken(16, "int", "int, boolean"));
                        } else if (fitaSendoLida.get(i).type == 17) {//BOOELAN
                            System.out.println("ENTROU NO BOOELAN");
                            addNaPilha(new LexicalToken(17, "boolean", "int, boolean"));
                        }*/
                        addNaPilha(new LexicalToken(9, ",", ","));

                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
                } else if (o1.method.equals("retorno_func")) {
                    //PODE GERAR VAZIO
                    if (retorno_func(fitaSendoLida.get(i))) {
                        addNaPilha(new LexicalToken(8, ";", ";"));
                        addNaPilha(new RegraProducao("retorno_func_sec"));
                        addNaPilha(new LexicalToken(20, "return", "return"));
                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
                } else if (o1.method.equals("retorno_func_sec")) {
                    //É o mesmo esquema da atribuição
                    if (retorno_func_sec(fitaSendoLida.get(i))) {
                        if (fitaSendoLida.get(i).type == 1//IDENTIFICADOR e PONTOVIRGULA
                                && lookAhead(fitaSendoLida.get(i + 1), new LexicalToken(8, ";"))) {
                            addNaPilha(new LexicalToken(1, "<identificador>", "identificador"));
                        } else if (fitaSendoLida.get(i).type == 0//COSNTANTE e PONTOVIRGULA
                                && lookAhead(fitaSendoLida.get(i + 1), new LexicalToken(8, ";"))) {
                            addNaPilha(new LexicalToken(0, "<numero>", "cosntante"));
                        } else if (fitaSendoLida.get(i).type == 25 //TRUE
                                && lookAhead(fitaSendoLida.get(i + 1), new LexicalToken(8, ";"))) {
                            addNaPilha(new LexicalToken(25, "true", "true"));
                        } else if (fitaSendoLida.get(i).type == 26 //FALSE
                                && lookAhead(fitaSendoLida.get(i + 1), new LexicalToken(8, ";"))) {
                            addNaPilha(new LexicalToken(26, "false", "false"));
                        } else if (fitaSendoLida.get(i).type == 36) {//CALL
                            addNaPilha(new RegraProducao("chamar_func"));
                        } else if (fitaSendoLida.get(i).type == 37) { //[ exp_arit
                            addNaPilha(new LexicalToken(38, "]", "]"));
                            addNaPilha(new RegraProducao("exp_arit"));
                            addNaPilha(new LexicalToken(37, "[", "["));
                        } else {
                            addNaPilha(new RegraProducao("exp_logic"));
                        }
                    } else {
                        throw new SintaticException("NÃO É <retorno_func_sec>");
                    }
                }
                i--;
            }
            //}

            /**
             * Quando chega ao final, verificar se DECLARAR_FUNC é vazio
             */
            if (pilha.get(pilha.size() - 1) == null) {

                if (fitaSendoLida.get(i) instanceof LexicalToken) {
                    throw new SintaticException("Unexpected token '" + (fitaSendoLida.get(i).lexeme)
                            + "' at line " + fitaSendoLida.get(i).line + " (expected: '" + fitaSendoLida.get(i).description + "').");
                } else {
                    throw new SintaticException("Pilha foi lida mas o código fonte ainda não acabou.");
                }
            }
            if (i + 1 == fitaSendoLida.size() && pilha.size() > 0) {
                //RegraProducao o1 = (RegraProducao) getNaPilha();

                Objeto o = getNaPilha();
                if (o instanceof RegraProducao && ((RegraProducao) o).method.equals("declarar_func")) {
                    //dummy if
                } else {
                    /*for (Objeto in : pilha) {
                        if (in instanceof LexicalToken) {
                            System.out.println(((LexicalToken) in).lexeme);
                        } else {
                            System.out.println(((RegraProducao) in).method);
                        }
                    }*/
                    //System.out.println(arr.get(i+1));
                    throw new SintaticException("All source code was readed, but stack is not EMPTY (" + pilha.size() + ").");
                }
            }
        }
    }

    private void addNaPilha(Objeto token) {
        pilha.add(token);
    }

    private Objeto getNaPilha() {
        try {
            Objeto t = pilha.get(pilha.size() - 1);
            if (t instanceof LexicalToken) {
                pilha.remove((LexicalToken) t);
            } else {
                pilha.remove((RegraProducao) t);
            }

            return t;
        } catch (Exception ex) {
            return null;
        }

    }

    private boolean lookAhead(LexicalToken get, LexicalToken get0) {
        return get.type == get0.type;
    }

    private boolean retorno_func_sec(LexicalToken token) {
        return token.type == 1 //identificador
                || token.type == 0 //constante
                || token.type == 25 //true
                || token.type == 26 //false
                || token.type == 36 //call
                || token.type == 37 //[
                || token.type == 38 //]
                || token.type == 4 // (
                ;
    }

    private boolean exp_logic(LexicalToken token) {
        return token.type == 0
                || token.type == 1
                || token.type == 4
                || token.type == 5
                || token.type == 25
                || token.type == 26;

    }

    private boolean oper_arit(LexicalToken token) {
        return token.type == 11 || token.type == 12
                || token.type == 13 || token.type == 14;
    }

    private boolean oper_logic(LexicalToken token) {
        return token.type == 28
                || token.type == 29
                || token.type == 30
                || token.type == 31
                || token.type == 32
                || token.type == 33;
    }

    private boolean exp_arit(LexicalToken token) {
        return token.type == 0
                || token.type == 1
                || token.type == 4
                || token.type == 5;

    }

    private boolean chamar_func(LexicalToken token) {
        return token.type == 36;
    }

    private boolean atrib(LexicalToken token) {
        return token.type == 1 //identificador
                || token.type == 0 //constante
                || token.type == 25 //true
                || token.type == 26 //false
                || token.type == 36 //call
                || token.type == 37 //[
                || token.type == 38 //]
                || token.type == 4 // (
                ;
    }

    private boolean retorno_func(LexicalToken token) {
        return token.type == 20;

    }

    private boolean lista_param_sec(LexicalToken token) {
        return token.type == 9;
    }

    private boolean lista_param(LexicalToken token) {
        return token.type == 16 || token.type == 17;
    }

    private boolean func_tipo(LexicalToken token) {
        return token.type == 15 || token.type == 16 || token.type == 17;
    }

    private boolean declarar_func(LexicalToken token) {
        return token.type == 24;
    }

    private boolean lista_arg_ter(LexicalToken token) {
        return token.type == 1 || token.type == 0
                || token.type == 25 || token.type == 26;
    }

    private boolean lista_arg_sec(LexicalToken token) {
        return token.type == 9;
    }

    private boolean lista_arg(LexicalToken token) {
        return token.type == 1 || token.type == 0
                || token.type == 25 || token.type == 26;
    }

    private boolean bloco_else(LexicalToken token) {
        return token.type == 22;
    }

    private boolean printar_sec(LexicalToken token) {
        return token.type == 1 || token.type == 0
                || token.type == 25 || token.type == 26;
    }

    private boolean printar(LexicalToken token) {
        return token.type == 27;
    }

    private boolean escopo(LexicalToken token) {
        return token.type == 16 || token.type == 17
                || token.type == 18 || token.type == 19
                || token.type == 1 || token.type == 27
                || token.type == 36 || token.type == 21
                || token.type == 23 || token.type == 7
                || token.type == 20;
    }

    private boolean programa(LexicalToken token) {
        return token.type == 3;
    }

    private boolean oper_logic_cont(LexicalToken token) {
        return token.type == 34 || token.type == 35;
    }
}
