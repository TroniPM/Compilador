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
    private ArrayList<Integer> labels = null;
    private String prefixo_variavel = "VAR_";

    public IntermediateCodeGenerator() {
        labels = new ArrayList<>();
        labels.add(0);
    }

    //operação arit: PRIORIDADE * / + -
    //operação logi: PRIORIDADE && ||
    public ArrayList<IntermediateCodeObject> init(ArrayList<Token> tokens1) throws IntermediateCodeGeneratorException {
        ArrayList<Token> tokens = new ArrayList<>();

        for (Token in : tokens1) {
            tokens.add(in.clone());
        }
        //this.tokens = tokens;

        ArrayList<IntermediateCodeObject> lista = new ArrayList<>();

        int i;
        for (i = 0; i < tokens.size(); i++) {
            Token atual = tokens.get(i);
            if (atual.type == 25 || atual.type == 26) {
                //Se for declaração de variável, ignoro
                i += 2;
                continue;
            } else if (atual.type == 21) {//if
                /*TODO fazer*/
            } else if (atual.type == 23) {//while
                /*TODO fazer*/
 /*FAZER verificação de break; e continue;*/
            } else if (atual.type == 24) {//function
                /*TODO fazer*/
            } else if (atual.type == 10) {//ATRIB
                if (tokens.get(i + 1).regra.contains("exp_logic")) {
                    //EXP_LOGIC
                    /*TODO fazer*/
                } else if (tokens.get(i + 1).regra.contains("exp_arit")) {
                    //EXP_ARIT
                    int j;
                    for (j = i - 1; j < tokens.size(); j++) {
                        if (tokens.get(j).type == 8) {//ponto e vírgula
                            break;
                        }
                    }
                    lista.addAll(exp_arit_atrib(tokens, i - 1, j));

                    /*for (IntermediateCodeObject in : exp_arit_atrib) {
                        in.print();
                    }*/
                } else if (tokens.get(i + 1).regra.contains("call_func")) {
                    //CALL_FUNCTION
                    /*TODO fazer*/
                } else {
                    throw new IntermediateCodeGeneratorException("Identificador depois do = não possui 'exp_logic, 'exp_arit' ou 'call_func'");
                }
            }
        }
        //return
        return lista;
    }

    private ArrayList<IntermediateCodeObject> exp_arit_atrib(ArrayList<Token> tokens, int inicioInt, int fimInt) throws IntermediateCodeGeneratorException {
        ArrayList<Token> exp_array = new ArrayList<>();
        ArrayList<IntermediateCodeObject> array = new ArrayList<>();

        String exp_str = null;

        for (int j = inicioInt; j <= fimInt; j++) {
            exp_array.add(tokens.get(j).clone());
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

                int label = (labels.get(labels.size() - 1) + 1);
                result.lexeme = prefixo_variavel + label;// System.nanoTime();
                labels.add(label);
                result.regra = anterior.regra;
                result.type = 1;

                IntermediateCodeObject ico = new IntermediateCodeObject();
                ico.parte1 = result.lexeme;
                ico.operacao1 = "=";
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
                int label = (labels.get(labels.size() - 1) + 1);
                result.lexeme = prefixo_variavel + label;// System.nanoTime();
                labels.add(label);
                result.regra = anterior.regra;
                result.type = 1;

                IntermediateCodeObject ico = new IntermediateCodeObject();
                ico.parte1 = result.lexeme;
                ico.operacao1 = "=";
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
                int label = (labels.get(labels.size() - 1) + 1);
                result.lexeme = prefixo_variavel + label;// System.nanoTime();
                labels.add(label);
                result.regra = anterior.regra;
                result.type = 1;

                IntermediateCodeObject ico = new IntermediateCodeObject();
                ico.parte1 = result.lexeme;
                ico.operacao1 = "=";
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
                int label = (labels.get(labels.size() - 1) + 1);
                result.lexeme = prefixo_variavel + label;// System.nanoTime();
                labels.add(label);
                result.regra = anterior.regra;
                result.type = 1;

                IntermediateCodeObject ico = new IntermediateCodeObject();
                ico.parte1 = result.lexeme;
                ico.operacao1 = "=";
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

    private ArrayList<IntermediateCodeObject> if_(Token inicio, int inicioIndex, Token fim, int fimIndex) {
        return null;
    }

    private String while_(Token inicio, Token abreParentese) {
        return null;
    }

    private String function_(Token inicio, Token abreParentese) {
        return null;
    }

    private String exp_logic_(Token inicio, Token pontoVirgula) {
        return null;
    }

    public static String gerarCode(ArrayList<IntermediateCodeObject> lista) {
        String a = "";
        for (IntermediateCodeObject in : lista) {
            a += in.getData() + "\n";
        }

        return a;
    }
}
