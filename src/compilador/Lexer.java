/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compilador;

import auxiliares.Archivo;

import auxiliares.LineaDeContenido;
import auxiliares.PalabraReservada;
import auxiliares.TipoDeToken;
import auxiliares.Token;
import auxiliares.MiError;
import auxiliares.TiposDeError;
import static compilador.Compilador.archivoDeSalida;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;

import java.util.StringTokenizer;

/**
 *
 * @author A Montero
 */
public class Lexer {

    private static List<String> programaEnPythonOriginal; //Almacena en cada entrada contiene una linea del codigo que se va a analizar
    private static List<List<Token>> listaDeTokens; //Almacena en cada entrada contiene una linea de codigo que se ha convertido en Tokens
    private static List<Token> tokens; //Almacena en cada entrada son los Tokens de la linea de codigo que se esta analizando

    //Lo cambiamos>
    private static List<String> programaEnPythonRevisado; //Almacena los tokens que se identifican

    private static HashMap<String, Integer> histograma = new HashMap<String, Integer>(); //Almacena la cuenta de los operadores de comparación

    //Manejo de errores en cada linea
    private List<LineaDeContenido> listaContenidoFinal = new ArrayList<>();
    private LineaDeContenido nuevoContenido;
    private List<MiError> erroresEncontrados;
    private List<String> archivoFinal;

    private int numeroLineaActual;   //Almacena el número de linea que se está analizando

    private int cantidadComentarios; //Almacena la cantidad de comentarios en el código

    public Lexer(List<String> content) {
        this.programaEnPythonOriginal = content;
        this.listaDeTokens = new ArrayList<>();
        this.programaEnPythonRevisado = new ArrayList<>();

        this.numeroLineaActual = 1;// Almancena en número de la línea que se esta leyento actualmente
        this.cantidadComentarios = 0;

    }

    public void analizadorLexico() throws IOException {

        // System.out.println("\n3 LEXER > INICIO LINEA DE CODIGO CONVERTIDA A CARACTERES  " + lineaDeCodigoActual);
        listaDeTokens = new ArrayList<>(); //Almacena cada linea del codigo como una lista de Token
        inicializarHistogramaOperadoresComparacion(); //Almacena el histograma que permite contar los operadores de comparacion

        //Itera sobre cada línea del archivo con el código fuente en Python que fue convertido en un List<String>
        for (int lineaActual = 0; lineaActual < programaEnPythonOriginal.size(); lineaActual++) {
            numeroLineaActual = lineaActual;
            erroresEncontrados = new ArrayList<>();//Almacena los errores encontrados en la linea que se lee

            String lineaDeCodigoActual = programaEnPythonOriginal.get(lineaActual).trim(); //Lee cada linea de codigo
            nuevoContenido = new LineaDeContenido(lineaActual,lineaDeCodigoActual.trim());
            listaContenidoFinal.add(nuevoContenido);

            //Agrega la linea que actualmente se analiza al archivo de salida 
            //registrarLineaAnalizadaEnProgramaPythonRevisado(lineaDeCodigoActual, numeroLineaActual);
            //listaContenidoFinal.add(nuevoContenido);
            System.out.println();
            System.out.println("76 La linea que estamos leyendo: " + lineaActual + " " + lineaDeCodigoActual);

            //Verifica si la linea esta en blanco 
            if (lineaDeCodigoActual.isBlank() || lineaDeCodigoActual.isEmpty()) {
                continue;
            }

            //Verifica si la linea es un comentario o si hay comentarios al final de una linea de código
            if (existeComentario(lineaDeCodigoActual)) {
                cantidadComentarios++;
                //Si hay un comentario despues de codigo,elimina la parte del comentario en la línea
                //para poder tokenizarlo
                lineaDeCodigoActual = lineaDeCodigoActual.split("#")[0].trim();
            }

            //Separa cada linea de codigo en Tokens
            StringTokenizer tokenizer = new StringTokenizer(lineaDeCodigoActual, " ()[]=<>*/+-:", true);

            System.out.println("94 Tokenizer to  La linea que estamos leyendo: " + lineaActual + " " + lineaDeCodigoActual);
            System.out.println();

            //tokenizer.toString();
            //Para poder ver hacia adelante convertimos el StringTokenizer en arreglo de Tokens
            /*
            String[] arregloDeTokens = new String[tokenizer.countTokens()];
            int j = 0;
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                arregloDeTokens[j++] = token;
            }
             */
            String[] arregloDeTokens = convertirStringTokenizerEnArregloDeStrings(tokenizer);

            //Almanacena los Token de una linea de codigo
            tokens = new ArrayList<Token>();
            //Itera sobre la linea de codigo ya convertida en arreglo de Strings
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
            
            listaDeTokens.add(tokens);
        } //Fin for que recorre linea por linea el programa en Python
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
        if(listaContenidoFinal != null){
            imprimirListasDeContenidoFinal(listaContenidoFinal);
        }
        
        System.out.println();
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

    //Valida si el token corresponde a un identificador valido
    public boolean verificarPrimerCaracterDeUnIdentificador(String string, int linea) {

        if (string == null || string.isEmpty()) {
            return false;
        }

        if (string.matches("^[a-zA-Z_].*")) {
            System.out.println("315 verificarPrimeraCaracter Borrar " + "inicia con letra o _");
            return true;
        } else {
            //System.out.println("300 verificarPrimeraCaracter Borrar " + auxiliares.TiposDeError.obtenerDescripcionDelError(200));
            int numeroError = 200;
            TiposDeError tipos = new TiposDeError();
            MiError e = new MiError(linea, numeroError, tipos.obtenerDescripcionDelError(numeroError));
            this.erroresEncontrados.add(e);

            nuevoContenido.setErroresEncontrados(erroresEncontrados);

            return false;
        }

    }

    //Verifica que el identificador sea una secuencia de letras y
    //numeros sin caracteres especiales a partir del segundo caracter
    public boolean verificarSecuenciaDeCaracteresDeUnIdentificador(String string, int linea) {

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
            MiError e = new MiError(linea, numeroError, tipos.obtenerDescripcionDelError(numeroError));
            this.erroresEncontrados.add(e);

            nuevoContenido.setErroresEncontrados(erroresEncontrados);

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
