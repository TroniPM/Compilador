package br.com.paulomatew.compilador.core;

import br.com.paulomatew.compilador.entities.Escopo;
import br.com.paulomatew.compilador.entities.Token;
import br.com.paulomatew.compilador.exceptions.SemanticException;
import java.util.ArrayList;

/**
 * Created by Paulo Mateus on 26/06/2017. For project Compiladores
 * (https://github.com/TroniPM/Compilador) Contact: <paulomatew@gmail.com>
 */
public class Semantic {

    private ArrayList<Token> tokens = null;
    private ArrayList<Escopo> escopos = null;
    private ArrayList<Token> funcoesDeclaracao = null;
    private ArrayList<Token> funcoesChamada = null;

    public void init(ArrayList<Token> tokens, ArrayList<Escopo> escopos) throws SemanticException {
        this.tokens = tokens;
        this.escopos = escopos;
        this.funcoesDeclaracao = new ArrayList<>();
        this.funcoesChamada = new ArrayList<>();

        Token flag = checkVariableAlreadyDefinedInScope();
        if (flag != null) {
            throw new SemanticException("Variable already defined in method: '"
                    + flag.lexeme + "' at line " + flag.line + ", position " + flag.position
                    + ", scope " + flag.scope);
        }

        flag = checkVariableWasDefinedInScopeBeforeUse();
        if (flag != null) {
            throw new SemanticException("Variable might not have been initialized: '"
                    + flag.lexeme + "' at line " + flag.line + ", position " + flag.position
                    + ", scope " + flag.scope);
        }

        flag = checkIfIdentifierHasSameNameMethod();
        if (flag != null) {
            throw new SemanticException("Variable already defined as a method: '"
                    + flag.lexeme + "' at line " + flag.line + ", position " + flag.position
                    + ", scope " + flag.scope);
        }

        flag = checkIfMethodAlreadyDeclared();
        if (flag != null) {
            throw new SemanticException("Method already defined: '"
                    + flag.lexeme + "' at line " + flag.line + ", position " + flag.position
                    + ", scope " + flag.scope);
        }

        flag = checkAtribs();
        if (flag != null) {
            throw new SemanticException("Expression has an unexpected type: '"
                    + flag.lexeme + "' at line " + flag.line + ", position " + flag.position
                    + ", scope " + flag.scope);
        }

        flag = checkIfFunctionHasReturn();
        if (flag != null) {
            throw new SemanticException("Method was declared with return type " + flag.description1 + ", " + flag.regra + ": '"
                    + flag.lexeme + "' at line " + flag.line + ", position " + flag.position
                    + ", scope " + flag.scope);
        }

        flag = checkReturnTypeMethodsNew();
        if (flag != null) {
            throw new SemanticException("Method has an unexpected return type: '"
                    + flag.lexeme + "' at line " + flag.line + ", position " + flag.position
                    + ", scope " + flag.scope);
        }

        flag = checkArgumentsNumber();
        if (flag != null) {
            throw new SemanticException("Method called with number of arguments wrong: '"
                    + flag.lexeme + "' at line " + flag.line + ", position " + flag.position
                    + ", scope " + flag.scope);
        }

        flag = checkArgumentsType();
        if (flag != null) {
            throw new SemanticException("Method called with wrong arguments type: '"
                    + flag.lexeme + "' at line " + flag.line + ", position " + flag.position
                    + ", scope " + flag.scope);
        }

        flag = checkMethodWasDefined();
        if (flag != null) {
            throw new SemanticException("Method might not have been initialized: '"
                    + flag.lexeme + "' at line " + flag.line + ", position " + flag.position
                    + ", scope " + flag.scope);
        }

        flag = checkReturnFromMethodAndVariableAssigned();
        if (flag != null) {
            throw new SemanticException("Method return and variable assigned has different types: '"
                    + flag.lexeme + "' at line " + flag.line + ", position " + flag.position
                    + ", scope " + flag.scope);
        }

        flag = checkExpressionLogic();
        if (flag != null) {
            throw new SemanticException("Expression has an unexpected behaviour: starting in '"
                    + flag.lexeme + "' at line " + flag.line + ", position " + flag.position
                    + ", scope " + flag.scope);
        }
    }

