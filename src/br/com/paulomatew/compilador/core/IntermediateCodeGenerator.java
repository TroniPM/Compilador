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
    private ArrayList<Token> tokens = null;
    private ArrayList<IntermediateCodeObject> lista = null;
    private ArrayList<Integer> labels = null;

    //operação arit: PRIORIDADE * / + -
    //operação logi: PRIORIDADE && ||
    public String init(ArrayList<Token> tokens) throws IntermediateCodeGeneratorException {
        this.tokens = tokens;

        labels = new ArrayList<>();
        labels.add(0);
        lista = new ArrayList<>();

        int i;
        for (i = 0; i < tokens.size(); i++) {
            Token atual = tokens.get(i);
            if (atual.type == 25 || atual.type == 26) {
                //Se for declaração de variável,  ignoro
                i += 2;
                continue;
            } else if (atual.type == 21) {//if
                /*Token abreChaves = null, abreParentese = null, fechaParentese = null;
                abreParentese = tokens.get(i + 1);
                int j;
                for (j = i; j < tokens.size(); j++) {
                    if (tokens.get(j).type == 6) {// { 6, ; 8
                        abreChaves = tokens.get(j);
                        fechaParentese = tokens.get(j - 1);
                        break;
                    }
                }

                if_(abreParentese, i + 1, fechaParentese, j - 1);*/
            } else if (atual.type == 23) {//while

            } else if (atual.type == 24) {//function

            } else if (atual.type == 10) {//ATRIB
                if (tokens.get(i + 1).regra.contains("exp_logic")) {
                    //EXP_LOGIC
                } else if (tokens.get(i + 1).regra.contains("exp_arit")) {
                    //EXP_ARIT
                    //Token inicio = null, fim = null;
                    //inicio = tokens.get(i - 1);
                    int j;
                    for (j = i - 1; j < tokens.size(); j++) {
                        //tokens.get(j).print();
                        if (tokens.get(j).type == 8) {
                            //fim = tokens.get(j);
                            break;
                        }
                    }
                    exp_arit_atrib(i - 1, j);

                    //X = Y + W;
                } else if (tokens.get(i + 1).regra.contains("call_func")) {
                    //CALL_FUNCTION
                } else {
                    throw new IntermediateCodeGeneratorException("Identificador depois do = não possui 'exp_logic, 'exp_arit' ou 'call_func'");
                }
            }
        }
        //return
        return code;
    }

    private ArrayList<IntermediateCodeObject> if_(Token inicio, int inicioIndex, Token fim, int fimIndex) {
        //if "(" i == true ")" {
        ArrayList<IntermediateCodeObject> esseBloco = new ArrayList<>();
        //esseBloco.add(new IntermediateCodeObject(1, "IF "));
        int type = 0;
        for (int i = inicioIndex; i <= fimIndex; i++) {
            if (tokens.get(i).type == 34 || tokens.get(i).type == 35) {
                //por enquanto ignorando se if tiver && ou ||
                type = 1;
                break;
            }
        }

        return esseBloco;
    }

    private String while_(Token inicio, Token abreParentese) {

        return null;
    }

    private String function_(Token inicio, Token abreParentese) {

        return null;
    }

    private String exp_arit_atrib(int inicioInt, int fimInt) throws IntermediateCodeGeneratorException {
        //IntermediateCodeObject identif = new IntermediateCodeObject(1, tokens.get(inicioInt).lexeme);
        //IntermediateCodeObject igual = new IntermediateCodeObject(1, tokens.get(inicioInt + 1).lexeme);

        ArrayList<Token> expressao = new ArrayList<>();
        ArrayList<IntermediateCodeObject> array = new ArrayList<>();

        String expression = "";
        for (int j = inicioInt; j <= fimInt; j++) {
            //tokens.get(j).print();
            //expression += tokens.get(j).lexeme + " ";
            //System.out.println(expression);
            expressao.add(tokens.get(j));
        }

        boolean temParentese = false;
        int i;
        for (i = inicioInt + 2; i < fimInt; i++) {
            if (tokens.get(i).type == 4) {
                temParentese = true;
                break;
            }
        }

        if (!temParentese) {
            for (int j = 0; j < expressao.size(); j++) {
                expression = "";
                for (Token in : expressao) {
                    expression += in.lexeme + " ";
                }

                //System.out.println(">> " + expressao.size() + " " + !expression.contains("*") + " " + !expression.contains("/") + "\t" + expression);
                if (!expression.contains("*") && !expression.contains("/") && !expression.contains("+") && expressao.get(j).type == 12) {// -
                    Token anterior = expressao.get(j - 1), depois = expressao.get(j + 1), operacao = expressao.get(j);

                    Token result = new Token();

                    int label = (labels.get(labels.size() - 1) + 1);
                    result.lexeme = "A_" + label;// System.nanoTime();
                    labels.add(label);
                    result.regra = anterior.regra;
                    result.type = anterior.type;

                    IntermediateCodeObject ico = new IntermediateCodeObject();
                    ico.parte1 = result.lexeme;
                    ico.operacao1 = "=";
                    ico.parte2 = anterior.lexeme;
                    ico.operacao2 = operacao.lexeme;
                    ico.parte3 = depois.lexeme;
                    array.add(ico);

                    expressao.set(j - 1, result);
                    expressao.remove(operacao);
                    expressao.remove(depois);
                    j = 0;
                    continue;
                } else if (!expression.contains("*") && !expression.contains("/") && expressao.get(j).type == 11) {// +
                    Token anterior = expressao.get(j - 1), depois = expressao.get(j + 1), operacao = expressao.get(j);

                    Token result = new Token();
                    int label = (labels.get(labels.size() - 1) + 1);
                    result.lexeme = "A_" + label;// System.nanoTime();
                    labels.add(label);
                    result.regra = anterior.regra;
                    result.type = anterior.type;

                    IntermediateCodeObject ico = new IntermediateCodeObject();
                    ico.parte1 = result.lexeme;
                    ico.operacao1 = "=";
                    ico.parte2 = anterior.lexeme;
                    ico.operacao2 = operacao.lexeme;
                    ico.parte3 = depois.lexeme;
                    array.add(ico);

                    expressao.set(j - 1, result);
                    expressao.remove(operacao);
                    expressao.remove(depois);
                    j = 0;
                    continue;
                } else if (!expression.contains("*") && expressao.get(j).type == 14) {// /
                    Token anterior = expressao.get(j - 1), depois = expressao.get(j + 1), operacao = expressao.get(j);

                    Token result = new Token();
                    int label = (labels.get(labels.size() - 1) + 1);
                    result.lexeme = "A_" + label;// System.nanoTime();
                    labels.add(label);
                    result.regra = anterior.regra;
                    result.type = anterior.type;

                    IntermediateCodeObject ico = new IntermediateCodeObject();
                    ico.parte1 = result.lexeme;
                    ico.operacao1 = "=";
                    ico.parte2 = anterior.lexeme;
                    ico.operacao2 = operacao.lexeme;
                    ico.parte3 = depois.lexeme;
                    array.add(ico);

                    expressao.set(j - 1, result);
                    expressao.remove(operacao);
                    expressao.remove(depois);
                    j = 0;
                    continue;
                } else if (expressao.get(j).type == 13) {// *
                    Token anterior = expressao.get(j - 1), depois = expressao.get(j + 1), operacao = expressao.get(j);

                    Token result = new Token();
                    int label = (labels.get(labels.size() - 1) + 1);
                    result.lexeme = "A_" + label;// System.nanoTime();
                    labels.add(label);
                    result.regra = anterior.regra;
                    result.type = anterior.type;

                    IntermediateCodeObject ico = new IntermediateCodeObject();
                    ico.parte1 = result.lexeme;
                    ico.operacao1 = "=";
                    ico.parte2 = anterior.lexeme;
                    ico.operacao2 = operacao.lexeme;
                    ico.parte3 = depois.lexeme;
                    array.add(ico);

                    expressao.set(j - 1, result);
                    expressao.remove(operacao);
                    expressao.remove(depois);
                    j = 0;
                    continue;
                }
            }

        } else {
            throw new IntermediateCodeGeneratorException("Expressão aritmética tem parênteses");
        }

        System.out.println("---------------------------------");
        for (IntermediateCodeObject in : array) {
            in.print();
        }
        return null;

    }

    private String exp_logic_(Token inicio, Token pontoVirgula) {

        return null;
    }

    public String gerarCode() {
        String a = "";
        for (IntermediateCodeObject in : lista) {
            a += in.getData() + "\n";
        }

        return a;
    }
}
