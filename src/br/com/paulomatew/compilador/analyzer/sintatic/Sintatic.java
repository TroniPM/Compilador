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
import java.util.List;
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

        //parserOld();
        parserNew();
    }

    private void parserOld() throws SintaticException {
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
            try {
                if (tokenListFromLexical.get(i + 1) != null) {
                    next = tokenListFromLexical.get(i + 1);
                }
            } catch (Exception ex) {
            }

            /**
             * Falta inserir BREAK/CONTINUE/RETURN
             */
            switch (current.type) {
                //constante
                case 0:
                    if (next != null
                            && (next.type == 5/* ) */
                            || next.type == 8/* ; */
                            || next.type == 9/* , */
                            || next.type == 11/* + */
                            || next.type == 12/* - */
                            || next.type == 13/* * */
                            || next.type == 14/* / */
                            || next.type == 28/* < */
                            || next.type == 29/* > */
                            || next.type == 30/* <= */
                            || next.type == 31/* >= */
                            || next.type == 32/* == */
                            || next.type == 33/* != */
                            || next.type == 34/* && */
                            || next.type == 35/* || */)) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // identificador
                case 1:
                    if (next != null
                            && (next.type == 4/* ( */
                            || next.type == 5/* ) */
                            || next.type == 8/* ; */
                            || next.type == 9/* , */
                            || next.type == 10/* = */
                            || next.type == 11/* + */
                            || next.type == 12/* - */
                            || next.type == 13/* * */
                            || next.type == 14/* / */
                            || next.type == 27/*print*/
                            || next.type == 28/* < */
                            || next.type == 29/* > */
                            || next.type == 30/* <= */
                            || next.type == 31/* >= */
                            || next.type == 32/* == */
                            || next.type == 33/* != */
                            || next.type == 34/* && */
                            || next.type == 35/* || */)) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // token desconhecido
                case 2://TODO: token desconhecido deve chegar a este ponto??
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
                            && (next.type == 0/*constante*/
                            || next.type == 1/*identificador*/
                            || next.type == 4/*abre_p*/
                            || next.type == 5/*fecha_p*/
                            || next.type == 16/*int*/
                            || next.type == 17/*boolean*/
                            || next.type == 25/*true*/
                            || next.type == 26/*false*/
                            || next.type == 36/*chamar função*/)) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // )
                case 5:
                    if (next != null
                            && (next.type == 5/* ) */
                            || next.type == 6/* { */
                            || next.type == 8/* ; */
                            || next.type == 11/* + */
                            || next.type == 12/* - */
                            || next.type == 13/* * */
                            || next.type == 14/* / */
                            || next.type == 28/* < */
                            || next.type == 29/* > */
                            || next.type == 30/* <= */
                            || next.type == 31/* >= */
                            || next.type == 32/* == */
                            || next.type == 33/* != */
                            || next.type == 34/* && */
                            || next.type == 35/* || */)) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // {
                case 6:
                    if (next != null//Não entra FUNCTION pq antes do function vem o tipo de retorno void/int/boolean
                            && (next.type == 1/*identificador*/
                            || next.type == 7/* } */
                            || next.type == 15/*void*/
                            || next.type == 16/*int*/
                            || next.type == 17/*boolean*/
                            || next.type == 18/*break*/
                            || next.type == 19/*continue*/
                            || next.type == 20/*return*/
                            || next.type == 21/*if*/
                            || next.type == 23/*while*/
                            || next.type == 27/*print*/
                            || next.type == 36/*chamar função*/)) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // }
                case 7:
                    if (next == null//next==null significa q é EOF
                            || (next.type == 1/*identificador*/
                            || next.type == 7/* } */
                            || next.type == 15/*void*/
                            || next.type == 16/*int*/
                            || next.type == 17/*boolean*/
                            || next.type == 20/*return*/
                            || next.type == 21/*if*/
                            || next.type == 22/*else*/
                            || next.type == 23/*while*/
                            || next.type == 27/*print*/
                            || next.type == 36/*chamar função*/)) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // ;
                case 8:
                    if (next != null
                            && (next.type == 1/*identificador*/
                            || next.type == 7/* } */
                            || next.type == 8/* ; FAZER COM Q ;; SEJA POSSÍVEL*/
                            || next.type == 15/*void*/
                            || next.type == 16/*int*/
                            || next.type == 17/*boolean*/
                            || next.type == 21/*if*/
                            || next.type == 23/*while*/
                            || next.type == 27/*print*/
                            || next.type == 36/*chamar função*/)) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // ,
                case 9:
                    if (next != null
                            && (next.type == 1/*identificador*/
                            || next.type == 16/*int*/
                            || next.type == 17/*boolean*/
                            || next.type == 36/*chamar função*/)) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // =
                case 10:
                    if (next != null
                            && (next.type == 0/*constante*/
                            || next.type == 1/*identificador*/
                            || next.type == 4/*abre_p*/
                            || next.type == 25/*true*/
                            || next.type == 26/*false*/
                            || next.type == 36/*chamar função*/)) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // +
                case 11:
                    if (next != null
                            && (next.type == 0/*constante*/
                            || next.type == 1/*identificador*/
                            || next.type == 4/*abre_p*/
                            || next.type == 36/*chamar função*/)) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // -
                case 12:
                    if (next != null
                            && (next.type == 0/*constante*/
                            || next.type == 1/*identificador*/
                            || next.type == 4/*abre_p*/
                            || next.type == 36/*chamar função*/)) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // *
                case 13:
                    if (next != null
                            && (next.type == 0/*constante*/
                            || next.type == 1/*identificador*/
                            || next.type == 4/*abre_p*/
                            || next.type == 36/*chamar função*/)) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // /
                case 14:
                    if (next != null
                            && (next.type == 0/*constante*/
                            || next.type == 1/*identificador*/
                            || next.type == 4/*abre_p*/
                            || next.type == 36/*chamar função*/)) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // void
                case 15:
                    if (next != null && next.type == 24/*function*/) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // int
                case 16:
                    if (next != null
                            && (next.type == 1/*identificador*/
                            || next.type == 24/*function*/)) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // boolean
                case 17:
                    if (next != null
                            && (next.type == 1/*identificador*/
                            || next.type == 24/*function*/)) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // break
                case 18:
                    if (next != null && next.type == 8/* , */) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // continue
                case 19:
                    if (next != null && next.type == 8/* , */) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // return
                case 20:
                    if (next != null
                            && (next.type == 1/*identificador*/
                            || next.type == 4/*abre_p*/
                            || next.type == 25/*true*/
                            || next.type == 26/*false*/
                            || next.type == 36/*chamar função*/)) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // if
                case 21:
                    if (next != null && next.type == 4/*abre_p*/) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // else
                case 22:
                    if (next != null && next.type == 6/*abre_c*/) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // while
                case 23:
                    if (next != null && next.type == 4/*abre_p*/) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // function
                case 24:
                    if (next != null && next.type == 1/*identificador*/) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // true
                case 25:
                    if (next != null
                            && (next.type == 5/* ) */
                            || next.type == 8/* ; */
                            || next.type == 9/* , */
                            || next.type == 32/* == */
                            || next.type == 33/* != */
                            || next.type == 34/* && */
                            || next.type == 35/* || */)) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // false
                case 26:
                    if (next != null
                            && (next.type == 5/* ) */
                            || next.type == 8/* ; */
                            || next.type == 9/* , */
                            || next.type == 32/* == */
                            || next.type == 33/* != */
                            || next.type == 34/* && */
                            || next.type == 35/* || */)) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // print
                case 27:
                    if (next != null && next.type == 4/*abre_p*/) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // <
                case 28:
                    if (next != null
                            && (next.type == 0/*constante*/
                            || next.type == 1/*identificador*/
                            || next.type == 4/*abre_p*/
                            || next.type == 36/*chamar função*/)) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // >
                case 29:
                    if (next != null
                            && (next.type == 0/*constante*/
                            || next.type == 1/*identificador*/
                            || next.type == 4/*abre_p*/
                            || next.type == 36/*chamar função*/)) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // <=
                case 30:
                    if (next != null
                            && (next.type == 0/*constante*/
                            || next.type == 1/*identificador*/
                            || next.type == 4/*abre_p*/
                            || next.type == 36/*chamar função*/)) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // >=
                case 31:
                    if (next != null
                            && (next.type == 0/*constante*/
                            || next.type == 1/*identificador*/
                            || next.type == 4/*abre_p*/
                            || next.type == 36/*chamar função*/)) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // ==
                case 32:
                    if (next != null
                            && (next.type == 0/*constante*/
                            || next.type == 1/*identificador*/
                            || next.type == 4/*abre_p*/
                            || next.type == 25/*true*/
                            || next.type == 26/*false*/
                            || next.type == 36/*chamar função*/)) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // !=
                case 33:
                    if (next != null
                            && (next.type == 0/*constante*/
                            || next.type == 1/*identificador*/
                            || next.type == 4/*abre_p*/
                            || next.type == 25/*true*/
                            || next.type == 26/*false*/
                            || next.type == 36/*chamar função*/)) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // &&
                case 34:
                    if (next != null
                            && (next.type == 0/*constante*/
                            || next.type == 1/*identificador*/
                            || next.type == 4/*abre_p*/
                            || next.type == 25/*true*/
                            || next.type == 26/*false*/
                            || next.type == 36/*chamar função*/)) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // ||
                case 35:
                    if (next != null
                            && (next.type == 0/*constante*/
                            || next.type == 1/*identificador*/
                            || next.type == 4/*abre_p*/
                            || next.type == 25/*true*/
                            || next.type == 26/*false*/
                            || next.type == 36/*chamar função*/)) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                // int
                case 36:
                    if (next != null
                            && (next.type == 1/*identificador*/)) {
                        //TODO: do stuff here
                        continue;
                    } else {
                        throw new SintaticException("Unexpected token after '" + current.lexeme + "' line " + current.line);
                    }
                default:
                    break;
            }
        }
        //numero de () e {} devem ser iguais entre si
        int abrP = 0, fecP = 0, abrC = 0, fecC = 0;

        List<Integer> abrPi = new ArrayList<>();
        List<Integer> fecPi = new ArrayList<>();
        List<Integer> abrCi = new ArrayList<>();
        List<Integer> fecCi = new ArrayList<>();

        for (int x = 0; x < tokenListFromLexical.size(); x++) {
            switch (tokenListFromLexical.get(x).type) {
                case 4:
                    abrP++;
                    abrPi.add(x);
                    break;
                case 5:
                    fecP++;
                    fecPi.add(x);
                    break;
                case 6:
                    abrC++;
                    abrCi.add(x);
                    break;
                case 7:
                    fecC++;
                    fecCi.add(x);
                    break;
                default:
                    break;
            }
        }

        if (abrP > fecP) {
            int a = abrPi.get(abrPi.size() - fecPi.size() - 1);
            throw new SintaticException("Missing rigth parenthesis of '"
                    + tokenListFromLexical.get(a).lexeme
                    + "' line " + tokenListFromLexical.get(a).line);
        } else if (abrP < fecP) {
            int a = fecPi.get(fecPi.size() - abrPi.size() - 1);
            throw new SintaticException("Missing left parenthesis of '"
                    + tokenListFromLexical.get(a).lexeme
                    + "' line " + tokenListFromLexical.get(a).line);
        }
        if (abrC > fecC) {
            int a = abrCi.get(abrCi.size() - fecCi.size() - 1);
            throw new SintaticException("Missing rigth braces of '"
                    + tokenListFromLexical.get(a).lexeme
                    + "' line " + tokenListFromLexical.get(a).line);
        } else if (abrC < fecC) {
            int a = fecCi.get(fecCi.size() - abrCi.size() - 1);
            throw new SintaticException("Missing left braces of '"
                    + tokenListFromLexical.get(a).lexeme
                    + "' line " + tokenListFromLexical.get(a).line);
        }
    }

    private void parserNew() throws SintaticException {
        ArrayList<LexicalToken> arr = this.tokenListFromLexical;

        ArrayList<LexicalToken> pilha = new ArrayList<>();

        for (int i = 0; i < arr.size(); i++) {
            if (pilha.size() > 0) {
                if (arr.get(i).type != pilha.get(0).type) {
                    throw new SintaticException("Unexpected token '" + (arr.get(i).lexeme)
                            + "' at line " + arr.get(i).line + " (expected: '" + pilha.get(0).description + "').");
                } else {
                    pilha.remove(0);
                }
            }

            if (i == 0) {
                if (programa(arr.get(i))) {
                    pilha.add(new LexicalToken(4, "(", "("));
                    pilha.add(new LexicalToken(5, ")", ")"));
                    pilha.add(new LexicalToken(6, "{", "{"));
                    continue;
                } else {
                    throw new SintaticException("Unexpected token '" + (arr.get(i).lexeme)
                            + "' at line " + arr.get(i).line + " (expected: 'main').");
                }
            }

        }
    }

    /*private boolean abre_parent(LexicalToken token) {

    }

    private boolean fecha_parent(LexicalToken token) {

    }

    private boolean abre_chaves(LexicalToken token) {

    }*/
    private boolean escopo(LexicalToken token) {
        return token.type==16 || token.type==17 ||token.type==1 || token.type==27 ;

    }

    private boolean programa(LexicalToken token) {
        return token.lexeme.equals("main");
    }
}
