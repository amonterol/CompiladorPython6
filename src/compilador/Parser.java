/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compilador;

import auxiliares.Archivo;
import auxiliares.LineaDeContenido;
import auxiliares.MiError;
import auxiliares.TipoDeToken;
import auxiliares.TiposDeError;
import auxiliares.Token;
import static compilador.Compilador.archivoDeSalida;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
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

    Parser(List<List<Token>> tokens, List<LineaDeContenido> programa, int cantidadDeComentarios) {
        this.listaDeTokens = tokens;
        this.listaContenidoFinal = programa;
        this.cantidadDeComentarios = cantidadDeComentarios;
    }

    public List<String> analisisSintactico() throws IOException {
        System.out.println();
        System.out.println(" 40 ESTAMOS EN EL ANALISIS SINTACTICO ");
        System.out.println();

        boolean existeInstruccionAntesDeImport = false;

        Set<Integer> lineasConImport = new HashSet<>();

        for (List<Token> tokens : listaDeTokens) {
            if (!tokens.isEmpty()) {

                for (int i = 0; i < tokens.size(); i++) {
                    Token tokenActual = tokens.get(i);

                    switch (tokenActual.getTipoDeToken().toString()) {
                        case "PALABRA_RESERVADA":

                            OUTER:
                            switch (tokenActual.getLexema()) {
                                case "import":

                                    if (tokenActual.getNumeroLinea() > 0) {

                                        if (!obtenerTokenAnterior(tokenActual.getNumeroLinea()).equals("import")) {
                                            //insertarMensajeDeErrorEnProgramaEnPythonRevisado(300, (tokenActual.getNumeroLinea()));

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

                                        } else {
                                            //Verificamos que el siguiente token en la linea sea un identificador valido
                                            System.out.println(" 78 ESTAMOS EN EL ANALISIS SINTACTICO " + "TOKEN ANTERIOR ES import");
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
        
        return  programaRevisado;
    }

    public String obtenerTokenAnterior(int linea) {
        return listaDeTokens.get(linea).get(0).getLexema();

    }


    public List<String> generarProgramaEnPythonRevisado(List<LineaDeContenido> listaContenidoFinal) {
        List<String> programaEnPythonRevisado = new ArrayList<>();
       
        for (int i = 0; i < listaContenidoFinal.size(); ++i) {
            int numeroDeLinea = listaContenidoFinal.get(i).getNumeroDeLinea() + 1;
            String instruccion = listaContenidoFinal.get(i).getInstruccion();
            
            String formatoLinea = String.format("%05d", numeroDeLinea );
            String formatoError = String.format("%14s", "Error ");
            
            if (listaContenidoFinal.get(i).getErroresEncontrados() == null) {
                programaEnPythonRevisado.add(formatoLinea + " " + instruccion);
            } else {
                programaEnPythonRevisado.add(formatoLinea + " " + instruccion);
                for (int k = 0; k < listaContenidoFinal.get(i).getErroresEncontrados().size(); ++k) {
                    int numeroDeError = listaContenidoFinal.get(i).getErroresEncontrados().get(k).getKey();
                    String descripcion = listaContenidoFinal.get(i).getErroresEncontrados().get(k).getDescripcion();
                    programaEnPythonRevisado.add(formatoError +   numeroDeError + ". " + descripcion);
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
       
        HashMap<String, Integer> histograma =  inicializarHistogramaOperadoresComparacion();

        for (List lista : listaDeTokens) {
            for (Object token : lista) {
                Token tkn = (Token) token;
                switch(tkn.getTipoDeToken().toString().trim()){
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

   
}
