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

    public void init(ArrayList<LexicalToken> arr) throws SemanticException {
        tokens = null;
        this.tokens = arr;

    }

}
