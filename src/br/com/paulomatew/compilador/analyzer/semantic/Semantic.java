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

    public void init(ArrayList<LexicalToken> tokens, ArrayList<Escopo> escopos) throws SemanticException {
        this.tokens = tokens;
        this.escopos = escopos;

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
            throw new SemanticException("Variable already defined as a method: "
                    + flag.lexeme + " at line " + flag.line + ", position " + flag.position
                    + ", scope " + flag.scope);
        }

        flag = checkIfMethodAlreadyDeclared();
        if (flag != null) {
            throw new SemanticException("Method already defined: "
                    + flag.lexeme + " at line " + flag.line + ", position " + flag.position
                    + ", scope " + flag.scope);
        }

        flag = checkAtribs();
        if (flag != null) {
            throw new SemanticException("Expression has an unexpected type: "
                    + flag.lexeme + " at line " + flag.line + ", position " + flag.position
                    + ", scope " + flag.scope);
        }

        flag = checkReturnTypeMethods();
        if (flag != null) {
            throw new SemanticException("Method has an unexpected return type: "
                    + flag.lexeme + " at line " + flag.line + ", position " + flag.position
                    + ", scope " + flag.scope);
        }

        flag = checkArgumentsNumber();
        if (flag != null) {
            throw new SemanticException("Method called with number of arguments wrong: "
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
                if (anterior.type == 15 || anterior.type == 16 || anterior.type == 17 || anterior.type == 36) {
                    //VOID, INT, BOOLEAN, CALL
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
                if ((anterior.type == 16 || anterior.type == 17)) {
                    //return true;
                    if (anterior.line < token.line) {
                        token.regra = atual.regra;
                        return true;
                    } else if (anterior.line == token.line && anterior.position < token.position) {
                        return true;
                    }
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

    private LexicalToken checkIfMethodAlreadyDeclared() {
        for (int i = 0; i < escopos.size(); i++) {
            String escopo = escopos.get(i).label;
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

    private LexicalToken checkReturnTypeMethods() {
        for (int i = 0; i < escopos.size(); i++) {
            Escopo escopo = escopos.get(i);

            for (int j = 0; j < tokens.size(); j++) {//apenas identificadores
                LexicalToken ident = tokens.get(j);
                if (ident.type != 1 || !ident.scope.equals(escopo.label)) {
                    continue;
                }
                LexicalToken funcao = tokens.get(j - 2);
                if (funcao.type != 24) {
                    //function
                    continue;
                }
//aqui
                //ident.print();
                LexicalToken retorno = null, pontoVirgula = null;
                int x;
                for (x = j; x < tokens.size(); x++) {
                    if (tokens.get(x).type == 20) {
                        retorno = tokens.get(x);
                        break;
                    }

                }
                if (retorno != null) {//evitar nullpointer em function VOID
                    retorno.regra = ident.regra;
                }

                int x1;
                for (x1 = x; x1 < tokens.size(); x1++) {
                    if (tokens.get(x1).type == 8) {
                        pontoVirgula = tokens.get(x1);
                        break;
                    }

                }

                for (int y = x + 1; y < x1; y++) {
                    LexicalToken atual = tokens.get(y);
                    LexicalToken anterior = tokens.get(y - 1);

                    if (ident.regra.equals("int")) {
                        if (atual.regra != null && (atual.regra.equals("exp_arit")
                                || atual.regra.equals("call_func")
                                //|| atual.regra.equals("param_boolean")
                                || atual.regra.equals("param_int")
                                || atual.regra.equals("func_iden")
                                || atual.regra.equals("int")
                                || (anterior.lexeme.equals("boolean") && atual.regra.equals("boolean")))
                                || atual.regra == null) {
                        } else {
                            return atual;
                        }
                    } else if (ident.regra.equals("boolean")) {
                        if (atual.regra != null && (atual.regra.equals("exp_logic")
                                || atual.regra.equals("call_func")
                                || atual.regra.equals("param_boolean")
                                //| atual.regra.equals("param_int")
                                || atual.regra.equals("func_iden")
                                || atual.regra.equals("boolean")
                                || (anterior.lexeme.equals("int") && atual.regra.equals("int"))
                                || (anterior.lexeme.equals("(") && atual.regra.equals("param_int")))
                                || atual.regra == null) {
                        } else {
                            return atual;
                        }
                    }
                }

                /*for (x = j; x < tokens.size(); x++) {
                    if (tokens.get(x).type == 20) {
                        if (identificador.regra.equals("int")
                                && (tokens.get(x + 1).regra.equals("exp_arit")
                                || tokens.get(x + 1).regra.equals("param_int"))) {

                        } else if (identificador.regra.equals("boolean")
                                && (tokens.get(x + 1).regra.equals("exp_logic")
                                || tokens.get(x + 1).regra.equals("param_boolean"))) {
                        } else {
                            return identificador;
                        }
                        break;
                    }
                }*/
            }
        }

        return null;
    }

    private LexicalToken checkAtribs() {
        int i;
        for (i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).type == 10) {
                LexicalToken ident = tokens.get(i - 1);
                LexicalToken token = tokens.get(i);

                LexicalToken pontoVirgula = null;
                int j;
                for (j = i; j < tokens.size(); j++) {
                    if (tokens.get(j).type == 8) {
                        pontoVirgula = tokens.get(j);
                        break;
                    }
                }
                for (int x = i + 1; x < j; x++) {
                    LexicalToken atual = tokens.get(x);
                    LexicalToken anterior = tokens.get(x - 1);

                    if (ident.regra.equals("int")) {
                        if (atual.regra != null && (atual.regra.equals("exp_arit")
                                || atual.regra.equals("call_func")
                                || atual.regra.equals("arg_bool")
                                || atual.regra.equals("arg_int")
                                || atual.regra.equals("func_iden")
                                || atual.regra.equals("int")
                                || (anterior.lexeme.equals(",") || anterior.lexeme.equals("(") && atual.regra.equals("boolean")))
                                || atual.regra == null) {
                        } else {
                            return atual;
                        }
                    } else if (ident.regra.equals("boolean")) {
                        if (atual.regra != null && (atual.regra.equals("exp_logic")
                                || atual.regra.equals("call_func")
                                || atual.regra.equals("arg_bool")
                                || atual.regra.equals("arg_int")
                                || atual.regra.equals("func_iden")
                                || atual.regra.equals("boolean")
                                || (anterior.lexeme.equals(",") || anterior.lexeme.equals("(") && atual.regra.equals("int")))
                                || atual.regra == null) {
                        } else {
                            return atual;
                        }
                    }
                }
            }
        }
        return null;
    }

    private LexicalToken checkArgumentsNumber() {
        for (int i = 0; i < escopos.size(); i++) {
            Escopo escopo = escopos.get(i);

            for (int j = 0; j < tokens.size(); j++) {//apenas identificadores
                LexicalToken declaracaofuncao = tokens.get(j);
                if (declaracaofuncao.type != 1 || !declaracaofuncao.scope.equals(escopo.label)) {
                    continue;
                }
                if (tokens.get(j - 2).type != 24) {
                    //function
                    continue;
                }
//aqui
                //ident.print();
                LexicalToken pontoVirgula1 = null;
                int m;
                for (m = j; j < tokens.size(); m++) {
                    if (tokens.get(m).type == 8) {
                        pontoVirgula1 = tokens.get(m);
                        break;
                    }
                }
                int qtdParametros = 0;
                for (int n = j; n < m; n++) {
                    if (tokens.get(n).lexeme.equals(",")) {
                        qtdParametros++;
                    }
                }
                System.out.println("#####################################");
                System.out.println("PARAM-> " + qtdParametros);
                if (qtdParametros != 0) {
                    qtdParametros++;//adiciono mais um, para ficar correto
                } else if (m - j == 8) {
                    //System.out.println("ENTROU");
                    //SE SÓ EXISTIR UM ARGUMENTO
                    qtdParametros = 1;
                }

                System.out.println("M: " + m + "\tJ: " + j + "\t m - j = " + (m - j));

                //System.out.println("m - j == " + (m - j));
                for (int x = 0; x < tokens.size(); x++) {
                    if (tokens.get(x).type == 36) {
                        LexicalToken call = tokens.get(x);
                        LexicalToken chamadaFuncao = tokens.get(x + 1);
                        if (chamadaFuncao.lexeme.equals(declaracaofuncao.lexeme)) {
                            //chamadaFuncao.print();

                            //LexicalToken pontoVirgula = null;
                            int y;
                            for (y = x + 1; y < tokens.size(); y++) {
                                if (tokens.get(y).type == 8) {
                                    //pontoVirgula = tokens.get(y);
                                    break;
                                }
                            }
                            int qtdArgumentos = 0;
                            for (int w = x + 1; w < y; w++) {
                                if (tokens.get(w).lexeme.equals(",")) {
                                    qtdArgumentos++;
                                }
                            }

                            if (qtdArgumentos != 0) {
                                qtdArgumentos++;//adiciono mais um, para ficar correto
                            } else if (y - x == 5) {
                                //SE SÓ EXISTIR UM ARGUMENTO
                                qtdArgumentos++;
                            }

                            System.out.println("QUANTIDADE PARÂMETROS: " + qtdParametros + "\tQUANTIDADE ARGUMENTOS: " + qtdArgumentos);

                            if (qtdArgumentos != qtdParametros) {
                                return chamadaFuncao;
                            }
                        }
                    }
                }

            }
        }
        return null;
    }
}
