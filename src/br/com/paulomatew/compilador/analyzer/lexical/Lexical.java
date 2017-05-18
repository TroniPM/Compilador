/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.paulomatew.compilador.analyzer.lexical;

import br.com.paulomatew.compilador.exceptions.LexicalException;
import br.com.paulomatew.compilador.main.Compilador;
import dnl.utils.text.table.TextTable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.nocrala.tools.texttablefmt.Table;

/**
 *
 * @author matt
 */
public class Lexical {

    private String sourceCode = null;
    public ArrayList<LexicalToken> tokenArray = null;

    public void init(String sourceCode) throws LexicalException {
        if (sourceCode == null || sourceCode.isEmpty()) {
            throw new LexicalException("Nenhum código fonte informado.");
        }

        this.sourceCode = formatSourceCode(sourceCode);

        tokenArray = parser();
    }

    public String getTokenListAsTable() {
        Table t = new Table(5);
        t.addCell("POS");
        t.addCell("TOKEN");
        t.addCell("LEXEMA");
        t.addCell("LINHA");
        t.addCell("DESCRIÇÃO");
        for (int i = 0; i < tokenArray.size(); i++) {
            LexicalToken in = tokenArray.get(i);
            t.addCell("" + String.valueOf(i + 1));
            t.addCell(Compilador.getToken(in.type));
            t.addCell(in.lexeme);
            t.addCell(String.valueOf(in.line));
            t.addCell(in.description);
        }

        return t.render();
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

    private ArrayList<LexicalToken> parser() {
        ArrayList<LexicalToken> arr = new ArrayList<>();

        String[] sourcePorLinha = sourceCode.split("\n");
        for (int j1 = 0; j1 < sourcePorLinha.length; j1++) {
            String linha = sourcePorLinha[j1];

            StringTokenizer st = new StringTokenizer(linha);

            outer:
            while (st.hasMoreTokens()) {
                String s = st.nextToken();

                boolean ctrl = false;
                for (int i = 0; i < Compilador.RESERVED_WORDS_AND_OPERATORS.size(); i++) {
                    if (s.equals(Compilador.RESERVED_WORDS_AND_OPERATORS.get(i))) {

                        LexicalToken l = new LexicalToken();
                        l.type = i + 3;//correção de indice para ocultar os 3 primeiros
                        l.lexeme = s;
                        l.line = j1 + 1;
                        arr.add(l);

                        ctrl = true;
                    }
                }
                if (ctrl) {
                    continue;
                }

                if (Character.isUpperCase(s.charAt(0))) {
                    LexicalToken l = new LexicalToken();
                    l.type = 2;// "token desconhecido";
                    l.lexeme = s;
                    l.line = j1 + 1;
                    arr.add(l);
                    continue;
                }

                if (Character.isLowerCase(s.charAt(0))) {
                    LexicalToken l = new LexicalToken();
                    l.type = 1;//"identificador";
                    l.lexeme = s;
                    l.line = j1 + 1;
                    arr.add(l);
                    continue;
                }

                if (Character.isDigit(s.charAt(0))) {
                    /*TODO: Começa com digito mas tem LETRA (fazer forma de reconhecer
                    com notação 1.5E12) */
                    if (s.matches(".*[a-zA-Z]+.*")) {
                        LexicalToken l = new LexicalToken();
                        l.type = 2;//"token desconhecido";
                        l.lexeme = s;
                        l.line = j1 + 1;
                        arr.add(l);
                        continue;
                    } else {

                        LexicalToken l = new LexicalToken();
                        l.type = 0;//"constante";
                        l.lexeme = s;
                        l.line = j1 + 1;
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
            System.out.println(i2 + " " + a);
            return (a.substring(i1, i2));
        } else {
            return (a.substring(i1, i2 + 1));
        }
    }
}
