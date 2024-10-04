/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compilador;

import auxiliares.LineaDeContenido;
import auxiliares.MiError;
import auxiliares.TiposDeError;
import auxiliares.Token;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 *
 * @author A Montero
 */
public class Parser {

    private final List<List<Token>> listaDeTokens; //Almacena los tokens que se identifican
    //private List<String> programaEnPythonRevisado; //Almacena los tokens que se identifican

    private List<LineaDeContenido> listaContenidoFinal;
    private LineaDeContenido nuevoContenido;
    private List<MiError> erroresEncontrados;
    private List<String> archivoFinal;
    private TiposDeError tipos = new TiposDeError();
    private int cantidadDeComentarios;

    private Map<Integer, List<MiError>> erroresEncontradosMap;

    Parser(List<List<Token>> tokens, List<LineaDeContenido> programa, int cantidadDeComentarios, Map<Integer, List<MiError>> errores) {
        this.listaDeTokens = tokens;
        this.listaContenidoFinal = programa;
        this.cantidadDeComentarios = cantidadDeComentarios;
        this.erroresEncontradosMap = errores;
    }

    public List<String> analisisSintactico() throws IOException {
        System.out.println();
        System.out.println(" 40 ESTAMOS EN EL ANALISIS SINTACTICO ");
        System.out.println();

        boolean existeInstruccionAntesDeImport = false;

        Set<Integer> lineasConImport = new HashSet<>();

        for (List<Token> tokensEnLaLinea : listaDeTokens) {
            if (!tokensEnLaLinea.isEmpty()) {

                for (int i = 0; i < tokensEnLaLinea.size(); i++) {
                    int numeroLinea = 0;
                    Token tokenActual = tokensEnLaLinea.get(i);
                    switch (tokenActual.getTipoDeToken().toString()) {
                        case "PALABRA_RESERVADA":

                            OUTER:
                            switch (tokenActual.getLexema()) {
                                case "import":
                                    numeroLinea = tokenActual.getNumeroLinea();
                                    if (numeroLinea > 0) {
                                        String tknAnterior = obtenerTokenAnterior((numeroLinea - 1), 0);
                                        if (tknAnterior.length() > 0) {
                                            if (!tknAnterior.equals("import")) {

                                                int numeroError = 300;
                                                incluirErrorEncontrado(numeroLinea, numeroError);

                                                //USANDO HASHMAP PARA ERRORES ENCONTRADOS
                                                MiError e = new MiError(numeroLinea, numeroError, tipos.obtenerDescripcionDelError(numeroError));
                                                if (erroresEncontradosMap.containsKey((numeroLinea + 1))) {
                                                    if (erroresEncontradosMap.get((numeroLinea + 1)) != null) {
                                                        List<MiError> errores3 = erroresEncontradosMap.get((numeroLinea + 1));
                                                        errores3.add(e);
                                                    }
                                                } else {
                                                    List<MiError> errores3 = new ArrayList<>();
                                                    errores3.add(e);
                                                    erroresEncontradosMap.put((numeroLinea + 1), errores3);
                                                }

                                            } else {
                                                //Verificamos que el siguiente token en la linea sea un identificador valido
                                                System.out.println(" 78 ESTAMOS EN EL ANALISIS SINTACTICO " + "TOKEN ANTERIOR ES import");
                                            }
                                        }

                                    }

                                    break;
                                case "input":
                                    numeroLinea = tokenActual.getNumeroLinea();
                                    int indiceTokenInput = tokensEnLaLinea.indexOf(tokenActual);

                                    //Valida los tokens antes de input
                                    System.out.println("111 Indice de input " + tokensEnLaLinea.indexOf(tokenActual));
                                    validarOperadoresAntesDeInput(tokensEnLaLinea, numeroLinea, indiceTokenInput);

                                    //Valida que los parentesis esten balanceados
                                    boolean parentesisBalanceados = verificarParentesisBalanceados(tokensEnLaLinea, numeroLinea);
                                    if (parentesisBalanceados) {
                                        System.out.println("108 Parentesis balanceados:" + parentesisBalanceados);
                                    } else {
                                        System.out.println("108 Parentesis NO balanceados:" + parentesisBalanceados);

                                    }
                                    //Valida
                                    break;
                                default:
                                    existeInstruccionAntesDeImport = true;
                            }
                        case "ASIGNACION":
                            int indiceOperadorAsignacion = i;
                            //validarOperadorDeAsignacion(tokensEnLaLinea, tokenActual.getNumeroLinea(), indiceOperadorAsignacio);
                            break;
                        default:
                            existeInstruccionAntesDeImport = false;
                    }

                }

            }
        }
        System.out.println();
        System.out.println("LISTA CONTENIDO FINAL PARSE:");
        //imprimirListas(listaContenidoFinal);
        imprimirListasDeContenidoFinal(listaContenidoFinal);
        System.out.println();

        System.out.println();
        System.out.println("PROGRAMA EN PYTHON REVISADO:");
        //imprimirListas(listaContenidoFinal);
        List<String> programaRevisado = generarProgramaEnPythonRevisado(listaContenidoFinal);
        imprimirListas(programaRevisado);
        System.out.println();

        System.out.println();
        System.out.println("140");
        System.out.println("PARSER: Contenido del mapa de errores encontrados " + erroresEncontradosMap.size());
        for (Map.Entry<Integer, List<MiError>> entry : erroresEncontradosMap.entrySet()) {
            Integer key = entry.getKey();
            List<MiError> errorList = entry.getValue();
            System.out.println("Linea: " + key);
            for (MiError error : errorList) {
                System.out.println(error.getKey() + "  " + error.getDescripcion());
            }
        }
        System.out.println();

        return programaRevisado;
    }

