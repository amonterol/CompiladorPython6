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

    private List<Token> listaDeTokens; //Almacena los tokens que se identifican
    private List<String> programaEnPythonRevisado; //Almacena los tokens que se identifican

    Parser() {

    }

    Parser(List<Token> tokens, List<String> programa) {
        this.listaDeTokens = tokens;
        this.programaEnPythonRevisado = programa;
    }

    public List<String> analisisSintactico() throws IOException {
        boolean existeInstruccionAntesDeImport = false;
        int numeroLinea = 0;
        Set<Integer> lineasConImport = new HashSet<>();
        
        
        if (!listaDeTokens.isEmpty()) {
            for (Token tokenActual : listaDeTokens) {
                switch (tokenActual.getTipoDeToken().toString()) {
                    case "PALABRA_RESERVADA":
                        
                        OUTER:
                        switch(tokenActual.getLexema()){
                            case "import":
                                int primeraLineaConTokenDiferenteDeImport =  obtenerPrimeraLineaConTokenDiferenteDeImport();
                                if(tokenActual.getNumeroLinea() > primeraLineaConTokenDiferenteDeImport){
                                    
                                }else {
                                    
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
        } else {

        }
        return null;
    }

    public boolean validarPosicionDeImport() {
       int primeraLineaConTokenDiferenteDeImport =  obtenerPrimeraLineaConTokenDiferenteDeImport();
       Set<Integer> lineasConInstruccionImport = obtenerLineasConImport();
       Integer strArray[] = lineasConInstruccionImport.toArray(new Integer[lineasConInstruccionImport.size()]);
       
       return primeraLineaConTokenDiferenteDeImport > strArray[0]; 
      
    }

    public void insertarMensajeDeErrorEnProgramaEnPythonRevisado(int numeroDeError, int numeroDeLinea) {
        programaEnPythonRevisado.add(numeroDeLinea + 1, String.format("%14s", "Error ") + numeroDeError
                + ". " + auxiliares.Error.obtenerDescripcionDeError(numeroDeError)
                + String.format("[%s%d]", "Linea ", numeroDeLinea));
    }

      public  Set<Integer> obtenerLineasConImport() {
        Set<Integer> lineasConImport =  new LinkedHashSet();
        Set<Integer> lineasProcesadas = new LinkedHashSet();

        for (Token token : this.listaDeTokens) {
            int numeroLinea = token.getNumeroLinea();

            // Solo procesamos el primer token de cada línea
            if (!lineasProcesadas.contains(numeroLinea)) {
                lineasProcesadas.add(numeroLinea);

                if (token.getTipoDeToken() == TipoDeToken.PALABRA_RESERVADA && token.getLexema().equals("import")) {
                    lineasConImport.add(numeroLinea);
                }
            }
        }
        System.out.println(lineasConImport);
        return lineasConImport;
    }
      
      public int obtenerPrimeraLineaConTokenDiferenteDeImport() {
        Set<Integer> lineasProcesadas = new HashSet<>();

        for (Token token : listaDeTokens) {
            int numeroLinea = token.getNumeroLinea();

            // Solo procesamos el primer token de cada línea
            if (!lineasProcesadas.contains(numeroLinea)) {
                lineasProcesadas.add(numeroLinea);

                if (!(token.getTipoDeToken() == TipoDeToken.PALABRA_RESERVADA && token.getLexema().equals("import"))) {
                    return numeroLinea;
                }
            }
        }

        // Si todos los primeros tokens son "import", devolvemos -1 o cualquier valor que indique que no se encontró
        return -1;
    }
}
