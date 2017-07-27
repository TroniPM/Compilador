package br.com.paulomatew.compilador.analyzer.sintatic;

import br.com.paulomatew.compilador.entities.RegraProducao;
import br.com.paulomatew.compilador.entities.LexicalToken;
import br.com.paulomatew.compilador.entities.Objeto;
import br.com.paulomatew.compilador.exceptions.SintaticException;
import java.util.ArrayList;

/**
 * Created by Paulo Mateus on 26/06/2017. For project Compiladores
 * (https://github.com/TroniPM/Compilador) Contact: <paulomatew@gmail.com>
 */
public class Sintatic {

    public ArrayList<LexicalToken> tokenListFromLexical = null;
    private ArrayList<Objeto> stack = null;
    public String stackState = null;
    private int iteracao = 1;

    public void init(ArrayList<LexicalToken> arr) throws SintaticException {
        /*if (arr == null || arr.isEmpty()) {
            throw new SintaticException("Nenhum token encontrado.");
        }*/

        this.tokenListFromLexical = arr;

        parserNew();
    }

    private void printStack() {
        for (int i = stack.size() - 1; i >= 0; i--) {
            Objeto o = stack.get(i);
            if (o instanceof LexicalToken) {
                System.out.println(((LexicalToken) o).lexeme);
            } else if (o instanceof RegraProducao) {
                System.out.println(((RegraProducao) o).method);
            }
        }
    }