    public String obtenerTokenAnterior(int numeroDeLinea, int posicion) {
        System.out.println();
        System.out.println();
        System.out.println("166 obtenerTokenAnterior-> " + listaDeTokens.get(numeroDeLinea).get(posicion).getLexema() + " numero de linea " + numeroDeLinea);
        System.out.println();
        if (listaDeTokens.get(numeroDeLinea).get(posicion).getLexema() != null) {
            return listaDeTokens.get(numeroDeLinea).get(posicion).getLexema();
        } else {
            return "";
        }
    }

    public String obtenerTokenSiguiente(int numeroDeLinea, int posicion) {
        System.out.println();
        System.out.println();
        System.out.println("170 obtenerTokenSiguiente-> " + listaDeTokens.get(numeroDeLinea).get(posicion).getLexema() + " numero de linea " + numeroDeLinea);
        System.out.println();
        if (listaDeTokens.get(numeroDeLinea).get(posicion).getLexema() != null) {
            return listaDeTokens.get(numeroDeLinea).get(posicion).getLexema();
        } else {
            return "";
        }

    }

    public void incluirErrorEncontrado(int numeroDeLinea, int numeroError) {
        MiError e = new MiError(numeroDeLinea, numeroError, tipos.obtenerDescripcionDelError(numeroError));
        for (LineaDeContenido linea : listaContenidoFinal) {
            if (linea.getNumeroDeLinea() == numeroDeLinea) {
                if (linea.getErroresEncontrados() == null) {
                    List<MiError> errores = new ArrayList<>();
                    errores.add(e);
                    linea.setErroresEncontrados(errores);
                } else {
                    linea.getErroresEncontrados().add(e);
                }
            }
        }
    }

    //Valida-> identificador, =, input, (, "string", )
    public void validarOperadoresAntesDeInput(List<Token> lineaDeTokens, int numeroDeLinea, int indiceTokenInput) {
        int numeroError = 0;
        String token_en_posicion_0 = "";
        switch (indiceTokenInput) {
            case 0:
                numeroError = 400;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
                numeroError = 401;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
                break;
            case 1:
                token_en_posicion_0 = lineaDeTokens.get(0).getTipoDeToken().toString();
                if (token_en_posicion_0.equals("IDENTIFICADOR")) { //Falta =
                    numeroError = 401;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                } else if (token_en_posicion_0.equals("ASIGNACION")) {//Falta ID
                    numeroError = 400;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                } else if (!(token_en_posicion_0.equals("IDENTIFICADOR") && token_en_posicion_0.equals("ASIGNACION"))) {
                    switch (token_en_posicion_0) {
                        case "DESCONOCIDO":
                            numeroError = 402;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            numeroError = 401;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            break;
                        case "PALABRA_RESERVADA":
                            numeroError = 404;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            numeroError = 401;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            break;
                        case "NUMERO_DECIMAL":
                        case "NUMERO_ENTERO":
                            numeroError = 405;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            numeroError = 401;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            break;
                        default:
                            numeroError = 400;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            numeroError = 401;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            break;
                    }
                }
                break;
            case 2:
                token_en_posicion_0 = lineaDeTokens.get(0).getTipoDeToken().toString();
                String token_en_posicion_1 = lineaDeTokens.get(1).getTipoDeToken().toString();
                if (!(token_en_posicion_0.equals("IDENTIFICADOR") && token_en_posicion_1.equals("ASIGNACION"))) {
                    numeroError = 406;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                } else if (!(token_en_posicion_0.equals("IDENTIFICADOR")) && token_en_posicion_1.equals("ASIGNACION")) {
                    switch (token_en_posicion_0) {
                        case "DESCONOCIDO":
                            numeroError = 402;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            numeroError = 401;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            break;
                        case "PALABRA_RESERVADA":
                            numeroError = 404;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            numeroError = 401;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            break;
                        case "NUMERO_DECIMAL":
                        case "NUMERO_ENTERO":
                            numeroError = 405;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            numeroError = 401;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            break;
                        default:
                            numeroError = 400;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            numeroError = 401;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            break;
                    }
                } else if (token_en_posicion_0.equals("IDENTIFICADOR") && !(token_en_posicion_1.equals("ASIGNACION"))) {
                    switch (token_en_posicion_0) {
                        case "DESCONOCIDO":
                            numeroError = 402;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            numeroError = 401;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            break;
                        case "PALABRA_RESERVADA":
                            numeroError = 404;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            numeroError = 401;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            break;
                        case "NUMERO_DECIMAL":
                        case "NUMERO_ENTERO":
                            numeroError = 405;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            numeroError = 401;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            break;
                        default:
                            numeroError = 400;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            numeroError = 401;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            break;
                    }
                }
                break;
            default:
                numeroError = 406;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
                break;
        }

    }

