package br.com.paulomatew.compilador.core;

import br.com.paulomatew.compilador.entities.Token;
import java.util.ArrayList;

/**
 * Created by Paulo Mateus on 31/07/2017. For project Compiladores
 * (https://github.com/TroniPM/Compilador) Contact: <paulomatew@gmail.com>
 */
public class IntermediateCodeGenerator {

    private String code = "";
    private ArrayList<Token> tokens = null;

    public String init(ArrayList<Token> tokens) {
        this.tokens = tokens;
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).type == 21) {//if
            } else if (tokens.get(i).type == 23) {//while

            } else if (tokens.get(i).type == 24) {//function

            }
        }
        //return
        return code;
    }

    private String if_(Token inicio, Token abreParentese) {

        return null;
    }

    private String while_(Token inicio, Token abreParentese) {

        return null;
    }

    private String function_(Token inicio, Token abreParentese) {

        return null;
    }

    private String exp_arit_(Token inicio, Token pontoVirgula) {

        return null;

    }

    private String exp_logic_(Token inicio, Token pontoVirgula) {

        return null;
    }
}
