/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compilador;

import auxiliares.TipoDeToken;
import auxiliares.Token;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author A Montero
 */
public class Parser {

    private List<List<Token>> listaDeTokens; //Almacena los tokens que se identifican
    private List<String> programaEnPythonRevisado; //Almacena los tokens que se identifican

    Parser() {

    }

    Parser(List<List<Token>> lista, List<String> programa) {
        this.listaDeTokens = lista;
        this.programaEnPythonRevisado = programa;
    }

    public List<String> analisisSintactico() throws IOException {
        boolean existeInstruccionAntesDeImport = false;
        
        Set<Integer> lineasConImport = new HashSet<>();

        for (List<Token> tokens : listaDeTokens) {
            if (!tokens.isEmpty()) {
                //for (Token tokenActual : tokens)

                for (int i = 0; i < tokens.size(); i++) {
                    Token tokenActual = tokens.get(i);

                    switch (tokenActual.getTipoDeToken().toString()) {
                        case "PALABRA_RESERVADA":

                            OUTER:
                            switch (tokenActual.getLexema()) {
                                case "import":
                                    if (tokenActual.getNumeroLinea() != 1) {
                                        if (!obtenerTokenAnterior(tokenActual.getNumeroLinea() - 1).equals("import")) {
                                            insertarMensajeDeErrorEnProgramaEnPythonRevisado(300, (tokenActual.getNumeroLinea()));
                                            System.out.println();
                                            System.out.println(" 55 Error se inserta en linea  " + (tokenActual.getNumeroLinea()));
                                            System.out.println();
                                        } else {
                                            //Verificamos que el siguiente token en la linea sea un identificador valido
                                        }
                                    }
                                    break;
                                case "input":

                                    break;
                                default:
                                    existeInstruccionAntesDeImport = true;
                            }
                        case "ASIGNACION":
                            break;
                        default:
                            existeInstruccionAntesDeImport = false;
                    }

                }

            }
        }
        System.out.println();
        imprimirListas(programaEnPythonRevisado);
        System.out.println();
        return null;
    }

    public String obtenerTokenAnterior(int linea) {
        return listaDeTokens.get(linea).get(0).getLexema();

    }

    public void insertarMensajeDeErrorEnProgramaEnPythonRevisado(int numeroDeError, int numeroDeLinea) {
        programaEnPythonRevisado.add(numeroDeLinea, String.format("%14s", "Error ") + numeroDeError
                + ". " + auxiliares.Error.obtenerDescripcionDeError(numeroDeError));
    }

    public void registrarMensajeDeErrorEnProgramaEnPythonRevisado(int numeroDeError, int numeroDeLinea) {
        programaEnPythonRevisado.add(String.format("%14s", "Error ") + numeroDeError
                + ". " + auxiliares.Error.obtenerDescripcionDeError(numeroDeError)
                + String.format("[%s%d]", "Linea ", numeroDeLinea));
    }

    //Recorre una lista de string y la imprime BORRAR
    public static void imprimirListas(List<String> contenidoArchivo) {
        for (String linea : contenidoArchivo) {
            System.out.println(linea);
        }
    }
}
