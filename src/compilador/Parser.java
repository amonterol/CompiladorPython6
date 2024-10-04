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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
                    Token tokenActual = tokensEnLaLinea.get(i);
                    switch (tokenActual.getTipoDeToken().toString()) {
                        case "PALABRA_RESERVADA":

                            OUTER:
                            switch (tokenActual.getLexema()) {
                                case "import":
                                    int numeroLinea = tokenActual.getNumeroLinea();
                                    if (numeroLinea > 0) {
                                        String tknAnterior = obtenerTokenAnterior((numeroLinea - 1), 0);
                                        if (!tknAnterior.equals("import")) {

                                            int numeroError = 300;
                                            MiError e = new MiError(numeroLinea, numeroError, tipos.obtenerDescripcionDelError(300));

                                            for (LineaDeContenido linea : listaContenidoFinal) {
                                                if (linea.getNumeroDeLinea() == numeroLinea) {
                                                    if (linea.getErroresEncontrados() == null) {
                                                        List<MiError> errores = new ArrayList<>();
                                                        errores.add(e);
                                                        linea.setErroresEncontrados(errores);
                                                    } else {
                                                        linea.getErroresEncontrados().add(e);
                                                    }

                                                }
                                            }

                                            //USANDO HASHMAP PARA ERRORES ENCONTRADOS
                                            if (erroresEncontradosMap.containsKey((numeroLinea + 1))) {
                                                if (erroresEncontradosMap.get((numeroLinea + 1)) != null) {
                                                    List<MiError> errores3 = erroresEncontradosMap.get((numeroLinea + 1));
                                                    errores3.add(new MiError(numeroLinea, numeroError, tipos.obtenerDescripcionDelError(numeroError)));
                                                }
                                            } else {
                                                List<MiError> errores3 = new ArrayList<>();
                                                errores3.add(new MiError(numeroLinea, numeroError, tipos.obtenerDescripcionDelError(numeroError)));
                                                erroresEncontradosMap.put((numeroLinea + 1), errores3);
                                            }

                                        } else {
                                            //Verificamos que el siguiente token en la linea sea un identificador valido
                                            System.out.println(" 78 ESTAMOS EN EL ANALISIS SINTACTICO " + "TOKEN ANTERIOR ES import");
                                        }
                                    }

                                    break;
                                case "input":
                                    int numeroDeLinea = tokenActual.getNumeroLinea();
                                    //validarInput();
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
        return listaDeTokens.get(numeroDeLinea).get(posicion).getLexema();

    }

    public String obtenerTokenSiguiente(int numeroDeLinea, int posicion) {
        if (listaDeTokens.get(numeroDeLinea).get(posicion).getLexema() != null) {
            return listaDeTokens.get(numeroDeLinea).get(posicion).getLexema();
        } else {
            return " ";
        }

    }

    //Valida-> identificador = identificador | numero | input
    public void validarInput(List<Token> lineaDeTokens, int numeroDeLinea, int indiceDelOperador) {
        Token tokenAnterior = new Token();
        Token tokenSiguiente = new Token();

        if (!lineaDeTokens.isEmpty()) {
            if (indiceDelOperador > 0) {
                tokenAnterior = lineaDeTokens.get((indiceDelOperador - 1));
            } else {
                MiError e = new MiError(numeroDeLinea, 300, tipos.obtenerDescripcionDelError(300));
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
            if (indiceDelOperador < lineaDeTokens.size()) {
                tokenAnterior = lineaDeTokens.get((indiceDelOperador + 1));
            } else {

            }

        } else {

        }

        /*
         MiError e = new MiError(tokenActual.getNumeroLinea(), 300, tipos.obtenerDescripcionDelError(300));
         for (LineaDeContenido linea : listaContenidoFinal) {
                                                if (linea.getNumeroDeLinea() == tokenActual.getNumeroLinea()) {
                                                    if (linea.getErroresEncontrados() == null) {
                                                        List<MiError> errores = new ArrayList<>();
                                                        errores.add(e);
                                                        linea.setErroresEncontrados(errores);
                                                    } else {
                                                        linea.getErroresEncontrados().add(e);
                                                    }

                                                }
                                            }
         */
    }

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
