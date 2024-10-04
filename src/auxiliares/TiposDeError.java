/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auxiliares;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author A Montero
 */
public class TiposDeError {

    private Map<Integer, String> errores;

    public TiposDeError() {
        errores = new HashMap<>();
        inicializarErrores();
    }

    private void inicializarErrores() {
        errores.put(100, "No se ha incluido el archivo con el código fuente en Python.");
        errores.put(101, "Se ha incluido más de un archivo para analizar.");
        errores.put(102, "El archivo para analizar debe tener la extensión .py.");
        errores.put(103, "El archivo para analizar no contiene información.");

        // Relacionados con verificación de identificadores válidos
        errores.put(200, "Identificador no comienza con una letra o guión bajo.");
        errores.put(201, "Identificador no válido porque contiene caracteres especiales diferentes de guión bajo.");
        errores.put(203, "Identificador no válido porque es una palabra reservada de Python.");

        // Relacionados con palabra reservada-> import
        errores.put(300, "La posición de la palabra reservada \"import\" es incorrecta.");

        //Relacionados con palabra reserva-> input
        errores.put(400, "Falta incluir una variable valida.");
        errores.put(401, "Falta incluir el operador de asignación: \"=\".");
        errores.put(402, "La variable a asignar debe ser un identificador valido.");
        errores.put(403, "Uso de operador incorrecto, debe utilizar: \"=\".");
        errores.put(404, "Una palabra reservada no se puede usar como variable valida.");
        errores.put(405, "Un valor numérico no es una variable valida.");
        errores.put(406, "Sintaxix incorrecto de funcion \"input\"-> variable = input(\"string\")");
           

        //Relacionados con operadores de agrupacion-> (, ), [, ]
        errores.put(500, "Falta el parénteris izquierdo: \"(\".");
        errores.put(501, "Falta el parénteris derecho: \")\".");
        errores.put(502, "Falta el corchete izquierdo: \"[\".");
        errores.put(503, "Falta el corchete derecho: \"]\".");
        errores.put(504, "Falta el parénteris izquierdo: \"{\" y el parénteris derecho: \"}\".");
        errores.put(505, "Falta el corchete izquierdo: \"[\" y corchete derecho: \"]\".");
        errores.put(506, "Falta \"(\". Seguido a la funcion input de aparecer un paretesis izquierdo.");
        errores.put(507, "Falta \")\". La funcion input termina con un parentesis derecho.");
    }

    public String obtenerDescripcionDelError(int key) {
        return errores.get(key);
    }

    public boolean contieneError(int key) {
        return errores.containsKey(key);
    }
}
