package br.com.paulomatew.compilador.core;

import br.com.paulomatew.compilador.entities.IntermediateCodeObject;
import br.com.paulomatew.compilador.entities.Token;
import br.com.paulomatew.compilador.exceptions.IntermediateCodeGeneratorException;
import java.util.ArrayList;

/**
 * Created by Paulo Mateus on 31/07/2017. For project Compiladores
 * (https://github.com/TroniPM/Compilador) Contact: <paulomatew@gmail.com>
 */
public class IntermediateCodeGenerator {

    private String code = "";
    //private ArrayList<Token> tokens = null;
    //private ArrayList<IntermediateCodeObject> lista = null;
    private ArrayList<Integer> labels_var = null;
    private ArrayList<Integer> labels_goto = null;
    private String prefixo_variavel = "VAR_";
    private String prefixo_goto = "L_";
    private String KEY_ATRIBUICAO = ":=";
    private String forceCurrentTokenThisLabel = null;

    public ArrayList<IntermediateCodeObject> parser(ArrayList<Token> tokens) throws IntermediateCodeGeneratorException {
        labels_var = new ArrayList<>();
        labels_var.add(0);
        labels_goto = new ArrayList<>();
        labels_goto.add(0);

        return init(tokens);
    }

    /**
     * TODO list: IF/ELSE, WHILE, DECLARAÇÃO DA FUNÇÃO; JA FEITO: ATRIBUIR
     * EXPR_ARITMETICA, CHAMADA DE FUNÇÃO, ATRIBUIR EXPR_BOOLEANA
     *
     * @param tokens1
     * @return
     * @throws IntermediateCodeGeneratorException
     */
    private ArrayList<IntermediateCodeObject> init(ArrayList<Token> tokens) throws IntermediateCodeGeneratorException {
        //ArrayList<Token> tokens = new ArrayList<>();

        /*for (Token in : tokens1) {
            tokens.add(in.clone());
        }*/
        //System.out.println("TAMANHO: " + tokens.size());
        ArrayList<IntermediateCodeObject> lista = new ArrayList<>();

        int i;
        for (i = 0; i < tokens.size(); i++) {
            Token atual = tokens.get(i);
            if (atual.wasMapped) {
                continue;
            }
            if (atual.type == 25 || atual.type == 26) {
                //Se for declaração de variável, ignoro
                i += 2;
                continue;
            } else if (atual.type == 21) {//if
                /*TODO fazer*/
                /**
                 * IF
                 */
                int j, x1 = 0, x2 = 0, indiceElse1 = 0, indiceElse2 = 0;
                ArrayList<IntermediateCodeObject> objetosDoElse = null;
                ArrayList<IntermediateCodeObject> objetosDoIf = null;
                for (j = i; j < tokens.size(); j++) {
                    if (tokens.get(j).type == 6) {//abre chaves
                        break;
                    }
                }
                ArrayList<IntermediateCodeObject> exp_arr = exp_logic_atrib(tokens, i + 2, j - 2);//ignoro os parenteses

                //excluo o caso de quando if(true)
                if (exp_arr.size() == 1) {
                    if (exp_arr.get(0).operacao1 != null && !exp_arr.get(0).operacao1.isEmpty()) {
                        lista.addAll(exp_arr);
                    }
                } else {
                    lista.addAll(exp_arr);
                }
                if (exp_arr.size() > 0) {
                    if (forceCurrentTokenThisLabel != null) {
                        exp_arr.get(0).txt = forceCurrentTokenThisLabel;
                        forceCurrentTokenThisLabel = null;
                    }
                }
                /*for (IntermediateCodeObject in : exp_arr) {
                    in.print();
                }*/
                IntermediateCodeObject ico = new IntermediateCodeObject();
                ico.parte1 = "if";
                ico.operacao1 = exp_arr.get(exp_arr.size() - 1).parte1;
                ico.parte2 = "then";
                ico.operacao2 = "goto";
                ico.parte3 = "****";
                lista.add(ico);

                boolean checkHasElse = false;
                for (x1 = i + 1; x1 < tokens.size(); x1++) {
                    if (tokens.get(x1).other != null && tokens.get(x1).other.equals(atual.other)) {
                        indiceElse1 = x1 + 2;//após a chave de abertura

                        for (x2 = x1 + 2; x2 < tokens.size(); x2++) {
                            if (tokens.get(x2).other != null
                                    && tokens.get(x2).other.equals(tokens.get(x1 + 1).other)) {
                                indiceElse2 = x2 - 1;//antes da chave de fechamento
                                break;
                            }
                        }
                        checkHasElse = true;
                        break;
                    }
                }
                if (checkHasElse) {
                    ArrayList<Token> arr1 = new ArrayList<>();
                    for (int x = indiceElse1; x <= indiceElse2; x++) {
                        arr1.add(tokens.get(x));
                    }

                    objetosDoElse = init(arr1);
                }

                int indiceIf1 = j, indiceIf2 = 0;
                for (int x = j; x < tokens.size(); x++) {
                    if (tokens.get(x).other != null && tokens.get(x).other.equals(tokens.get(j).other)) {
                        indiceIf2 = x - 1;
                        break;
                    }
                }

                ArrayList<Token> arr2 = new ArrayList<>();
                for (int x = indiceIf1; x <= indiceIf2; x++) {
                    arr2.add(tokens.get(x));
                }

                objetosDoIf = init(arr2);

                String label = getLabelGotoRandomName();
                if (checkHasElse) {
                    ico.parte3 = label;
                    if (objetosDoIf.size() > 0) {
                        objetosDoIf.get(0).txt = label;
                    } else {
                        forceCurrentTokenThisLabel = label;
                    }
                    lista.addAll(objetosDoElse);
                    lista.addAll(objetosDoIf);
                } else {
                    forceCurrentTokenThisLabel = label;
                    ico.parte3 = label;
                    //System.out.println("OPERAÇÃO ANTERIOR: " + ico.operacao1);
                    //String op = inverterRelacao(ico.operacao1);
                    //System.out.println("OPERAÇÃO INVERSA: " + op + "\tisNull: " + (op == null));
                    ico.operacao1 = "NOT(" + ico.operacao1 + ")";

                    /*if (op == null) {
                        ico.operacao1 = "NOT" + ico.operacao1;
                    } else {
                        ico.operacao1 = op;
                    }*/
                    lista.addAll(objetosDoIf);
                }

            } else if (atual.type == 23) {//while
                /*TODO fazer*/
 /*FAZER verificação de break; e continue;*/
            } else if (atual.type == 24) {//function
                /*TODO fazer*/
            } else if (atual.type == 10) {//ATRIB
                if (tokens.get(i + 1).regra.contains("exp_logic")) {
                    /**
                     * EXP_LOGIC
                     */
                    int j;
                    for (j = i - 1; j < tokens.size(); j++) {
                        if (tokens.get(j).type == 8) {//ponto e vírgula
                            break;
                        }
                    }
                    ArrayList<IntermediateCodeObject> exp_arr = exp_logic_atrib(tokens, i - 1, j);
                    if (exp_arr.size() > 0) {
                        exp_arr.get(exp_arr.size() - 1).parte1 = tokens.get(i - 1).lexeme;
                        if (forceCurrentTokenThisLabel != null) {
                            exp_arr.get(0).txt = forceCurrentTokenThisLabel;
                            forceCurrentTokenThisLabel = null;
                        }
                    }
                    lista.addAll(exp_arr);
                    /*for (IntermediateCodeObject in : exp_arr) {
                        in.print();
                    }*/
                } else if (tokens.get(i + 1).regra.contains("exp_arit")) {
                    /**
                     * EXP_ARIT
                     */
                    int j;
                    for (j = i - 1; j < tokens.size(); j++) {
                        if (tokens.get(j).type == 8) {//ponto e vírgula
                            break;
                        }
                    }
                    ArrayList<IntermediateCodeObject> exp_arr = exp_arit_atrib(tokens, i - 1, j);
                    if (exp_arr.size() > 0) {
                        exp_arr.get(exp_arr.size() - 1).parte1 = tokens.get(i - 1).lexeme;
                        if (forceCurrentTokenThisLabel != null) {
                            exp_arr.get(0).txt = forceCurrentTokenThisLabel;
                            forceCurrentTokenThisLabel = null;
                        }
                    }
                    lista.addAll(exp_arr);

                } else if (tokens.get(i + 1).regra.contains("call_func")) {
                    /**
                     * CALL_FUNCTION
                     */
                    int j;
                    for (j = i - 1; j < tokens.size(); j++) {
                        if (tokens.get(j).type == 8) {//ponto e vírgula
                            break;
                        }
                    }
                    ArrayList<IntermediateCodeObject> exp_arr = chamar_func_(tokens, i - 1, j);
                    if (exp_arr.size() > 0) {
                        exp_arr.get(exp_arr.size() - 1).parte1 = tokens.get(i - 1).lexeme;
                        if (forceCurrentTokenThisLabel != null) {
                            exp_arr.get(0).txt = forceCurrentTokenThisLabel;
                            forceCurrentTokenThisLabel = null;
                        }
                    }
                    lista.addAll(exp_arr);
                } else {
                    throw new IntermediateCodeGeneratorException("Identificador depois do = não possui 'exp_logic, 'exp_arit' ou 'call_func'");
                }
            } else if (atual.type == 36) {//call
                /**
                 * CALL_FUNCTION
                 */
                int j;
                for (j = i; j < tokens.size(); j++) {
                    if (tokens.get(j).type == 8) {//ponto e vírgula
                        break;
                    }
                }
                ArrayList<IntermediateCodeObject> exp_arr = chamar_func_(tokens, i, j);

                if (exp_arr.size() > 0) {
                    if (forceCurrentTokenThisLabel != null) {
                        exp_arr.get(0).txt = forceCurrentTokenThisLabel;
                        forceCurrentTokenThisLabel = null;
                    }
                }

                lista.addAll(exp_arr);
            }
        }
        //return
        return lista;
    }

