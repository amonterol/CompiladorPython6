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
        errores.put(104, "No se esperaba la indentación.");

        //Relacionados con operadores aritméticos
        errores.put(120, "Un operador aritmético no puede comenzar una instrucción. Se esperaba variable_valida operador_aritmetico variable_valida o número");
        errores.put(121, "Un operador aritmético no puede terminar una instrucción. Se esperaba variable_valida operador_aritmetico variable_valida o número");
        errores.put(122, "Se esperaba una variable_valida o un número antes del operador");
        errores.put(123, "Se esperaba una variable_valida o un número después del operador");
        errores.put(124, "El lado izquierdo de una operación aritmética debe ser una sola variable_valida o un número");
        errores.put(125, "El lado derecho de una operación aritmética debe ser una sola variable_valida o un número");
        errores.put(126, "El numero de valores en el lado derecho debe coincidir con el número de variables a asignar");
        errores.put(127, "El operador unario solo acepta una variable valida antes o despues del operador");

        // Relacionados con verificación de identificadores válidos
        errores.put(200, "Identificador no comienza con una letra o guión bajo.");
        errores.put(201, "Identificador contiene caracteres especiales diferentes de guión bajo.");
        errores.put(203, "Identificador no válido porque es una palabra reservada de Python.");

        // Relacionados con palabra reservada-> import
        errores.put(300, "La posición de la palabra reservada \"import\" es incorrecta.");

        //Relacionados con palabra reserva-> input
        errores.put(401, "Sintaxis incorrecta. Se esperaba \"(\" despues de input.");
        errores.put(402, "Sintaxis incorrecta. Se esperaba  \")\" como último token en la instrucción input.");
        errores.put(403, "El argumento de input no puede ser una palabra reservada.");
        errores.put(404, "El argumento de input debe ser un identificador valido o una cadena de caracteres.");
        errores.put(405, "La variable incluida en el argumento de la función no ha sido declarada anteriormente.");

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
        errores.put(524, "Sintaxis incorrecta. Las comillas no están balanceadas.");

        //Errores relacionados con operador de asignacion
        errores.put(600, "Sintaxis incorrecta. Se esperaba una asignación en el formato Variable_valida = Número o Variable_valida");
        errores.put(601, "Sintaxis incorrecta. Palabra reservada no puede ser usada como variable válida");
        errores.put(602, "El lado izquierdo de un asignación debe ser una variable válida.");
        errores.put(603, "El lado derecho de una asignación debe ser una variable valida o un número.");
        errores.put(604, "Se esperaba una variable valida despues del operador \",\".");
        errores.put(605, "Lado izquierdo de una asignacion deber una variable valida o una lista de variables valida.");
        errores.put(606, "Una lista de variables en el lado izquierdo de una asignación no puede terminar en coma.");

        //Errores relacionados con operador while
        errores.put(620, "Expresión inválida. No se esperaba cambio en la indentacion.");
        errores.put(621, "Expresión inválida. Se esperaba while -> condicion -> :");
        errores.put(622, "\"Error de sintaxis: la línea de código contiene caracteres no permitidos antes del while.\"");
        errores.put(623, "Sintaxis incorrecta. Falta la condicion a evaluar.");
        errores.put(624, "Error de sintaxis: La instrucción 'while' debe finalizar con el operador ':' para iniciar el bloque de código.");
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
        errores.put(654, "Bloque de código 'def' sin instrucciones.");
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
        errores.put(666, "Sintaxis incorrecta. Se espera el nombre de la función, los paréntesis (), los parámetros de la función (si los hay) y los dos puntos : después de def.");
        errores.put(667, "Sintaxis incorrecta. Se esperaba nombre_funcion -> ( -> parametros (si existen) -> )");
        errores.put(668, "La línea de código de una llamada de función comenzar con el nombre de la función.");
        errores.put(669, "La línea de código de una llamada de función debe terminar con un )");
        errores.put(670, "La línea de código de return debe comenzar con la palabra return");
        errores.put(671, "La línea de código de return no debe terminar con una palabra reservada.");
        errores.put(672, "La línea de código de return termina con un operador relacional");
        errores.put(673, "La línea de código de return termina con un operador artimetico");
        errores.put(674, "La línea de código de return termina caon caracter especial");
        errores.put(675, "La función no ha sido definida previamente");
        errores.put(676, "La función ya fue definida anteriormente.");

        //Errores relacionados con operador PRINT
        errores.put(700, "Error de sintaxis. La línea de código contiene caracteres no permitidos antes de la función print.");
        errores.put(701, "Error de sintaxis. La línea de código contiene caracteres no permitidos después del cierre del paréntesis de la función print.");
        errores.put(702, "Error de sintaxis. Se espera un paréntesis de apertura \"(\" después de la función print.");

        //Errores relacionados con operador try
        errores.put(750, "Indentación no coincide con alguno de los bloques definidos. Se requiere usar  0 o múltiplo de 4 espacios por nivel de indentación.");
        errores.put(751, "Sintaxis incorrecta. Se esperaba try -> : ");
        errores.put(752, "Sintaxis incorrecta. La línea de la instrucción try debe comenzar la palabra try");
        errores.put(753, "Sintaxis incorrecta. La línea de la instrucción try debe terminar con :");
        errores.put(754, "Expresión inválida. Falta operador de inicio de bloque debe ser \":\"");
        errores.put(755, "Sintaxis incorrecta. No se permiten expresiones despues de :, cualquier instrucción debe estar en la siguiente línea");
        errores.put(756, "En el bloque de instrucciones se esperaba una indentación mayor que la de la instrucción try");
        errores.put(757, "Bloque try sin instrucciones.");
        errores.put(758, "Error de sintaxis. Falta instruccion 'except' para cerrar el bloque 'try' abierto.");
        errores.put(759, "Error de sintaxis. Falta instruccion 'try'.");

        //Errores relacionados con operador except
        errores.put(850, "Error de sintaxis. Cambio en la indentación. Cualquier instrucción fuera de un bloque debe tener indentación igual a 0");
        errores.put(851, "Sintaxis incorrecta. Se esperaba except -> ArithmeticError | ZeroDivisionError | ValueError -> : ");
        errores.put(852, "Sintaxis incorrecta. La línea de la instrucción except debe comenzar la palabra except");
        errores.put(853, "Sintaxis incorrecta. La línea de la instrucción except debe terminar con :");
        errores.put(854, "Expresión inválida. Falta operador de inicio de bloque debe ser \":\"");
        errores.put(855, "Sintaxis incorrecta. No se permiten expresiones despues de :, cualquier instrucción debe estar en la siguiente línea");
        errores.put(856, "En el bloque de instrucciones se esperaba una indentación mayor que la de la instrucción except");
        errores.put(857, "Bloque except sin instrucciones.");
        errores.put(858, "Sintaxis incorrecta. Falta tipo de excepción.");
        errores.put(859, "Sintaxis incorrecta. No es permitido usar el nombre de una variable como tipo de excepción.");
        errores.put(860, "Bloque except sin instrucción.");
        errores.put(861, "Error de sintaxis. La línea de código siguiente a except debe comenzar con el comando print.");
        errores.put(863, "Error de sintaxis. Bloque except sin instrucciones.");
        errores.put(864, "Error de sintaxis. No existe un bloque 'try' definido antes de 'except'.");
        errores.put(865, "Error de sintaxis. Se esperaba 'except' para cerrar el bloque 'try'.");

    }

    public String obtenerDescripcionDelError(int key) {
        return errores.get(key);
    }

    public boolean contieneError(int key) {
        return errores.containsKey(key);
    }
}
