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

    public void agregarSimbolo(String nombreDeVariable, Simbolo datosDeVariable) {
        tabla.put(nombreDeVariable, datosDeVariable);
    }

    public Simbolo obtenerSimbolo(String nombreDeVariable) {
        return tabla.get(nombreDeVariable);
    }

    public boolean contieneSimbolo(String nombreDeVariable) {
        return tabla.containsKey(nombreDeVariable);
    }
       
    public Set<String> obtenerClaves() {
        return tabla.keySet();
    }
    
        @Override
    public String toString() {
        StringBuilder simbolosEnTabla = new StringBuilder();
        for (String clave : obtenerClaves()) {
            Simbolo simbolo = obtenerSimbolo(clave);
            simbolosEnTabla.append("Nombre: ").append(clave)
              .append(", Tipo: ").append(simbolo.getTipo())
              .append(", Valor: ").append(simbolo.getValor())
              .append(", Línea: ").append(simbolo.getNumeroLinea())
              .append("\n");
        }
        return simbolosEnTabla.toString();
    }
}