    private void saveStackState(String atual) {
        stackState += (iteracao++) + ") -------------- ( " + atual + " )\n";

        //TODO inverter posição da pilha
        for (int i = stack.size() - 1; i >= 0; i--) {
            if (stack.get(i) instanceof LexicalToken) {
                stackState += ((LexicalToken) stack.get(i)).lexeme + "\n";
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
        //ArrayList<LexicalToken> tokenListFromLexical = new ArrayList<>();
        /*for (LexicalToken in : tokenListFromLexical) {
            tokenListFromLexical.add(in);
        }*/

        stack = new ArrayList<>();

        stackState = "";
        iteracao = 1;

        addToStack(new LexicalToken(99, "$", "FIM DO PROGRAMA ($), <declarar_func>"));
        addToStack(new RegraProducao("programa"));
        //salvarEstadoDaPilha("programa");

        if (tokenListFromLexical.size() == 0) {
            throw new SintaticException("Unexpected token 'EMPTY' at line 1, position 1 (expected: 'main').");
        }
        for (int i = 0; i < tokenListFromLexical.size(); i++) {
            Objeto tokenDaPilha = getObjectFromStack();
            if (tokenDaPilha instanceof LexicalToken) {

                LexicalToken o1 = (LexicalToken) tokenDaPilha;
                saveStackState(o1.lexeme);

                if (tokenListFromLexical.get(i).type != o1.type) {
                    //o1.print();
                    //token.print();
                    throw new SintaticException("Unexpected token '" + (tokenListFromLexical.get(i).lexeme)
                            + "' at line " + tokenListFromLexical.get(i).line + ", position " + tokenListFromLexical.get(i).position
                            + " (expected: '" + o1.description + "').");
                } else {
                    //throw new SintaticException("TOKEN DIFERENTE. COMPILADOR NÃO RECONHECE ESSA LINGUAGEM");
                }

            } else if (tokenDaPilha instanceof RegraProducao) {
                RegraProducao o1 = (RegraProducao) tokenDaPilha;

                saveStackState(o1.method);

                if (o1.method.equals("programa")) {
                    //throw new SintaticException("CHAMOU PROGRAMA. NÃO DEVERIA");
                    if (programa(tokenListFromLexical.get(i))) {
                        addToStack(new RegraProducao("declarar_func"));
                        addToStack(new LexicalToken(7, "}", "}"));
                        addToStack(new RegraProducao("escopo"));
                        addToStack(new LexicalToken(6, "{", "{"));
                        addToStack(new LexicalToken(5, ")", ")"));
                        addToStack(new LexicalToken(4, "(", "("));
                        addToStack(new LexicalToken(3, "main", "main"));

                        //i++;
                    } else {
                        throw new SintaticException("Unexpected token '" + (tokenListFromLexical.get(i).lexeme)
                                + "' at line " + tokenListFromLexical.get(i).line + " (expected: 'main').");
                    }
                } else if (o1.method.equals("escopo")) {
                    String spe = "int, boolean, identificador, print, call, if, while, break, continue, }";
                    if (escopo(tokenListFromLexical.get(i))) {
                        //<escopo> sempre tem q ser a primeira chamada (pra ser a ultima na pila)
                        if (tokenListFromLexical.get(i).type == 16) {//DECLARAÇÃO INT
                            tokenListFromLexical.get(i + 1).regra = "int";
                            addToStack(new RegraProducao("escopo"));
                            addToStack(new LexicalToken(8, ";", ";"));
                            addToStack(new LexicalToken(1, "<identificador>", "identificador"));
                            addToStack(new LexicalToken(16, "int", spe));
                        } else if (tokenListFromLexical.get(i).type == 17) {//DECLARAÇÃO BOOLEAN
                            tokenListFromLexical.get(i + 1).regra = "boolean";
                            addToStack(new RegraProducao("escopo"));
                            addToStack(new LexicalToken(8, ";", ";"));
                            addToStack(new LexicalToken(1, "<identificador>", "identificador"));
                            addToStack(new LexicalToken(17, "boolean", spe));
                        } else if (tokenListFromLexical.get(i).type == 1) {//Atribuição
                            tokenListFromLexical.get(i + 1).regra = "atrib";
                            addToStack(new RegraProducao("escopo"));
                            addToStack(new LexicalToken(8, ";", ";"));
                            addToStack(new RegraProducao("atrib"));
                            addToStack(new LexicalToken(10, "=", "="));
                            addToStack(new LexicalToken(1, "<identificador>", spe));

                            //throw new SintaticException("FAZER REGRAS DE <atrib>");
                        } else if (tokenListFromLexical.get(i).type == 27) {//Print
                            addToStack(new RegraProducao("escopo"));
                            addToStack(new LexicalToken(8, ";", ";"));
                            addToStack(new LexicalToken(5, ")", "false, true, identificador, constante, )"));
                            addToStack(new RegraProducao("printar_sec"));
                            addToStack(new LexicalToken(4, "(", "("));
                            addToStack(new LexicalToken(27, "print", spe));
                        } else if (tokenListFromLexical.get(i).type == 21) {//IF
                            addToStack(new RegraProducao("escopo"));
                            addToStack(new RegraProducao("bloco_else"));
                            addToStack(new LexicalToken(7, "}", "}"));
                            addToStack(new RegraProducao("escopo"));
                            addToStack(new LexicalToken(6, "{", "{"));
                            addToStack(new LexicalToken(5, ")", ")"));
                            addToStack(new RegraProducao("exp_logic"));
                            addToStack(new LexicalToken(4, "(", "("));
                            addToStack(new LexicalToken(21, "if", spe));
                        } else if (tokenListFromLexical.get(i).type == 23) {//WHILE
                            addToStack(new RegraProducao("escopo"));
                            addToStack(new LexicalToken(7, "}", "}"));
                            addToStack(new RegraProducao("escopo"));
                            addToStack(new LexicalToken(6, "{", "{"));
                            addToStack(new LexicalToken(5, ")", ")"));
                            addToStack(new RegraProducao("exp_logic"));
                            addToStack(new LexicalToken(4, "(", "("));
                            addToStack(new LexicalToken(23, "while", spe));
                        } else if (tokenListFromLexical.get(i).type == 36) {//CALL
                            addToStack(new RegraProducao("escopo"));
                            addToStack(new LexicalToken(8, ";", ";"));
                            addToStack(new RegraProducao("chamar_func"));

                        } else if (tokenListFromLexical.get(i).type == 18) {//BREAK
                            addToStack(new RegraProducao("escopo"));
                            addToStack(new LexicalToken(8, ";", ";"));
                            addToStack(new LexicalToken(18, "break", spe));
                        } else if (tokenListFromLexical.get(i).type == 19) {//CONTINUE
                            addToStack(new RegraProducao("escopo"));
                            addToStack(new LexicalToken(8, ";", ";"));
                            addToStack(new LexicalToken(19, "continue", spe));
                        }

                    } else {
                        //throw new SintaticException("NÃO É <escopo>");
                        throw new SintaticException("Unexpected token '" + (tokenListFromLexical.get(i).lexeme)
                                + "' at line " + tokenListFromLexical.get(i).line + " (expected: '" + spe + "').");
                    }
                } else if (o1.method.equals("atrib")) {
                    //UTILIZANDO LOOK AHEAD
                    if (atrib(tokenListFromLexical.get(i))) {
                        if (tokenListFromLexical.get(i).type == 1//IDENTIFICADOR e PONTOVIRGULA
                                && lookAhead(tokenListFromLexical.get(i + 1), new LexicalToken(8, ";"))) {
                            tokenListFromLexical.get(i).regra = "exp_arit";
                            addToStack(new LexicalToken(1, "<identificador>", "identificador"));
                        } else if (tokenListFromLexical.get(i).type == 0//COSNTANTE e PONTOVIRGULA
                                && lookAhead(tokenListFromLexical.get(i + 1), new LexicalToken(8, ";"))) {
                            tokenListFromLexical.get(i).regra = "exp_arit";
                            addToStack(new LexicalToken(0, "<numero>", "cosntante"));
                        } else if (tokenListFromLexical.get(i).type == 25 //TRUE
                                && lookAhead(tokenListFromLexical.get(i + 1), new LexicalToken(8, ";"))) {
                            tokenListFromLexical.get(i).regra = "exp_logic";
                            addToStack(new LexicalToken(25, "true", "true"));
                        } else if (tokenListFromLexical.get(i).type == 26 //FALSE
                                && lookAhead(tokenListFromLexical.get(i + 1), new LexicalToken(8, ";"))) {
                            tokenListFromLexical.get(i).regra = "exp_logic";
                            addToStack(new LexicalToken(26, "false", "false"));
                        } else if (tokenListFromLexical.get(i).type == 36) {//CALL
                            addToStack(new RegraProducao("chamar_func"));
                        } else if (tokenListFromLexical.get(i).type == 37) { //[ exp_arit
                            tokenListFromLexical.get(i).regra = "exp_arit";
                            addToStack(new LexicalToken(38, "]", "]"));
                            addToStack(new RegraProducao("exp_arit"));
                            addToStack(new LexicalToken(37, "[", "["));
                        } else {
                            addToStack(new RegraProducao("exp_logic"));
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
                    if (chamar_func(tokenListFromLexical.get(i))) {
                        tokenListFromLexical.get(i).regra = "call_func";
                        tokenListFromLexical.get(i + 1).regra = "func_iden";
                        addToStack(new LexicalToken(5, ")", ")"));
                        addToStack(new RegraProducao("lista_arg"));
                        addToStack(new LexicalToken(4, "(", "("));
                        addToStack(new LexicalToken(1, "<identificador>", "identificador"));
                        addToStack(new LexicalToken(36, "call", "call"));
                    } else {
                        throw new SintaticException("Unexpected token '" + (tokenListFromLexical.get(i).lexeme)
                                + "' at line " + tokenListFromLexical.get(i).line + " (expected: 'call').");
                    }
                } else if (o1.method.equals("exp_arit")) {
                    if (exp_arit(tokenListFromLexical.get(i))) {
                        if (tokenListFromLexical.get(i).type == 1) {
                            tokenListFromLexical.get(i).regra = "exp_arit";
                            addToStack(new RegraProducao("oper_arit"));
                            addToStack(new LexicalToken(1, "<identificador>", "identificador"));
                        } else if (tokenListFromLexical.get(i).type == 0) {
                            tokenListFromLexical.get(i).regra = "exp_arit";
                            addToStack(new RegraProducao("oper_arit"));
                            addToStack(new LexicalToken(0, "<numero>", "constante"));
                        } else if (tokenListFromLexical.get(i).type == 4) {//(
                            tokenListFromLexical.get(i).regra = "exp_arit";
                            addToStack(new RegraProducao("oper_arit"));
                            addToStack(new LexicalToken(5, ")", ")"));
                            addToStack(new RegraProducao("exp_arit"));
                            addToStack(new LexicalToken(4, "(", "("));
                        }
                    } else if (o1.dontPrintException) {
                        System.out.println("exp_arit >> o1.dontPrintException");
                    } else {
                        throw new SintaticException("Unexpected token '" + (tokenListFromLexical.get(i).lexeme)
                                + "' at line " + tokenListFromLexical.get(i).line + " (<exp_arit>).");
                    }
                } else if (o1.method.equals("oper_arit")) {
                    //PODE GERAR VAZIO
                    if (oper_arit(tokenListFromLexical.get(i))) {
                        if (tokenListFromLexical.get(i).type == 11) {
                            tokenListFromLexical.get(i).regra = "exp_arit";
                            addToStack(new RegraProducao("exp_arit"));
                            addToStack(new LexicalToken(11, "+", "+"));
                        } else if (tokenListFromLexical.get(i).type == 12) {
                            tokenListFromLexical.get(i).regra = "exp_arit";
                            addToStack(new RegraProducao("exp_arit"));
                            addToStack(new LexicalToken(12, "-", "-"));
                        } else if (tokenListFromLexical.get(i).type == 13) {
                            tokenListFromLexical.get(i).regra = "exp_arit";
                            addToStack(new RegraProducao("exp_arit"));
                            addToStack(new LexicalToken(13, "*", "*"));
                        } else if (tokenListFromLexical.get(i).type == 14) {
                            tokenListFromLexical.get(i).regra = "exp_arit";
                            addToStack(new RegraProducao("exp_arit"));
                            addToStack(new LexicalToken(14, "/", "/"));
                        }

                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
                } else if (o1.method.equals("exp_logic")) {
                    if (exp_logic(tokenListFromLexical.get(i))) {
                        if (tokenListFromLexical.get(i).type == 1) {
                            tokenListFromLexical.get(i).regra = "exp_logic";
                            addToStack(new RegraProducao("oper_logic"));
                            addToStack(new LexicalToken(1, "<identificador>", "identificador"));
                        } else if (tokenListFromLexical.get(i).type == 0) {
                            tokenListFromLexical.get(i).regra = "exp_logic";
                            addToStack(new RegraProducao("oper_logic"));
                            addToStack(new LexicalToken(0, "<numero>", "constante"));
                        } else if (tokenListFromLexical.get(i).type == 25) {
                            tokenListFromLexical.get(i).regra = "exp_logic";
                            addToStack(new RegraProducao("oper_logic"));
                            addToStack(new LexicalToken(25, "true", "true"));
                        } else if (tokenListFromLexical.get(i).type == 26) {
                            tokenListFromLexical.get(i).regra = "exp_logic";
                            addToStack(new RegraProducao("oper_logic"));
                            addToStack(new LexicalToken(26, "false", "false"));
                        } else if (tokenListFromLexical.get(i).type == 4) {//(
                            tokenListFromLexical.get(i).regra = "exp_logic";
                            addToStack(new RegraProducao("exp_logic_cont"));
                            addToStack(new RegraProducao("oper_logic"));
                            addToStack(new LexicalToken(5, ")", ")"));
                            addToStack(new RegraProducao("exp_logic"));
                            addToStack(new LexicalToken(4, "(", "("));
                        } else {
                            //Evitar que seja passada uma condição vazia.
                            throw new SintaticException("Unexpected token '" + (tokenListFromLexical.get(i).lexeme)
                                    + "' at line " + tokenListFromLexical.get(i).line + " (<exp_logic>).");
                        }

                    } else if (o1.dontPrintException) {
                        System.out.println("exp_logic >> o1.dontPrintException");

                    } else {
                        //throw new SintaticException("NÃO É <exp_logic>");

                        throw new SintaticException("Unexpected token '" + (tokenListFromLexical.get(i).lexeme)
                                + "' at line " + tokenListFromLexical.get(i).line + " (<exp_logic>).");
                    }

                } else if (o1.method.equals("oper_logic")) {
                    //PODE GERAR VAZIO
                    if (oper_logic(tokenListFromLexical.get(i))) {
                        if (tokenListFromLexical.get(i).type == 28) {
                            tokenListFromLexical.get(i).regra = "exp_logic";
                            addToStack(new RegraProducao("exp_logic_cont"));
                            addToStack(new RegraProducao("exp_logic"));
                            addToStack(new LexicalToken(28, "<", "<"));
                        } else if (tokenListFromLexical.get(i).type == 29) {
                            tokenListFromLexical.get(i).regra = "exp_logic";
                            addToStack(new RegraProducao("exp_logic_cont"));
                            addToStack(new RegraProducao("exp_logic"));
                            addToStack(new LexicalToken(29, ">", ">"));
                        } else if (tokenListFromLexical.get(i).type == 30) {
                            tokenListFromLexical.get(i).regra = "exp_logic";
                            addToStack(new RegraProducao("exp_logic_cont"));
                            addToStack(new RegraProducao("exp_logic"));
                            addToStack(new LexicalToken(30, "<=", "<="));
                        } else if (tokenListFromLexical.get(i).type == 31) {
                            tokenListFromLexical.get(i).regra = "exp_logic";
                            addToStack(new RegraProducao("exp_logic_cont"));
                            addToStack(new RegraProducao("exp_logic"));
                            addToStack(new LexicalToken(31, ">=", ">="));
                        } else if (tokenListFromLexical.get(i).type == 32) {
                            tokenListFromLexical.get(i).regra = "exp_logic";
                            addToStack(new RegraProducao("exp_logic_cont"));
                            addToStack(new RegraProducao("exp_logic"));
                            addToStack(new LexicalToken(32, "==", "=="));
                        } else if (tokenListFromLexical.get(i).type == 33) {
                            tokenListFromLexical.get(i).regra = "exp_logic";
                            addToStack(new RegraProducao("exp_logic_cont"));
                            addToStack(new RegraProducao("exp_logic"));
                            addToStack(new LexicalToken(33, "!=", "!="));
                        }

                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
                } else if (o1.method.equals("exp_logic_cont")) {
                    //PODE GERAR VAZIO
                    if (oper_logic_cont(tokenListFromLexical.get(i))) {
                        if (tokenListFromLexical.get(i).type == 34) {
                            tokenListFromLexical.get(i).regra = "exp_logic";
                            addToStack(new RegraProducao("exp_logic"));
                            addToStack(new LexicalToken(34, "&&", "&&"));
                        } else if (tokenListFromLexical.get(i).type == 35) {
                            tokenListFromLexical.get(i).regra = "exp_logic";
                            addToStack(new RegraProducao("exp_logic"));
                            addToStack(new LexicalToken(35, "||", "||"));
                        }
                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
                } else if (o1.method.equals("printar_sec")) {
                    //PODE GERAR VAZIO
                    if (printar_sec(tokenListFromLexical.get(i))) {
                        String esp = "false, true, identificador, constante";
                        if (tokenListFromLexical.get(i).type == 1) {//IDENTIFICADOR
                            addToStack(new LexicalToken(1, "<identificador>", esp));
                        } else if (tokenListFromLexical.get(i).type == 0) {//CONSTANTE
                            addToStack(new LexicalToken(0, "<numero>", esp));
                        } else if (tokenListFromLexical.get(i).type == 25) {//TRUE
                            addToStack(new LexicalToken(25, "true", esp));
                        } else if (tokenListFromLexical.get(i).type == 26) {//FALSE
                            addToStack(new LexicalToken(26, "false", esp));
                        }
                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/

                } else if (o1.method.equals("bloco_else")) {
                    //PODE GERAR VAZIO
                    if (bloco_else(tokenListFromLexical.get(i))) {
                        addToStack(new LexicalToken(7, "}", "}"));
                        addToStack(new RegraProducao("escopo"));
                        addToStack(new LexicalToken(6, "{", "{"));
                        addToStack(new LexicalToken(22, "else", "else"));

                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
                } else if (o1.method.equals("lista_arg")) {
                    //PODE GERAR VAZIO
                    if (lista_arg(tokenListFromLexical.get(i))) {
                        if (tokenListFromLexical.get(i).type == 0) {//CONSTANTE
                            tokenListFromLexical.get(i).regra = "arg_int";
                            addToStack(new RegraProducao("lista_arg_sec"));
                            addToStack(new LexicalToken(0, "<numero>", "constante"));
                        } else if (tokenListFromLexical.get(i).type == 1) {//IDENTIFICADOR
                            tokenListFromLexical.get(i).regra = "argument";
                            addToStack(new RegraProducao("lista_arg_sec"));
                            addToStack(new LexicalToken(1, "<identificador>", "identificador"));
                        } else if (tokenListFromLexical.get(i).type == 25) {//TRUE
                            tokenListFromLexical.get(i).regra = "arg_bool";
                            addToStack(new RegraProducao("lista_arg_sec"));
                            addToStack(new LexicalToken(25, "true", "true"));
                        } else if (tokenListFromLexical.get(i).type == 26) {//FALSE
                            tokenListFromLexical.get(i).regra = "arg_bool";
                            addToStack(new RegraProducao("lista_arg_sec"));
                            addToStack(new LexicalToken(26, "false", "false"));
                        }
                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
                } else if (o1.method.equals("lista_arg_sec")) {
                    if (lista_arg_sec(tokenListFromLexical.get(i))) {
                        if (tokenListFromLexical.get(i).type == 9) {//VIRGULA
                            addToStack(new RegraProducao("lista_arg_sec"));
                            addToStack(new RegraProducao("lista_arg_ter"));
                            addToStack(new LexicalToken(9, ",", ","));
                        }
                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
                } else if (o1.method.equals("lista_arg_ter")) {
                    String esp = "false, true, identificador, constante";
                    if (lista_arg_ter(tokenListFromLexical.get(i))) {
                        if (tokenListFromLexical.get(i).type == 0) {//CONSTANTE
                            tokenListFromLexical.get(i).regra = "arg_int";
                            addToStack(new LexicalToken(0, "<numero>", esp));
                        } else if (tokenListFromLexical.get(i).type == 1) {//IDENTIFICADOR
                            tokenListFromLexical.get(i).regra = "argument";
                            addToStack(new LexicalToken(1, "<identificador>", esp));
                        } else if (tokenListFromLexical.get(i).type == 25) {//TRUE
                            tokenListFromLexical.get(i).regra = "arg_bool";
                            addToStack(new LexicalToken(25, "true", esp));
                        } else if (tokenListFromLexical.get(i).type == 26) {//FALSE
                            tokenListFromLexical.get(i).regra = "arg_bool";
                            addToStack(new LexicalToken(26, "false", esp));
                        }
                    } else {
                        throw new SintaticException("Unexpected token '" + (tokenListFromLexical.get(i).lexeme)
                                + "' at line " + tokenListFromLexical.get(i).line + " (expected: '" + esp + "').");
                    }
                } else if (o1.method.equals("declarar_func")) {
                    //PODE GERAR VAZIO
                    if (declarar_func(tokenListFromLexical.get(i))) {
                        addToStack(new RegraProducao("declarar_func"));
                        addToStack(new LexicalToken(7, "}", "}"));
                        addToStack(new RegraProducao("retorno_func"));
                        addToStack(new RegraProducao("escopo"));
                        addToStack(new LexicalToken(6, "{", "{"));
                        addToStack(new LexicalToken(5, ")", ")"));
                        addToStack(new RegraProducao("lista_param"));
                        addToStack(new LexicalToken(4, "(", "("));
                        addToStack(new LexicalToken(1, "<identificador>", "identificador"));
                        addToStack(new RegraProducao("func_tipo"));
                        addToStack(new LexicalToken(24, "function", "function"));

                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
                } else if (o1.method.equals("func_tipo")) {
                    String esp = "void, int, boolean";
                    if (func_tipo(tokenListFromLexical.get(i))) {
                        if (tokenListFromLexical.get(i).type == 15) {//VOID
                            tokenListFromLexical.get(i).regra = "func_void";
                            tokenListFromLexical.get(i + 1).regra = "void";
                            addToStack(new LexicalToken(15, "void", esp));
                        } else if (tokenListFromLexical.get(i).type == 16) {//INT
                            tokenListFromLexical.get(i).regra = "func_int";
                            tokenListFromLexical.get(i + 1).regra = "int";
                            addToStack(new LexicalToken(16, "int", esp));
                        } else if (tokenListFromLexical.get(i).type == 17) {//BOOELAN
                            tokenListFromLexical.get(i).regra = "func_bool";
                            tokenListFromLexical.get(i + 1).regra = "boolean";
                            addToStack(new LexicalToken(17, "boolean", esp));
                        }
                    } else {
                        throw new SintaticException("Unexpected token '" + (tokenListFromLexical.get(i).lexeme)
                                + "' at line " + tokenListFromLexical.get(i).line + " (expected: '" + esp + "').");
                    }
                } else if (o1.method.equals("lista_param")) {
                    //PODE GERAR VAZIO
                    String esp = "int, boolean";
                    if (lista_param(tokenListFromLexical.get(i))) {
                        if (tokenListFromLexical.get(i).type == 16) {//INT
                            tokenListFromLexical.get(i).regra = "param_type";
                            tokenListFromLexical.get(i + 1).regra = "param_int";
                            addToStack(new RegraProducao("lista_param_sec"));
                            addToStack(new LexicalToken(1, "<identificador>", "identificador"));
                            addToStack(new LexicalToken(16, "int", esp));
                        } else if (tokenListFromLexical.get(i).type == 17) {//BOOELAN
                            tokenListFromLexical.get(i).regra = "param_type";
                            tokenListFromLexical.get(i + 1).regra = "param_boolean";
                            addToStack(new RegraProducao("lista_param_sec"));
                            addToStack(new LexicalToken(1, "<identificador>", "identificador"));
                            addToStack(new LexicalToken(17, "boolean", esp));
                        }
                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
                } else if (o1.method.equals("lista_param_sec")) {
                    //PODE GERAR VAZIO
                    if (lista_param_sec(tokenListFromLexical.get(i))) {
                        tokenListFromLexical.get(i + 2).regra = "param_iden";
                        addToStack(new RegraProducao("lista_param_sec"));
                        addToStack(new LexicalToken(1, "<identificador>", "identificador"));
                        //System.out.println(">>>>>>>>>>>>>>>>>> " + tokenListFromLexical.get(i).type);
                        if (tokenListFromLexical.get(i).type == 9//VIRGULA e INTEIRO
                                && lookAhead(tokenListFromLexical.get(i + 1), new LexicalToken(16, "int", "int, boolean"))) {
                            tokenListFromLexical.get(i + 1).regra = "param_type";
                            addToStack(new LexicalToken(16, "int", "int, boolean"));
                        } else if (tokenListFromLexical.get(i).type == 9//VIRGULA e BOLEAN
                                && lookAhead(tokenListFromLexical.get(i + 1), new LexicalToken(17, "boolean", "int, boolean"))) {
                            tokenListFromLexical.get(i + 1).regra = "param_type";
                            addToStack(new LexicalToken(17, "int", "int, boolean"));
                        }

                        /*if (tokenListFromLexical.get(i).type == 16) {//INT
                            System.out.println("ENTROU NO INT");
                            addNaPilha(new LexicalToken(16, "int", "int, boolean"));
                        } else if (tokenListFromLexical.get(i).type == 17) {//BOOELAN
                            System.out.println("ENTROU NO BOOELAN");
                            addNaPilha(new LexicalToken(17, "boolean", "int, boolean"));
                        }*/
                        addToStack(new LexicalToken(9, ",", ","));

                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
                } else if (o1.method.equals("retorno_func")) {
                    //PODE GERAR VAZIO
                    if (retorno_func(tokenListFromLexical.get(i))) {
                        addToStack(new LexicalToken(8, ";", ";"));
                        addToStack(new RegraProducao("retorno_func_sec"));
                        addToStack(new LexicalToken(20, "return", "return"));
                    }
                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
                } else if (o1.method.equals("retorno_func_sec")) {
                    //É o mesmo esquema da atribuição
                    if (retorno_func_sec(tokenListFromLexical.get(i))) {
                        if (tokenListFromLexical.get(i).type == 1//IDENTIFICADOR e PONTOVIRGULA
                                && lookAhead(tokenListFromLexical.get(i + 1), new LexicalToken(8, ";"))) {
                            tokenListFromLexical.get(i).regra = "0";
                            addToStack(new LexicalToken(1, "<identificador>", "identificador"));
                        } else if (tokenListFromLexical.get(i).type == 0//COSNTANTE e PONTOVIRGULA
                                && lookAhead(tokenListFromLexical.get(i + 1), new LexicalToken(8, ";"))) {
                            tokenListFromLexical.get(i).regra = "0";
                            addToStack(new LexicalToken(0, "<numero>", "constante"));
                        } else if (tokenListFromLexical.get(i).type == 25 //TRUE
                                && lookAhead(tokenListFromLexical.get(i + 1), new LexicalToken(8, ";"))) {
                            tokenListFromLexical.get(i).regra = "0";
                            addToStack(new LexicalToken(25, "true", "true"));
                        } else if (tokenListFromLexical.get(i).type == 26 //FALSE
                                && lookAhead(tokenListFromLexical.get(i + 1), new LexicalToken(8, ";"))) {
                            tokenListFromLexical.get(i).regra = "0";
                            addToStack(new LexicalToken(26, "false", "false"));
                        } else if (tokenListFromLexical.get(i).type == 36) {//CALL
                            addToStack(new RegraProducao("chamar_func"));
                        } else if (tokenListFromLexical.get(i).type == 37) { //[ exp_arit
                            tokenListFromLexical.get(i).regra = "exp_arit";
                            addToStack(new LexicalToken(38, "]", "]"));
                            addToStack(new RegraProducao("exp_arit"));
                            addToStack(new LexicalToken(37, "[", "["));
                        } else {
                            addToStack(new RegraProducao("exp_logic"));
                        }
                    } else {
                        //throw new SintaticException("NÃO É <retorno_func_sec>");
                        throw new SintaticException("Unexpected token '" + (tokenListFromLexical.get(i).lexeme)
                                + "' at line " + tokenListFromLexical.get(i).line + " (expected: 'identificador, constante, true, false, <chamar_func>, [<exp_arit>], <exp_logic>').");
                    }
                }
                i--;
            }
            //}

            /**
             * Quando chega ao final, verificar se DECLARAR_FUNC é vazio
             */
            if (stack.get(stack.size() - 1) == null) {

                if (tokenListFromLexical.get(i) instanceof LexicalToken) {
                    throw new SintaticException("Unexpected token '" + (tokenListFromLexical.get(i).lexeme)
                            + "' at line " + tokenListFromLexical.get(i).line + " (expected: '" + tokenListFromLexical.get(i).description + "').");
                } else {
                    throw new SintaticException("Pilha foi lida mas o código fonte ainda não acabou.");
                }
            }
            if (i + 1 == tokenListFromLexical.size() && stack.size() > 0) {
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
                        if (out instanceof LexicalToken) {
                            in += ((LexicalToken) out).lexeme + ", ";

                            if (x1 == 0) {
                                esp = ((LexicalToken) out).lexeme;
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
            if (t instanceof LexicalToken) {
                stack.remove((LexicalToken) t);
            } else {
                stack.remove((RegraProducao) t);
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