    //Verifica que los parentesis este balanceados 
    public boolean verificarParentesisBalanceados(List<Token> lineaDeTokens, int numeroDeLinea) {
        Stack<String> pila = new Stack<>();

        for (Token token : lineaDeTokens) {
            switch (token.getTipoDeToken().toString()) {
                case "PARENTESIS_IZQUIERDO":
                    pila.push("(");
                    break;

                case "PARENTESIS_DERECHO": {
                    if (pila.isEmpty()) {
                        return false;
                    }
                    String ultimoParentesis = pila.pop();
                    if (!coinciden(ultimoParentesis, ")")) {
                        return false;
                    }
                    break;
                }

                default:
                    break;
            }
        }

        return pila.isEmpty();
    }

    public boolean verificarCorchetesBalanceados(List<Token> lineaDeTokens, int numeroDeLinea) {
        Stack<String> pila = new Stack<>();

        for (Token token : lineaDeTokens) {
            switch (token.getTipoDeToken().toString()) {

                case "CORCHETE_IZQUIERDO":
                    pila.push("[");
                    break;

                case "CORCHETE_DERECHO": {
                    if (pila.isEmpty()) {
                        return false;
                    }
                    String ultimoParentesis = pila.pop();
                    if (!coinciden(ultimoParentesis, "]")) {
                        return false;
                    }
                    break;
                }
                default:
                    break;
            }
        }

        return pila.isEmpty();
    }

    private static boolean coinciden(String apertura, String cierre) {
        return (apertura.equals("(") && cierre.equals(")"))
                || (apertura.equals("[") && cierre.equals("]"));
    }

    private void parentesis(List<Token> lineaDeTokens, int numeroDeLinea) {
        Set<String> parentesis = new HashSet<>(Arrays.asList("(", ")"));

        int numeroError = 0;
        int ultimoIndiceLineaDeTokens = (lineaDeTokens.size() - 1);
        int posicion_parentesis_izquierdo = 0;
        int posicion_parentesis_derecho = 0;

        if (!lineaDeTokens.get(3).getTipoDeToken().toString().equals("PARENTESIS_IZQUIERDO")) {
            numeroError = 506;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }
        if (!lineaDeTokens.get(ultimoIndiceLineaDeTokens).getTipoDeToken().toString().equals("PARENTESIS_DERECHO")) {
            numeroError = 507;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }

    }

