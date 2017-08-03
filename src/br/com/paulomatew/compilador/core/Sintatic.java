package br.com.paulomatew.compilador.core;

import br.com.paulomatew.compilador.entities.RegraProducao;
import br.com.paulomatew.compilador.entities.Token;
import br.com.paulomatew.compilador.entities.Objeto;
import br.com.paulomatew.compilador.exceptions.SintaticException;
import java.util.ArrayList;

/**
 * Created by Paulo Mateus on 26/06/2017. For project Compiladores
 * (https://github.com/TroniPM/Compilador) Contact: <paulomatew@gmail.com>
 */
public class Sintatic {

    public ArrayList<Token> tokensLexical = null;
    private ArrayList<Objeto> stack = null;
    public String stackState = null;
    private int iteracao = 1;
    private ArrayList<Integer> labels_parentese = null;
    private ArrayList<Integer> labels_chaves = null;
    private ArrayList<Integer> labels_if = null;
    private ArrayList<Integer> labels_if_atual = null;

    public void init(ArrayList<Token> arr) throws SintaticException {
        /*if (arr == null || arr.isEmpty()) {
            throw new SintaticException("Nenhum token encontrado.");
        }*/

        this.tokensLexical = arr;

        parserNew();
    }

    private void printStack() {
        for (int i = stack.size() - 1; i >= 0; i--) {
            Objeto o = stack.get(i);
            if (o instanceof Token) {
                System.out.println(((Token) o).lexeme);
            } else if (o instanceof RegraProducao) {
                System.out.println(((RegraProducao) o).method);
            }
        }
    }

    private void saveStackState(String atual) {
        stackState += (iteracao++) + ") -------------- ( " + atual + " )\n";

        //TODO inverter posição da pilha
        for (int i = stack.size() - 1; i >= 0; i--) {
            if (stack.get(i) instanceof Token) {
                stackState += ((Token) stack.get(i)).lexeme + "\n";
            } else if (stack.get(i) instanceof RegraProducao) {
                stackState += ((RegraProducao) stack.get(i)).method + "\n";
            } else {
                stackState += "FIM DA PILHA\n";
            }
        }
        /*for (Objeto in : pilha) {
            if (in instanceof LexicalToken) {
                estadoDaPilha += ((LexicalToken) in).lexeme + "\n";
            } else {
                estadoDaPilha += "<" + ((RegraProducao) in).method + ">\n";
            }
        }*/
        stackState += "------------------------------\n";
    }

