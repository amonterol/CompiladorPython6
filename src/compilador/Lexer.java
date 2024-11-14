/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compilador;

import auxiliares.LineaDeContenido;
import auxiliares.PalabraReservada;
import auxiliares.TipoDeToken;
import auxiliares.Token;
import auxiliares.MiError;
import auxiliares.Simbolo;
import auxiliares.TablaDeSimbolos;
import auxiliares.TiposDeError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

import java.util.StringTokenizer;

/**
 *
 * @author A Montero
 */
public class Lexer {

    private static List<String> programaEnPythonOriginal; //Almacena en cada entrada contiene una linea del codigo que se va a analizar
    private static List<List<Token>> listaDeTokens; //Almacena en cada entrada contiene una linea de codigo que se ha convertido en Tokens
    private static List<Token> tokens; //Almacena en cada entrada son los Tokens de la linea de codigo que se esta analizando
    private TablaDeSimbolos tablaDeSimbolos;

    //Lo cambiamos>
    private static List<String> programaEnPythonRevisado; //Almacena los tokens que se identifican

    //Manejo de errores en cada linea
    private static Map<Integer, List<MiError>> erroresEncontradosMap = new HashMap<>();//Almacena los errores encontrados, cada llave es un linea de codigo
    private List<LineaDeContenido> listaContenidoFinal = new ArrayList<>();
    private LineaDeContenido nuevoContenido;
    private List<MiError> erroresEncontrados;
    private List<String> archivoFinal;

    private int numeroLineaActual;   //Almacena el número de linea que se está analizando
    private int cantidadComentarios; //Almacena la cantidad de comentarios en el código
    private static int cantidadVariablesDeclaradas = 0; // Almacena la cantidad de variables que ha sido declaradas en la tabla de símbolos

    public Lexer(List<String> content) {
        this.programaEnPythonOriginal = content;
        this.listaDeTokens = new ArrayList<>();
        this.programaEnPythonRevisado = new ArrayList<>();
        this.tablaDeSimbolos = new TablaDeSimbolos();

        this.numeroLineaActual = 1;// Almancena en número de la línea que se esta leyento actualmente
        this.cantidadComentarios = 0;

    }

