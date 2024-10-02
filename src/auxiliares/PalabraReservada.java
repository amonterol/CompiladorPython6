/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auxiliares;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author abmon
 */
public final class PalabraReservada {

    private List<String> palabrasReservadas = new ArrayList<>();
    
    public PalabraReservada(){
        this.palabrasReservadas = crearListaDePalabrasReservadas();
    }

    public List crearListaDePalabrasReservadas() {
        this.palabrasReservadas.add("import");
        this.palabrasReservadas.add("def");
        this.palabrasReservadas.add("for");
        this.palabrasReservadas.add("if");
        this.palabrasReservadas.add("print");
        this.palabrasReservadas.add("return");
        this.palabrasReservadas.add("in");
        this.palabrasReservadas.add("while");
        this.palabrasReservadas.add("try");
        this.palabrasReservadas.add("range");

        this.palabrasReservadas.add("break");
        this.palabrasReservadas.add("continue");
        this.palabrasReservadas.add("else");
        this.palabrasReservadas.add("and");
        this.palabrasReservadas.add("as");
        this.palabrasReservadas.add("assert");
        this.palabrasReservadas.add("async");
        this.palabrasReservadas.add("await");
        this.palabrasReservadas.add("class");
        this.palabrasReservadas.add("input");

        this.palabrasReservadas.add("elif");
        this.palabrasReservadas.add("False");
        this.palabrasReservadas.add("finally");
        this.palabrasReservadas.add("from");
        this.palabrasReservadas.add("global");
        this.palabrasReservadas.add("is");
        this.palabrasReservadas.add("lambda");
        this.palabrasReservadas.add("None");
        this.palabrasReservadas.add("nonlocal");

        this.palabrasReservadas.add("or");
        this.palabrasReservadas.add("pass");
        this.palabrasReservadas.add("raise");
        this.palabrasReservadas.add("True");
        this.palabrasReservadas.add("with");
        this.palabrasReservadas.add("yield");
        this.palabrasReservadas.add("not");
        this.palabrasReservadas.add("del");
        this.palabrasReservadas.add("except");

        return palabrasReservadas;
    }
    
    
    
    public boolean esPalabraReservada(String palabra) {
        return  palabrasReservadas.contains(palabra);
    }
    
    public int obtenerIndicePalabraReservada(String palabra){
        return palabrasReservadas.indexOf(palabra);
    }
    
    public String obtenerPalabraReservada(int indice){
        return palabrasReservadas.get(indice);
    }

}