    /**
     * Prioridades: (), *, /, +, -
     *
     * A prioridade é feita porque SEMPRE restarto o j a medida que entro em
     * algum if, então enquanto houver uma prioridade maior, ela sempre será
     * resolvida primeiro. A ORDEM dos ifs altera o resultado.
     *
     * @param tokens
     * @param inicioInt
     * @param fimInt
     * @return
     * @throws IntermediateCodeGeneratorException
     */
    private ArrayList<IntermediateCodeObject> exp_arit_atrib(ArrayList<Token> tokens, int inicioInt, int fimInt) throws IntermediateCodeGeneratorException {
        ArrayList<Token> exp_array = new ArrayList<>();
        ArrayList<IntermediateCodeObject> array = new ArrayList<>();

        String exp_str = null;

        for (int j = inicioInt; j <= fimInt; j++) {
            tokens.get(j).wasMapped = true;
            exp_array.add(tokens.get(j).clone());
        }

        //i = 20/id;
        if (exp_array.size() == 4) {
            IntermediateCodeObject ico = new IntermediateCodeObject();
            ico.parte1 = exp_array.get(0).lexeme;
            ico.operacao1 = KEY_ATRIBUICAO;
            ico.parte2 = exp_array.get(2).lexeme;
            array.add(ico);

            return array;
        }

        for (int j = 0; j < exp_array.size(); j++) {
            exp_str = "";
            for (Token in : exp_array) {
                exp_str += in.lexeme + " ";
            }

            if (exp_str.contains("(")) {// ( tem prioridade de resolução
                int x2 = 0, x1 = 0;
                for (x2 = 0; x2 < exp_array.size(); x2++) {
                    if (exp_array.get(x2).type == 5) {
                        //acha o primeiro fecha parenteses
                        for (x1 = 0; x1 < x2; x1++) {
                            if (exp_array.get(x1).other != null && exp_array.get(x1).other.equals(exp_array.get(x2).other)) {
                                //acha abre parenteses correspondente ao primeiro fecha parenteses
                                break;
                            }
                        }
                        break;
                    }

                }
                ArrayList<IntermediateCodeObject> lista = exp_arit_atrib(exp_array, x1 + 1, x2 - 1);//+1 para ignorar os próprios parênteses

                //concatenar arrays (intermediateobject)
                array.addAll(lista);
                //substituir valores de expressão[x] até expressao[x1] pelo por lista[ultima posição].parte1
                IntermediateCodeObject o = lista.get(lista.size() - 1);
                Token t = new Token(0, o.parte1);
                exp_array.set(x1, t);

                ArrayList<Token> dummyArray = new ArrayList<>();
                for (int w = x1 + 1; w <= x2; w++) {
                    dummyArray.add(exp_array.get(w));
                }

                exp_array.removeAll(dummyArray);
                j = 0;
                continue;
            } else if (!exp_str.contains("*") && !exp_str.contains("/") && !exp_str.contains("+") && exp_array.get(j).type == 12) {// -
                Token anterior = exp_array.get(j - 1), depois = exp_array.get(j + 1), operacao = exp_array.get(j);

                Token result = new Token();
                result.lexeme = getVarRandomName();// System.nanoTime();
                result.regra = anterior.regra;
                result.type = 1;

                IntermediateCodeObject ico = new IntermediateCodeObject();
                ico.parte1 = result.lexeme;
                ico.operacao1 = KEY_ATRIBUICAO;
                ico.parte2 = anterior.lexeme;
                ico.operacao2 = operacao.lexeme;
                ico.parte3 = depois.lexeme;
                array.add(ico);

                exp_array.set(j - 1, result);
                exp_array.remove(operacao);
                exp_array.remove(depois);
                j = 0;
                continue;
            } else if (!exp_str.contains("*") && !exp_str.contains("/") && exp_array.get(j).type == 11) {// +
                Token anterior = exp_array.get(j - 1), depois = exp_array.get(j + 1), operacao = exp_array.get(j);

                Token result = new Token();
                result.lexeme = getVarRandomName();// System.nanoTime();
                result.regra = anterior.regra;
                result.type = 1;

                IntermediateCodeObject ico = new IntermediateCodeObject();
                ico.parte1 = result.lexeme;
                ico.operacao1 = KEY_ATRIBUICAO;
                ico.parte2 = anterior.lexeme;
                ico.operacao2 = operacao.lexeme;
                ico.parte3 = depois.lexeme;
                array.add(ico);

                exp_array.set(j - 1, result);
                exp_array.remove(operacao);
                exp_array.remove(depois);
                j = 0;
                continue;
            } else if (!exp_str.contains("*") && exp_array.get(j).type == 14) {// /
                Token anterior = exp_array.get(j - 1), depois = exp_array.get(j + 1), operacao = exp_array.get(j);

                Token result = new Token();
                result.lexeme = getVarRandomName();// System.nanoTime();
                result.regra = anterior.regra;
                result.type = 1;

                IntermediateCodeObject ico = new IntermediateCodeObject();
                ico.parte1 = result.lexeme;
                ico.operacao1 = KEY_ATRIBUICAO;
                ico.parte2 = anterior.lexeme;
                ico.operacao2 = operacao.lexeme;
                ico.parte3 = depois.lexeme;
                array.add(ico);

                exp_array.set(j - 1, result);
                exp_array.remove(operacao);
                exp_array.remove(depois);
                j = 0;
                continue;
            } else if (exp_array.get(j).type == 13) {// *
                Token anterior = exp_array.get(j - 1), depois = exp_array.get(j + 1), operacao = exp_array.get(j);

                Token result = new Token();
                result.lexeme = getVarRandomName();// System.nanoTime();
                result.regra = anterior.regra;
                result.type = 1;

                IntermediateCodeObject ico = new IntermediateCodeObject();
                ico.parte1 = result.lexeme;
                ico.operacao1 = KEY_ATRIBUICAO;
                ico.parte2 = anterior.lexeme;
                ico.operacao2 = operacao.lexeme;
                ico.parte3 = depois.lexeme;
                array.add(ico);

                exp_array.set(j - 1, result);
                exp_array.remove(operacao);
                exp_array.remove(depois);
                j = 0;
                continue;
            }
        }
        return array;

    }