    public void analizadorLexico() throws IOException {

        // System.out.println("\n3 LEXER > INICIO LINEA DE CODIGO CONVERTIDA A CARACTERES  " + lineaDeCodigoActual);
        listaDeTokens = new ArrayList<>(); //Almacena cada linea del codigo como una lista de Token

        //Itera sobre cada línea del archivo con el código fuente en Python que fue convertido en un List<String>
        for (int lineaActual = 0; lineaActual < programaEnPythonOriginal.size(); lineaActual++) {
            numeroLineaActual = lineaActual;
            erroresEncontrados = new ArrayList<>();//Almacena los errores encontrados en la linea que se lee

            String lineaDeCodigoActual = programaEnPythonOriginal.get(lineaActual); //Lee cada linea de codigo
            nuevoContenido = new LineaDeContenido(lineaActual, lineaDeCodigoActual);
            listaContenidoFinal.add(nuevoContenido);

            //Pattern pattern = Pattern.compile("print\\(f?\".*\".*\\)| print\\(\"\\|.*\"\\)");
            //Matcher matcher = pattern.matcher(lineaDeCodigoActual);
            //Problemas con algunas lineas if que contiene == asi que combinamos enfoques
            /*
            if (lineaDeCodigoActual.contains("print(f\"")
                    || lineaDeCodigoActual.contains("print(\"|\",")
                    || lineaDeCodigoActual.contains("if")
                    || lineaDeCodigoActual.contains("all(")
                    || lineaDeCodigoActual.contains(" fila, columna")
                    || lineaDeCodigoActual.contains("resultado = f\"\\n")
                    || lineaDeCodigoActual.contains("if jugador_actual ==")
                    || lineaDeCodigoActual.contains("tablero[fila][columna]")
                    || lineaDeCodigoActual.contains("tablero = [[")
                   ) {

                //System.out.println();
                //System.out.println("84 Saldo de linea: " + lineaDeCodigoActual + " en la linea " + lineaActual);
                if (existeComentarioDeUnaLinea(lineaDeCodigoActual)) {
                    cantidadComentarios++;
                }
                // Saltar la línea
                continue;
            } else {
                //System.out.println();
                //System.out.println("89 No saldo la linea: " + lineaDeCodigoActual + " en la linea " + lineaActual);
            }
             */
            //Agrega la linea que actualmente se analiza al archivo de salida 
            //registrarLineaAnalizadaEnProgramaPythonRevisado(lineaDeCodigoActual, numeroLineaActual);
            //listaContenidoFinal.add(nuevoContenido);
            //System.out.println();
            //System.out.println("76 La linea que estamos leyendo: " + lineaDeCodigoActual + " en la linea " + lineaActual);
            //Verifica si la linea esta en blanco 
            if (lineaDeCodigoActual.isBlank() || lineaDeCodigoActual.isEmpty()) {
                continue;
            }

            //Verifica si la linea con comentarios """ 
            if (existeComentarioVariasLineas(lineaDeCodigoActual)) {
                continue;
            }

            //Verifica si la linea es un comentario o si hay comentarios al final de una linea de código
            if (existeComentarioDeUnaLinea(lineaDeCodigoActual)) {
                cantidadComentarios++;
                //Si hay un comentario despues de codigo,elimina la parte del comentario en la línea
                //para poder tokenizarlo
                lineaDeCodigoActual = lineaDeCodigoActual.split("#")[0];
                if (lineaDeCodigoActual.isEmpty() || lineaDeCodigoActual.isBlank()) {
                    continue;
                }
            }

            int contadorIndentacion = contarIndentacion(lineaDeCodigoActual);

            System.out.println();
            System.out.println("129 La linea de codigo actual es: " + lineaDeCodigoActual + " en la linea " + lineaActual);

            //Separa cada linea de codigo en Tokens, quitamos la espacios vacios antes del primer token de la linea.
            StringTokenizer tokenizer = new StringTokenizer(lineaDeCodigoActual.trim(), " \n()[]{}=<>*/+-:\",.", true);

            //System.out.println("135 Tokenizer a linea que estamos leyendo: " + lineaDeCodigoActual + " en la linea " + lineaActual);
            //System.out.println();
            String[] arregloDeTokens = convertirStringTokenizerEnArregloDeStrings(tokenizer);

            System.out.println("137 Contenido de arreglo de Tokens: ");
            Arrays.stream(arregloDeTokens)
                    .forEach(token -> System.out.print(token + "->"));
            System.out.println();

            //Almanacena los Token clasificados de la linea actual de codigo
            tokens = new ArrayList<Token>();

            agregarNuevoToken(TipoDeToken.INDENTACION, null, String.valueOf(contadorIndentacion), numeroLineaActual);
            contadorIndentacion = 0;

            boolean existeInput = false;
            boolean existePrint = false;
            StringBuilder textoEntreComillas = new StringBuilder();
            boolean existeTextoEntreComillas = false;
            boolean existeOperadorUnario = false;
            //Itera sobre la linea de codigo ya convertida en arreglo de Strings
            for (int indice = 0; indice < arregloDeTokens.length; ++indice) {

                boolean existeComillasIniciales = false;

                String tokenActual = arregloDeTokens[indice];
                String tokenSiguiente = " ";
                String tokenSiguienteSiguiente = " ";

                if (indice == arregloDeTokens.length - 1) {
                    tokenSiguiente = " ";
                } else {
                    tokenSiguiente = arregloDeTokens[indice + 1];
                }
                //System.out.println("133 Antes switch tokenActual es " + tokenActual + " esta en " + indice + " <" + arregloDeTokens.length);
                //System.out.println();

                if (existeOperadorUnario) {
                    existeOperadorUnario = false;
                    continue;
                }
                switch (tokenActual) {

                    case " ":
                        break;

                    //Analiza los operadores aritméticos    
                    case "+":
                        if (tokenSiguiente.equals("+")) {
                            agregarNuevoToken(TipoDeToken.UNARIO_SUMA, "++", numeroLineaActual);
                            existeOperadorUnario = true;
                        } else {
                            agregarNuevoToken(TipoDeToken.SUMA, "+", numeroLineaActual);
                        }
                        break;
                    case "-":
                        if (tokenSiguiente.equals("-")) {
                            agregarNuevoToken(TipoDeToken.UNARIO_RESTA, "--", numeroLineaActual);

                        } else {
                            agregarNuevoToken(TipoDeToken.RESTA, "-", numeroLineaActual);
                        }
                        break;
                    case "*":
                        agregarNuevoToken(TipoDeToken.MULTIPLICACION, "*", numeroLineaActual);
                        break;
                    case "/":
                        agregarNuevoToken(TipoDeToken.DIVISION, "/", numeroLineaActual);
                        break;

                    //Analiza los operadores aritméticos       
                    case "=":
                        if (tokenSiguiente.equals("=")) {
                            agregarNuevoToken(TipoDeToken.IGUAL_QUE, "==", numeroLineaActual);
                            existeOperadorUnario = true;
                        } else {
                            agregarNuevoToken(TipoDeToken.ASIGNACION, "=", numeroLineaActual);
                        }
                        break;
                    case "!":
                        if (tokenSiguiente.equals("=")) {
                            agregarNuevoToken(TipoDeToken.DIFERENTE_QUE, "!=", numeroLineaActual);

                        } else {
                            //No contemplado en Python
                        }
                        break;
                    case ">":
                        if (tokenSiguiente.equals("=")) {
                            agregarNuevoToken(TipoDeToken.MAYOR_O_IGUAL_QUE, ">=", numeroLineaActual);

                        } else {
                            agregarNuevoToken(TipoDeToken.MAYOR_QUE, ">", numeroLineaActual);

                        }
                        break;
                    case "<":
                        if (tokenSiguiente.equals("=")) {
                            agregarNuevoToken(TipoDeToken.MENOR_O_IGUAL_QUE, "<=", numeroLineaActual);

                        } else {
                            agregarNuevoToken(TipoDeToken.MENOR_QUE, "<", numeroLineaActual);

                        }
                        break;

                    //Identifica los operadores de agrupación
                    case "(":
                        agregarNuevoToken(TipoDeToken.PARENTESIS_IZQUIERDO, "(", numeroLineaActual);
                        break;
                    case ")":
                        agregarNuevoToken(TipoDeToken.PARENTESIS_DERECHO, ")", numeroLineaActual);
                        //System.out.println(" 245 estamos en parentesis  " + tokenActual + " esta en indice " + indice + " < " + arregloDeTokens.length);
                        break;
                    case "[":
                        agregarNuevoToken(TipoDeToken.CORCHETE_IZQUIERDO, "[", numeroLineaActual);

                        break;
                    case "]":
                        agregarNuevoToken(TipoDeToken.CORCHETE_DERECHO, "]", numeroLineaActual);
                        break;
                    case "{":
                        agregarNuevoToken(TipoDeToken.LLAVE_IZQUIERDA, "{", numeroLineaActual);
                        break;
                    case "}":
                        agregarNuevoToken(TipoDeToken.LLAVE_DERECHA, "}", numeroLineaActual);
                        break;

                    //Identifica los operadores dos puntos ->  de sublista, subcadena o subarreglos y tipo de retorno de una función    
                    case ":":
                        agregarNuevoToken(TipoDeToken.DOS_PUNTOS, ":", numeroLineaActual);
                        break;

                    case ".":
                        agregarNuevoToken(TipoDeToken.PUNTO, ".", numeroLineaActual);
                        break;

                    case ",":
                        agregarNuevoToken(TipoDeToken.COMA, ",", numeroLineaActual);
                        break;

                    case "\"":
                        //System.out.println(" 225 NUEVAMENTE EN EL COMILLAS " + tokenActual + " esta en indice " + indice + " < " + arregloDeTokens.length);
                        agregarNuevoToken(TipoDeToken.COMILLAS, "\"", null, numeroLineaActual);
                        if (!existeTextoEntreComillas) {

                            textoEntreComillas = new StringBuilder();

                            //System.out.println(" 238 ANTES DE IF " + (indice + 1) + " <" + arregloDeTokens.length);
                            if (indice + 1 < arregloDeTokens.length) {
                                //System.out.println(" 240  tokenActual es  " + tokenActual + " esta en indice " + indice + " < " + arregloDeTokens.length);

                                ++indice;

                                while (indice + 1 < arregloDeTokens.length) {
                                    tokenActual = arregloDeTokens[indice];
                                    //  System.out.println(" 246  tokenActual es  " + tokenActual + " esta en indice " + indice + " < " + arregloDeTokens.length);
                                    if (tokenActual.equals("\"") || (tokenActual.equals(")") && indice == (arregloDeTokens.length - 1))) {

                                        //       System.out.println();
                                        //       System.out.println(" 250 token actual es " + tokenActual + "  esta en indice " + indice + " <" + arregloDeTokens.length);
                                        break;
                                    } else {
                                        textoEntreComillas.append(tokenActual);
                                        // System.out.println();
                                        //System.out.println(" 256 token actual es " + tokenActual + "  esta en indice " + indice + " <" + arregloDeTokens.length);
                                        //System.out.println(" 257 texto entre comillas: " + textoEntreComillas);
                                        //System.out.println();
                                        ++indice;

                                    }

                                }

                                System.out.println();

                                if (!textoEntreComillas.isEmpty()) {
                                    agregarNuevoToken(TipoDeToken.TEXTO_ENTRE_COMILLAS, textoEntreComillas.toString(), null, numeroLineaActual);
                                    existeTextoEntreComillas = true;
                                    //System.out.println(" 270 Texto entre comillas: " + textoEntreComillas.toString() + " indice = " + indice);
                                } else {
                                    //System.out.println(" 272 Texto entre comillas: " + textoEntreComillas.toString() + " indice = " + indice);
                                }

                                --indice;
                            }//fin if existeTextoEntreComillas
                        } else {

                        }

                        break;

                    case "|":
                        break;

                    case "\\":

                        if (indice < arregloDeTokens.length) {
                            //System.out.println(" 269 (indice < arregloDeTokens.length): " + (indice < arregloDeTokens.length));
                            if ((arregloDeTokens[indice + 1].equals("n"))) {
                                agregarNuevoToken(TipoDeToken.SALTO_DE_LINEA, "\n", null, numeroLineaActual);
                                ++indice;
                            }
                        }
                        break;

                    case "ArithmeticError":
                        agregarNuevoToken(TipoDeToken.EXCEPCION, "ArithmeticError", null, numeroLineaActual);
                        break;
                    case "ZeroDivisionError":
                        agregarNuevoToken(TipoDeToken.EXCEPCION, "ZeroDivisionError", null, numeroLineaActual);
                        break;
                    case "ValueError":
                        agregarNuevoToken(TipoDeToken.EXCEPCION, "ValueError", null, numeroLineaActual);
                        break;

                    default:
                        PalabraReservada palabraReservada = new PalabraReservada();
                        //System.out.println("307 estamos en palabra reservada: " + tokenActual);
                        if (palabraReservada.esPalabraReservada(tokenActual.trim())) {
                            agregarNuevoToken(TipoDeToken.PALABRA_RESERVADA, tokenActual.trim(), null, this.numeroLineaActual);
                            String[] argumentoDeLaFuncion = new String[2];
                            //System.out.println("310 estamos en palabra reservada: " + tokenActual);
                            if (tokenActual.trim().equals("input")) {
                                existeInput = true;
                            }
                            if (tokenActual.trim().equals("print")) {
                                existePrint = true;
                            }

                            if (existeInput || existePrint) {

                                for (int i = 0; i < arregloDeTokens.length; i++) {
                                    System.out.println(i + ": " + arregloDeTokens[i]);
                                }

                                indice = extraerArgumentoDeUnaFuncion(arregloDeTokens, indice);

                                /*
                                if (indice + 1 < arregloDeTokens.length) { //Valida si hay mas tokens
                                    tokenSiguiente = arregloDeTokens[indice + 1]; //Obtiene el token siguiente a input
                                    System.out.println(" 377 tokenSiguiente " + tokenSiguiente);
                                }
                                if (indice + 2 < arregloDeTokens.length) { //Valida si hay mas tokens

                                    tokenSiguienteSiguiente = arregloDeTokens[indice + 2]; //Obtiene el token siguiente a input
                                    System.out.println(" 382 tokenSiguienteSiguiente = " + tokenSiguienteSiguiente);
                                }
                              
                                if (!tokenSiguiente.equals("(") && !tokenSiguienteSiguiente.equals("\"")) {
                                    inicio = indice + 1;
                                } 
                                 if (tokenSiguiente.equals("(") && !tokenSiguienteSiguiente.equals("\"")) {
                                    inicio = indice + 2;
                                } else {
                                    inicio = indice + 1;
                                }
                                
                                System.out.println("392 inicio = " + inicio);
                                if (inicio >= 1) {
                                    argumentoDeLaFuncion = extraerArgumentoDeUnaFuncion(arregloDeTokens, indice);
                                    System.out.println("392 El argumento de print o input es: " + argumentoDeLaFuncion[0] + " y el indice es " + indice);
                                    // Dividir la cadena en palabras usando espacios como delimitadores
                                    String[] words = argumentoDeLaFuncion[0].split("\\s+");
                                    System.out.println();
                                    System.out.println(" 396Contenido de words");
                                    for (String str : words) {
                                        System.out.println(str);
                                    }

                                    //  System.out.println(" 435 el tamano de word es  " + words.length);
                                    // Verificar si hay más de una palabra
                                    if (words.length > 1) {
                                        agregarNuevoToken(TipoDeToken.TEXTO_ENTRE_COMILLAS, argumentoDeLaFuncion[0], null, this.numeroLineaActual);
                                    } else {
                                        //    System.out.println(" 440 El StringBuilder contiene solo una palabra." + content);
                                        if (palabraReservada.esPalabraReservada(argumentoDeLaFuncion[0].trim())) {
                                            agregarNuevoToken(TipoDeToken.PALABRA_RESERVADA, argumentoDeLaFuncion[0], null, this.numeroLineaActual);
                                            //      System.out.println(" 443 encontro una palabra reservada en el input " + content.trim() + " en linea " + numeroLineaActual + "\n");
                                        } else if (verificarPrimerCaracterDeUnIdentificador(argumentoDeLaFuncion[0], numeroLineaActual)
                                                && verificarSecuenciaDeCaracteresDeUnIdentificador(argumentoDeLaFuncion[0], numeroLineaActual)) {
                                            //    System.out.println(" 446 Este es el textoEntreComillas que es un identificador " + content.trim() + " en linea " + numeroLineaActual + "\n");

                                            //agregarNuevoToken(TipoDeToken.IDENTIFICADOR, tokenActual.trim(), null, this.numeroLineaActual);
                                            Token nuevoToken = new Token(TipoDeToken.IDENTIFICADOR, argumentoDeLaFuncion[0].trim(), null, this.numeroLineaActual);
                                            tokens.add(nuevoToken);

                                            //Solo probando construir una tabla de simbolos
                                            //incluirNuevaVariableEnTablaDeSimbolos(nuevoToken);
                                            //  System.out.println("455 LEXER TABLA DE SIMBOLOS ");
                                            //imprimirTablaDeSimbolos();
                                        } else {
                                            agregarNuevoToken(TipoDeToken.DESCONOCIDO, argumentoDeLaFuncion[0].trim(), null, this.numeroLineaActual);
                                            //  System.out.println(" 379 SE AGREGO UN TOKE DESCONOCIDO:   " + content.trim() + " en linea " + numeroLineaActual + "\n");
                                        }
                                    }
                                }

                            }
                            existeInput = false;
                            existePrint = false;
                            if (argumentoDeLaFuncion[1] != null) {
                                indice = Integer.parseInt(argumentoDeLaFuncion[1]);
                            }
                                 */

 /*
                            if (existeInput || existePrint) {
                                textoEntreComillas.append(tokenActual.trim());
                                textoEntreComillas.append(" ");
                                String tokenSiguienteDeInput = " ";
                                if (indice + 1 < arregloDeTokens.length) { //Valida si hay mas tokens
                                    // System.out.println(" 413 (indice < arregloDeTokens.length): " + (indice < arregloDeTokens.length) + " el indice es " + indice);
                                    tokenSiguienteDeInput = arregloDeTokens[indice + 1]; //Obtiene el token siguiente a input
                                    //System.out.println(" 415 el indice es " + indice + " y el token en el arreglo es " + tokenSiguienteDeInput);
                                }
                                if (tokenSiguienteDeInput.equals("\"") || tokenSiguienteDeInput.equals(")") || indice == arregloDeTokens.length) {
                                    existeInput = false;
                                    existePrint = false;

                                    // System.out.println(" 420 El texto entre comillas es  " + textoEntreComillas.toString() + " y el indice es " + indice);
                                    // Convertir el contenido del StringBuilder a una cadena
                                    String content = textoEntreComillas.toString().trim();
                                    textoEntreComillas.setLength(0); //borramos el StringBuilder 
                                    // System.out.println(" 350 el indice es " + indice + " el texto entre comillas es " + textoEntreComillas);

                                    // Dividir la cadena en palabras usando espacios como delimitadores
                                    String[] words = content.split("\\s+");
                                    System.out.println();
                                    System.out.println(" 356 estee es el contenido de words");
                                    for (String str : words) {
                                        System.out.println(str);
                                    }

                                    //  System.out.println(" 435 el tamano de word es  " + words.length);
                                    // Verificar si hay más de una palabra
                                    if (words.length > 1) {
                                        agregarNuevoToken(TipoDeToken.TEXTO_ENTRE_COMILLAS, content.trim(), null, this.numeroLineaActual);
                                    } else {
                                        //    System.out.println(" 440 El StringBuilder contiene solo una palabra." + content);
                                        if (palabraReservada.esPalabraReservada(content.trim())) {
                                            agregarNuevoToken(TipoDeToken.PALABRA_RESERVADA, content.trim(), null, this.numeroLineaActual);
                                            //      System.out.println(" 443 encontro una palabra reservada en el input " + content.trim() + " en linea " + numeroLineaActual + "\n");
                                        } else if (verificarPrimerCaracterDeUnIdentificador(content, numeroLineaActual)
                                                && verificarSecuenciaDeCaracteresDeUnIdentificador(content, numeroLineaActual)) {
                                            //    System.out.println(" 446 Este es el textoEntreComillas que es un identificador " + content.trim() + " en linea " + numeroLineaActual + "\n");

                                            //agregarNuevoToken(TipoDeToken.IDENTIFICADOR, tokenActual.trim(), null, this.numeroLineaActual);
                                            Token nuevoToken = new Token(TipoDeToken.IDENTIFICADOR, content.trim(), null, this.numeroLineaActual);
                                            tokens.add(nuevoToken);

                                            //Solo probando construir una tabla de simbolos
                                            //incluirNuevaVariableEnTablaDeSimbolos(nuevoToken);
                                            //  System.out.println("455 LEXER TABLA DE SIMBOLOS ");
                                            //imprimirTablaDeSimbolos();
                                        } else {
                                            agregarNuevoToken(TipoDeToken.DESCONOCIDO, content.trim(), null, this.numeroLineaActual);
                                            //  System.out.println(" 379 SE AGREGO UN TOKE DESCONOCIDO:   " + content.trim() + " en linea " + numeroLineaActual + "\n");
                                        }
                                    }
                                }

                            }
                                 */
                            }
                        } else if (esNumeroEntero(tokenActual.trim())) {
                            agregarNuevoToken(TipoDeToken.NUMERO_ENTERO, tokenActual.trim(), null, this.numeroLineaActual);
                        } else if (esNumeroDecimal(tokenActual.trim())) {
                            agregarNuevoToken(TipoDeToken.NUMERO_DECIMAL, tokenActual.trim(), null, this.numeroLineaActual);
                        } else if (verificarPrimerCaracterDeUnIdentificador(tokenActual.trim(), numeroLineaActual)
                                && verificarSecuenciaDeCaracteresDeUnIdentificador(tokenActual.trim(), numeroLineaActual)) {

                            // System.out.println("409 ENTRAMOS A IDENTIFICADOR " + tokenActual.trim() + " POSICION " + indice + "\n");
                            //System.out.println("410 Este es el TOKEN:   " + tokenActual.trim() + " en linea " + numeroLineaActual + "\n");
                            //agregarNuevoToken(TipoDeToken.IDENTIFICADOR, tokenActual.trim(), null, this.numeroLineaActual);
                            Token nuevoToken = new Token(TipoDeToken.IDENTIFICADOR, tokenActual.trim(), null, this.numeroLineaActual);
                            tokens.add(nuevoToken);

                            //Solo probando construir una tabla de simbolos
                            incluirNuevaVariableEnTablaDeSimbolos(nuevoToken);

                            System.out.println();
                            System.out.println("440 - LEXER - TABLA DE SIMBOLOS ");
                            imprimirTablaDeSimbolos();

                        } else {
                            agregarNuevoToken(TipoDeToken.DESCONOCIDO, tokenActual.trim(), null, this.numeroLineaActual);
                            //System.out.println(" 410 SE AGREGO UN TOKE DESCONOCIDO:   " + tokenActual.trim() + " en linea " + numeroLineaActual + "\n");
                        } //aqui
                        break;
                } //fin switch

            }//fir FOR arregloTokens

            //System.out.println(" 368 agregadon token:   " + tokens.toString() + " en linea " + numeroLineaActual + "\n");
            listaDeTokens.add(tokens);

        } //Fin for que recorre linea por linea el programa en Python

        System.out.println();
        System.out.println("Lista de Tokens al salir del LEXER tiene tamanio " + listaDeTokens.size());
        int count = 0;
        for (List tkn : listaDeTokens) {
            for (Object token : tkn) {
                System.out.println(token.toString());
                ++count;
            }

        }
        //System.out.println("Tokens en linea  " + count);

        //System.out.println();
        System.out.println("469 LEXER: Contenido del mapa de errores encontrados " + erroresEncontrados.size());
        for (Map.Entry<Integer, List<MiError>> entry : erroresEncontradosMap.entrySet()) {
            Integer key = entry.getKey();
            List<MiError> errorList = entry.getValue();
            System.out.println("Linea: " + key);
            for (MiError error : errorList) {
                System.out.println(error.getKey() + "  " + error.getDescripcion());
            }
        }
        System.out.println();

        /*
        Parser parser = new Parser(listaDeTokens, programaEnPythonRevisado);
        parser.analisisSintactico();

        System.out.println("Generacion del histograma");
        generarHistogramaOperadoresComparacion();

        System.out.println("Generacion del archivo de analisis");
        generarArchivoDeSalida();
         */
        System.out.println("254 LISTA CONTENIDO FINAL LEXER");
        //imprimirListas(listaContenidoFinal);
        if (listaContenidoFinal != null) {
            imprimirListasDeContenidoFinal(listaContenidoFinal);
        }

        System.out.println();
    } //Fin del metodo analizadorSintactico

