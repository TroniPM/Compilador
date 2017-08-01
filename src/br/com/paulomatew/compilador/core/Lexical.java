package br.com.paulomatew.compilador.core;

import br.com.paulomatew.compilador.entities.Escopo;
import br.com.paulomatew.compilador.entities.Token;
import br.com.paulomatew.compilador.exceptions.LexicalException;
import br.com.paulomatew.compilador.main.Compilador;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;
import org.nocrala.tools.texttablefmt.Table;

/**
 * Created by Paulo Mateus on 26/06/2017. For project Compiladores
 * (https://github.com/TroniPM/Compilador) Contact: <paulomatew@gmail.com>
 */
public class Lexical {

    private String sourceCode = null;
    public ArrayList<Token> tokenArray = null;
    private ArrayList<String> escopos = null;
    //public Escopo arvoreEscopos = null;

    public ArrayList<Escopo> escoposArvore = null;

    public void init(String sourceCode) throws LexicalException {
        /*if (sourceCode == null || sourceCode.isEmpty()) {
            throw new LexicalException("Nenhum código fonte informado.");
        }*/

        this.sourceCode = formatSourceCode(sourceCode);

        escopos = new ArrayList<>();
        escoposArvore = new ArrayList<>();
        //System.out.println(this.sourceCode);
        tokenArray = parser();
    }

    public String getTokenListAsTable() {
        Table t = new Table(9);
        t.addCell("POS");
        t.addCell("TOKEN");
        t.addCell("LEXEMA");
        t.addCell("LINHA");
        t.addCell("COLUNA");
        t.addCell("TIPO");
        t.addCell("ESCOPO");
        t.addCell("REGRA");
        t.addCell("PAR");
        for (int i = 0; i < tokenArray.size(); i++) {
            Token in = tokenArray.get(i);
            t.addCell("" + (i + 1));
            t.addCell(Compilador.getToken(in.type));
            t.addCell(in.lexeme + "");
            t.addCell("" + (in.line));
            t.addCell("" + (in.position));
            t.addCell("" + (in.type));
            t.addCell(in.scope);
            t.addCell(in.regra);
            t.addCell(in.other);

            //System.out.println(in.lexeme);
        }
        String a = t.render().replace("[0m", "");//correção por causa do [
        return a;
    }

    private String formatSourceCode(String msg) {
        msg = msg.replace("(", " ( ")
                .replace(")", " ) ")
                .replace("{", " { ")
                .replace("}", " } ")
                //.replace("=", " = ")
                .replace(",", " , ")
                .replace("+", " + ")
                .replace("-", " - ")
                .replace("*", " * ")
                .replace("/", " / ")
                .replace(",", " , ")
                .replace(";", " ; ")
                .replace("[", " [ ")
                .replace("]", " ] ")
                .replace("&&", " && ")
                .replace("||", " || ");

        //Fazer com q o espaço seja dado para APENAS o = ou apenas suas variaçõpes completas
        StringBuilder codigoFonte = new StringBuilder();

        char[] arr = msg.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == '<') {
                if (arr[i + 1] == '=') {
                    codigoFonte.append(" <= ");
                    i++;
                    continue;
                } else {
                    codigoFonte.append(" < ");
                    continue;
                }
            }
            if (arr[i] == '>') {
                if (arr[i + 1] == '=') {
                    codigoFonte.append(" >= ");
                    i++;
                    continue;
                } else {
                    codigoFonte.append(" > ");
                    continue;
                }
            }

            if (arr[i] == '=') {
                if (arr[i + 1] == '=') {
                    codigoFonte.append(" == ");
                    i++;
                    continue;
                } else {
                    codigoFonte.append(" = ");
                    continue;
                }
            }

            if (arr[i] == '!') {
                if (arr[i + 1] == '=') {
                    codigoFonte.append(" != ");
                    i++;
                    continue;
                } else {
                    codigoFonte.append(" ! ");
                    continue;
                }
            }

