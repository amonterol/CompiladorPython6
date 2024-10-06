/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auxiliares;

/**
 *
 * @author A Montero
 */
public class Token {

    private TipoDeToken tipoDeToken;
    private String lexema;
    private String literal; //Solo para numeros y strings
    private int numeroLinea;

    public Token() {
    }

    public Token(TipoDeToken tipo, String lexema, String literal, int linea) {
        this.tipoDeToken = tipo;
        this.lexema = lexema;
        this.literal = literal;
        this.numeroLinea = linea;
    }

    public Token(TipoDeToken tipo, String lexema, int linea) {
        this.tipoDeToken = tipo;
         this.lexema = lexema;
        this.numeroLinea = linea;
    }
    public Token(TipoDeToken tipo, int linea) {
        this.tipoDeToken = tipo;
        this.numeroLinea = linea;
    }

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public TipoDeToken getTipoDeToken() {
        return tipoDeToken;
    }

    public void setTipoDeToken(TipoDeToken tipoDeToken) {
        this.tipoDeToken = tipoDeToken;
    }

    public String getLiteral() {
        return literal;
    }

    public void setLiteral(String literal) {
        this.literal = literal;
    }

    public int getNumeroLinea() {
        return numeroLinea;
    }

    public void setNumeroLinea(int numeroLinea) {
        this.numeroLinea = numeroLinea;
    }

    @Override
    public String toString() {
        return "Token{" + " tipoDeToken = " + tipoDeToken + ", lexema = " + lexema + ", literal = " + literal + ", numeroLinea = " + numeroLinea + '\n' +'}';
    }

}