    //Extrae el argumento de la funcion print o input desde el 
    public int extraerArgumentoDeUnaFuncion(String[] arregloDeTokens, int indiceTokenActual) {
        StringBuilder texto = new StringBuilder();
        int inicio = indiceTokenActual + 1;
        int fin = arregloDeTokens.length - 1; // Por defecto, hasta el final del arreglo

        /*
        TipoDeToken tipo = null;

        for (int i = inicio; i <= fin; i++) {
            texto.append(arregloDeTokens[i]);
            ++contador;

        }
        String str = texto.toString();
        System.out.println("598 texto entre parentesis " + str);

        boolean startsWithParenthesisAndQuote = str.matches("^\\(\".*");
        boolean endsWithQuoteAndParenthesis = str.matches(".*\"\\)$");

        boolean startsWithParenthesisAndNotQuote = str.matches("^\\([^\"].*");

        boolean startsWithParenthesis = str.matches("^\\(.*");
        boolean startsWithQuote = str.matches("^\".*");

        boolean endsWithParenthesis = str.matches(".*\\)$");
        boolean endsWithQuote = str.matches(".*\"$");

        System.out.println("605 startsWithParenthesisAndQuote " + startsWithParenthesisAndQuote);
        System.out.println("607 endsWithQuoteAndParenthesis " + endsWithQuoteAndParenthesis);

        //caso: print ("texto")
        if (startsWithParenthesisAndQuote && endsWithQuoteAndParenthesis) {
            System.out.println("618");
            agregarNuevoToken(TipoDeToken.PARENTESIS_IZQUIERDO, "(", null, this.numeroLineaActual);
            agregarNuevoToken(TipoDeToken.COMILLAS, "\"", null, this.numeroLineaActual);

            primerIndiceDeParentesisIzquierdo = str.indexOf('(');
            primerIndiceDeInicioComillas = str.indexOf('\"');
            inicio = 2; //inicio = primerIndiceDeInicioComillas + 1;

            ultimoIndiceDeParentesisDerecho = str.lastIndexOf(')');
            ultimoIndiceDeFinComillas = str.lastIndexOf('\"');
            fin = str.length() - 2; //fin = ultimoIndiceDeFinComillas;

            String subStr = str.substring(inicio, fin).trim();
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("TEXTO_ENTRE_COMILLAS")) {
                agregarNuevoToken(TipoDeToken.TEXTO_ENTRE_COMILLAS, subStr, null, this.numeroLineaActual);
            }
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("PALABRA_RESERVADA")) {
                agregarNuevoToken(TipoDeToken.PALABRA_RESERVADA, subStr, null, this.numeroLineaActual);
            }
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("IDENTIFICADOR")) {
                agregarNuevoToken(TipoDeToken.IDENTIFICADOR, subStr, null, this.numeroLineaActual);
            }
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("DESCONOCIDO")) {
                agregarNuevoToken(TipoDeToken.DESCONOCIDO, subStr, null, this.numeroLineaActual);
            }
            agregarNuevoToken(TipoDeToken.COMILLAS, "\"", null, this.numeroLineaActual);
            agregarNuevoToken(TipoDeToken.PARENTESIS_DERECHO, ")", null, this.numeroLineaActual);

            System.out.println("624 ultimoIndiceDeParentesisDerecho + 1 " + ultimoIndiceDeParentesisDerecho + 1);
            return (ultimoIndiceDeParentesisDerecho + 1);
        }

        //caso: print ("texto" o print ("texto)
        if (startsWithParenthesisAndQuote && (endsWithParenthesis || endsWithQuote)) {
            System.out.println("641");
            agregarNuevoToken(TipoDeToken.PARENTESIS_IZQUIERDO, "(", null, this.numeroLineaActual);
            agregarNuevoToken(TipoDeToken.COMILLAS, "\"", null, this.numeroLineaActual);

            primerIndiceDeParentesisIzquierdo = str.indexOf('(');
            primerIndiceDeInicioComillas = str.indexOf('\"');
            inicio = 2; //inicio = primerIndiceDeInicioComillas + 1;

            fin = str.length() - 1;

            String subStr = str.substring(inicio, fin).trim();
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("TEXTO_ENTRE_COMILLAS")) {
                agregarNuevoToken(TipoDeToken.TEXTO_ENTRE_COMILLAS, subStr, null, this.numeroLineaActual);
            }
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("PALABRA_RESERVADA")) {
                agregarNuevoToken(TipoDeToken.PALABRA_RESERVADA, subStr, null, this.numeroLineaActual);
            }
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("IDENTIFICADOR")) {
                agregarNuevoToken(TipoDeToken.IDENTIFICADOR, subStr, null, this.numeroLineaActual);
            }
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("DESCONOCIDO")) {
                agregarNuevoToken(TipoDeToken.DESCONOCIDO, subStr, null, this.numeroLineaActual);
            }

            if (endsWithQuote) {
                agregarNuevoToken(TipoDeToken.COMILLAS, "\"", null, this.numeroLineaActual);
            }
            if (endsWithParenthesis) {
                agregarNuevoToken(TipoDeToken.PARENTESIS_DERECHO, ")", null, this.numeroLineaActual);
            }

            System.out.println("624 ultimoIndiceDeParentesisDerecho + 1 " + ultimoIndiceDeParentesisDerecho + 1);
            return (fin + 1);
        }

        //caso: print ("texto
        if (startsWithParenthesisAndQuote && !endsWithParenthesis && !endsWithQuote) {
            System.out.println("666");
            agregarNuevoToken(TipoDeToken.PARENTESIS_IZQUIERDO, "(", null, this.numeroLineaActual);
            agregarNuevoToken(TipoDeToken.COMILLAS, "\"", null, this.numeroLineaActual);

            primerIndiceDeParentesisIzquierdo = str.indexOf('(');
            primerIndiceDeInicioComillas = str.indexOf('\"');
            inicio = 2; // inicio = primerIndiceDeInicioComillas + 1;

            fin = str.length();

            String subStr = str.substring(inicio, fin).trim();
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("TEXTO_ENTRE_COMILLAS")) {
                agregarNuevoToken(TipoDeToken.TEXTO_ENTRE_COMILLAS, subStr, null, this.numeroLineaActual);
            }
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("PALABRA_RESERVADA")) {
                agregarNuevoToken(TipoDeToken.PALABRA_RESERVADA, subStr, null, this.numeroLineaActual);
            }
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("IDENTIFICADOR")) {
                agregarNuevoToken(TipoDeToken.IDENTIFICADOR, subStr, null, this.numeroLineaActual);
            }
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("DESCONOCIDO")) {
                agregarNuevoToken(TipoDeToken.DESCONOCIDO, subStr, null, this.numeroLineaActual);
            }

            System.out.println("624 ultimoIndiceDeParentesisDerecho + 1 " + ultimoIndiceDeParentesisDerecho + 1);
            return fin;
        }

        System.out.println("632  startsWithParenthesisAndNotQuote " + startsWithParenthesisAndNotQuote);

        //caso: print (texto") o "texto")
        if ((startsWithParenthesis || startsWithQuote) && endsWithQuoteAndParenthesis) {
            System.out.println("687");

            if (startsWithParenthesis) {
                agregarNuevoToken(TipoDeToken.PARENTESIS_IZQUIERDO, "(", null, this.numeroLineaActual);
            }
            if (startsWithQuote) {
                agregarNuevoToken(TipoDeToken.COMILLAS, "\"", null, this.numeroLineaActual);
            }

            primerIndiceDeParentesisIzquierdo = str.indexOf('(');
            inicio = 1; //inicio = primerIndiceDeParentesisIzquierdo + 1;

            ultimoIndiceDeParentesisDerecho = str.lastIndexOf(')');
            ultimoIndiceDeFinComillas = str.lastIndexOf('\"');
            fin = str.length() - 2;

            String subStr = str.substring(inicio, fin).trim();
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("TEXTO_ENTRE_COMILLAS")) {
                agregarNuevoToken(TipoDeToken.TEXTO_ENTRE_COMILLAS, subStr, null, this.numeroLineaActual);
            }
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("PALABRA_RESERVADA")) {
                agregarNuevoToken(TipoDeToken.PALABRA_RESERVADA, subStr, null, this.numeroLineaActual);
            }
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("IDENTIFICADOR")) {
                agregarNuevoToken(TipoDeToken.IDENTIFICADOR, subStr, null, this.numeroLineaActual);
            }
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("DESCONOCIDO")) {
                agregarNuevoToken(TipoDeToken.DESCONOCIDO, subStr, null, this.numeroLineaActual);
            }

            agregarNuevoToken(TipoDeToken.COMILLAS, "\"", null, this.numeroLineaActual);
            agregarNuevoToken(TipoDeToken.PARENTESIS_DERECHO, ")", null, this.numeroLineaActual);

            System.out.println("624 ultimoIndiceDeParentesisDerecho + 1 " + ultimoIndiceDeParentesisDerecho + 1);
            return (fin + 2);
        }

        //caso: print texto")
        if (!startsWithParenthesis && !startsWithQuote && endsWithQuoteAndParenthesis) {
            System.out.println("714");
            inicio = 0;

            ultimoIndiceDeParentesisDerecho = str.lastIndexOf(')');
            ultimoIndiceDeFinComillas = str.lastIndexOf('\"');
            fin = str.length() - 2;

            String subStr = str.substring(inicio, fin).trim();
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("TEXTO_ENTRE_COMILLAS")) {
                agregarNuevoToken(TipoDeToken.TEXTO_ENTRE_COMILLAS, subStr, null, this.numeroLineaActual);
            }
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("PALABRA_RESERVADA")) {
                agregarNuevoToken(TipoDeToken.PALABRA_RESERVADA, subStr, null, this.numeroLineaActual);
            }
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("IDENTIFICADOR")) {
                agregarNuevoToken(TipoDeToken.IDENTIFICADOR, subStr, null, this.numeroLineaActual);
            }
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("DESCONOCIDO")) {
                agregarNuevoToken(TipoDeToken.DESCONOCIDO, subStr, null, this.numeroLineaActual);
            }

            agregarNuevoToken(TipoDeToken.COMILLAS, "\"", null, this.numeroLineaActual);
            agregarNuevoToken(TipoDeToken.PARENTESIS_DERECHO, ")", null, this.numeroLineaActual);

            System.out.println("624 ultimoIndiceDeParentesisDerecho + 1 " + ultimoIndiceDeParentesisDerecho + 1);
            return (fin + 2);
        }

        //caso: print (texto" o (texto) o "texto" o "texto)
        if ((startsWithParenthesis || startsWithQuote) && (endsWithParenthesis || endsWithQuote)) {
            System.out.println("732");
            if (startsWithParenthesis) {
                agregarNuevoToken(TipoDeToken.PARENTESIS_IZQUIERDO, "(", null, this.numeroLineaActual);
            }
            if (startsWithQuote) {
                agregarNuevoToken(TipoDeToken.COMILLAS, "\"", null, this.numeroLineaActual);
            }

            inicio = 1;

            ultimoIndiceDeParentesisDerecho = str.lastIndexOf(')');
            ultimoIndiceDeFinComillas = str.lastIndexOf('\"');
            fin = str.length() - 1;

            String subStr = str.substring(inicio, fin).trim();
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("TEXTO_ENTRE_COMILLAS")) {
                agregarNuevoToken(TipoDeToken.TEXTO_ENTRE_COMILLAS, subStr, null, this.numeroLineaActual);
            }
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("PALABRA_RESERVADA")) {
                agregarNuevoToken(TipoDeToken.PALABRA_RESERVADA, subStr, null, this.numeroLineaActual);
            }
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("IDENTIFICADOR")) {
                agregarNuevoToken(TipoDeToken.IDENTIFICADOR, subStr, null, this.numeroLineaActual);
            }
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("DESCONOCIDO")) {
                agregarNuevoToken(TipoDeToken.DESCONOCIDO, subStr, null, this.numeroLineaActual);
            }

            if (endsWithQuote) {
                agregarNuevoToken(TipoDeToken.COMILLAS, "\"", null, this.numeroLineaActual);
            }
            if (endsWithParenthesis) {
                agregarNuevoToken(TipoDeToken.PARENTESIS_DERECHO, ")", null, this.numeroLineaActual);
            }

            System.out.println("624 ultimoIndiceDeParentesisDerecho + 1 " + ultimoIndiceDeParentesisDerecho + 1);
            return (fin + 2);
        }

        //caso: print texto" o print texto)
        if (!startsWithParenthesis && !startsWithQuote && (endsWithParenthesis || endsWithQuote)) {
            System.out.println("762");
            inicio = 0;
            fin = str.length() - 1;

            String subStr = str.substring(inicio, fin).trim();
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("TEXTO_ENTRE_COMILLAS")) {
                agregarNuevoToken(TipoDeToken.TEXTO_ENTRE_COMILLAS, subStr, null, this.numeroLineaActual);
            }
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("PALABRA_RESERVADA")) {
                agregarNuevoToken(TipoDeToken.PALABRA_RESERVADA, subStr, null, this.numeroLineaActual);
            }
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("IDENTIFICADOR")) {
                agregarNuevoToken(TipoDeToken.IDENTIFICADOR, subStr, null, this.numeroLineaActual);
            }
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("DESCONOCIDO")) {
                agregarNuevoToken(TipoDeToken.DESCONOCIDO, subStr, null, this.numeroLineaActual);
            }

            if (endsWithQuote) {
                agregarNuevoToken(TipoDeToken.COMILLAS, "\"", null, this.numeroLineaActual);
            }
            if (endsWithParenthesis) {
                agregarNuevoToken(TipoDeToken.PARENTESIS_DERECHO, ")", null, this.numeroLineaActual);
            }

            //System.out.println("624 subStr " + subStr);
            return (fin + 1);
        }
        //caso: print (texto o print "texto
        if ((startsWithParenthesis || startsWithQuote) && !endsWithParenthesis && !endsWithQuote && !endsWithQuoteAndParenthesis) {
            System.out.println("780");
            inicio = 1;
            fin = str.length();

            String subStr = str.substring(inicio, fin).trim();
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("TEXTO_ENTRE_COMILLAS")) {
                agregarNuevoToken(TipoDeToken.TEXTO_ENTRE_COMILLAS, subStr, null, this.numeroLineaActual);
            }
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("PALABRA_RESERVADA")) {
                agregarNuevoToken(TipoDeToken.PALABRA_RESERVADA, subStr, null, this.numeroLineaActual);
            }
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("IDENTIFICADOR")) {
                agregarNuevoToken(TipoDeToken.IDENTIFICADOR, subStr, null, this.numeroLineaActual);
            }
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("DESCONOCIDO")) {
                agregarNuevoToken(TipoDeToken.DESCONOCIDO, subStr, null, this.numeroLineaActual);
            }

            //System.out.println("624 subStr " + subStr);
            return (fin + 1);
        }

        //caso: print texto 
        if (!startsWithParenthesis && !startsWithQuote && !endsWithParenthesis && !endsWithQuote) {
            System.out.println("793");
            inicio = 0;
            fin = str.length();

            String subStr = str.substring(inicio, fin).trim();
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("TEXTO_ENTRE_COMILLAS")) {
                agregarNuevoToken(TipoDeToken.TEXTO_ENTRE_COMILLAS, subStr, null, this.numeroLineaActual);
            }
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("PALABRA_RESERVADA")) {
                agregarNuevoToken(TipoDeToken.PALABRA_RESERVADA, subStr, null, this.numeroLineaActual);
            }
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("IDENTIFICADOR")) {
                agregarNuevoToken(TipoDeToken.IDENTIFICADOR, subStr, null, this.numeroLineaActual);
            }
            if (tipo != null && verificarTipoTokenDeUnTextoEntreComillas(subStr).toString().equals("DESCONOCIDO")) {
                agregarNuevoToken(TipoDeToken.DESCONOCIDO, subStr, null, this.numeroLineaActual);
            }

            //System.out.println("624 subStr " + subStr);
            return (fin + 1);
        }
        return -1;
         */
        for (int i = inicio; i <= fin; i++) {
            texto.append(arregloDeTokens[i]);

        }

        String str = texto.toString();
        System.out.println("598 texto entre parentesis " + str);

        // Expresiones regulares
        boolean startsWithParenthesisAndQuote = str.matches("^\\(\".*");
        boolean endsWithQuoteAndParenthesis = str.matches(".*\"\\)$");
        //boolean startsWithParenthesisAndNotQuote = str.matches("^\\([^\"].*");
        boolean startsWithParenthesis = str.matches("^\\(.*");
        boolean startsWithQuote = str.matches("^\".*");
        boolean endsWithParenthesis = str.matches(".*\\)$");
        boolean endsWithQuote = str.matches(".*\"$");

        //caso: print ("texto")
        if (startsWithParenthesisAndQuote && endsWithQuoteAndParenthesis) {
            procesarTokens(str, 2, str.length() - 2, true, true, true, true);
            return (str.length() + 1);
        }
        //caso: print ("texto" 
        if (startsWithParenthesisAndQuote && endsWithQuote) {
            procesarTokens(str, 2, str.length() - 1, true, true, true, false);
            return str.length();
        }
        //caso: print ("texto) //ojo con ")
        if (startsWithParenthesisAndQuote && endsWithParenthesis && !endsWithQuoteAndParenthesis) {
            procesarTokens(str, 2, str.length() - 1, true, true, false, true);
            return str.length();
        }

        //caso: print ("texto
        if (startsWithParenthesisAndQuote && !endsWithParenthesis && !endsWithQuote) {
            procesarTokens(str, 2, str.length(), true, true, false, false);
            return str.length();
        }

        //caso: print (texto") 
        if (startsWithParenthesis && !startsWithParenthesisAndQuote && endsWithQuoteAndParenthesis) {
            procesarTokens(str, 1, str.length() - 2, true, false, true, true);
            return str.length();
        }
        //caso: print  "texto")
        if (startsWithQuote && endsWithQuoteAndParenthesis) {
            procesarTokens(str, 1, str.length() - 2, false, true, true, true);
            return str.length();
        }

        //caso: print texto")
        if (!startsWithParenthesis && !startsWithQuote && !startsWithParenthesisAndQuote && endsWithQuoteAndParenthesis) {
            procesarTokens(str, 0, str.length() - 2, false, false, true, true);
            return str.length();
        }

        //caso: print (texto" 
        if (startsWithParenthesis && !startsWithParenthesisAndQuote && endsWithQuote) {
            procesarTokens(str, 1, str.length() - 1, true, false, true, false);
            return (str.length() + 1);
        }
        //caso: print (texto) ->  ojo: texto podria ser un identificador
        if (startsWithParenthesis && !startsWithParenthesisAndQuote && endsWithParenthesis) {
            procesarTokens(str, 1, str.length() - 1, true, false, false, true);
            return (str.length() + 1);
        }
        //caso: print  "texto" 
        if (startsWithQuote && endsWithQuote) {
            procesarTokens(str, 1, str.length() - 1, false, true, true, false);
            return (str.length() + 1);
        }
        //caso: print  "texto)
        if (startsWithQuote && endsWithParenthesis && !endsWithQuoteAndParenthesis) {
            procesarTokens(str, 1, str.length() - 1, false, true, false, true);
            return (str.length() + 1);
        }

        //caso: print texto" 
        if (!startsWithParenthesis && !startsWithQuote && endsWithQuote) {
            procesarTokens(str, 0, str.length() - 1, false, false, true, false);
            return str.length();
        }
        //caso: print texto) -> ojo: texto podria ser un identificador
        if (!startsWithParenthesis && !startsWithQuote && endsWithParenthesis) {
            procesarTokens(str, 0, str.length() - 1, false, false, false, true);
            return str.length();
        }

        //caso: print (texto -> ojo: texto podria ser un identificador
        if (startsWithParenthesis && !endsWithQuote && !endsWithParenthesis && !endsWithQuoteAndParenthesis) {
            procesarTokens(str, 1, str.length(), true, false, false, false);
            return (str.length() + 1);
        }
        //caso: print "texto
        if (startsWithQuote && !endsWithQuote && !endsWithParenthesis && !endsWithQuoteAndParenthesis) {
            procesarTokens(str, 1, str.length(), false, true, false, false);
            return (str.length() + 1);
        }

        //caso: print texto -> ojo: texto podria ser un identificador
        if (!startsWithParenthesis && !startsWithQuote && !endsWithParenthesis && !endsWithQuote && !endsWithQuoteAndParenthesis) {
            procesarTokens(str, 0, str.length(), false, false, false, false);
            return (str.length() + 1);
        }
        return -1;
    }