    private ArrayList<IntermediateCodeObject> chamar_func_(ArrayList<Token> tokens, int inicioInt, int fimInt) {
        //i = call IDENTIFICADOR (param1,...paramM){
        /*
        CASO:
        call f();
            5
        I = call f();
            7
        I = call f(a);
            8
        I = call f(a, b, c);
            N
        
        DEVE TER SAIDA:
        param a
        param b
        param c
        _t1 := call f,3 
        
        
         */
        ArrayList<Token> exp_array = new ArrayList<>();
        ArrayList<IntermediateCodeObject> array = new ArrayList<>();
        String exp_str = "";
        for (int j = inicioInt; j <= fimInt; j++) {
            tokens.get(j).wasMapped = true;
            exp_array.add(tokens.get(j).clone());
        }
        for (Token in : exp_array) {
            exp_str += in.lexeme + " ";
        }

        int qtd = exp_str.length() - exp_str.replace(",", "").length() + 1;//Sempre q houver virgula, terá um a mais
        if (qtd == 1) {
            if (exp_array.size() == 7 || exp_array.size() == 5) {
                qtd = 0;
            }/* else if (exp_array.size() == 8) {
                qtd = 1;
            }*/

        }

        //cria params
        boolean flag = false;
        for (int j = 0; j < exp_array.size(); j++) {
            if (flag && exp_array.get(j).type != 9
                    && exp_array.get(j).type != 8
                    && exp_array.get(j).type != 5) {// 8-> ; | 9-> , | 5-> )

                IntermediateCodeObject ico = new IntermediateCodeObject();
                ico.parte1 = "param";
                ico.operacao1 = exp_array.get(j).lexeme;
                array.add(ico);

            } else if (exp_array.get(j).type == 4) {// (
                flag = true;
            }
        }
        //_t1 := call f,3               result.lexeme = getVarRandomName();// System.nanoTime();

        IntermediateCodeObject ico = new IntermediateCodeObject();

        ico.operacao1 = KEY_ATRIBUICAO;
        ico.parte2 = "call";
        if (exp_array.get(2).lexeme.equals("call")) {
            ico.parte1 = exp_array.get(0).lexeme;
            ico.operacao2 = exp_array.get(3).lexeme + ",";//chamada padrão "I = call IDENTIFICADOR();"
        } else {
            ico.parte1 = getVarRandomName();
            ico.operacao2 = exp_array.get(1).lexeme + ",";//chamada seca "call IDENTIFICADOR();"
        }
        //ico.operacao2 = exp_array.get(3).lexeme + ",";//I = call IDENTIFICADOR();
        //ico.parte3 = ",";
        ico.parte3 = String.valueOf(qtd);
        array.add(ico);

        /*System.out.println("--------------------------------");
        for (IntermediateCodeObject in : array) {
            in.print();
        }*/
        return array;
    }

