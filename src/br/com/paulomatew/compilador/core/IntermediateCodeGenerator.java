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

        //return
        return code;
    }
}