    // Método auxiliar para procesar tokens
    private void procesarTokens(String str, int inicio, int fin, boolean addLeftParen, boolean addStartQuotes, boolean addEndQuotes, boolean addRigthParen) {
        if (addLeftParen) {
            agregarNuevoToken(TipoDeToken.PARENTESIS_IZQUIERDO, "(", null, this.numeroLineaActual);
        }
        if (addStartQuotes) {
            agregarNuevoToken(TipoDeToken.COMILLAS, "\"", null, this.numeroLineaActual);
        }

        String argumentoDeLaFuncion = str.substring(inicio, fin).trim();
        String[] words = argumentoDeLaFuncion.split("\\s+");
        //Si no hay comillas entonces texto entre comillas podria ser un identificador o una palabra reservada
        if (words.length == 1) {
            if (!addStartQuotes && !addEndQuotes) {
                PalabraReservada palabraReservada = new PalabraReservada();
                if (palabraReservada.esPalabraReservada(argumentoDeLaFuncion.trim())) {
                    agregarNuevoToken(TipoDeToken.PALABRA_RESERVADA, argumentoDeLaFuncion, null, this.numeroLineaActual);
                    //      System.out.println(" 443 encontro una palabra reservada en el input " + content.trim() + " en linea " + numeroLineaActual + "\n");
                } else if (verificarPrimerCaracterDeUnIdentificador(argumentoDeLaFuncion, numeroLineaActual)
                        && verificarSecuenciaDeCaracteresDeUnIdentificador(argumentoDeLaFuncion, numeroLineaActual)) {
                    //    System.out.println(" 446 Este es el textoEntreComillas que es un identificador " + content.trim() + " en linea " + numeroLineaActual + "\n");

                    //agregarNuevoToken(TipoDeToken.IDENTIFICADOR, tokenActual.trim(), null, this.numeroLineaActual);
                    Token nuevoToken = new Token(TipoDeToken.IDENTIFICADOR, argumentoDeLaFuncion.trim(), null, this.numeroLineaActual);
                    tokens.add(nuevoToken);

                    //Solo probando construir una tabla de simbolos
                    incluirNuevaVariableEnTablaDeSimbolos(nuevoToken);

                } else {
                    agregarNuevoToken(TipoDeToken.DESCONOCIDO, argumentoDeLaFuncion.trim(), null, this.numeroLineaActual);
                    //  System.out.println(" 379 SE AGREGO UN TOKE DESCONOCIDO:   " + content.trim() + " en linea " + numeroLineaActual + "\n");
                }
            }
        } else {
            agregarNuevoToken(TipoDeToken.TEXTO_ENTRE_COMILLAS, argumentoDeLaFuncion, null, this.numeroLineaActual);
        }

        if (addEndQuotes) {
            agregarNuevoToken(TipoDeToken.COMILLAS, "\"", null, this.numeroLineaActual);
        }
        if (addRigthParen) {
            agregarNuevoToken(TipoDeToken.PARENTESIS_DERECHO, ")", null, this.numeroLineaActual);
        }

    }