    /**
     * Checa se varíavel foi definida duas vezes no mesmo escopo
     *
     * @return
     */
    private Token checkVariableAlreadyDefinedInScope() {
        for (int i = 0; i < escopos.size(); i++) {
            String escopo = escopos.get(i).label;
            ArrayList<String> token = new ArrayList<>();

            for (int j = 0; j < tokens.size(); j++) {//apenas identificadores
                Token atual = tokens.get(j);
                if (atual.type != 1 || !atual.scope.equals(escopo)) {
                    continue;
                }
                Token anterior = tokens.get(j - 1);
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

    /**
     * Checa se variável foi criada (apenas se ela for utilizada, claro)
     *
     * @return
     */
    private Token checkVariableWasDefinedInScopeBeforeUse() {
        for (int i = 0; i < escopos.size(); i++) {
            Escopo escopo = escopos.get(i);
            ArrayList<String> token = new ArrayList<>();

            boolean erro = false;

            for (int j = 0; j < tokens.size(); j++) {//apenas identificadores
                Token atual = tokens.get(j);
                if (atual.type != 1 || !atual.scope.equals(escopo.label)) {
                    continue;
                }
                Token anterior = tokens.get(j - 1);
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

    /**
     * Utilizado recursivamente por "checkVariableDefinedInScopeTree"
     *
     * @param token
     * @param escopo
     * @return
     */
    private boolean checkVariableDefinedInScope(Token token, Escopo escopo) {
        for (int i = 0; i < tokens.size(); i++) {
            Token atual = tokens.get(i);

            if (atual.type == 1 && atual.scope.equals(escopo.label)
                    && atual.lexeme.equals(token.lexeme)) {

                Token anterior = tokens.get(i - 1);
                if ((anterior.type == 16 || anterior.type == 17)) {
                    //return true;
                    if (anterior.line < token.line) {

                        /**
                         * Expressão lógica tem um tratamento específico,
                         * fazendo com q o identificador tenha informação que é
                         * uma expressão lógica e um INT ou BOOLEAN, isso para
                         * fazer a verificação de se uma expressão booleana é
                         * aceitável ou não.
                         */
                        if (token.regra != null && token.regra.equals("exp_logic")) {
                            //System.out.println("token.regra.equals");
                            //System.out.println(token.lexeme);
                            //token.print();
                            token.regra += "_" + atual.regra;
                        } else if (token.regra != null && token.regra.contains("exp_logic")) {

                        } else {
                            token.regra = atual.regra;
                        }
                        return true;
                    } else if (anterior.line == token.line && anterior.position < token.position) {
                        return true;
                    }
                }
            }

        }
        return false;
    }

    /**
     * Verifica se identificador foi definida no escopo, ou escopo ascendente
     * (em "linhas" anteriores)
     *
     * @param token
     * @param escopo
     * @return
     */
    private boolean checkVariableDefinedInScopeTree(Token token, Escopo escopo) {
        if (checkVariableDefinedInScope(token, escopo)) {
            return true;
        }

        if (escopo.pai == null) {
            return false;
        }

        return checkVariableDefinedInScopeTree(token, escopo.pai);
    }

    /**
     * Procura se variável criada já foi criada como nome de método
     *
     * @return
     */
    private Token checkIfIdentifierHasSameNameMethod() {
        boolean mesmoNome = false;
        for (int i = 0; i < escopos.size(); i++) {
            String escopo = escopos.get(i).label;
            ArrayList<String> token = new ArrayList<>();

            for (int j = 0; j < tokens.size(); j++) {//apenas identificadores
                Token atual = tokens.get(j);
                if (atual.type != 1 || !atual.scope.equals(escopo)) {
                    continue;
                }
                Token anterior1 = tokens.get(j - 1);
                Token anterior2 = tokens.get(j - 2);
                if (anterior2.type == 24 || anterior1.type != 16 && anterior1.type != 17) {
                    //int, boolean, function
                    continue;
                }
//aqui
                for (int x = 0; x < tokens.size(); x++) {
                    if (tokens.get(x).type == 24) {
                        Token identificadorFuncao = tokens.get(x + 2);
                        if (atual.lexeme.equals(identificadorFuncao.lexeme)) {
                            return atual;
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Verifica se metodo foi declarado (apenas se ele for utilizado, claro)
     *
     * @return
     */
    private Token checkIfMethodAlreadyDeclared() {
        for (int i = 0; i < escopos.size(); i++) {
            String escopo = escopos.get(i).label;
            for (int j = 0; j < tokens.size(); j++) {//apenas identificadores
                Token atual = tokens.get(j);
                if (atual.type != 1 || !atual.scope.equals(escopo)) {
                    continue;
                }
                Token anterior1 = tokens.get(j - 2);
                if (anterior1.type != 24) {
                    //function
                    continue;
                }
//aqui
                for (int x = 0; x < tokens.size(); x++) {
                    if (tokens.get(x).type == 24 && tokens.get(x + 2) != atual) {

                        Token identificadorFuncao = tokens.get(x + 2);
                        if (atual.lexeme.equals(identificadorFuncao.lexeme)) {
                            return atual;
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Checa o tipo de retorno de uma expressão com o seu tipo de retorno
     * declarado.
     *
     * @return
     */
    private Token checkReturnTypeMethodsOld() {
        for (int i = 0; i < escopos.size(); i++) {
            Escopo escopo = escopos.get(i);

            for (int j = 0; j < tokens.size(); j++) {//apenas identificadores
                Token ident = tokens.get(j);
                if (ident.type != 1 || !ident.scope.equals(escopo.label)) {
                    continue;
                }
                Token funcao = tokens.get(j - 2);
                if (funcao.type != 24) {
                    //function
                    continue;
                }
//aqui
                funcoesDeclaracao.add(ident);
                //ident.print();
                Token retorno = null;
                Token pontoVirgula = null;
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
                    Token atual = tokens.get(y);
                    Token anterior = tokens.get(y - 1);

                    if (ident.regra.equals("int")) {
                        if (atual.regra != null && (atual.regra.contains("exp_arit")
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
                        if (atual.regra != null && (atual.regra.contains("exp_logic")
                                || atual.regra.equals("call_func")
                                || atual.regra.equals("param_boolean")
                                //| atual.regra.equals("param_int")
                                || atual.regra.equals("func_iden")
                                || atual.regra.equals("boolean")
                                || (anterior.lexeme.contains("int") && atual.regra.contains("int"))
                                || (anterior.lexeme.equals("(") && atual.regra.equals("param_int")))
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

    private Token checkReturnTypeMethodsNew() {
        for (int i = 0; i < escopos.size(); i++) {
            Escopo escopo = escopos.get(i);

            for (int j = 0; j < tokens.size(); j++) {//apenas identificadores
                Token ident = tokens.get(j);
                if (ident.type != 1 || !ident.scope.equals(escopo.label)) {
                    continue;
                }
                Token funcao = tokens.get(j - 2);
                if (funcao.type != 24) {
                    //function
                    continue;
                }
//aqui
                funcoesDeclaracao.add(ident);
                //ident.print();
                Token retorno = null;
                Token pontoVirgula = null;
                int x;
                for (x = j; x < tokens.size(); x++) {
                    if (tokens.get(x).type == 20) {
                        retorno = tokens.get(x);
                        //retorno.print();
                        break;
                    }

                }
                if (retorno != null) {//evitar nullpointer em function VOID
                    //ident.print();
                    retorno.regra = ident.regra;
                } else {
                    continue;
                }

                int x1;
                for (x1 = x; x1 < tokens.size(); x1++) {
                    if (tokens.get(x1).type == 8) {
                        pontoVirgula = tokens.get(x1);
                        break;
                    }

                }
                //aqui
                if (retorno.regra.contains("boolean")) {
                    if (x1 - x == 2) {//só identificador
                        Token ident_or_truefalse = tokens.get(x + 1);
                        if (!ident_or_truefalse.regra.contains("boolean")) {
                            return ident_or_truefalse;
                        }
                    } else {
                        for (int w = x; w < x1; w++) {
                            Token atual = tokens.get(w);
                            Token operador = tokens.get(w + 1);
                            Token proximo = tokens.get(w + 2);
                            //averiguar todos os itens "i" até o "j"

                            if (atual.type == 1) {//identificador
                                //System.out.println("IDENTIFICADOR");
                                //X > X
                                //X > 1
                                //X > 1)
                                //(28) "<", ">", "<=", ">=", "==", "!="
                                if (atual.regra != null && atual.regra.contains("int")) {
                                    if ((operador.type == 28 || operador.type == 29
                                            || operador.type == 30 || operador.type == 31
                                            || operador.type == 32 || operador.type == 33) && proximo.regra.contains("int")) {

                                    } else {
                                        return atual;
                                    }
                                } else if (atual.regra != null && atual.regra.contains("boolean")) {
                                    if ((operador.type == 32 || operador.type == 33) && proximo.regra.contains("boolean")) {

                                    } else {
                                        return atual;
                                    }
                                }

                                w += 2;
                            } else if (atual.type == 0) {//constante
                                //System.out.println("CONSTANTE");
                                //(28) "<", ">", "<=", ">=", "==", "!="
                                if (atual.regra != null && atual.regra.contains("int")) {
                                    if ((operador.type == 28 || operador.type == 29
                                            || operador.type == 30 || operador.type == 31
                                            || operador.type == 32 || operador.type == 33) && proximo.regra.contains("int")) {

                                    } else {
                                        return atual;
                                    }
                                }
                                w += 2;
                            } else if (atual.type == 25 || atual.type == 26) {//true e false
                                //System.out.println("TRUE|FALSE");
                                //(28) "<", ">", "<=", ">=", "==", "!="
                                if (atual.regra != null && atual.regra.contains("boolean")) {
                                    if ((operador.type == 32 || operador.type == 33) && proximo.regra.contains("boolean")) {

                                    } else {
                                        return atual;
                                    }
                                }

                                w += 2;
                            }
                        }
                    }
                } else if (retorno.regra.contains("int")) {
                    if (x1 - x == 2) {//só identificador
                        Token ident_or_const = tokens.get(x + 1);
                        if (!ident_or_const.regra.contains("int")) {
                            return ident_or_const;
                        }
                    } else {
                        for (int w = x; w < x1; w++) {
                            Token atual = tokens.get(w);
                            Token operador = tokens.get(w + 1);
                            Token proximo = tokens.get(w + 2);
                            //averiguar todos os itens "i" até o "j"

                            if (atual.type == 1 || atual.type == 0) {//identificador
                                if (atual.regra != null && atual.regra.contains("int")) {
                                } else {
                                    return atual;
                                }

                                //w += 1;
                            } else if (atual.type == 25 || atual.type == 26) {//true e false
                                return atual;
                            }
                        }
                    }
                }
                //ate aqui
            }
        }

        return null;
    }

    /**
     * Verifica se exp_logic e exp_arit são atribuídas de maneira correta, de
     * acordo com o tipo da variável atribuída. Por exemplo, se numa soma tem um
     * identificador do tipo boolean, isso irá dar erro.
     *
     * @return
     */
    private Token checkAtribs() {
        int i;
        for (i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).type == 10) {//=
                Token ident = tokens.get(i - 1);
                //LexicalToken atribuicao = tokens.get(i);
                //LexicalToken pontoVirgula = null;
                int j;
                boolean isFunctionCall = false;
                for (j = i; j < tokens.size(); j++) {
                    if (tokens.get(j).type == 8) {
                        //pontoVirgula = tokens.get(j);
                        break;
                    }
                    if (tokens.get(j).type == 36) {
                        isFunctionCall = true;
                    }
                }
                for (int x = i + 1; x < j; x++) {
                    Token atual = tokens.get(x);

                    if (ident.regra.equals("int")) {
                        if (atual.regra != null && (atual.regra.contains("exp_arit")
                                || atual.regra.equals("call_func")
                                || atual.regra.equals("arg_boolean")
                                || atual.regra.equals("arg_int")
                                || atual.regra.equals("func_iden")
                                || atual.regra.equals("int")
                                || (isFunctionCall && atual.regra.equals("boolean")))
                                || atual.regra == null) {
                        } else {
                            return atual;
                        }
                    } else if (ident.regra.equals("boolean")) {
                        if (atual.regra != null && (atual.regra.contains("exp_logic")
                                || atual.regra.equals("call_func")
                                || atual.regra.equals("arg_boolean")
                                || atual.regra.equals("arg_int")
                                || atual.regra.equals("func_iden")
                                || atual.regra.contains("boolean")
                                || (isFunctionCall && atual.regra.contains("int")))
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

    /**
     * Verifica se o número de argumentos passados é igual ao número de
     * argumentos da definição do método.
     *
     * @return
     */
    private Token checkArgumentsNumber() {
        for (int i = 0; i < escopos.size(); i++) {
            Escopo escopo = escopos.get(i);

            for (int j = 0; j < tokens.size(); j++) {//apenas identificadores
                Token declaracaofuncao = tokens.get(j);
                if (declaracaofuncao.type != 1 || !declaracaofuncao.scope.equals(escopo.label)) {
                    continue;
                }
                if (tokens.get(j - 2).type != 24) {
                    //function
                    continue;
                }
//aqui
                Token pontoVirgula1 = null;
                int m;
                for (m = j; j < tokens.size(); m++) {
                    if (tokens.get(m).type == 6) {
                        pontoVirgula1 = tokens.get(m);

                        //pontoVirgula1.print();
                        break;
                    }
                }
                int qtdParametros = 0;
                for (int n = j; n < m; n++) {
                    if (tokens.get(n).lexeme.equals(",")) {
                        qtdParametros++;
                    }
                }
                if (qtdParametros != 0) {
                    qtdParametros++;//adiciono mais um, para ficar correto
                } else if (m - j == 5) {
                    //SE SÓ EXISTIR UM ARGUMENTO
                    qtdParametros = 1;
                }

                for (int x = 0; x < tokens.size(); x++) {
                    if (tokens.get(x).type == 36) {
                        Token chamadaFuncao = tokens.get(x + 1);
                        if (chamadaFuncao.lexeme.equals(declaracaofuncao.lexeme)) {
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

                            //System.out.println("QUANTIDADE PARÂMETROS: " + qtdParametros + "\tQUANTIDADE ARGUMENTOS: " + qtdArgumentos);
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

    /**
     * Verifica se os tipos de argumentos passados são iguais aos tipos de
     * argumentos declarados.
     *
     * @return
     */
    private Token checkArgumentsType() {
        for (int i = 0; i < escopos.size(); i++) {
            Escopo escopo = escopos.get(i);

            for (int j = 0; j < tokens.size(); j++) {//apenas identificadores
                Token declaracaofuncao = tokens.get(j);
                if (declaracaofuncao.type != 1 || !declaracaofuncao.scope.equals(escopo.label)) {
                    continue;
                }
                if (tokens.get(j - 2).type != 24) {
                    //function
                    continue;
                }
                //LexicalToken pontoVirgula1 = null;
                int m;
                for (m = j; m < tokens.size(); m++) {
                    if (tokens.get(m).type == 6) {
                        //pontoVirgula1 = tokens.get(m);
                        break;
                    }
                }

                ArrayList<Token> tiposDeclaracao = new ArrayList<>();
                for (int w = j + 1; w < m; w++) {
                    if (tokens.get(w).lexeme.contains("int") || tokens.get(w).lexeme.contains("boolean")) {
                        tiposDeclaracao.add(tokens.get(w));
                    }
                }
                /*for (LexicalToken in : tipos) {
                    System.out.println(in.lexeme);
                }*/
//aqui1

                for (int x = 0; x < tokens.size(); x++) {
                    if (tokens.get(x).type == 36) {
                        Token chamadaFuncao = tokens.get(x + 1);
                        if (chamadaFuncao.lexeme.equals(declaracaofuncao.lexeme)) {
//aqui2
                            funcoesChamada.add(chamadaFuncao);
                            chamadaFuncao.regra = declaracaofuncao.regra;
                            //declaracaofuncao.print();
                            //chamadaFuncao.print();
                            int m1;
                            for (m1 = x + 1; m1 < tokens.size(); m1++) {
                                if (tokens.get(m1).type == 8) {
                                    //tokens.get(m1).print();
                                    break;
                                }
                            }
//aqui3
                            ArrayList<Token> tiposChamada = new ArrayList<>();
                            for (int w1 = x + 2; w1 < m1; w1++) {
                                Token t = tokens.get(w1);
                                if (t.regra == null
                                        || t.lexeme.equals(",")
                                        || t.lexeme.equals("(")
                                        || t.lexeme.equals(")")) {
                                    continue;
                                }
                                tiposChamada.add(t);
                            }
                            for (int mm = 0; mm < tiposDeclaracao.size(); mm++) {
                                //System.out.println(tiposDeclaracao.get(mm).regra + "\t" + tiposChamada.get(mm).regra);
                                //for (int mm1 = 0; mm1 < tiposChamada.size(); mm1++) {
                                if (tiposDeclaracao.get(mm).regra.equals("param_type_int")
                                        && tiposChamada.get(mm).regra.contains("int")) {

                                } else if (tiposDeclaracao.get(mm).regra.equals("param_type_boolean")
                                        && tiposChamada.get(mm).regra.contains("boolean")) {

                                } else {
                                    return tiposChamada.get(mm);
                                }
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Verifica se algum método foi utilizado e se foi declarado. Se ele foi
     * utilizado mas não foi declarado, dará erro.
     *
     * @return
     */
    private Token checkMethodWasDefined() {//TODO 7
        for (int i = 0; i < tokens.size(); i++) {
            boolean flag = true;
            if (tokens.get(i).type == 36) {
                //tokens.get(i).print();
                Token t = tokens.get(i + 1);
                for (int j = 0; j < funcoesDeclaracao.size(); j++) {
                    //System.out.println(funcoes.get(j).lexeme);
                    if (t.lexeme.equals(funcoesDeclaracao.get(j).lexeme)) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    return t;
                }
            }
        }
        return null;
    }

    /**
     * Verifica se o tipo de retorno do método é compatível com a variável a ser
     * atribuído esse valor.
     *
     * @return
     */
    private Token checkReturnFromMethodAndVariableAssigned() {
        for (int i = 0; i < tokens.size(); i++) {
            //boolean flag = true;
            if (tokens.get(i).type == 36) {
                if (tokens.get(i - 1).type != 10) {//chamar método sem atribuir a variável
                    continue;
                }
                Token identificadorFuncao = tokens.get(i + 1);
                Token identificadorAtribuido = tokens.get(i - 2);

                if (!identificadorFuncao.regra.equals(identificadorAtribuido.regra)) {
                    return identificadorFuncao;
                }
            }
        }
        return null;
    }

    /**
     * Verifica se expressão lógica está com comparações adequadas: "1 > 2" e
     * não "1 > true". Também verifica tipos de identificadores em relação as
     * comparações.
     *
     * @return
     */
    private Token checkExpressionLogic() {
        /*if (true) {
            return null;
        }*/
        /**
         * CASO IF 21 | CASO WHILE 23
         */
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).type == 21 || tokens.get(i).type == 23) {
                Token lexemaIF = tokens.get(i);
                //lexemaIF.print();
                Token fechaParentese = null;
                int j;
                for (j = i; j < tokens.size(); j++) {
                    if (tokens.get(j).type == 6) {// { 6, ; 8
                        fechaParentese = tokens.get(j);
                        break;
                    }
                }

                if (j - i == 4) {//só identificador
                    Token ident_or_truefalse = tokens.get(i + 2);
                    if (!ident_or_truefalse.regra.contains("boolean")) {
                        return ident_or_truefalse;
                    }
                } else {

                    for (int x = i; x < j; x++) {
                        Token atual = tokens.get(x);
                        Token operador = tokens.get(x + 1);
                        Token proximo = tokens.get(x + 2);
                        //averiguar todos os itens "i" até o "j"

                        if (atual.type == 1) {//identificador
                            //System.out.println("IDENTIFICADOR");
                            //X > X
                            //X > 1
                            //X > 1)
                            //(28) "<", ">", "<=", ">=", "==", "!="
                            if (atual.regra != null && atual.regra.contains("int")) {
                                if ((operador.type == 28 || operador.type == 29
                                        || operador.type == 30 || operador.type == 31
                                        || operador.type == 32 || operador.type == 33) && proximo.regra.contains("int")) {

                                } else {
                                    return atual;
                                }
                            } else if (atual.regra != null && atual.regra.contains("boolean")) {
                                if ((operador.type == 32 || operador.type == 33) && proximo.regra.contains("boolean")) {

                                } else {
                                    return atual;
                                }
                            }

                            x += 2;
                        } else if (atual.type == 0) {//constante
                            //System.out.println("CONSTANTE");
                            //(28) "<", ">", "<=", ">=", "==", "!="
                            if (atual.regra != null && atual.regra.contains("int")) {
                                if ((operador.type == 28 || operador.type == 29
                                        || operador.type == 30 || operador.type == 31
                                        || operador.type == 32 || operador.type == 33) && proximo.regra.contains("int")) {

                                } else {
                                    return atual;
                                }
                            }
                            x += 2;
                        } else if (atual.type == 25 || atual.type == 26) {//true e false
                            //System.out.println("TRUE|FALSE");
                            //(28) "<", ">", "<=", ">=", "==", "!="
                            if (atual.regra != null && atual.regra.contains("boolean")) {
                                if ((operador.type == 32 || operador.type == 33) && proximo.regra.contains("boolean")) {

                                } else {
                                    return atual;
                                }
                            }

                            x += 2;
                        }
                    }
                }
            }
        }

        /**
         * CASO ATRIBUIÇÃO 10
         */
        for (int i = 0; i < tokens.size(); i++) {//caso ATRIBUIÇÃO 10
            if (tokens.get(i).type == 10 && tokens.get(i).regra != null && tokens.get(i).regra.equals("atrib_exp_logic")) {
                Token lexemaATRIBUICAO = tokens.get(i);
                Token pontoVirgula = null;
                int j;
                for (j = i; j < tokens.size(); j++) {
                    if (tokens.get(j).type == 8) {// { 6, ; 8
                        pontoVirgula = tokens.get(j);
                        break;
                    }
                }

                for (int x = i; x < j; x++) {
                    Token atual = tokens.get(x);
                    Token operador = tokens.get(x + 1);
                    Token proximo = tokens.get(x + 2);
                    //averiguar todos os itens "i" até o "j"

                    if (atual.type == 1) {//identificador
                        //(28) "<", ">", "<=", ">=", "==", "!="
                        if (atual.regra != null && atual.regra.contains("int")) {
                            if ((operador.type == 28 || operador.type == 29
                                    || operador.type == 30 || operador.type == 31
                                    || operador.type == 32 || operador.type == 33) && proximo.regra.contains("int")) {

                            } else {
                                return atual;
                            }
                        } else if (atual.regra != null && atual.regra.contains("boolean")) {
                            if ((operador.type == 32 || operador.type == 33) && proximo.regra.contains("boolean")) {

                            } else {
                                return atual;
                            }
                        }

                        x += 2;
                    } else if (atual.type == 0) {//constante
                        //System.out.println("CONSTANTE");
                        //(28) "<", ">", "<=", ">=", "==", "!="
                        if (atual.regra != null && atual.regra.contains("int")) {
                            if ((operador.type == 28 || operador.type == 29
                                    || operador.type == 30 || operador.type == 31
                                    || operador.type == 32 || operador.type == 33) && proximo.regra.contains("int")) {

                            } else {
                                return atual;
                            }
                        }
                        x += 2;
                    } else if (atual.type == 25 || atual.type == 26) {//true e false
                        //System.out.println("TRUE|FALSE");
                        //(28) "<", ">", "<=", ">=", "==", "!="
                        if (atual.regra != null && atual.regra.contains("boolean")) {
                            if ((operador.type == 32 || operador.type == 33) && proximo.regra.contains("boolean")) {

                            } else {
                                return atual;
                            }
                        }

                        x += 2;
                    }
                }

            }
        }
        //VER SE ISSO JÁ NÃO ESTÁ SENDO FEITO NO MÉTODO checkReturnTypeMethods()
        /*for (int i = 0; i < tokens.size(); i++) {//caso return 20
            if (tokens.get(i).type == 20) {
                LexicalToken lexemaRETURN = tokens.get(i);
                LexicalToken pontoVirgula = null;
                int j;
                for (j = i; j < tokens.size(); j++) {
                    if (tokens.get(j).type == 8) {// { 6, ; 8
                        pontoVirgula = tokens.get(j);
                        break;
                    }
                }
                for (int x = i; x < j; x++) {
                    LexicalToken atual = tokens.get(x);
                    //averiguar todos os itens "i" até o "j"
                }
            }
        }*/
        return null;
    }

    /**
     * O analisador sintático deixa passar se função foi declarada com retorno
     * int/boolean e mas sem declarar a expressão 'return'. Ess método verifica
     * se int/boolean: não tem return e lança erro caso seja não tenha expressão
     * return. void: lança erro caso tenha return;
     *
     * @return
     */
    private Token checkIfFunctionHasReturn() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        for (int i = 0; i < tokens.size(); i++) {

            if (tokens.get(i).type != 24) {
                continue;
            }

            Token func = tokens.get(i);
            Token tipo_retorno = tokens.get(i + 1);
            Token identificador = tokens.get(i + 2);

            if (tipo_retorno.regra.contains("void")) {
                //continue;

                for (int j = i + 1; j < tokens.size(); j++) {
                    //não encontrar return ou chegar no final codigo fonte e n encontrar nada
                    if (tokens.get(j).type == 24 || j + 1 == tokens.size()) {
                        break;
                    } else if (tokens.get(j).type == 20) {
                        identificador.description1 = "void";
                        identificador.regra = "but has expression 'return'";
                        return identificador;
                    }
                }
            } else {

                for (int j = i + 1; j < tokens.size(); j++) {
                    //não encontrar return ou chegar no final codigo fonte e n encontrar nada
                    if (tokens.get(j).type == 24 || j + 1 == tokens.size()) {
                        identificador.description1 = "int/boolean";
                        identificador.regra = "but hasn't expression 'return'";
                        return identificador;
                    } else if (tokens.get(j).type == 20) {
                        break;
                    }
                }
            }
        }

        return null;
    }
}
