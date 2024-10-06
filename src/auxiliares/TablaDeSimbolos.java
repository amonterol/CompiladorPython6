/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auxiliares;

import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author A Montero
 */
public class TablaDeSimbolos {
     private final HashMap<String, Simbolo> tabla;

    public TablaDeSimbolos() {
        tabla = new HashMap<>();
    }

    public void agregarSimbolo(String valor, Simbolo simbolo) {
        tabla.put(valor, simbolo);
    }

    public Simbolo obtenerSimbolo(String valor) {
        return tabla.get(valor);
    }

    public boolean contieneSimbolo(String valor) {
        return tabla.containsKey(valor);
    }
       
    public Set<String> obtenerClaves() {
        return tabla.keySet();
    }
    
        @Override
    public String toString() {
        StringBuilder simbolosEnTabla = new StringBuilder();
        for (String valor : obtenerClaves()) {
            Simbolo simbolo = obtenerSimbolo(valor);
            simbolosEnTabla.append("Nombre: ").append(valor)
              .append(", Tipo: ").append(simbolo.getTipo())
              .append(", Valor: ").append(simbolo.getValor())
              .append(", Línea: ").append(simbolo.getNumeroLinea())
              .append("\n");
        }
        return simbolosEnTabla.toString();
    }
}