    private TipoDeToken verificarTipoTokenDeUnTextoEntreComillas(String texto) {
        // Dividir la cadena en palabras usando espacios como delimitadores
        String[] words = texto.split("\\s+");
        PalabraReservada palabraReservada = new PalabraReservada();
        TipoDeToken tipo = null;

        System.out.println(" 809 Contenido de words");
        for (String str : words) {
            System.out.println(str);
        }
        // Verificar si hay más de una palabra lo que implica que no es una variable

        if (words.length > 1) {
            tipo = TipoDeToken.TEXTO_ENTRE_COMILLAS;
        } else {
            //    System.out.println(" 440 El StringBuilder contiene solo una palabra." + content);
            if (palabraReservada.esPalabraReservada(texto.trim())) {
                tipo = TipoDeToken.PALABRA_RESERVADA;
                //      System.out.println(" 443 encontro una palabra reservada en el input " + content.trim() + " en linea " + numeroLineaActual + "\n");
            } else if (verificarPrimerCaracterDeUnIdentificador(texto, numeroLineaActual)
                    && verificarSecuenciaDeCaracteresDeUnIdentificador(texto, numeroLineaActual)) {
                //    System.out.println(" 446 Este es el textoEntreComillas que es un identificador " + content.trim() + " en linea " + numeroLineaActual + "\n");

                //agregarNuevoToken(TipoDeToken.IDENTIFICADOR, tokenActual.trim(), null, this.numeroLineaActual);
                tipo = TipoDeToken.IDENTIFICADOR;

            } else {
                tipo = TipoDeToken.DESCONOCIDO;
                //  System.out.println(" 379 SE AGREGO UN TOKE DESCONOCIDO:   " + content.trim() + " en linea " + numeroLineaActual + "\n");
            }
        }
        return tipo;
    }