    /**
     * PRIORIDADES: () <, >, <=, >=, ==, !=, &&, ||
     *
     * @param tokens
     * @param inicioInt
     * @param fimInt
     * @return
     */
    private ArrayList<IntermediateCodeObject> exp_logic_atrib(ArrayList<Token> tokens, int inicioInt, int fimInt) {
        ArrayList<Token> exp_array = new ArrayList<>();
        ArrayList<IntermediateCodeObject> array = new ArrayList<>();

        String exp_str = null;

        for (int j = inicioInt; j <= fimInt; j++) {
            tokens.get(j).wasMapped = true;
            exp_array.add(tokens.get(j).clone());
        }

        //i = false;
        if (exp_array.size() == 4) {
            /*for (Token in : exp_array) {
                exp_str += in.lexeme + " ";
            }
            System.out.println(exp_str);*/

            IntermediateCodeObject ico = new IntermediateCodeObject();
            ico.parte1 = exp_array.get(0).lexeme;
            ico.operacao1 = KEY_ATRIBUICAO;
            ico.parte2 = exp_array.get(2).lexeme;
            array.add(ico);

            return array;
        } else if (exp_array.size() == 1) {//i(false){;
            //System.out.println("------------------1-----------------");
            /*for (Token in : exp_array) {
                exp_str += in.lexeme + " ";
            }*/
            //exp_array.get(0).print();
            //System.out.println(exp_str);

            IntermediateCodeObject ico = new IntermediateCodeObject();
            ico.parte1 = exp_array.get(0).lexeme;
            /*ico.operacao1 = KEY_ATRIBUICAO;
            ico.parte2 = exp_array.get(2).lexeme;*/
            array.add(ico);

            return array;
        }

        for (int j = 0; j < exp_array.size(); j++) {
            exp_str = "";
            for (Token in : exp_array) {
                exp_str += in.lexeme + " ";
            }
            //System.out.println(exp_str);

            if (exp_str.contains("(")) {// ( tem prioridade de resolução
                int x2 = 0, x1 = 0;
                for (x2 = 0; x2 < exp_array.size(); x2++) {
                    if (exp_array.get(x2).type == 5) {
                        //acha o primeiro fecha parenteses
                        for (x1 = 0; x1 < x2; x1++) {
                            if (exp_array.get(x1).other != null && exp_array.get(x1).other.equals(exp_array.get(x2).other)) {
                                //acha abre parenteses correspondente ao primeiro fecha parenteses
                                break;
                            }
                        }
                        break;
                    }

                }
                //exp_array.get(x1 + 1).print();
                //exp_array.get(x2 - 1).print();
                ArrayList<IntermediateCodeObject> lista = exp_logic_atrib(exp_array, x1 + 1, x2 - 1);//+1 para ignorar os próprios parênteses

                //concatenar arrays (intermediateobject)
                array.addAll(lista);
                //substituir valores de expressão[x] até expressao[x1] pelo por lista[ultima posição].parte1
                /*System.out.println("----------LISTA-----------");
                for (IntermediateCodeObject in : lista) {
                    in.print();
                }*/

                IntermediateCodeObject o = lista.get(lista.size() - 1);
                Token t = new Token(0, o.parte1);
                exp_array.set(x1, t);

                ArrayList<Token> dummyArray = new ArrayList<>();
                for (int w = x1 + 1; w <= x2; w++) {
                    dummyArray.add(exp_array.get(w));
                }

                exp_array.removeAll(dummyArray);
                j = 0;
                continue;

            } else if (exp_array.get(j).type >= 28 && exp_array.get(j).type <= 33) {
                //"<", ">", "<="/*30*/, ">=", "==", "!=", "&&", "||"/*35*/
                Token anterior = exp_array.get(j - 1), depois = exp_array.get(j + 1), operacao = exp_array.get(j);

                Token result = new Token();
                result.lexeme = getVarRandomName();// System.nanoTime();
                result.regra = anterior.regra;
                result.type = 1;

                IntermediateCodeObject ico = new IntermediateCodeObject();
                ico.parte1 = result.lexeme;
                ico.operacao1 = KEY_ATRIBUICAO;
                ico.parte2 = anterior.lexeme;
                ico.operacao2 = operacao.lexeme;
                ico.parte3 = depois.lexeme;
                array.add(ico);

                exp_array.set(j - 1, result);
                exp_array.remove(operacao);
                exp_array.remove(depois);
                j = 0;
                continue;
            } else if (!exp_str.contains("<")
                    && !exp_str.contains(">")
                    && !exp_str.contains("<=")
                    && !exp_str.contains(">=")
                    && !exp_str.contains("==")
                    && !exp_str.contains("!=")
                    && exp_array.get(j).type >= 34) {//&&
                //"<", ">", "<="/*30*/, ">=", "==", "!=", "&&", "||"/*35*/
                Token anterior = exp_array.get(j - 1), depois = exp_array.get(j + 1), operacao = exp_array.get(j);

                Token result = new Token();
                result.lexeme = getVarRandomName();// System.nanoTime();
                result.regra = anterior.regra;
                result.type = 1;

                IntermediateCodeObject ico = new IntermediateCodeObject();
                ico.parte1 = result.lexeme;
                ico.operacao1 = KEY_ATRIBUICAO;
                ico.parte2 = anterior.lexeme;
                ico.operacao2 = operacao.lexeme;
                ico.parte3 = depois.lexeme;
                array.add(ico);

                exp_array.set(j - 1, result);
                exp_array.remove(operacao);
                exp_array.remove(depois);
                j = 0;
                continue;
            } else if (!exp_str.contains("<")
                    && !exp_str.contains(">")
                    && !exp_str.contains("<=")
                    && !exp_str.contains(">=")
                    && !exp_str.contains("==")
                    && !exp_str.contains("!=")
                    && exp_array.get(j).type >= 35) {// ||
                //"<", ">", "<="/*30*/, ">=", "==", "!=", "&&", "||"/*35*/
                Token anterior = exp_array.get(j - 1), depois = exp_array.get(j + 1), operacao = exp_array.get(j);

                Token result = new Token();
                result.lexeme = getVarRandomName();// System.nanoTime();
                result.regra = anterior.regra;
                result.type = 1;

                IntermediateCodeObject ico = new IntermediateCodeObject();
                ico.parte1 = result.lexeme;
                ico.operacao1 = KEY_ATRIBUICAO;
                ico.parte2 = anterior.lexeme;
                ico.operacao2 = operacao.lexeme;
                ico.parte3 = depois.lexeme;
                array.add(ico);

                exp_array.set(j - 1, result);
                exp_array.remove(operacao);
                exp_array.remove(depois);
                j = 0;
                continue;
            }
        }

        /*System.out.println("--------------------------------------");
        for (IntermediateCodeObject in : array) {
            in.print();
        }*/
        return array;
    }