            codigoFonte.append(arr[i]);
        }
        msg = codigoFonte.toString();

        return msg;
        //return msg.trim().replaceAll(" +", " ");
    }

    private int getRandomNumberScope() {
        Random gerador = new Random();
        int g = gerador.nextInt();

        while (escopos.contains(g)) {
            g = gerador.nextInt();
        }
        //escopos.add(g);

        return g;
    }

    private ArrayList<Token> parser() throws LexicalException {
        ArrayList<Token> arr = new ArrayList<>();

        String[] sourcePorLinha = sourceCode.split("\n");

        String escopoAtual = "0";
        ArrayList<String> escoposAtivos = new ArrayList<>();
        escoposAtivos.add(escopoAtual);
        escoposArvore.add(new Escopo(escopoAtual));
        String nextEscopo = getRandomNumberScope() + "";
        boolean escopoDeIdentificadorEmMetodo = false;
        /*escopoDeIdentificadorEmMetodo faz com q identificadores q estejam na
        declaração de uma função estejam no escopo da função, e não no escopo
        superior.*/
        for (int j1 = 0; j1 < sourcePorLinha.length; j1++) {
            String linha = sourcePorLinha[j1];

            //ignorar comentários
            if (linha.trim().startsWith("#")) {
                continue;
            }

            StringTokenizer st = new StringTokenizer(linha);

            int pos = 0;
            outer:
            while (st.hasMoreTokens()) {
                pos++;
                String s = st.nextToken();

                boolean ctrl = false;
                for (int i = 0; i < Compilador.RESERVED_WORDS_AND_OPERATORS.size(); i++) {
                    if (s.equals(Compilador.RESERVED_WORDS_AND_OPERATORS.get(i))) {

                        Token l = new Token();
                        l.type = i + 3;//correção de indice para ocultar os 3 primeiros
                        l.lexeme = s;
                        l.line = j1 + 1;
                        l.position = pos;
                        l.scope = escopoAtual;
                        arr.add(l);

                        ctrl = true;

                        if (i == 3) {//NOVO ESCOPO
                            Escopo e = new Escopo(nextEscopo);

                            for (int x = 0; x < escoposArvore.size(); x++) {
                                if (escoposArvore.get(x).label.equals(escopoAtual)) {
                                    e.pai = escoposArvore.get(x);
                                    break;
                                }
                            }

                            escoposArvore.add(e);

                            escopoAtual = nextEscopo;
                            escoposAtivos.add(escopoAtual);
                            escopos.add(escopoAtual);
                            nextEscopo = getRandomNumberScope() + "";

                            escopoDeIdentificadorEmMetodo = false;
                        } else if (i == 4) {//ESCOPO ATUAL ENCERRADO
                            escoposAtivos.remove(escopoAtual);
                            escopoAtual = escoposAtivos.get(escoposAtivos.size() - 1);
                            l.scope = escopoAtual;//ficar com escopo igual ao do ABRE_PARENTESES

                        } else if (i == 21) {//DECLARAÇÃO DE FUNÇÃO
                            escopoDeIdentificadorEmMetodo = true;
                        } else if (i == 13 && escopoDeIdentificadorEmMetodo) {//int
                            l.scope = nextEscopo;
                        } else if (i == 14 && escopoDeIdentificadorEmMetodo) {//boolean
                            l.scope = nextEscopo;
                        }
                    }
                }
                if (ctrl) {
                    continue;
                }

                if (Character.isUpperCase(s.charAt(0))) {
                    Token l = new Token();
                    l.type = 2;// "token desconhecido";
                    l.lexeme = s;
                    l.line = j1 + 1;
                    l.position = pos;
                    l.scope = escopoAtual;
                    arr.add(l);

                    throw new LexicalException("Unknow token '" + l.lexeme + "' at line " + l.line);
                    //continue;
                }

                if (Character.isLowerCase(s.charAt(0))) {
                    Token l = new Token();
                    l.type = 1;//"identificador";
                    l.lexeme = s;
                    l.line = j1 + 1;
                    l.position = pos;
                    if (!escopoDeIdentificadorEmMetodo) {
                        l.scope = escopoAtual;
                    } else {
                        l.scope = nextEscopo;
                    }
                    arr.add(l);
                    continue;
                }

                if (Character.isDigit(s.charAt(0))) {
                    /*TODO: Começa com digito mas tem LETRA (fazer forma de reconhecer
                    com notação 1.5E12) */
                    if (s.matches(".*[a-zA-Z]+.*")) {
                        Token l = new Token();
                        l.type = 2;//"token desconhecido";
                        l.lexeme = s;
                        l.line = j1 + 1;
                        l.position = pos;
                        l.scope = escopoAtual;
                        arr.add(l);

                        throw new LexicalException("Unknow token '" + l.lexeme + "' at line " + l.line);

                        //continue;
                    } else {

                        Token l = new Token();
                        l.type = 0;//"constante";
                        l.lexeme = s;
                        l.line = j1 + 1;
                        l.position = pos;
                        l.scope = escopoAtual;
                        arr.add(l);
                        continue;
                    }
                }

            }
        }
        return arr;
    }

    private String getString(String a, int i1, int i2) {
        if (a.length() >= i2 + 1) {
            //System.out.println(i2 + " " + a);
            return (a.substring(i1, i2));
        } else {
            return (a.substring(i1, i2 + 1));
        }
    }
}