    private int contarIndentacion(String linea) {
        int contador = 0;
        OUTER:
        for (char c : linea.toCharArray()) {
            switch (c) {
                case ' ':
                    contador++;
                    break;
                case '\t':
                    contador += 8; // Asumiendo que un tabulador equivale a 4 espacios
                    break;
                default:
                    break OUTER;
            }
        }
        return contador;
    }

    public String[] extraerTextoEntreComillas(List<Token> lineaDeTokens) {
        StringBuilder textoEntreComillas = new StringBuilder();
        boolean dentroDeComillas = false;
        int contadorTokens = 0;

        for (Token token : lineaDeTokens) {
            if (token.getTipoDeToken().toString().equals("COMILLAS")) {
                if (dentroDeComillas) {
                    // Salir del bucle cuando se encuentra la segunda comilla
                    dentroDeComillas = false;
                } else {
                    // Encontrar la primera comilla
                    dentroDeComillas = true;
                }
            } else if (dentroDeComillas) {
                if (token.getLexema().equals(")")) {
                    // Salir del bucle si se encuentra un paréntesis de cierre
                    break;
                }
                // Agregar lexema al texto entre comillas
                textoEntreComillas.append(token.getLexema());
                textoEntreComillas.append(" ");
                contadorTokens++;
            }
        }

        String[] resultado = new String[2];
        resultado[0] = textoEntreComillas.toString();
        resultado[1] = String.valueOf(contadorTokens + 1);

        return resultado;
    }

