package br.com.paulomatew.compilador.analyzer.semantic;

import br.com.paulomatew.compilador.entities.Escopo;
import br.com.paulomatew.compilador.entities.LexicalToken;
import br.com.paulomatew.compilador.exceptions.SemanticException;
import java.util.ArrayList;

/**
 * Created by Paulo Mateus on 26/06/2017. For project Compiladores
 * (https://github.com/TroniPM/Compilador) Contact: <paulomatew@gmail.com>
 */
public class Semantic {

    private ArrayList<LexicalToken> tokens = null;
    private ArrayList<Escopo> escopos = null;

    //private ArrayList<LexicalToken> indentifiersDefinitions = null;
    //private ArrayList<LexicalToken> indentifiersUse = null;
    public void init(ArrayList<LexicalToken> tokens, ArrayList<Escopo> escopos) throws SemanticException {
        this.tokens = tokens;
        this.escopos = escopos;

        //this.indentifiersDefinitions = getIndentifiersDefinitions();
        //this.indentifiersUse = getIndentifiersUse();
        //for (LexicalToken in : indentifiersDefinitions) {
        //    in.print();
        //}

        /*for (LexicalToken in : tokens) {
            System.out.println(in.lexeme);
        }*/
        LexicalToken flag = checkVariableAlreadyDefinedInScope();
        if (flag != null) {
            throw new SemanticException("Variable already defined in method: "
                    + flag.lexeme + " at line " + flag.line + ", position " + flag.position
                    + ", scope " + flag.scope);
        }

        flag = checkVariableWasDefinedInScopeBeforeUse();
        if (flag != null) {
            throw new SemanticException("Variable might not have been initialized: "
                    + flag.lexeme + " at line " + flag.line + ", position " + flag.position
                    + ", scope " + flag.scope);
        }

        flag = checkIfIdentifierHasSameNameMethod();
        if (flag != null) {
            throw new SemanticException("Variable already defined as method: "
                    + flag.lexeme + " at line " + flag.line + ", position " + flag.position
                    + ", scope " + flag.scope);
        }

        flag = checkIfMethodAlradeyDeclared();
        if (flag != null) {
            throw new SemanticException("Method already defined: "
                    + flag.lexeme + " at line " + flag.line + ", position " + flag.position
                    + ", scope " + flag.scope);
        }
    }

    private LexicalToken checkVariableAlreadyDefinedInScope() {
        for (int i = 0; i < escopos.size(); i++) {
            String escopo = escopos.get(i).label;
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
            Escopo escopo = escopos.get(i);
            ArrayList<String> token = new ArrayList<>();

            boolean erro = false;

            for (int j = 0; j < tokens.size(); j++) {//apenas identificadores
                LexicalToken atual = tokens.get(j);
                if (atual.type != 1 || !atual.scope.equals(escopo.label)) {
                    continue;
                }
                LexicalToken anterior = tokens.get(j - 1);
                if (anterior.type == 16 || anterior.type == 17 || anterior.type == 36) {
                    //INT, BOOLEAN, CALL
                    continue;
                }
                boolean flag = checkVariableDefinedInScopeTree(atual, escopo);
                if (!flag) {
                    return atual;
                }
            }
        }

        return null;
    }

    private boolean checkVariableDefinedInScope(LexicalToken token, Escopo escopo) {
        for (int i = 0; i < tokens.size(); i++) {
            LexicalToken atual = tokens.get(i);

            if (atual.type == 1 && atual.scope.equals(escopo.label)
                    && atual.lexeme.equals(token.lexeme)) {

                LexicalToken anterior = tokens.get(i - 1);
                if (anterior.type == 16 || anterior.type == 17) {
                    return true;
                }
            }

        }
        return false;
    }

    private boolean checkVariableDefinedInScopeTree(LexicalToken token, Escopo escopo) {
        if (checkVariableDefinedInScope(token, escopo)) {
            return true;
        }

        if (escopo.pai == null) {
            return false;
        }

        return checkVariableDefinedInScopeTree(token, escopo.pai);
    }

    private LexicalToken checkIfIdentifierHasSameNameMethod() {
        boolean mesmoNome = false;
        for (int i = 0; i < escopos.size(); i++) {
            String escopo = escopos.get(i).label;
            ArrayList<String> token = new ArrayList<>();

            for (int j = 0; j < tokens.size(); j++) {//apenas identificadores
                LexicalToken atual = tokens.get(j);
                if (atual.type != 1 || !atual.scope.equals(escopo)) {
                    continue;
                }
                LexicalToken anterior1 = tokens.get(j - 1);
                LexicalToken anterior2 = tokens.get(j - 2);
                if (anterior2.type == 24 || anterior1.type != 16 && anterior1.type != 17) {
                    //int, boolean, function
                    continue;
                }
//aqui
                for (int x = 0; x < tokens.size(); x++) {
                    if (tokens.get(x).type == 24) {
                        LexicalToken identificadorFuncao = tokens.get(x + 2);
                        if (atual.lexeme.equals(identificadorFuncao.lexeme)) {
                            return atual;
                        }
                    }
                }
            }
        }

        return null;
    }

    private LexicalToken checkIfMethodAlradeyDeclared() {
        for (int i = 0; i < escopos.size(); i++) {
            String escopo = escopos.get(i).label;
            ArrayList<String> token = new ArrayList<>();

            for (int j = 0; j < tokens.size(); j++) {//apenas identificadores
                LexicalToken atual = tokens.get(j);
                if (atual.type != 1 || !atual.scope.equals(escopo)) {
                    continue;
                }
                LexicalToken anterior1 = tokens.get(j - 2);
                if (anterior1.type != 24) {
                    //function
                    continue;
                }
//aqui
                for (int x = 0; x < tokens.size(); x++) {
                    if (tokens.get(x).type == 24 && tokens.get(x + 2) != atual) {

                        LexicalToken identificadorFuncao = tokens.get(x + 2);
                        if (atual.lexeme.equals(identificadorFuncao.lexeme)) {
                            return atual;
                        }
                    }
                }
            }
        }

        return null;
    }

    private ArrayList<LexicalToken> getIndentifiersUse() {
        ArrayList<LexicalToken> arr = new ArrayList<>();

        for (int i = 0; i < escopos.size(); i++) {
            Escopo escopo = escopos.get(i);
            ArrayList<String> token = new ArrayList<>();

            boolean erro = false;

            for (int j = 0; j < tokens.size(); j++) {//apenas identificadores
                LexicalToken atual = tokens.get(j);
                if (atual.type != 1 || !atual.scope.equals(escopo.label)) {
                    continue;
                }
                LexicalToken anterior = tokens.get(j - 1);
                if (anterior.type == 16 || anterior.type == 17 || anterior.type == 36) {
                    //INT, BOOLEAN, CALL
                    continue;
                }

                arr.add(atual);
            }
        }

        return arr;
    }
}