    /*
    public boolean verificarExistenciaDeAmbosParentesis(List<Token> lineaDeTokens, int numeroDeLinea) {
        Set<String> parentesis = new HashSet<>(Arrays.asList("(", ")"));

        int ultimoIndiceLineaDeTokens = (lineaDeTokens.size() - 1);
        String posicion_parentesis_izquierdo = lineaDeTokens.get(3).getTipoDeToken().toString();
        String posicion_parentesis_derecho = lineaDeTokens.get(ultimoIndiceLineaDeTokens).getTipoDeToken().toString();

        for (Token token : lineaDeTokens) {
            if (token.getTipoDeToken().toString().equals("PARENTESIS_IZQUIERDO")) {

            }
        }
        if (lineaDeTokens.containsAll(parentesis)) {
            System.out.println("La lista contiene ambos elementos del set.");
            return true;
        } else {
            System.out.println("La lista no contiene ambos elementos del set.");
            return false;
        }
    }
     */
 /*
    public static String encontrarParentesis(List<Token> lineaDeTokens, int numeroDeLinea, int indiceTokenInput) {

        Set<String> parentesis = new HashSet<>(Arrays.asList("(", ")"));
        String encontrado = null;
        int count = 0;

        for (String elemento : parentesis) {
            if (lineaDeTokens.contains(elemento)     {
                encontrado = elemento;
                count++;
            }
        }
        String parentesisEncontrado = count == 1 ? encontrado : null;

        if (parentesisEncontrado != null) {
            System.out.println("La lista contiene solo uno de los elementos del set: " + parentesisEncontrado);
            return parentesisEncontrado;
        } else {
            System.out.println("La lista no contiene exactamente uno de los elementos del set.");
            return "";
        }

    }
     */
    public List<String> generarProgramaEnPythonRevisado(List<LineaDeContenido> listaContenidoFinal) {
        List<String> programaEnPythonRevisado = new ArrayList<>();

        for (int i = 0; i < listaContenidoFinal.size(); ++i) {
            int numeroDeLinea = listaContenidoFinal.get(i).getNumeroDeLinea() + 1;
            String instruccion = listaContenidoFinal.get(i).getInstruccion();

            String formatoLinea = String.format("%05d", numeroDeLinea);
            String formatoError = String.format("%14s", "Error ");

            if (listaContenidoFinal.get(i).getErroresEncontrados() == null) {
                programaEnPythonRevisado.add(formatoLinea + " " + instruccion);
            } else {
                programaEnPythonRevisado.add(formatoLinea + " " + instruccion);
                for (int k = 0; k < listaContenidoFinal.get(i).getErroresEncontrados().size(); ++k) {
                    int numeroDeError = listaContenidoFinal.get(i).getErroresEncontrados().get(k).getKey();
                    String descripcion = listaContenidoFinal.get(i).getErroresEncontrados().get(k).getDescripcion();
                    programaEnPythonRevisado.add(formatoError + numeroDeError + ". " + descripcion);
                }
            }
        }

        HashMap<String, Integer> histograma = contarLaCantidadOperadoresComparacion();

        ArrayList<String> operadores = new ArrayList<String>(histograma.keySet());
        programaEnPythonRevisado.add("=================================================");
        for (String operador : operadores) {
            programaEnPythonRevisado.add(histograma.get(operador) + " Token " + operador);
        }
        programaEnPythonRevisado.add(this.cantidadDeComentarios + " lineas de comentario");

        return programaEnPythonRevisado;
    }

    //Recorre una lista de string y la imprime BORRAR
    public static void imprimirListas(List<String> contenidoArchivo) {
        for (String linea : contenidoArchivo) {
            System.out.println(linea);
        }
    }

    public static void imprimirListasDeContenidoFinal(List<LineaDeContenido> contenido) {
        for (LineaDeContenido linea : contenido) {
            System.out.println(linea);
        }
    }

    public HashMap<String, Integer> inicializarHistogramaOperadoresComparacion() {
        HashMap<String, Integer> histograma = new HashMap<String, Integer>(); //Almacena la cuenta de los operadores de comparación
        histograma.put(">", 0);
        histograma.put(">=", 0);
        histograma.put("<", 0);
        histograma.put("<=", 0);
        histograma.put("==", 0);
        histograma.put("!=", 0);

        return histograma;

    }

    public HashMap<String, Integer> contarLaCantidadOperadoresComparacion() {

        HashMap<String, Integer> histograma = inicializarHistogramaOperadoresComparacion();

        for (List lista : listaDeTokens) {
            for (Object token : lista) {
                Token tkn = (Token) token;
                switch (tkn.getTipoDeToken().toString().trim()) {
                    case "MAYOR_QUE":
                        histograma.put(">", histograma.get(">") + 1);
                        break;
                    case "MAYOR_O_IGUAL_QUE":
                        histograma.put(">=", histograma.get(">=") + 1);
                        break;
                    case "MENOR_QUE":
                        histograma.put("<", histograma.get("<") + 1);
                        break;
                    case "MENOR_O_IGUAL_QUE":
                        histograma.put("<=", histograma.get("<=") + 1);
                        break;
                    case "IGUAL_QUE":
                        histograma.put("==", histograma.get("==") + 1);
                        break;
                    case "DIFERENTE_QUE":
                        histograma.put("!=", histograma.get("!=") + 1);
                        break;
                    default:
                        break;

                }

            }
        }
        return histograma;

    }

    public Map<Integer, List<Error>> inicializarHashMapErroresEncontrados() {

        Map<Integer, List<Error>> erroresEncontradosMap = new HashMap<>();

        return erroresEncontradosMap;

    }

}
