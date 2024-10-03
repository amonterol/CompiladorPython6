/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compilador;

import auxiliares.Archivo;
import auxiliares.PalabraReservada;
import auxiliares.TipoDeToken;
import auxiliares.Token;
import static compilador.Compilador.archivoDeSalida;
import static compilador.Compilador.contenidoArchivo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 *
 * @author A Montero
 */
public class Lexer {

    private static List<String> programaEnPythonOriginal; //Código que se analiza en forma de lista
    private static List<List<Token>> listaDeTokens; //Almacena cada linea de codigo convertida a Token
    private static List<Token> tokens; //Almacena una linea de codigo convertida en Token
    private static List<String> programaEnPythonRevisado; //Almacena los tokens que se identifican
    private static auxiliares.Error erroresEnProgamaEnPythonOriginal;
    private static HashMap<String, Integer> histograma = new HashMap<String, Integer>(); //Cuenta los operadores de comparación

    private int numeroLineaActual;   //Almacena el número de linea que se está analizando
    private int indiceCaracterActual;   //Almacena la posición del cáracter que el analizador esta leyendo actualmente   

    private int cantidadComentarios; // almacena la cantidad de comentarios en el código
    private int cantidadOperadoresComparacion; // almacena la cantidad de operadores de comparación

    public Lexer(List<String> content) {
        this.programaEnPythonOriginal = content;
        this.listaDeTokens = new ArrayList<>();
        this.programaEnPythonRevisado = new ArrayList<>();
        this.erroresEnProgamaEnPythonOriginal = new auxiliares.Error();
        this.numeroLineaActual = 1;// Almancena en número de la línea que se esta leyento actualmente
        this.indiceCaracterActual = 0;   //Almacena el cáracter que el analizador esta leyendo actualmente
        this.cantidadComentarios = 0;
        this.cantidadOperadoresComparacion = 0;
    }

