/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.paulomatew.compilador.analyzer.sintatic;

import br.com.paulomatew.compilador.analyzer.lexical.LexicalToken;
import br.com.paulomatew.compilador.exceptions.SintaticException;
import br.com.paulomatew.compilador.main.Compilador;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author matt
 */
public class Sintatic {

    public ArrayList<LexicalToken> tokenListFromLexical = null;

    public void init(ArrayList<LexicalToken> arr) throws SintaticException {
        if (arr == null || arr.isEmpty()) {
            throw new SintaticException("Nenhum token encontrado.");
        }

        this.tokenListFromLexical = arr;

        parser();
    }

    private void parser() throws SintaticException {
        //programa deve iniciar com main
        if (tokenListFromLexical.get(0).type != 3) {
            throw new SintaticException(
                    "Program starts with unexpected token '" + tokenListFromLexical.get(0).lexeme
                    + "' line " + tokenListFromLexical.get(0).line);
        }
        //ultimo token deve ser fecha_aspas
        if (tokenListFromLexical.get(tokenListFromLexical.size() - 1).type != 7) {
            throw new SintaticException("Unexpected EOF after '" + tokenListFromLexical.get(tokenListFromLexical.size() - 1).lexeme
                    + "' line " + tokenListFromLexical.get(tokenListFromLexical.size() - 1).line);
        }
        for (int i = 0; i < tokenListFromLexical.size(); i++) {
            LexicalToken current = tokenListFromLexical.get(i), next = null;
            if (tokenListFromLexical.get(i + 1) != null) {
                next = tokenListFromLexical.get(i + 1);
            }

            switch (current.type) {
                //constante
                case 0:
                    break;
                // identificador
                case 1:
                    break;
                // token desconhecido
                case 2:
                    break;
                // main
                case 3:
                    if (next != null && next.type == 4) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // (
                case 4:
                    if (next != null
                            && (next.type == 1/*identi*/
                            && next.type == 5/*fecha_p*/
                            && next.type == 16/*int*/
                            && next.type == 17/*boolean*/)) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // )
                case 5:
                    if (next != null
                            && (next.type == 6/*{*/
                            && next.type == 8/*;*/
                            && next.type == 11/* + */
                            && next.type == 12/* - */
                            && next.type == 13/* * */
                            && next.type == 14/* / */)) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // {
                case 6:
                    break;
                // }
                case 7:
                    break;
                // ;
                case 8:
                    break;
                // ,
                case 9:
                    break;
                // =
                case 10:
                    break;
                // +
                case 11:
                    break;
                // -
                case 12:
                    break;
                // *
                case 13:
                    break;
                // /
                case 14:
                    break;
                // void
                case 15:
                    break;
                // int
                case 16:
                    break;
                // boolean
                case 17:
                    break;
                // break
                case 18:
                    break;
                // continue
                case 19:
                    break;
                // return
                case 20:
                    break;
                // if
                case 21:
                    break;
                // else
                case 22:
                    break;
                // while
                case 23:
                    break;
                // function
                case 24:
                    break;
                // true
                case 25:
                    break;
                // false
                case 26:
                    break;
                // print
                case 27:
                    break;
                // <
                case 28:
                    break;
                // >
                case 29:
                    break;
                // <=
                case 30:
                    break;
                // >=
                case 31:
                    break;
                // ==
                case 32:
                    break;
                // !=
                case 33:
                    break;
                default:
                    break;
            }
        }
    }
}
