/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package compilador;

import auxiliares.LineaDeContenido;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import auxiliares.Archivo;
import auxiliares.TiposDeError;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author A Montero
 */
public class Compilador {

    public static final Error listaDeErrores = new Error();
    public static String archivoDeEntrada;
    public static String archivoDeSalida;
    public static List<String> contenidoArchivo = new ArrayList();
    public static TiposDeError tiposError = new TiposDeError();

    public static void main(String[] args) {

        if (validarArchivoParaAnalizar(args)) {
            archivoDeEntrada = args[0]; //Solo hay un archivo para analizar
            if (validarExtensionArchivoParaAnalizar(archivoDeEntrada)) {
                if (validarContenidoArchivoParaAnalizar(archivoDeEntrada)) {
                    try {
                        Archivo archivo = new Archivo();

                        archivoDeEntrada = args[0]; //Solo hay un archivo para analizar
                        archivoDeSalida = archivoDeEntrada.replace(".py", "-errores.log");

                        //Lee el archivo para analizar y crea una lista de array con cada linea del archivo para analizar
                        try {
                            contenidoArchivo = archivo.leerArchivo(archivoDeEntrada);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        System.out.println("""
                                       
                                       2 CLASE COMIPILADOR BORRAR: ESTE ES EL CONTENIDO DEL ARCHIVO                                       
                                       """);

                        imprimirListas(contenidoArchivo); //BORRAR

                       
                        

                        Lexer lexer = new Lexer(contenidoArchivo);
                        lexer.analizadorLexico();

                        Parser parser = new Parser(lexer.getListaDeTokens(), lexer.getListaContenidoFinal(), lexer.getCantidadComentarios(), lexer.getErroresEncontradosMap());
                        List<String>  programaRevisado = parser.analisisSintactico();

                        System.out.println("""
                                        
                                       3 CLASE COMIPILADOR BORRAR: ESTE ES EL CONTENIDO DEL ARCHIVO log
                                       
                                       """);
                        archivo.escribirArchivo( programaRevisado, archivoDeSalida);
                        archivo.imprimirArchivo(archivoDeSalida); //BORRAR
                 
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(null, "Error al leer el archivo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    System.exit(0);
                }

            } else {
                System.exit(0);
            }

        } else {
            System.exit(0);
        }

    } // fin metodo main

    public static void imprimirListasDeContenidoFinal(List<LineaDeContenido> contenido) {
        for (LineaDeContenido linea : contenido) {
            System.out.println(linea);
        }
    }

    //Verifica que exista un solo archivo a analizar 
    public static boolean validarArchivoParaAnalizar(String[] args) {

        if (args.length == 0) {
            JOptionPane.showMessageDialog(null, tiposError.obtenerDescripcionDelError(100), "Error en archivo", JOptionPane.WARNING_MESSAGE);
            //System.out.println(" 4 CLASE COMIPILADOR BORRAR " + tiposError.obtenerDescripcionDelError(100));
            return false;
        } else if (args.length > 1) {
            JOptionPane.showMessageDialog(null, tiposError.obtenerDescripcionDelError(101), "Error en archivo", JOptionPane.WARNING_MESSAGE);
            System.out.println("5 CLASE COMIPILADOR BORRAR " + tiposError.obtenerDescripcionDelError(101));
            return false;
        } else {
            System.out.println("6 CLASE COMIPILADOR BORRAR: Este es el archivo para analizar: " + args[0]);
            return true;

        }

    }

    //Verifica que la extension del archivo a analizar tenga extension .py
    public static boolean validarExtensionArchivoParaAnalizar(String archivo) {
        System.out.println("7 CLASE COMIPILADOR CLASE COMIPILADOR BORRAR: Archivo con extension correcta: " + archivo.toLowerCase().endsWith(".py"));
        if (archivo.toLowerCase().endsWith(".py")) {
            return true;
        } else {
            JOptionPane.showMessageDialog(null, tiposError.obtenerDescripcionDelError(102), "Código Fuente", JOptionPane.WARNING_MESSAGE);
            System.out.println("8 CLASE COMIPILADOR BORRAR " + tiposError.obtenerDescripcionDelError(102));
            return false;
        }
    }

    //Valida que el archivo adjunto tenga algun contenido
    public static boolean validarContenidoArchivoParaAnalizar(String archivo) {
        Archivo adjunto = new Archivo();
        List<String> lineas = new ArrayList<>();
        try {
            lineas = adjunto.leerArchivo(archivo);
        } catch (IOException ex) {
            Logger.getLogger(Compilador.class.getName()).log(Level.SEVERE, null, ex);
        }
        int contador = 0;
        for(String linea : lineas){
            if(!(linea.length() == 0)){
                ++contador;
            }
        }
        
        if (contador > 0) {
            //Contiene informacion
            return true;
        } else {
            //No contiene informacion
            System.out.println("111 Error en archivo adjunto. " + tiposError.obtenerDescripcionDelError(103) + " " + lineas.isEmpty());
            return false;
        }

    }
    
      public static void generarArchivoDeSalida(List<String> programaEnPythonRevisado) {
        auxiliares.Archivo archivo = new auxiliares.Archivo();
        archivo.escribirArchivo(programaEnPythonRevisado, archivoDeSalida);

    }
    

    //Recorre una lista de string y la imprime BORRAR
    public static void imprimirListas(List<String> contenidoArchivo) {
        for (String linea : contenidoArchivo) {
            System.out.println(linea);
        }
    }

}
