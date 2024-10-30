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

        //Relacionados con operadores aritméticos
        errores.put(120, "Un operador aritmético no puede comenzar una instrucción. Se esperava variable_valida operador_aritmetico variable_valida o número");
        errores.put(121, "Un operador aritmético no puede terminar una instrucción. Se esperava variable_valida operador_aritmetico variable_valida o número");
        errores.put(122, "Se esperaba una variable_valida o un número antes del operador");
        errores.put(123, "Se esperaba una variable_valida o un número después del operador");
        errores.put(124, "El lado izquierdo de una operación aritmética debe ser una sola variable_valida o un número");
        errores.put(125, "El lado derecho de una operación aritmética debe ser una sola variable_valida o un número");

        // Relacionados con verificación de identificadores válidos
        errores.put(200, "Identificador no comienza con una letra o guión bajo.");
        errores.put(201, "Identificador contiene caracteres especiales diferentes de guión bajo.");
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
        errores.put(506, "Falta \"(\". Sintaxis incorrecta de input.");
        errores.put(507, "Falta \")\". Sintaxis incorrecta de input.");
        errores.put(508, "Falta \"(\". Sintaxis incorrecta de input");
        errores.put(509, "Falta \")\". Sintaxis incorrecta de input.");
        errores.put(510, "Sintaxis incorrecta. Los parentesis redondos no están balanceados.");
        errores.put(511, "Sintaxis incorrecta. Se esperaban parentesis redondos izquierdo \"(\".");
        errores.put(512, "Sintaxis incorrecta. Se esperaban parentesis redondos izquierdo \")\".");
        errores.put(513, "Sintaxis incorrecta. Se esperaba \"(\" en lugar de \"[\".");
        errores.put(514, "Sintaxis incorrecta. Se esperaba \"(\" en lugar de \"{\".");
        errores.put(515, "Sintaxis incorrecta. Se esperaba \")\" en lugar de \"]\".");
        errores.put(516, "Sintaxis incorrecta. Se esperaba \")\" en lugar de \"}\".");
        errores.put(517, "Sintaxis incorrecta. Faltan ambas comillas");
        errores.put(518, "Sintaxis incorrecta. Faltan comillas de inicio");
        errores.put(519, "Sintaxis incorrecta. Faltan comillas de cierre");
        errores.put(520, "Sintaxis incorrecta. Faltan texto entre comillas");
        errores.put(521, "Sintaxis incorrecta. Debe usar parentesis, en lugar de corchetes o llaves.");
        errores.put(522, "Sintaxis incorrecta. Los corchetes no están balanceados.");
        errores.put(523, "Sintaxis incorrecta. Las llaves no están balanceadas.");

        //Errores relacionados con operador de asignacion
        errores.put(600, "Expresión inválida. Se esperaba Variable_valida = Numero o Variable_valida");
        errores.put(601, "Sintaxis incorrecta. Palabra reservada no puede ser usada como variable válida");
        errores.put(602, "El lado izquierdo de un asignación debe ser una variable válida.");
        errores.put(603, "El lado derecho de una asignación debe ser una variable valida o un número.");
        errores.put(604, "Se esperaba una variable valida despues del operador \",\".");
        errores.put(605, "Lado izquierdo de una asignacion deber una variable valida o una lista de variables valida.");
        errores.put(606, "Una lista de variables en el lado izquierdo de una asignación no puede terminar en coma.");

        //Errores relacionados con operador while
        errores.put(620, "Expresión inválida. No se esperaba cambio en la indentacion.");
        errores.put(621, "Expresión inválida. Se esperaba while -> condicion -> :");
        errores.put(622, "Sintaxis incorrecta. La instruccion while debe comenzar la palabra while");
        errores.put(623, "Sintaxis incorrecta. Falta la condicion a evaluar.");
        errores.put(624, "Sintaxis incorrecta. La instruccion while debe terminar con el operador \":\" que da inicio al bloque de código");
        errores.put(625, "Expresión inválida. Falta operador de inicio de bloque debe ser \":\"");
        errores.put(626, "Sintaxis incorrecta. El operador \":\" debe ser el último token de la línea.");
        errores.put(627, "Sintaxis incorrecta. Uso incorrecto del operador aritmetico");
        errores.put(628, "Sintaxis incorrecta. Uso incorrecto del operador relacional");
        errores.put(629, "Sintaxis incorrecta. Bloque de código while sin instrucciones.");
        errores.put(630, "Sintaxis incorrecta. Indentación incorrecta en bloque de código while.");

        //Errores relacionados con operador def
        errores.put(650, "No se esperaba cambio en la indentacion.");
        errores.put(651, "Sintaxix incorrecta. Se esperaba def -> nombre_de_la_funcion -> ( -> parametros -> ) -> : ");
        errores.put(652, "La definición de una función debe comenzar con la palabra def.");
        errores.put(653, "Falta el nombre de la funcion.");
        errores.put(654, "Bloque de código while sin instrucciones.");
        errores.put(655, "La definición de una nueva función debe comenzar solo con la palabra reservada def.");
        errores.put(656, "La instrucción def no se debe usar para terminar una linea de código");
        errores.put(657, "Sintaxis incorrecta. Se esperaba \":\" al final de la línea de código.");
        errores.put(658, "Sintaxis incorrecta. Uso incorrecto del operador aritmetico");
        errores.put(659, "Sintaxis incorrecta. Uso incorrecto del operador relacional");
        errores.put(660, "Sintaxis incorrecta. Falta nombre de la funcion, los paréntesis y los parámetros de la funcion (caso de existir).");
        errores.put(661, " El operador \":\" debe ser el último token de la línea");
        errores.put(662, "Una palabra reservada no puede ser usada como nombre de la función.");
        errores.put(663, "El nombre de la función debe ser un identificador válido");
        errores.put(664, "Falta bloque de código.");
        errores.put(665, "Faltan los paréntesis y los parametros.");
        errores.put(666, "Sintaxis incorrecta. Falta nombre de la funcion, los paréntesis, los parámetros de la funcion, los dos puntos.");

    }

    public String obtenerDescripcionDelError(int key) {
        return errores.get(key);
    }

    public boolean contieneError(int key) {
        return errores.containsKey(key);
    }
}