    private ArrayList<IntermediateCodeObject> if_(ArrayList<Token> tokens, int inicioInt, int fimInt) {
        return null;
    }

    private ArrayList<IntermediateCodeObject> while_(ArrayList<Token> tokens, int inicioInt, int fimInt) {
        return null;
    }

    private ArrayList<IntermediateCodeObject> declarar_func_(ArrayList<Token> tokens, int inicioInt, int fimInt) {
        return null;
    }

    private String getVarRandomName() {
        int label = (labels_var.get(labels_var.size() - 1) + 1);
        labels_var.add(label);
        return prefixo_variavel + label;
    }

    private String getLabelGotoRandomName() {
        int label = (labels_goto.get(labels_goto.size() - 1) + 1);
        labels_goto.add(label);
        return prefixo_goto + label;
    }

    /**
     * Retorna valor oposto ao da relação. Nos casos && e ||, retorna null, para
     * q no código posterior, apenas NEGUE a variável.
     *
     * @param id
     * @return
     */
    private String inverterRelacao(String id) {
        //"<", ">", "<="/*30*/, ">=", "==", "!=", "&&", "||"/*35*/
        String a = "";
        if (id.equals("<")) {//<
            a = ">=";
        } else if (id.equals(">")) {//>
            a = "<=";
        } else if (id.equals("<=")) {//<=
            a = ">";
        } else if (id.equals(">=")) {//>=
            a = "<";
        } else if (id.equals("==")) {//==
            a = "!=";
        } else if (id.equals("!=")) {//!=
            a = "==";
        } else if (id.equals("&&") || id.equals("||")) {//"&&", "||"
            a = null;
        }
        return a;
    }

    public static String gerarCode(ArrayList<IntermediateCodeObject> lista) {
        String a = "";
        for (IntermediateCodeObject in : lista) {
            a += in.getData() + "\n";
        }

        return a;
    }
}
