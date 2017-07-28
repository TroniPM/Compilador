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
    private ArrayList<LexicalToken> funcoesDeclaracao = null;
    private ArrayList<LexicalToken> funcoesChamada = null;

    public void init(ArrayList<LexicalToken> tokens, ArrayList<Escopo> escopos) throws SemanticException {
        this.tokens = tokens;
        this.escopos = escopos;
        this.funcoesDeclaracao = new ArrayList<>();
        this.funcoesChamada = new ArrayList<>();

        LexicalToken flag = checkVariableAlreadyDefinedInScope();
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

        flag = checkReturnTypeMethods();
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
            throw new SemanticException("Expression has an unexpected behaviour: '"
                    + flag.lexeme + "' at line " + flag.line + ", position " + flag.position
                    + ", scope " + flag.scope);
        }
    }

    /**
     * Checa se varíavel foi definida duas vezes no mesmo escopo
     *
     * @return
     */
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

    /**
     * Checa se variável foi criada (apenas se ela for utilizada, claro)
     *
     * @return
     */
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

    /**
     * Utilizado recursivamente por "checkVariableDefinedInScopeTree"
     *
     * @param token
     * @param escopo
     * @return
     */
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

    /**
     * Verifica se identificador foi definida no escopo, ou escopo ascendente
     * (em "linhas" anteriores)
     *
     * @param token
     * @param escopo
     * @return
     */
    private boolean checkVariableDefinedInScopeTree(LexicalToken token, Escopo escopo) {
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

    /**
     * Verifica se metodo foi declarado (apenas se ele for utilizado, claro)
     *
     * @return
     */
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

    /**
     * Checa o tipo de retorno de uma expressão com o seu tipo de retorno
     * declarado.
     *
     * @return
     */
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
                funcoesDeclaracao.add(ident);
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
    private LexicalToken checkAtribs() {
        int i;
        for (i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).type == 10) {//=
                LexicalToken ident = tokens.get(i - 1);
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
                    LexicalToken atual = tokens.get(x);

                    if (ident.regra.equals("int")) {
                        if (atual.regra != null && (atual.regra.equals("exp_arit")
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
                        if (atual.regra != null && (atual.regra.equals("exp_logic")
                                || atual.regra.equals("call_func")
                                || atual.regra.equals("arg_boolean")
                                || atual.regra.equals("arg_int")
                                || atual.regra.equals("func_iden")
                                || atual.regra.equals("boolean")
                                || (isFunctionCall && atual.regra.equals("int")))
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
                LexicalToken pontoVirgula1 = null;
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
                        LexicalToken chamadaFuncao = tokens.get(x + 1);
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
    private LexicalToken checkArgumentsType() {
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
                //LexicalToken pontoVirgula1 = null;
                int m;
                for (m = j; m < tokens.size(); m++) {
                    if (tokens.get(m).type == 6) {
                        //pontoVirgula1 = tokens.get(m);
                        break;
                    }
                }

                ArrayList<LexicalToken> tiposDeclaracao = new ArrayList<>();
                for (int w = j + 1; w < m; w++) {
                    if (tokens.get(w).lexeme.equals("int") || tokens.get(w).lexeme.equals("boolean")) {
                        tiposDeclaracao.add(tokens.get(w));
                    }
                }
                /*for (LexicalToken in : tipos) {
                    System.out.println(in.lexeme);
                }*/
//aqui1

                for (int x = 0; x < tokens.size(); x++) {
                    if (tokens.get(x).type == 36) {
                        LexicalToken chamadaFuncao = tokens.get(x + 1);
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
                            ArrayList<LexicalToken> tiposChamada = new ArrayList<>();
                            for (int w1 = x + 2; w1 < m1; w1++) {
                                LexicalToken t = tokens.get(w1);
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
    private LexicalToken checkMethodWasDefined() {//TODO 7
        for (int i = 0; i < tokens.size(); i++) {
            boolean flag = true;
            if (tokens.get(i).type == 36) {
                //tokens.get(i).print();
                LexicalToken t = tokens.get(i + 1);
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
    private LexicalToken checkReturnFromMethodAndVariableAssigned() {
        for (int i = 0; i < tokens.size(); i++) {
            //boolean flag = true;
            if (tokens.get(i).type == 36) {
                if (tokens.get(i - 1).type != 10) {//chamar método sem atribuir a variável
                    continue;
                }
                LexicalToken identificadorFuncao = tokens.get(i + 1);
                LexicalToken identificadorAtribuido = tokens.get(i - 2);

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
    private LexicalToken checkExpressionLogic() {
        if (true) {
            return null;
        }
        for (int i = 0; i < tokens.size(); i++) {//caso IF 21
            if (tokens.get(i).type == 21) {
                LexicalToken lexemaIF = tokens.get(i);
                LexicalToken fechaParentese = null;
                int j;
                for (j = i; j < tokens.size(); j++) {
                    if (tokens.get(j).type == 6) {// { 6, ; 8
                        fechaParentese = tokens.get(j);
                        break;
                    }
                }

                for (int x = i; x < j; x++) {
                    LexicalToken atual = tokens.get(x);
                    //averiguar todos os itens "i" até o "j"
                }
            }
        }
        for (int i = 0; i < tokens.size(); i++) {//caso WHILE 23
            if (tokens.get(i).type == 23) {
                LexicalToken lexemaWHILE = tokens.get(i);
                LexicalToken fechaParentese = null;
                int j;
                for (j = i; j < tokens.size(); j++) {
                    if (tokens.get(j).type == 6) {// { 6, ; 8
                        fechaParentese = tokens.get(j);
                        break;
                    }
                }
                for (int x = i; x < j; x++) {
                    LexicalToken atual = tokens.get(x);
                    //averiguar todos os itens "i" até o "j"
                }
            }
        }
        for (int i = 0; i < tokens.size(); i++) {//caso ATRIBUIÇÃO 10
            if (tokens.get(i).type == 10) {
                LexicalToken lexemaATRIBUICAO = tokens.get(i);
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
        }

        //VER SE ISSO JÁ NÃO ESTÁ SENDO FEITO NO MÉTODO checkReturnTypeMethods()
        for (int i = 0; i < tokens.size(); i++) {//caso return 20
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
        }

        return null;
    }
}
