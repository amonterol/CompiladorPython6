/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compilador;

import auxiliares.Token;
import java.util.List;

/**
 *
 * @author A Montero
 */
public class Parser {
    private final List<Token> listaDeTokens;
    private int current = 0;
    
    

    Parser(List<Token> tokens) {
        this.listaDeTokens = tokens;
    }
}