    public void analizadorLexico(List<String> contenido) throws IOException {

        // System.out.println("\n3 LEXER > INICIO LINEA DE CODIGO CONVERTIDA A CARACTERES  " + lineaDeCodigoActual);
        char caracterActual = ' '; //Almacena el caracter que actualmente se lee en la línea de código
        char caracterSiguiente = ' '; //Almacena el caracter siguiente al que actualmente se lee en la línea de código

        boolean posibleIdentificador = false;
        boolean posibleNumero = false;

        String identificador = "";
        String string = "";
        String comentario = "";
        String numero = ""; //Almacena transitoriamente un cadena constituido por digitos

        listaDeTokens = new ArrayList<List<Token>>();
        inicializarHistogramaOperadoresComparacion();

        //Itera sobre cada línea del archivo con el código fuente en Python
        //for (String lineaDeCodigoActual : programaEnPythonOriginal) {
        for (int lineaActual = 0; lineaActual < programaEnPythonOriginal.size(); lineaActual++) {

            String contenidoDeLaLineaActual = programaEnPythonOriginal.get(lineaActual).trim();
             //Agrega la linea que actualmente se analiza al archivo de salida 
            registrarLineaAnalizadaEnProgramaPythonRevisado(contenidoDeLaLineaActual, numeroLineaActual);
            System.out.println("74 La linea que estamos leyendo: " + lineaActual + " " + contenidoDeLaLineaActual);

            //Verifica si la linea esta en blanco
            if (contenidoDeLaLineaActual.isBlank() || contenidoDeLaLineaActual.isEmpty()) {
                //Agrega la linea que actualmente se analiza al archivo de salida 
                //registrarLineaAnalizadaEnProgramaPythonRevisado(contenidoDeLaLineaActual, numeroLineaActual);
                ++numeroLineaActual;
                //continue;
            }

            //Verifica si la linea es un comentario o si hay comentarios al final de una linea de código
            if (existeComentario(contenidoDeLaLineaActual)) {
                //Agrega la linea que actualmente se analiza al archivo de salida 
                //registrarLineaAnalizadaEnProgramaPythonRevisado(contenidoDeLaLineaActual, numeroLineaActual);
                cantidadComentarios++;
                contenidoDeLaLineaActual = contenidoDeLaLineaActual.split("#")[0].trim(); // Elimina la parte del comentario en la línea
            }

            StringTokenizer tokenizer = new StringTokenizer(contenidoDeLaLineaActual, " ()[]=<>*/+-:", true);

            System.out.println("94 Tokenizer to  La linea que estamos leyendo: " + lineaActual + " " + contenidoDeLaLineaActual);
            tokenizer.toString();
            //Para poder ver hacia adelante
            String[] arregloDeTokens = new String[tokenizer.countTokens()];
            int j = 0;
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                arregloDeTokens[j++] = token;
            }
            tokens = new ArrayList<Token>();
            for (int indice = 0; indice < arregloDeTokens.length; ++indice) {
                
                String tokenActual = arregloDeTokens[indice];
                String tokenSiguiente = " ";

                if (indice == arregloDeTokens.length - 1) {
                    tokenSiguiente = " ";
                } else {
                    tokenSiguiente = arregloDeTokens[indice + 1];
                }

                switch (tokenActual) {
                    //Ignora los espacios en blanco
                    case " ":
                        break;

                    //Analiza los operadores aritméticos    
                    case "+":
                        agregarNuevoToken(TipoDeToken.SUMA, numeroLineaActual);
                        break;
                    case "-":
                        agregarNuevoToken(TipoDeToken.RESTA, numeroLineaActual);
                        break;
                    case "*":
                        agregarNuevoToken(TipoDeToken.MULTIPLICACION, numeroLineaActual);
                        break;
                    case "/":
                        agregarNuevoToken(TipoDeToken.DIVISION, numeroLineaActual);
                        break;

                    //Analiza los operadores aritméticos       
                    case "=":
                        if (tokenSiguiente.equals("=")) {
                            agregarNuevoToken(TipoDeToken.IGUAL_QUE, numeroLineaActual);
                            incrementarCantidadOperadoresComparacion("==");
                        } else {
                            agregarNuevoToken(TipoDeToken.ASIGNACION, numeroLineaActual);
                        }
                        break;
                    case "!":
                        if (tokenSiguiente.equals("=")) {
                            agregarNuevoToken(TipoDeToken.DIFERENTE_QUE, numeroLineaActual);
                            incrementarCantidadOperadoresComparacion("!=");
                        } else {
                            //No contemplado en Python
                        }
                        break;
                    case ">":
                        if (tokenSiguiente.equals("=")) {
                            agregarNuevoToken(TipoDeToken.MAYOR_O_IGUAL_QUE, numeroLineaActual);
                            incrementarCantidadOperadoresComparacion(">=");
                        } else {
                            agregarNuevoToken(TipoDeToken.MAYOR_QUE, numeroLineaActual);
                            incrementarCantidadOperadoresComparacion(">");
                        }
                        break;
                    case "<":
                        if (tokenSiguiente.equals("=")) {
                            agregarNuevoToken(TipoDeToken.MENOR_O_IGUAL_QUE, numeroLineaActual);
                            incrementarCantidadOperadoresComparacion("<=");
                        } else {
                            agregarNuevoToken(TipoDeToken.MENOR_QUE, numeroLineaActual);
                            incrementarCantidadOperadoresComparacion("<");
                        }
                        break;

                    //Identifica los operadores dos puntos ->  de sublista, subcadena o subarreglos y tipo de retorno de una función    
                    case ":":
                        agregarNuevoToken(TipoDeToken.DOS_PUNTOS, numeroLineaActual);
                        break;

                    //Identifica los operadores de agrupación
                    case "(":
                        agregarNuevoToken(TipoDeToken.PARENTESIS_IZQUIERDO, numeroLineaActual);
                        break;
                    case ")":
                        agregarNuevoToken(TipoDeToken.PARENTESIS_DERECHO, numeroLineaActual);
                        break;
                    case "[":
                        agregarNuevoToken(TipoDeToken.CORCHETE_IZQUIERDO, numeroLineaActual);
                        break;
                    case "]":
                        agregarNuevoToken(TipoDeToken.CORCHETE_DERECHO, numeroLineaActual);
                        break;
                    default:
                        PalabraReservada palabraReservada = new PalabraReservada();

                        if (palabraReservada.esPalabraReservada(tokenActual.trim())) {
                            agregarNuevoToken(TipoDeToken.PALABRA_RESERVADA, tokenActual.trim(), null, this.numeroLineaActual);
                        } else if (esNumeroEntero(tokenActual.trim())) {
                            agregarNuevoToken(TipoDeToken.NUMERO_ENTERO, tokenActual.trim(), null, this.numeroLineaActual);
                        } else if (esNumeroDecimal(tokenActual.trim())) {
                            agregarNuevoToken(TipoDeToken.NUMERO_DECIMAL, tokenActual.trim(), null, this.numeroLineaActual);
                        } else if (verificarPrimerCaracterDeUnIdentificador(tokenActual.trim(), numeroLineaActual)
                                && verificarSecuenciaDeCaracteresDeUnIdentificador(tokenActual.trim(), numeroLineaActual)) {
                            System.out.println(" 194 Este es el TOKEN  BORRAR:   " + tokenActual.trim() + " en linea " + numeroLineaActual + "\n");
                            agregarNuevoToken(TipoDeToken.IDENTIFICADOR, tokenActual.trim(), null, this.numeroLineaActual);

                        } else {
                            agregarNuevoToken(TipoDeToken.DESCONOCIDO, tokenActual.trim(), null, this.numeroLineaActual);
                        }
                        break;
                }

            }
            ++numeroLineaActual;
            listaDeTokens.add(tokens);
        }
        /*
        System.out.println("Tokens tiene tamanio " + tokens.size());
        for (Token token : tokens) {
            System.out.println(token.toString());
        }
         */
        System.out.println("Lista de Tokens tiene tamanio " + listaDeTokens.size());
        for (List tkn : listaDeTokens) {
            for (Object token : tkn) {
                System.out.println(token.toString());
            }

        }
        /* BORRAR
        if (listaDeTokens.get(0).get(0).getLexema().equals("import")) {
            System.out.println();
            System.out.println("228 FUNCIONA " + listaDeTokens.get(0).get(0).getLexema());
            System.out.println();
        }
         */

