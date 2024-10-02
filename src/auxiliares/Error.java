/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auxiliares;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author abmon
 */
public class Error {
        
    private static Map<Integer, String> tiposDeErrores = new HashMap();

    public Error() {

        //Relacionados con archivo para analizar
        tiposDeErrores.put(100, "No se ha incluido el archivo con el código fuente en Python.");
        tiposDeErrores.put(101, "Se ha incluido más de un archivo para analizar.");
        tiposDeErrores.put(102, "El archivo para analizar debe tener la extensión .py ");
        tiposDeErrores.put(103, "El archivo para analizar no contiene información.");
        
        
        //Relacionados con verificacion de identificadores validos
        tiposDeErrores.put(200, "Identificador no comienza con una letra o guión bajo.");
        tiposDeErrores.put(201, "Identificador no válido porque contiene caracteres especiales diferentes de guión bajo.");
        tiposDeErrores.put(203, "Identificador no válido porque es una palabra reservada de Python.");
        
        //Relacionados con palabras reservadas
        tiposDeErrores.put(300, "La posición de la palabra reservada \"import\" es incorrecta");
     
    }

    public static String obtenerDescripcionDeError(Integer key) {
        return tiposDeErrores.get(key);
    }
}