    private void parserNew() throws SintaticException {
        stack = new ArrayList<>();
        labels_parentese = new ArrayList<>();
        labels_parentese.add(0);
        labels_chaves = new ArrayList<>();
        labels_chaves.add(0);
        labels_if = new ArrayList<>();
        labels_if.add(0);
        labels_if_atual = new ArrayList<>();
        labels_if_atual.add(0);

        stackState = "";
        iteracao = 1;

        addToStack(new Token(99, "$", "FIM DO PROGRAMA ($), <declarar_func>"));
        addToStack(new RegraProducao("programa"));
        //salvarEstadoDaPilha("programa");

        if (tokensLexical.size() == 0) {
            throw new SintaticException("Unexpected token 'EMPTY' at line 1, position 1 (expected: 'main').");
        }
        for (int i = 0; i < tokensLexical.size(); i++) {
            Objeto tokenDaPilha = getObjectFromStack();
            if (tokenDaPilha instanceof Token) {

                Token o1 = (Token) tokenDaPilha;
                saveStackState(o1.lexeme);

                if (tokensLexical.get(i).type != o1.type) {
                    //o1.print();
                    //token.print();
                    throw new SintaticException("Unexpected token '" + (tokensLexical.get(i).lexeme)
                            + "' at line " + tokensLexical.get(i).line + ", position " + tokensLexical.get(i).position
                            + " (expected: '" + o1.description1 + "').");
                } else //throw new SintaticException("TOKEN DIFERENTE. COMPILADOR NÃO RECONHECE ESSA LINGUAGEM");
                //System.out.println(o1.other);
                 if (o1.other != null) {
                        //System.out.println(o1.other);
                        tokensLexical.get(i).other = o1.other;
                    }

            } else if (tokenDaPilha instanceof RegraProducao) {
                RegraProducao o1 = (RegraProducao) tokenDaPilha;

                saveStackState(o1.method);

                if (o1.method.equals("programa")) {
                    //throw new SintaticException("CHAMOU PROGRAMA. NÃO DEVERIA");
                    if (programa(tokensLexical.get(i))) {
                        int label = (labels_chaves.get(labels_chaves.size() - 1) + 1);
                        //, "C" + String.valueOf(label)
                        labels_chaves.add(label);
                        addToStack(new RegraProducao("declarar_func"));
                        addToStack(new Token(7, "}", "}", "C" + String.valueOf(label)));
                        addToStack(new RegraProducao("escopo"));
                        addToStack(new Token(6, "{", "{", "C" + String.valueOf(label)));

                        label = (labels_parentese.get(labels_parentese.size() - 1) + 1);
                        //, "P" + String.valueOf(label)
                        labels_parentese.add(label);

                        addToStack(new Token(5, ")", ")", "P" + String.valueOf(label)));
                        addToStack(new Token(4, "(", "(", "P" + String.valueOf(label)));
                        addToStack(new Token(3, "main", "main"));

                        //i++;
                    } else {
                        throw new SintaticException("Unexpected token '" + (tokensLexical.get(i).lexeme)
                                + "' at line " + tokensLexical.get(i).line + " (expected: 'main').");
                    }
                } else if (o1.method.equals("escopo")) {
                    String spe = "int, boolean, identificador, print, call, if, while, break, continue, }";
                    if (escopo(tokensLexical.get(i))) {
                        //<escopo> sempre tem q ser a primeira chamada (pra ser a ultima na pila)
                        if (tokensLexical.get(i).type == 16) {//DECLARAÇÃO INT
                            tokensLexical.get(i + 1).regra = "int";
                            addToStack(new RegraProducao("escopo"));
                            addToStack(new Token(8, ";", ";"));
                            addToStack(new Token(1, "<identificador>", "identificador"));
                            addToStack(new Token(16, "int", spe));
                        } else if (tokensLexical.get(i).type == 17) {//DECLARAÇÃO BOOLEAN
                            tokensLexical.get(i + 1).regra = "boolean";
                            addToStack(new RegraProducao("escopo"));
                            addToStack(new Token(8, ";", ";"));
                            addToStack(new Token(1, "<identificador>", "identificador"));
                            addToStack(new Token(17, "boolean", spe));
                        } else if (tokensLexical.get(i).type == 1) {//Atribuição
                            tokensLexical.get(i + 1).regra = "atrib";
                            addToStack(new RegraProducao("escopo"));
                            addToStack(new Token(8, ";", ";"));
                            addToStack(new RegraProducao("atrib"));
                            addToStack(new Token(10, "=", "="));
                            addToStack(new Token(1, "<identificador>", spe));

                            //throw new SintaticException("FAZER REGRAS DE <atrib>");
                        } else if (tokensLexical.get(i).type == 27) {//Print
                            int label = (labels_parentese.get(labels_parentese.size() - 1) + 1);
                            //, "P" + String.valueOf(label)
                            labels_parentese.add(label);
                            addToStack(new RegraProducao("escopo"));
                            addToStack(new Token(8, ";", ";"));
                            addToStack(new Token(5, ")", "false, true, identificador, constante, )", "P" + String.valueOf(label)));
                            addToStack(new RegraProducao("printar_sec"));
                            addToStack(new Token(4, "(", "(", "P" + String.valueOf(label)));
                            addToStack(new Token(27, "print", spe));
                        } else if (tokensLexical.get(i).type == 21) {//IF
                            int label = (labels_chaves.get(labels_chaves.size() - 1) + 1);
                            //, "C" + String.valueOf(label)
                            labels_chaves.add(label);
                            addToStack(new RegraProducao("escopo"));
                            addToStack(new RegraProducao("bloco_else"));
                            addToStack(new Token(7, "}", "}", "C" + String.valueOf(label)));
                            addToStack(new RegraProducao("escopo"));
                            addToStack(new Token(6, "{", "{", "C" + String.valueOf(label)));

                            label = (labels_parentese.get(labels_parentese.size() - 1) + 1);
                            //, "P" + String.valueOf(label)
                            labels_parentese.add(label);

                            addToStack(new Token(5, ")", ")", "P" + String.valueOf(label)));
                            addToStack(new RegraProducao("exp_logic"));
                            addToStack(new Token(4, "(", "(", "P" + String.valueOf(label)));

                            label = (labels_if.get(labels_if.size() - 1) + 1);
                            //, "I" + String.valueOf(label)
                            labels_if.add(label);
                            labels_if_atual.add(label);

                            addToStack(new Token(21, "if", spe, "I" + String.valueOf(label)));
                        } else if (tokensLexical.get(i).type == 23) {//WHILE
                            int label = (labels_chaves.get(labels_chaves.size() - 1) + 1);
                            //, "C" + String.valueOf(label)
                            labels_chaves.add(label);
                            addToStack(new RegraProducao("escopo"));
                            addToStack(new Token(7, "}", "}", "C" + String.valueOf(label)));
                            addToStack(new RegraProducao("escopo"));
                            addToStack(new Token(6, "{", "{", "C" + String.valueOf(label)));

                            label = (labels_parentese.get(labels_parentese.size() - 1) + 1);
                            //, "P" + String.valueOf(label)
                            labels_parentese.add(label);

                            addToStack(new Token(5, ")", ")", "P" + String.valueOf(label)));
                            addToStack(new RegraProducao("exp_logic"));
                            addToStack(new Token(4, "(", "(", "P" + String.valueOf(label)));
                            addToStack(new Token(23, "while", spe));
                        } else if (tokensLexical.get(i).type == 36) {//CALL
                            addToStack(new RegraProducao("escopo"));
                            addToStack(new Token(8, ";", ";"));
                            addToStack(new RegraProducao("chamar_func"));

                        } else if (tokensLexical.get(i).type == 18) {//BREAK
                            addToStack(new RegraProducao("escopo"));
                            addToStack(new Token(8, ";", ";"));
                            addToStack(new Token(18, "break", spe));
                        } else if (tokensLexical.get(i).type == 19) {//CONTINUE
                            addToStack(new RegraProducao("escopo"));
                            addToStack(new Token(8, ";", ";"));
                            addToStack(new Token(19, "continue", spe));
                        }

                    } else {
                        //throw new SintaticException("NÃO É <escopo>");
                        throw new SintaticException("Unexpected token '" + (tokensLexical.get(i).lexeme)
                                + "' at line " + tokensLexical.get(i).line + " (expected: '" + spe + "').");
                    }
                } else if (o1.method.equals("atrib")) {
                    //UTILIZANDO LOOK AHEAD
                    if (atrib(tokensLexical.get(i))) {
                        if (tokensLexical.get(i).type == 1//IDENTIFICADOR e PONTOVIRGULA
                                && lookAhead(tokensLexical.get(i + 1), new Token(8, ";"))) {
                            tokensLexical.get(i).regra = "exp_arit";
                            addToStack(new Token(1, "<identificador>", "identificador"));
                        } else if (tokensLexical.get(i).type == 0//COSNTANTE e PONTOVIRGULA
                                && lookAhead(tokensLexical.get(i + 1), new Token(8, ";"))) {
                            tokensLexical.get(i).regra = "exp_arit_int";
                            addToStack(new Token(0, "<numero>", "cosntante"));
                        } else if (tokensLexical.get(i).type == 25 //TRUE
                                && lookAhead(tokensLexical.get(i + 1), new Token(8, ";"))) {
                            tokensLexical.get(i).regra = "exp_logic_boolean";
                            addToStack(new Token(25, "true", "true"));
                        } else if (tokensLexical.get(i).type == 26 //FALSE
                                && lookAhead(tokensLexical.get(i + 1), new Token(8, ";"))) {
                            tokensLexical.get(i).regra = "exp_logic_boolean";
                            addToStack(new Token(26, "false", "false"));
                        } else if (tokensLexical.get(i).type == 36) {//CALL
                            addToStack(new RegraProducao("chamar_func"));
                        } else if (tokensLexical.get(i).type == 37) { //[ exp_arit
                            tokensLexical.get(i).regra = "exp_arit";
                            addToStack(new Token(38, "]", "]"));
                            addToStack(new RegraProducao("exp_arit"));
                            addToStack(new Token(37, "[", "["));
                        } else {
                            //tokensLexical.get(i).print();
                            tokensLexical.get(i - 1).regra += "_" + "exp_logic";
                            addToStack(new RegraProducao("exp_logic"));
                        }
                    } else {
                        throw new SintaticException("NÃO É <atrib>");
                    }
                } else if (o1.method.equals("chamar_func")) {
                    if (chamar_func(tokensLexical.get(i))) {
                        int label = (labels_parentese.get(labels_parentese.size() - 1) + 1);
                        //, "P" + String.valueOf(label)
                        labels_parentese.add(label);

                        tokensLexical.get(i).regra = "call_func";
                        tokensLexical.get(i + 1).regra = "func_iden";
                        addToStack(new Token(5, ")", ")", "P" + String.valueOf(label)));
                        addToStack(new RegraProducao("lista_arg"));
                        addToStack(new Token(4, "(", "(", "P" + String.valueOf(label)));
                        addToStack(new Token(1, "<identificador>", "identificador"));
                        addToStack(new Token(36, "call", "call"));
                    } else {
                        throw new SintaticException("Unexpected token '" + (tokensLexical.get(i).lexeme)
                                + "' at line " + tokensLexical.get(i).line + " (expected: 'call').");
                    }
                } else if (o1.method.equals("exp_arit")) {
                    if (exp_arit(tokensLexical.get(i))) {
                        if (tokensLexical.get(i).type == 1) {
                            tokensLexical.get(i).regra = "exp_arit_int";
                            addToStack(new RegraProducao("oper_arit"));
                            addToStack(new Token(1, "<identificador>", "identificador"));
                        } else if (tokensLexical.get(i).type == 0) {
                            tokensLexical.get(i).regra = "exp_arit_int";
                            addToStack(new RegraProducao("oper_arit"));
                            addToStack(new Token(0, "<numero>", "constante"));
                        } else if (tokensLexical.get(i).type == 4) {//(
                            int label = (labels_parentese.get(labels_parentese.size() - 1) + 1);
                            //, "C" + String.valueOf(label)
                            labels_parentese.add(label);

                            //tokensLexical.get(i).regra = ;
                            addToStack(new RegraProducao("oper_arit"));
                            addToStack(new Token(5, ")", ")", "P" + String.valueOf(label)));
                            addToStack(new RegraProducao("exp_arit"));
                            addToStack(new Token(4, "(", "(", "P" + String.valueOf(label)));
                        }
                    } else if (o1.dontPrintException) {
                        System.out.println("exp_arit >> o1.dontPrintException");
                    } else {
                        throw new SintaticException("Unexpected token '" + (tokensLexical.get(i).lexeme)
                                + "' at line " + tokensLexical.get(i).line + " (<exp_arit>).");
                    }
                } else if (o1.method.equals("oper_arit")) {
                    //PODE GERAR VAZIO
                    if (oper_arit(tokensLexical.get(i))) {
                        if (tokensLexical.get(i).type == 11) {
                            tokensLexical.get(i).regra = "exp_arit";
                            addToStack(new RegraProducao("exp_arit"));
                            addToStack(new Token(11, "+", "+"));
                        } else if (tokensLexical.get(i).type == 12) {
                            tokensLexical.get(i).regra = "exp_arit";
                            addToStack(new RegraProducao("exp_arit"));
                            addToStack(new Token(12, "-", "-"));
                        } else if (tokensLexical.get(i).type == 13) {
                            tokensLexical.get(i).regra = "exp_arit";
                            addToStack(new RegraProducao("exp_arit"));
                            addToStack(new Token(13, "*", "*"));
                        } else if (tokensLexical.get(i).type == 14) {
                            tokensLexical.get(i).regra = "exp_arit";
                            addToStack(new RegraProducao("exp_arit"));
                            addToStack(new Token(14, "/", "/"));
                        }

                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
                } else if (o1.method.equals("exp_logic")) {
                    if (exp_logic(tokensLexical.get(i))) {
                        if (tokensLexical.get(i).type == 1) {
                            tokensLexical.get(i).regra = "exp_logic";
                            addToStack(new RegraProducao("oper_logic"));
                            addToStack(new Token(1, "<identificador>", "identificador"));
                        } else if (tokensLexical.get(i).type == 0) {
                            tokensLexical.get(i).regra = "exp_logic_int";
                            addToStack(new RegraProducao("oper_logic"));
                            addToStack(new Token(0, "<numero>", "constante"));
                        } else if (tokensLexical.get(i).type == 25) {
                            tokensLexical.get(i).regra = "exp_logic_boolean";
                            addToStack(new RegraProducao("oper_logic"));
                            addToStack(new Token(25, "true", "true"));
                        } else if (tokensLexical.get(i).type == 26) {
                            tokensLexical.get(i).regra = "exp_logic_boolean";
                            addToStack(new RegraProducao("oper_logic"));
                            addToStack(new Token(26, "false", "false"));
                        } else if (tokensLexical.get(i).type == 4) {//(
                            int label = (labels_parentese.get(labels_parentese.size() - 1) + 1);
                            //, String.valueOf(label)
                            labels_parentese.add(label);
                            tokensLexical.get(i).regra = "exp_logic";
                            addToStack(new RegraProducao("exp_logic_cont"));
                            addToStack(new RegraProducao("oper_logic"));
                            addToStack(new Token(5, ")", ")", "P" + String.valueOf(label)));
                            addToStack(new RegraProducao("exp_logic"));
                            addToStack(new Token(4, "(", "(", "P" + String.valueOf(label)));
                        } else {
                            //Evitar que seja passada uma condição vazia.
                            throw new SintaticException("Unexpected token '" + (tokensLexical.get(i).lexeme)
                                    + "' at line " + tokensLexical.get(i).line + " (<exp_logic>).");
                        }

                    } else if (o1.dontPrintException) {
                        System.out.println("exp_logic >> o1.dontPrintException");

                    } else {
                        //throw new SintaticException("NÃO É <exp_logic>");

                        throw new SintaticException("Unexpected token '" + (tokensLexical.get(i).lexeme)
                                + "' at line " + tokensLexical.get(i).line + " (<exp_logic>).");
                    }

                } else if (o1.method.equals("oper_logic")) {
                    //PODE GERAR VAZIO
                    if (oper_logic(tokensLexical.get(i))) {
                        if (tokensLexical.get(i).type == 28) {
                            tokensLexical.get(i).regra = "exp_logic";
                            addToStack(new RegraProducao("exp_logic_cont"));
                            addToStack(new RegraProducao("exp_logic"));
                            addToStack(new Token(28, "<", "<"));
                        } else if (tokensLexical.get(i).type == 29) {
                            tokensLexical.get(i).regra = "exp_logic";
                            addToStack(new RegraProducao("exp_logic_cont"));
                            addToStack(new RegraProducao("exp_logic"));
                            addToStack(new Token(29, ">", ">"));
                        } else if (tokensLexical.get(i).type == 30) {
                            tokensLexical.get(i).regra = "exp_logic";
                            addToStack(new RegraProducao("exp_logic_cont"));
                            addToStack(new RegraProducao("exp_logic"));
                            addToStack(new Token(30, "<=", "<="));
                        } else if (tokensLexical.get(i).type == 31) {
                            tokensLexical.get(i).regra = "exp_logic";
                            addToStack(new RegraProducao("exp_logic_cont"));
                            addToStack(new RegraProducao("exp_logic"));
                            addToStack(new Token(31, ">=", ">="));
                        } else if (tokensLexical.get(i).type == 32) {
                            tokensLexical.get(i).regra = "exp_logic";
                            addToStack(new RegraProducao("exp_logic_cont"));
                            addToStack(new RegraProducao("exp_logic"));
                            addToStack(new Token(32, "==", "=="));
                        } else if (tokensLexical.get(i).type == 33) {
                            tokensLexical.get(i).regra = "exp_logic";
                            addToStack(new RegraProducao("exp_logic_cont"));
                            addToStack(new RegraProducao("exp_logic"));
                            addToStack(new Token(33, "!=", "!="));
                        }

                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
                } else if (o1.method.equals("exp_logic_cont")) {
                    //PODE GERAR VAZIO
                    if (oper_logic_cont(tokensLexical.get(i))) {
                        if (tokensLexical.get(i).type == 34) {
                            tokensLexical.get(i).regra = "exp_logic";
                            addToStack(new RegraProducao("exp_logic"));
                            addToStack(new Token(34, "&&", "&&"));
                        } else if (tokensLexical.get(i).type == 35) {
                            tokensLexical.get(i).regra = "exp_logic";
                            addToStack(new RegraProducao("exp_logic"));
                            addToStack(new Token(35, "||", "||"));
                        }
                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
                } else if (o1.method.equals("printar_sec")) {
                    //PODE GERAR VAZIO
                    if (printar_sec(tokensLexical.get(i))) {
                        String esp = "false, true, identificador, constante";
                        if (tokensLexical.get(i).type == 1) {//IDENTIFICADOR
                            addToStack(new Token(1, "<identificador>", esp));
                        } else if (tokensLexical.get(i).type == 0) {//CONSTANTE
                            addToStack(new Token(0, "<numero>", esp));
                        } else if (tokensLexical.get(i).type == 25) {//TRUE
                            addToStack(new Token(25, "true", esp));
                        } else if (tokensLexical.get(i).type == 26) {//FALSE
                            addToStack(new Token(26, "false", esp));
                        }
                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/

                } else if (o1.method.equals("bloco_else")) {
                    //PODE GERAR VAZIO
                    if (bloco_else(tokensLexical.get(i))) {
                        int label = (labels_chaves.get(labels_chaves.size() - 1) + 1);
                        //, "C" + String.valueOf(label)
                        labels_chaves.add(label);
                        addToStack(new Token(7, "}", "}", "C" + String.valueOf(label)));
                        addToStack(new RegraProducao("escopo"));
                        addToStack(new Token(6, "{", "{", "C" + String.valueOf(label)));

                        label = labels_if_atual.get(labels_if_atual.size() - 1);
                        labels_if_atual.remove(labels_if_atual.size() - 1);
                        addToStack(new Token(22, "else", "else", "I" + String.valueOf(label)));

                    } else {
                        //quando não tiver bloco if, removo o if de ifs atuais
                        labels_if_atual.remove(labels_if_atual.size() - 1);
                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
                } else if (o1.method.equals("lista_arg")) {
                    //PODE GERAR VAZIO
                    if (lista_arg(tokensLexical.get(i))) {
                        if (tokensLexical.get(i).type == 0) {//CONSTANTE
                            tokensLexical.get(i).regra = "arg_int";
                            addToStack(new RegraProducao("lista_arg_sec"));
                            addToStack(new Token(0, "<numero>", "constante"));
                        } else if (tokensLexical.get(i).type == 1) {//IDENTIFICADOR
                            tokensLexical.get(i).regra = "argument";
                            addToStack(new RegraProducao("lista_arg_sec"));
                            addToStack(new Token(1, "<identificador>", "identificador"));
                        } else if (tokensLexical.get(i).type == 25) {//TRUE
                            tokensLexical.get(i).regra = "arg_boolean";
                            addToStack(new RegraProducao("lista_arg_sec"));
                            addToStack(new Token(25, "true", "true"));
                        } else if (tokensLexical.get(i).type == 26) {//FALSE
                            tokensLexical.get(i).regra = "arg_boolean";
                            addToStack(new RegraProducao("lista_arg_sec"));
                            addToStack(new Token(26, "false", "false"));
                        }
                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
                } else if (o1.method.equals("lista_arg_sec")) {
                    if (lista_arg_sec(tokensLexical.get(i))) {
                        if (tokensLexical.get(i).type == 9) {//VIRGULA
                            addToStack(new RegraProducao("lista_arg_sec"));
                            addToStack(new RegraProducao("lista_arg_ter"));
                            addToStack(new Token(9, ",", ","));
                        }
                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
                } else if (o1.method.equals("lista_arg_ter")) {
                    String esp = "false, true, identificador, constante";
                    if (lista_arg_ter(tokensLexical.get(i))) {
                        if (tokensLexical.get(i).type == 0) {//CONSTANTE
                            tokensLexical.get(i).regra = "arg_int";
                            addToStack(new Token(0, "<numero>", esp));
                        } else if (tokensLexical.get(i).type == 1) {//IDENTIFICADOR
                            tokensLexical.get(i).regra = "argument";
                            addToStack(new Token(1, "<identificador>", esp));
                        } else if (tokensLexical.get(i).type == 25) {//TRUE
                            tokensLexical.get(i).regra = "arg_boolean";
                            addToStack(new Token(25, "true", esp));
                        } else if (tokensLexical.get(i).type == 26) {//FALSE
                            tokensLexical.get(i).regra = "arg_boolean";
                            addToStack(new Token(26, "false", esp));
                        }
                    } else {
                        throw new SintaticException("Unexpected token '" + (tokensLexical.get(i).lexeme)
                                + "' at line " + tokensLexical.get(i).line + " (expected: '" + esp + "').");
                    }
                } else if (o1.method.equals("declarar_func")) {
                    //PODE GERAR VAZIO
                    if (declarar_func(tokensLexical.get(i))) {
                        int label = (labels_chaves.get(labels_chaves.size() - 1) + 1);
                        //, "C" + String.valueOf(label)
                        labels_chaves.add(label);

                        addToStack(new RegraProducao("declarar_func"));
                        addToStack(new Token(7, "}", "}", "C" + String.valueOf(label)));
                        addToStack(new RegraProducao("retorno_func"));
                        addToStack(new RegraProducao("escopo"));
                        addToStack(new Token(6, "{", "{", "C" + String.valueOf(label)));

                        label = (labels_parentese.get(labels_parentese.size() - 1) + 1);
                        //, "P" + String.valueOf(label)
                        labels_parentese.add(label);

                        addToStack(new Token(5, ")", ")", "P" + String.valueOf(label)));
                        addToStack(new RegraProducao("lista_param"));
                        addToStack(new Token(4, "(", "(", "P" + String.valueOf(label)));
                        addToStack(new Token(1, "<identificador>", "identificador"));
                        addToStack(new RegraProducao("func_tipo"));
                        addToStack(new Token(24, "function", "function"));

                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
                } else if (o1.method.equals("func_tipo")) {
                    String esp = "void, int, boolean";
                    if (func_tipo(tokensLexical.get(i))) {
                        if (tokensLexical.get(i).type == 15) {//VOID
                            tokensLexical.get(i).regra = "func_void";
                            tokensLexical.get(i + 1).regra = "void";
                            addToStack(new Token(15, "void", esp));
                        } else if (tokensLexical.get(i).type == 16) {//INT
                            tokensLexical.get(i).regra = "func_int";
                            tokensLexical.get(i + 1).regra = "int";
                            addToStack(new Token(16, "int", esp));
                        } else if (tokensLexical.get(i).type == 17) {//BOOELAN
                            tokensLexical.get(i).regra = "func_bool";
                            tokensLexical.get(i + 1).regra = "boolean";
                            addToStack(new Token(17, "boolean", esp));
                        }
                    } else {
                        throw new SintaticException("Unexpected token '" + (tokensLexical.get(i).lexeme)
                                + "' at line " + tokensLexical.get(i).line + " (expected: '" + esp + "').");
                    }
                } else if (o1.method.equals("lista_param")) {
                    //PODE GERAR VAZIO
                    String esp = "int, boolean";
                    if (lista_param(tokensLexical.get(i))) {
                        if (tokensLexical.get(i).type == 16) {//INT
                            tokensLexical.get(i).regra = "param_type_int";
                            tokensLexical.get(i + 1).regra = "param_int";
                            addToStack(new RegraProducao("lista_param_sec"));
                            addToStack(new Token(1, "<identificador>", "identificador"));
                            addToStack(new Token(16, "int", esp));
                        } else if (tokensLexical.get(i).type == 17) {//BOOELAN
                            tokensLexical.get(i).regra = "param_type_boolean";
                            tokensLexical.get(i + 1).regra = "param_boolean";
                            addToStack(new RegraProducao("lista_param_sec"));
                            addToStack(new Token(1, "<identificador>", "identificador"));
                            addToStack(new Token(17, "boolean", esp));
                        }
                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
                } else if (o1.method.equals("lista_param_sec")) {
                    //PODE GERAR VAZIO
                    if (lista_param_sec(tokensLexical.get(i))) {
                        //tokenListFromLexical.get(i + 2).regra = "param_iden";
                        addToStack(new RegraProducao("lista_param_sec"));
                        addToStack(new Token(1, "<identificador>", "identificador"));
                        //System.out.println(">>>>>>>>>>>>>>>>>> " + tokenListFromLexical.get(i).type);
                        if (tokensLexical.get(i).type == 9//VIRGULA e INTEIRO
                                && lookAhead(tokensLexical.get(i + 1), new Token(16, "int", "int, boolean"))) {
                            tokensLexical.get(i + 1).regra = "param_type_int";
                            tokensLexical.get(i + 2).regra = "param_int";
                            addToStack(new Token(16, "int", "int, boolean"));
                        } else if (tokensLexical.get(i).type == 9//VIRGULA e BOLEAN
                                && lookAhead(tokensLexical.get(i + 1), new Token(17, "boolean", "int, boolean"))) {
                            tokensLexical.get(i + 1).regra = "param_type_boolean";
                            tokensLexical.get(i + 2).regra = "param_boolean";
                            addToStack(new Token(17, "int", "int, boolean"));
                        }

                        addToStack(new Token(9, ",", ","));

                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
                } else if (o1.method.equals("retorno_func")) {
                    //PODE GERAR VAZIO
                    if (retorno_func(tokensLexical.get(i))) {
                        addToStack(new Token(8, ";", ";"));
                        addToStack(new RegraProducao("retorno_func_sec"));
                        addToStack(new Token(20, "return", "return"));
                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
                } else if (o1.method.equals("retorno_func_sec")) {
                    //É o mesmo esquema da atribuição
                    if (retorno_func_sec(tokensLexical.get(i))) {
                        if (tokensLexical.get(i).type == 1//IDENTIFICADOR e PONTOVIRGULA
                                && lookAhead(tokensLexical.get(i + 1), new Token(8, ";"))) {
                            tokensLexical.get(i).regra = "ident";
                            addToStack(new Token(1, "<identificador>", "identificador"));
                        } else if (tokensLexical.get(i).type == 0//COSNTANTE e PONTOVIRGULA
                                && lookAhead(tokensLexical.get(i + 1), new Token(8, ";"))) {
                            tokensLexical.get(i).regra = "exp_arit_int";
                            addToStack(new Token(0, "<numero>", "constante"));
                        } else if (tokensLexical.get(i).type == 25 //TRUE
                                && lookAhead(tokensLexical.get(i + 1), new Token(8, ";"))) {
                            tokensLexical.get(i).regra = "exp_logic_boolean";
                            addToStack(new Token(25, "true", "true"));
                        } else if (tokensLexical.get(i).type == 26 //FALSE
                                && lookAhead(tokensLexical.get(i + 1), new Token(8, ";"))) {
                            tokensLexical.get(i).regra = "exp_logic_boolean";
                            addToStack(new Token(26, "false", "false"));
                        } else if (tokensLexical.get(i).type == 36) {//CALL
                            addToStack(new RegraProducao("chamar_func"));
                        } else if (tokensLexical.get(i).type == 37) { //[ exp_arit
                            tokensLexical.get(i).regra = "exp_arit";
                            addToStack(new Token(38, "]", "]"));
                            addToStack(new RegraProducao("exp_arit"));
                            addToStack(new Token(37, "[", "["));
                        } else {
                            addToStack(new RegraProducao("exp_logic"));
                        }
                    } else {
                        //throw new SintaticException("NÃO É <retorno_func_sec>");
                        throw new SintaticException("Unexpected token '" + (tokensLexical.get(i).lexeme)
                                + "' at line " + tokensLexical.get(i).line + " (expected: 'identificador, constante, true, false, <chamar_func>, [<exp_arit>], <exp_logic>').");
                    }
                }
                i--;
            }
            //}

            /**
             * Quando chega ao final, verificar se DECLARAR_FUNC é vazio
             */
            if (stack.get(stack.size() - 1) == null) {

                if (tokensLexical.get(i) instanceof Token) {
                    throw new SintaticException("Unexpected token '" + (tokensLexical.get(i).lexeme)
                            + "' at line " + tokensLexical.get(i).line + " (expected: '" + tokensLexical.get(i).description1 + "').");
                } else {
                    throw new SintaticException("Pilha foi lida mas o código fonte ainda não acabou.");
                }
            }
            if (i + 1 == tokensLexical.size() && stack.size() > 0) {
                //RegraProducao o1 = (RegraProducao) getNaPilha();

                Objeto o = getObjectFromStack();
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
                    String in = "";

                    String esp = "";
                    for (int x1 = 0; x1 < stack.size(); x1++) {
                        Objeto out = getObjectFromStack();
                        if (out instanceof Token) {
                            in += ((Token) out).lexeme + ", ";

                            if (x1 == 0) {
                                esp = ((Token) out).lexeme;
                            }
                        } else if (out instanceof RegraProducao) {
                            in += (((RegraProducao) out).method) + ", ";

                            if (x1 == 0) {
                                esp = ((RegraProducao) out).method;
                            }
                        }
                    }
                    throw new SintaticException("All source code was readed, but stack is not EMPTY (size: " + stack.size() + ", tokens: " + in + "). Expected: '" + esp + "'");
                }
            }
        }
    }

    private void addToStack(Objeto token) {
        stack.add(token);
    }

    private Objeto getObjectFromStack() {
        try {
            Objeto t = stack.get(stack.size() - 1);
            if (t instanceof Token) {
                stack.remove((Token) t);
            } else {
                stack.remove((RegraProducao) t);
            }

            return t;
        } catch (Exception ex) {
            return null;
        }

    }

    private boolean lookAhead(Token get, Token get0) {
        return get.type == get0.type;
    }

    private boolean retorno_func_sec(Token token) {
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

    private boolean exp_logic(Token token) {
        return token.type == 0
                || token.type == 1
                || token.type == 4
                || token.type == 5
                || token.type == 25
                || token.type == 26;

    }

    private boolean oper_arit(Token token) {
        return token.type == 11 || token.type == 12
                || token.type == 13 || token.type == 14;
    }

    private boolean oper_logic(Token token) {
        return token.type == 28
                || token.type == 29
                || token.type == 30
                || token.type == 31
                || token.type == 32
                || token.type == 33;
    }

    private boolean exp_arit(Token token) {
        return token.type == 0
                || token.type == 1
                || token.type == 4
                || token.type == 5;

    }

    private boolean chamar_func(Token token) {
        return token.type == 36;
    }

    private boolean atrib(Token token) {
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

    private boolean retorno_func(Token token) {
        return token.type == 20;

    }

    private boolean lista_param_sec(Token token) {
        return token.type == 9;
    }

    private boolean lista_param(Token token) {
        return token.type == 16 || token.type == 17;
    }

    private boolean func_tipo(Token token) {
        return token.type == 15 || token.type == 16 || token.type == 17;
    }

    private boolean declarar_func(Token token) {
        return token.type == 24;
    }

    private boolean lista_arg_ter(Token token) {
        return token.type == 1 || token.type == 0
                || token.type == 25 || token.type == 26;
    }

    private boolean lista_arg_sec(Token token) {
        return token.type == 9;
    }

    private boolean lista_arg(Token token) {
        return token.type == 1 || token.type == 0
                || token.type == 25 || token.type == 26;
    }

    private boolean bloco_else(Token token) {
        return token.type == 22;
    }

    private boolean printar_sec(Token token) {
        return token.type == 1 || token.type == 0
                || token.type == 25 || token.type == 26;
    }

    private boolean printar(Token token) {
        return token.type == 27;
    }

    private boolean escopo(Token token) {
        return token.type == 16 || token.type == 17
                || token.type == 18 || token.type == 19
                || token.type == 1 || token.type == 27
                || token.type == 36 || token.type == 21
                || token.type == 23 || token.type == 7
                || token.type == 20;
    }

    private boolean programa(Token token) {
        return token.type == 3;
    }

    private boolean oper_logic_cont(Token token) {
        return token.type == 34 || token.type == 35;
    }
}
