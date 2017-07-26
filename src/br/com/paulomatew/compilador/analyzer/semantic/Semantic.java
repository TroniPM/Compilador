package br.com.paulomatew.compilador.analyzer.semantic;

import br.com.paulomatew.compilador.entities.LexicalToken;
import br.com.paulomatew.compilador.exceptions.SemanticException;
import java.util.ArrayList;

/**
 * Created by Paulo Mateus on 26/06/2017. For project Compiladores
 * (https://github.com/TroniPM/Compilador) Contact: <paulomatew@gmail.com>
 */
public class Semantic {

    private ArrayList<LexicalToken> tokens = null;
    private ArrayList<String> escopos = null;

    private ArrayList<LexicalToken> identificadoresDeclaracao = null;

    public void init(ArrayList<LexicalToken> tokens, ArrayList<String> escopos) throws SemanticException {
        this.tokens = tokens;
        this.escopos = escopos;

        /*for (LexicalToken in : tokens) {
            System.out.println(in.lexeme);
        }*/
        LexicalToken flag = checkVariableAlreadyDefinedInScope();
        if (flag != null) {
            throw new SemanticException("Variable already defined in method: "
                    + flag.lexeme + " at line " + flag.line + ", position " + flag.position);
        }

        flag = checkVariableWasDefinedInScopeBeforeUse();
        if (flag != null) {
            throw new SemanticException("Variable might not have been initialized: "
                    + flag.lexeme + " at line " + flag.line + ", position " + flag.position);
        }
    }

    private LexicalToken checkVariableAlreadyDefinedInScope() {
        for (int i = 0; i < escopos.size(); i++) {
            String escopo = escopos.get(i);
            ArrayList<String> token = new ArrayList<>();

            for (int j = 0; j < tokens.size(); j++) {//apenas identificadores
                LexicalToken atual = tokens.get(j);
                if (atual.type != 1 || !atual.scope.equals(escopo)) {
                    continue;
                }
                LexicalToken anterior = tokens.get(j - 1);
                if (anterior.type != 16 && anterior.type != 17) {
                    continue;
                }

                if (!token.contains(atual.lexeme)) {
                    token.add(atual.lexeme);
                } else if (token.contains(atual.lexeme)) {
                    return atual;
                }
            }
        }

        return null;
    }

    private LexicalToken checkVariableWasDefinedInScopeBeforeUse() {
        for (int i = 0; i < escopos.size(); i++) {
            String escopo = escopos.get(i);
            ArrayList<String> token = new ArrayList<>();

            boolean erro = false;

            for (int j = 0; j < tokens.size(); j++) {//apenas identificadores
                LexicalToken atual = tokens.get(j);
                if (atual.type != 1 || !atual.scope.equals(escopo)) {
                    continue;
                }
                LexicalToken anterior = tokens.get(j - 1);
                if (anterior.type == 16 || anterior.type == 17 || anterior.type == 36) {
                    //INT, BOOLEAN, CALL
                    continue;
                }
                /*
                fazer verificação agora se variável foi "criada" antes de ser utilizada.
                 */

                //atual.print();
                /*lala:
                for (int x = 0; x <= j; x++) {
                    if (tokens.get(x).scope.equals(atual.scope)
                            && tokens.get(x).lexeme.equals(atual.lexeme)
                            && (tokens.get(x).type == 16 || tokens.get(x).type == 17)) {
                        erro = false;
                        break lala;
                    } else {
                        erro = true;
                    }
                }

                if (erro) {
                    return atual;
                }*/
            }
        }

        return null;
    }

}