    public int getCantidadComentarios() {
        return cantidadComentarios;
    }

    public static void imprimirListasDeContenidoFinal(List<LineaDeContenido> contenido) {
        for (LineaDeContenido linea : contenido) {
            System.out.println(linea);
        }
    }

    public List<List<Token>> getListaDeTokens() {
        return listaDeTokens;
    }

    public List<LineaDeContenido> getListaContenidoFinal() {
        return listaContenidoFinal;
    }

    public Map<Integer, List<MiError>> getErroresEncontradosMap() {
        return erroresEncontradosMap;
    }

    public TablaDeSimbolos getTablaDeSimbolos() {
        return tablaDeSimbolos;
    }

    //FUNCIONES AUXILIARES
    public static String[] convertirStringTokenizerEnArregloDeStrings(StringTokenizer tokenizer) {
        String[] arreglo = new String[tokenizer.countTokens()];
        int j = 0;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            arreglo[j++] = token;
        }
        return arreglo;
    }

    public static void agregarNuevoToken(TipoDeToken tipoDeToken, String lexema, String literal, int numeroLinea) {
        Token nuevoToken = new Token(tipoDeToken, lexema, literal, numeroLinea);
        tokens.add(nuevoToken);
    }

    public static void agregarNuevoToken(TipoDeToken tipoDeToken, String lexema, int numeroLinea) {
        Token nuevoToken = new Token(tipoDeToken, lexema, numeroLinea);
        tokens.add(nuevoToken);
    }

    public static void agregarNuevoToken(TipoDeToken tipoDeToken, int numeroLinea) {
        Token nuevoToken = new Token(tipoDeToken, numeroLinea);
        tokens.add(nuevoToken);
    }

    //Verifica si la linea de codigo que se esta leyendo contiene un comentario
    public boolean existeComentarioDeUnaLinea(String lineaActual) {
        Character caracterDeComentario = '#';

        //System.out.println(" 630 Existe el caracter de comentarios " + lineaActual.contains(String.valueOf(caracterDeComentario)));
        return lineaActual.contains(String.valueOf(caracterDeComentario));

    }

    public boolean existeComentarioVariasLineas(String lineaActual) {
        String tresComillas = "\"\"\"";

        //System.out.println(" 638 Existe el caracter de comentarios multilineas " + lineaActual.contains(tresComillas));
        return lineaActual.contains(tresComillas);

    }

    //Valida si el token corresponde a un identificador valido
    public boolean verificarPrimerCaracterDeUnIdentificador(String string, int numeroLinea) {

        if (string == null || string.isEmpty()) {
            return false;
        }

        if (string.matches("^[a-zA-Z_].*")) {
            //System.out.println("651 verificarPrimeraCaracter Borrar " + "inicia con letra o _ " + string);
            return true;
        } else {
            //System.out.println("654 verificarPrimeraCaracter Borrar " + auxiliares.TiposDeError.obtenerDescripcionDelError(200));
            int numeroError = 200;
            TiposDeError tipos = new TiposDeError();
            MiError e = new MiError(numeroLinea, numeroError, tipos.obtenerDescripcionDelError(numeroError));
            this.erroresEncontrados.add(e);

            nuevoContenido.setErroresEncontrados(erroresEncontrados);

            //USANDO HASHMAP PARA ERRORES ENCONTRADOS
            List<MiError> errores1 = new ArrayList<>();
            errores1.add(new MiError(numeroLinea, numeroError, tipos.obtenerDescripcionDelError(numeroError)));
            erroresEncontradosMap.put((numeroLinea + 1), errores1);

            return false;
        }

    }

    //Verifica que el identificador sea una secuencia de letras y
    //numeros sin caracteres especiales a partir del segundo caracter
    public boolean verificarSecuenciaDeCaracteresDeUnIdentificador(String string, int numeroLinea) {

        // Verificar los caracteres restantes
        if (string == null || string.isEmpty()) {
            System.out.println("315 verificarPrimeraCaracter Borrar " + "inicia con letra o _");
            return false;
        }

        if (string.matches("[a-zA-Z0-9_]*$")) {
            //System.out.println("684 verificarSecuenciaDeCaracteres Borrar " + "cadena de caracteres alfanumericos  " + string);

            return true;
        } else {
            //System.out.println("324 metodo verificarSecuenciaDeCaracter Borrar " + auxiliares.TiposDeError.obtenerDescripcionDelError(201));
            int numeroError = 201;
            TiposDeError tipos = new TiposDeError();
            MiError e = new MiError(numeroLinea, numeroError, tipos.obtenerDescripcionDelError(numeroError));
            this.erroresEncontrados.add(e);

            nuevoContenido.setErroresEncontrados(erroresEncontrados);

            //USANDO HASHMAP PARA ERRORES ENCONTRADOS
            List<MiError> errores2 = new ArrayList<>();
            errores2.add(new MiError(numeroLinea, numeroError, tipos.obtenerDescripcionDelError(numeroError)));
            erroresEncontradosMap.put((numeroLinea + 1), errores2);

            return false;
        }

    }

    //Valida si el token corresponde a una palabra reservada
    public static boolean esNumeroEntero(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        try {
            Integer.valueOf(str);
            return true; // El token es un número entero
        } catch (NumberFormatException e) {
            return false; // El token no es entero 
        }
    }

    //Valida si el token corresponde a un numero decimal o entero
    public static boolean esNumeroDecimal(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        try {
            Double.valueOf(str);
            return true; // El token es un número decimal
        } catch (NumberFormatException e) {
            return false; // El token no es un número decimal
        }
    }

    public static boolean esAlfaNumerico(String str) {
        return str.matches("[a-zA-Z0-9_]*$");
    }

    public void incluirNuevaVariableEnTablaDeSimbolos(Token token) {
        String nombre = token.getLexema();
        String tipo = "variable";
        String literal = token.getLiteral();

        int numeroLinea = token.getNumeroLinea(); // Obtiene el número de línea donde se declaro por primera vez

        Simbolo simbolo = new Simbolo(tipo, literal, numeroLinea);
        tablaDeSimbolos.agregarSimbolo(nombre, simbolo);

    }

    public void imprimirTablaDeSimbolos() {
        System.out.println(tablaDeSimbolos.toString());
    }
} //fin clase lexer