        Parser parser = new Parser(listaDeTokens, programaEnPythonRevisado);
        parser.analisisSintactico();

        System.out.println("Generacion del histograma");
        generarHistogramaOperadoresComparacion();

        System.out.println("Generacion del archivo de analisis");
        generarArchivoDeSalida();

    }

    //FUNCIONES AUXILIARES
    public static void generarArchivoDeSalida() {
        Archivo archivo = new Archivo();
        archivo.escribirArchivo(programaEnPythonRevisado, archivoDeSalida);

    }

    public static void agregarNuevoToken(TipoDeToken tipoDeToken, String lexema, String literal, int numeroLinea) {
        Token nuevoToken = new Token(tipoDeToken, lexema, literal, numeroLinea);
        tokens.add(nuevoToken);
    }

    public static void agregarNuevoToken(TipoDeToken tipoDeToken, int numeroLinea) {
        Token nuevoToken = new Token(tipoDeToken, numeroLinea);
        tokens.add(nuevoToken);
    }

    //Verifica si la linea de codigo que se esta leyendo contiene un comentario
    public boolean existeComentario(String lineaActual) {
        Character caracterDeComentario = '#';

        System.out.println(" Existe el caracter de comentarios " + lineaActual.contains(String.valueOf(caracterDeComentario)));
        return lineaActual.contains(String.valueOf(caracterDeComentario));

    }

    //Verifica el caracter que sigue al que actualmente se esta leyendo
    private boolean verificarCaracterSiguiente(String tokenAnterior, String tokenEsperado) {

        return true;

    }

    //Valida si el token corresponde a un identificador valido
    public static boolean verificarPrimerCaracterDeUnIdentificador(String string, int numero) {

        if (string == null || string.isEmpty()) {
            return false;
        }

        if (string.matches("^[a-zA-Z_].*")) {
            System.out.println("315 verificarPrimeraCaracter Borrar " + "inicia con letra o _");
            return true;
        } else {
            System.out.println("318 verificarPrimeraCaracter Borrar " + auxiliares.Error.obtenerDescripcionDeError(200));
            registrarMensajeDeErrorEnProgramaEnPythonRevisado(200, numero);
            return false;
        }

    }

    //Verifica que el identificador sea una secuencia de letras y
    //numeros sin caracteres especiales a partir del segundo caracter
    public boolean verificarSecuenciaDeCaracteresDeUnIdentificador(String string, int numero) {
        // Verificar los caracteres restantes
        if (string == null || string.isEmpty()) {
            System.out.println("315 verificarPrimeraCaracter Borrar " + "inicia con letra o _");
            return false;
        }

        if (string.matches("[a-zA-Z0-9_]*$")) {
            System.out.println("335 verificarSecuenciaDeCaracteres Borrar " + "cadena de caracteres alfanumericos  " + string);

            return true;
        } else {
            System.out.println("338 metodo verificarSecuenciaDeCaracter Borrar " + auxiliares.Error.obtenerDescripcionDeError(201));
            registrarMensajeDeErrorEnProgramaEnPythonRevisado(201, numero);
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

    public static void registrarLineaAnalizadaEnProgramaPythonRevisado(String instruccion, int numeroDeLinea) {
        String formatoLinea1 = String.format("%05d", numeroDeLinea);
        programaEnPythonRevisado.add(formatoLinea1 + " " + instruccion);
    }

    public static void registrarMensajeDeErrorEnProgramaEnPythonRevisado(int numeroDeError, int numeroDeLinea) {
        programaEnPythonRevisado.add(String.format("%14s", "Error ") + numeroDeError
                + ". " + auxiliares.Error.obtenerDescripcionDeError(numeroDeError)
        );
    }

    public static void inicializarHistogramaOperadoresComparacion() {
        histograma.put("<", 0);
        histograma.put("<=", 0);
        histograma.put(">", 0);
        histograma.put(">=", 0);
        histograma.put("==", 0);
        histograma.put("!=", 0);
    }

    public static void incrementarCantidadOperadoresComparacion(String operador) {
        histograma.put(operador, histograma.get(operador) + 1);
    }

    public static void generarHistogramaOperadoresComparacion() {
        ArrayList<String> operadores = new ArrayList<String>(histograma.keySet());
        programaEnPythonRevisado.add("*************");
        for (String operador : operadores) {
            programaEnPythonRevisado.add(histograma.get(operador) + " Token " + operador);
        }
        //BORRAR
        System.out.println("**************************");
        for (String operador : operadores) {
            System.out.println(histograma.get(operador) + " Token " + operador);
        }
    }

}
