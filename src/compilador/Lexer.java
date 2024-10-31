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

            //Agrega la linea que actualmente se analiza al archivo de salida 
            //registrarLineaAnalizadaEnProgramaPythonRevisado(lineaDeCodigoActual, numeroLineaActual);
            //listaContenidoFinal.add(nuevoContenido);
            System.out.println();
            System.out.println("76 La linea que estamos leyendo: " + lineaDeCodigoActual + " en la linea " + lineaActual);

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
            }
            
            int contadorIndentacion = contarIndentacion(lineaDeCodigoActual);
      


            
            
            //Separa cada linea de codigo en Tokens, quitamos la espacios vacios antes del primer token de la linea.
            StringTokenizer tokenizer = new StringTokenizer(lineaDeCodigoActual.trim(), " \n()[]{}=<>*/+-:\",.", true);

            System.out.println("94 Tokenizer to  La linea que estamos leyendo: " + lineaDeCodigoActual + " en la linea " + lineaActual);
            System.out.println();

            String[] arregloDeTokens = convertirStringTokenizerEnArregloDeStrings(tokenizer);

            System.out.println("114 Contenido de arreglo de Tokens: ");
            Arrays.stream(arregloDeTokens).forEach(System.out::println);
            System.out.println();

            //Almanacena los Token clasificados de la linea actual de codigo
            tokens = new ArrayList<Token>();
            agregarNuevoToken(TipoDeToken.INDENTACION, null, String.valueOf(contadorIndentacion), numeroLineaActual);
            contadorIndentacion = 0;
            boolean existeTextoEntreComillas = false;

            //Itera sobre la linea de codigo ya convertida en arreglo de Strings
            for (int indice = 0; indice < arregloDeTokens.length; ++indice) {
                boolean existeFuncionInput = false;
                boolean existeComillasIniciales = false;

                String tokenActual = arregloDeTokens[indice];
                String tokenSiguiente = " ";

                if (indice == arregloDeTokens.length - 1) {
                    tokenSiguiente = " ";
                } else {
                    tokenSiguiente = arregloDeTokens[indice + 1];
                }
                System.out.println("133 Antes switch tokenActual es " + tokenActual + " esta en " + indice + " <" + arregloDeTokens.length);
                System.out.println();
                
                
                switch (tokenActual) {
                  
                    case " ":
                        break;

                    //Analiza los operadores aritméticos    
                    case "+":
                        agregarNuevoToken(TipoDeToken.SUMA, "+", numeroLineaActual);
                        break;
                    case "-":
                        agregarNuevoToken(TipoDeToken.RESTA, "-", numeroLineaActual);
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
                         System.out.println(" 225 NUEVAMENTE EN EL COMILLAS " + tokenActual + " esta en indice " + indice + " < " + arregloDeTokens.length);
                        agregarNuevoToken(TipoDeToken.COMILLAS, "\"", null, numeroLineaActual);
                        if(!existeTextoEntreComillas) {
                       

                        StringBuilder textoEntreComillas = new StringBuilder();

                        System.out.println(" 230 ANTES DE IF " + (indice + 1) + " <" + arregloDeTokens.length);
                        if (indice + 1 < arregloDeTokens.length) {
                            System.out.println(" 230  tokenActual es  " + tokenActual + " esta en indice " + indice + " < " + arregloDeTokens.length);

                            ++indice;

                            while (indice + 1 < arregloDeTokens.length) {
                                tokenActual = arregloDeTokens[indice];
                                System.out.println(" 236  tokenActual es  " + tokenActual + " esta en indice " + indice + " < " + arregloDeTokens.length);
                                if (tokenActual.equals("\"") || (tokenActual.equals(")") && indice == (arregloDeTokens.length - 1))) {

                                    System.out.println();
                                    System.out.println(" 239 token actual es " + tokenActual + "  esta en indice " + indice + " <" + arregloDeTokens.length);

                                    break;
                                } else {
                                    textoEntreComillas.append(tokenActual);
                                    System.out.println();
                                    System.out.println(" 244 token actual es " + tokenActual + "  esta en indice " + indice + " <" + arregloDeTokens.length);
                                    System.out.println(" 245 texto entre comillas: " + textoEntreComillas);
                                    System.out.println();
                                    ++indice;

                                }

                            }
                            System.out.println();
                            if (!textoEntreComillas.isEmpty()) {
                                agregarNuevoToken(TipoDeToken.TEXTO_ENTRE_COMILLAS, textoEntreComillas.toString(), null, numeroLineaActual);
                                existeTextoEntreComillas = true;
                                System.out.println(" 249 Texto entre comillas: " + textoEntreComillas.toString() + " indice = " + indice);
                            } else {
                                System.out.println(" 250 Texto entre comillas: " + textoEntreComillas.toString() + " indice = " + indice);
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
                            System.out.println(" 269 (indice < arregloDeTokens.length): " + (indice < arregloDeTokens.length));
                            if ((arregloDeTokens[indice + 1].equals("n"))) {
                                agregarNuevoToken(TipoDeToken.SALTO_DE_LINEA, "\n", null, numeroLineaActual);
                                ++indice;
                            }
                        }
                        break;
                        
                    case "ArithmeticError":
                        agregarNuevoToken(TipoDeToken.EXCEPCION, "ArithmeticError" , null, numeroLineaActual);
                        break;
                    case "ZeroDivisionError": 
                         agregarNuevoToken(TipoDeToken.EXCEPCION, "ZeroDivisionError" , null, numeroLineaActual);
                        break;
                    case "ValueError":
                         agregarNuevoToken(TipoDeToken.EXCEPCION, "ValueError" , null, numeroLineaActual);
                        break;
                        
                    default:
                        PalabraReservada palabraReservada = new PalabraReservada();

                        if (palabraReservada.esPalabraReservada(tokenActual.trim())) {
                            agregarNuevoToken(TipoDeToken.PALABRA_RESERVADA, tokenActual.trim(), null, this.numeroLineaActual);
                            existeFuncionInput = true;

                            if (tokenActual.trim().equals("input")) {
                                StringBuilder texoEntreComillasEnInput = new StringBuilder();
                                System.out.println();
                                System.out.println("267 el token es print: " + tokenActual.trim());

                                if (indice + 1 < arregloDeTokens.length) { //Valida si hay mas tokens
                                    System.out.println(" 269 (indice < arregloDeTokens.length): " + (indice < arregloDeTokens.length));
                                    String tokenSiguienteDeInput = arregloDeTokens[++indice]; //Obtiene el token siguiente a input
                                    switch (tokenSiguienteDeInput) {
                                        case "(":
                                            agregarNuevoToken(TipoDeToken.PARENTESIS_IZQUIERDO, "(", numeroLineaActual);
                                            break;
                                        case "{":
                                            agregarNuevoToken(TipoDeToken.LLAVE_IZQUIERDA, "{", numeroLineaActual);
                                            break;
                                        case "[":
                                            agregarNuevoToken(TipoDeToken.CORCHETE_IZQUIERDO, "[", numeroLineaActual);
                                            break;
                                        case "\"":
                                            agregarNuevoToken(TipoDeToken.COMILLAS, "\"", numeroLineaActual);
                                            break;
                                        case " ":
                                            System.out.println("298 Se incluyo un token espaso en blanco" + tokenSiguienteDeInput + " indice pasa a " + indice);
                                            break;
                                        default:
                                            System.out.println(" 299 En default: " + tokenSiguienteDeInput + " indice igual " + indice);
                                            break;
                                    }

                                    System.out.println();
                                    System.out.println(" 300 Se incluyo un token " + tokenSiguienteDeInput + " indice es " + indice);

                                    if (indice + 1 < arregloDeTokens.length) { //Valida si hay mas tokens
                                        tokenSiguienteDeInput = arregloDeTokens[++indice]; //Obtiene el token de (, { o [
                                        tokenActual = tokenSiguienteDeInput;
                                        if (tokenActual.equals("\"")) {
                                            agregarNuevoToken(TipoDeToken.COMILLAS, "\"", null, numeroLineaActual);
                                            System.out.println();
                                            System.out.println(" 309 Se incluyo un token " + "\"" + " indice pasa a " + indice);
                                            System.out.println();
                                        } else {
                                            System.out.println(" 316 No hay comillas iniciales  el token es: " + tokenActual + " indice pasa a " + indice);
                                            texoEntreComillasEnInput.append(tokenActual);
                                        }

                                        if (indice + 1 < arregloDeTokens.length) {
                                            System.out.println(" 276 arregloDeTokens[indice + 1].equals(\"(\"): " + arregloDeTokens[indice + 1].equals("("));
                                            // ++indice;
                                            System.out.println(" 316 Antes del while  indice pasa a " + indice);
                                            while (indice + 1 < arregloDeTokens.length) {

                                                tokenActual = arregloDeTokens[++indice];
                                                if (tokenActual.equals("\"") || tokenActual.equals(")") || tokenActual.equals("}") || tokenActual.equals("]")) {
                                                    --indice;
                                                    break;
                                                } else {
                                                    texoEntreComillasEnInput.append(tokenActual);
                                                    // ++indice;
                                                    System.out.println();
                                                    System.out.println(" 326 de agrego al stringbuilder " + tokenActual + " indice pasa a " + indice);
                                                }

                                            }
                                            System.out.println();
                                            System.out.println(" 331 Texto entre comillas print: " + texoEntreComillasEnInput.toString() + " indice es " + indice);
                                            agregarNuevoToken(TipoDeToken.TEXTO_ENTRE_COMILLAS, texoEntreComillasEnInput.toString(), null, numeroLineaActual);
                                        }

                                    }

                                }
                            }
                            /*
                            for (String str : arregloDeTokens) {
                                if (str.contains("print")) {
                                    int startPrint = str.indexOf("print");
                                    int startParen = str.indexOf('(');
                                    int endParen = str.lastIndexOf(')');
                                    int startQuote = str.indexOf('"', startParen);
                                    int endQuote = str.lastIndexOf('"', endParen);

                                    if (startParen != -1 && endParen != -1 && startQuote != -1 && endQuote != -1) {
                                        String content = str.substring(startQuote + 1, endQuote);
                                        String[] tokens = content.split("\\s+");
                                        List<String> tokenList = new ArrayList<>();
                                        for (String token : tokens) {
                                            tokenList.add(token);
                                        }
                                        System.out.println("362 Tokens: " + tokenList);
                                        System.out.println("363 Number of tokens: " + tokenList.size());
                                    } else {
                                        System.out.println("365 Paréntesis o comillas faltantes en: " + str);
                                    }
                                } else {
                                    System.out.println("368 La cadena no contiene 'print': " + str);
                                }
                            }
                             */
 /*

                            //Caso de funcion print con texto entre comillas
                            if (tokenActual.trim().equals("print")) {
                                StringBuilder texoEntreComillasEnPrint = new StringBuilder();
                                System.out.println();
                                System.out.println("267 el token es print: " + tokenActual.trim());

                                //Miramos el token siguiente a print
                                if (indice + 1 < arregloDeTokens.length) { //Valida si hay mas tokens
                                    System.out.println(" 355 Hay al menos un tokens mas: " + (indice < arregloDeTokens.length));
                                    String tokenSiguienteDePrint = arregloDeTokens[++indice]; //Obtiene el token siguiente a input
                                    tokenActual = tokenSiguienteDePrint;
                                    switch (tokenActual) {
                                        case "(":
                                            agregarNuevoToken(TipoDeToken.PARENTESIS_IZQUIERDO, "(", numeroLineaActual);
                                            break;
                                        case "{":
                                            agregarNuevoToken(TipoDeToken.LLAVE_IZQUIERDA, "{", numeroLineaActual);
                                            break;
                                        case "[":
                                            agregarNuevoToken(TipoDeToken.CORCHETE_IZQUIERDO, "[", numeroLineaActual);
                                            break;
                                        case "\"":
                                            agregarNuevoToken(TipoDeToken.COMILLAS, "\"", numeroLineaActual);
                                            break;
                                        case " ":
                                            System.out.println("372 Se incluyo un token espaso en blanco" + tokenActual + " indice pasa a " + indice);
                                            break;
                                        default:
                                            System.out.println("375 En default: " + tokenActual + " indice igual " + indice);
                                            break;
                                    }

                                    System.out.println();
                                    System.out.println(" 380 Se incluyo un token " + tokenActual + " indice es " + indice);

                                    
                                    if (indice + 1 < arregloDeTokens.length) { //Valida si hay mas tokens
                                        tokenSiguienteDePrint = arregloDeTokens[++indice]; //Se mueve al token siguiente del siguiente de print
                                        tokenActual = tokenSiguienteDePrint; //Nuevamente lo leemos como token actual
                                        if (tokenActual.equals("\"")) {
                                            agregarNuevoToken(TipoDeToken.COMILLAS, "\"", null, numeroLineaActual);
                                            System.out.println();
                                            System.out.println(" 309 Se incluyo un token " + "\"" + " indice pasa a " + indice);
                                            System.out.println();
                                        } else if (  arregloDeTokens.length - (indice + 1) > 1 ){
                                        
                                        }else {
                                            System.out.println(" 316 No hay comillas iniciales  el token es: " + tokenActual + " indice pasa a " + indice);
                                            texoEntreComillasEnPrint.append(tokenActual);
                                        }

                                        if (indice + 1 < arregloDeTokens.length) {
                                            System.out.println(" 400 arregloDeTokens[indice + 1].equals(\"(\"): " + arregloDeTokens[indice + 1].equals("("));
                                            // ++indice;
                                            System.out.println(" 402 Antes del while  indice pasa a " + indice);
                                            while (indice + 1 < arregloDeTokens.length) {

                                                tokenActual = arregloDeTokens[++indice];
                                                if (tokenActual.equals("\"") || tokenActual.equals(")") || tokenActual.equals("}") || tokenActual.equals("]")) {
                                                    --indice;
                                                    break;
                                                } else {
                                                    texoEntreComillasEnPrint.append(tokenActual);
                                                    // ++indice;
                                                    System.out.println();
                                                    System.out.println(" 326 de agrego al stringbuilder " + tokenActual + " indice pasa a " + indice);
                                                }

                                            }
                                            System.out.println();
                                            System.out.println(" 331 Texto entre comillas print: " + texoEntreComillasEnPrint.toString() + " indice es " + indice);
                                            agregarNuevoToken(TipoDeToken.TEXTO_ENTRE_COMILLAS, texoEntreComillasEnPrint.toString(), null, numeroLineaActual);
                                        }

                                    }

                                }

                            }

                            System.out.println();
                            System.out.println(" 345 Salimos de palabra reservada " + tokenActual + " indice pasa a " + indice);
                             */
                        } else if (esNumeroEntero(tokenActual.trim())) {
                            agregarNuevoToken(TipoDeToken.NUMERO_ENTERO, tokenActual.trim(), null, this.numeroLineaActual);
                        } else if (esNumeroDecimal(tokenActual.trim())) {
                            agregarNuevoToken(TipoDeToken.NUMERO_DECIMAL, tokenActual.trim(), null, this.numeroLineaActual);
                        } else if (verificarPrimerCaracterDeUnIdentificador(tokenActual.trim(), numeroLineaActual)
                                && verificarSecuenciaDeCaracteresDeUnIdentificador(tokenActual.trim(), numeroLineaActual)) {
                            System.out.println(" 194 Este es el TOKEN  BORRAR:   " + tokenActual.trim() + " en linea " + numeroLineaActual + "\n");

                            //agregarNuevoToken(TipoDeToken.IDENTIFICADOR, tokenActual.trim(), null, this.numeroLineaActual);
                            Token nuevoToken = new Token(TipoDeToken.IDENTIFICADOR, tokenActual.trim(), null, this.numeroLineaActual);
                            tokens.add(nuevoToken);

                            //Solo probando construir una tabla de simbolos
                            incluirNuevaVariableEnTablaDeSimbolos(nuevoToken);

                            System.out.println("500 LEXER TABLA DE SIMBOLOS ");
                            imprimirTablaDeSimbolos();

                        } else {
                            agregarNuevoToken(TipoDeToken.DESCONOCIDO, tokenActual.trim(), null, this.numeroLineaActual);
                        }
                        break;
                }

            }

            listaDeTokens.add(tokens);
        } //Fin for que recorre linea por linea el programa en Python
        /*
        System.out.println("Tokens tiene tamanio " + tokens.size());
        for (Token token : tokens) {
            System.out.println(token.toString());
        }
         */
        System.out.println();
        System.out.println("Lista de Tokens tiene tamanio " + listaDeTokens.size());
        int count = 0;
        for (List tkn : listaDeTokens) {
            for (Object token : tkn) {
                System.out.println(token.toString());
                ++count;
            }

        }
        System.out.println("Tokens en linea  " + count);

        System.out.println();
        System.out.println("253");
        System.out.println("LEXER: Contenido del mapa de errores encontrados " + erroresEncontrados.size());
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
        System.out.println("254");
        System.out.println("LISTA CONTENIDO FINAL LEXER");
        //imprimirListas(listaContenidoFinal);
        if (listaContenidoFinal != null) {
            imprimirListasDeContenidoFinal(listaContenidoFinal);
        }

        System.out.println();
    }

    public String extractStringBetweenQuotes(List<Token> tokens) {
        StringBuilder extractedString = new StringBuilder();
        boolean insideQuotes = false;

        for (Token token : tokens) {
            String lexema = token.getLexema();
            if (lexema.equals("\"")) {
                if (insideQuotes && extractedString.length() > 0 && extractedString.charAt(extractedString.length() - 1) == '\\') {
                    // Remove the escape character and add the quote
                    extractedString.setCharAt(extractedString.length() - 1, '\"');
                } else {
                    insideQuotes = !insideQuotes;
                    continue;
                }
            }
            if (insideQuotes) {
                extractedString.append(lexema);
            }
        }

        return extractedString.toString();
    }
    
    private static int contarIndentacion(String linea) {
        int contador = 0;
        for (char c : linea.toCharArray()) {
            if (c == ' ' || c == '\t') {
                contador++;
            } else {
                break;
            }
        }
        return contador;
    }

    
    public static String[] extraerTextoEntreComillas(List<Token> lineaDeTokens) {
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

        System.out.println(" 332 Existe el caracter de comentarios " + lineaActual.contains(String.valueOf(caracterDeComentario)));
        return lineaActual.contains(String.valueOf(caracterDeComentario));

    }

    public boolean existeComentarioVariasLineas(String lineaActual) {
        String tresComillas = "\"\"\"";

        System.out.println(" 342 Existe el caracter de comentarios multilineas " + lineaActual.contains(tresComillas));
        return lineaActual.contains(tresComillas);

    }

    //Valida si el token corresponde a un identificador valido
    public boolean verificarPrimerCaracterDeUnIdentificador(String string, int numeroLinea) {

        if (string == null || string.isEmpty()) {
            return false;
        }

        if (string.matches("^[a-zA-Z_].*")) {
            System.out.println("315 verificarPrimeraCaracter Borrar " + "inicia con letra o _ " + string);
            return true;
        } else {
            //System.out.println("300 verificarPrimeraCaracter Borrar " + auxiliares.TiposDeError.obtenerDescripcionDelError(200));
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
            System.out.println("320 verificarSecuenciaDeCaracteres Borrar " + "cadena de caracteres alfanumericos  " + string);

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
}
