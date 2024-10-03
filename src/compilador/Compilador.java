/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package compilador;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import utilitarios.Archivo;
import utilitarios.Error;

/**
 *
 * @author A Montero
 */

public class Compilador {

    public static final Error listaDeErrores = new Error();
    public static String archivoDeEntrada;
    public static String archivoDeSalida;
    public static List<String> contenidoArchivo = new ArrayList();

    public static void main(String[] args) {

        if (validarArchivoParaAnalizar(args)) {
            archivoDeEntrada = args[0]; //Solo hay un archivo para analizar
            if (validarExtensionArchivoParaAnalizar(archivoDeEntrada)) {
                if (!validarContenidoArchivoParaAnalizar(archivoDeEntrada)) {
                    try {
                        Archivo archivo = new Archivo();

                        archivoDeEntrada = args[0]; //Solo hay un archivo para analizar
                        archivoDeSalida = archivoDeEntrada.replace(".py", "-error.log");

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

                        archivo.escribirArchivo(contenidoArchivo, archivoDeSalida);
                        System.out.println("""
                                        
                                       3 CLASE COMIPILADOR BORRAR: ESTE ES EL CONTENIDO DEL ARCHIVO log
                                       
                                       """);
                        archivo.imprimirArchivo(archivoDeSalida); //BORRAR

                        Lexer lexer = new Lexer(contenidoArchivo);
                        lexer.analizadorLexico(contenidoArchivo);
                       


                        /*
                   
                    Parser parser = new Parser(lexer.getTokens());
                    parser.parse();
                    ErrorChecker errorChecker = new ErrorChecker(parser.getASTNodes(), parser.getSymbolTable());
                    errorChecker.checkErrors();
                         */
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

    //Verifica que exista un solo archivo a analizar 
    public static boolean validarArchivoParaAnalizar(String[] args) {

        if (args.length == 0) {
            JOptionPane.showMessageDialog(null, listaDeErrores.obtenerDescripcionDeError(100), "Error en archivo", JOptionPane.WARNING_MESSAGE);
            System.out.println(" 4 CLASE COMIPILADOR BORRAR " + listaDeErrores.obtenerDescripcionDeError(100));
            return false;
        } else if (args.length > 1) {
            JOptionPane.showMessageDialog(null, listaDeErrores.obtenerDescripcionDeError(101), "Error en archivo", JOptionPane.WARNING_MESSAGE);
            System.out.println("5 CLASE COMIPILADOR BORRAR " + listaDeErrores.obtenerDescripcionDeError(101));
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
            JOptionPane.showMessageDialog(null, listaDeErrores.obtenerDescripcionDeError(102), "Código Fuente", JOptionPane.WARNING_MESSAGE);
            System.out.println("8 CLASE COMIPILADOR BORRAR " + listaDeErrores.obtenerDescripcionDeError(102));
            return false;
        }
    }

    public static boolean validarContenidoArchivoParaAnalizar(String archivo) {

        if (archivo.isEmpty() || archivo.isBlank()) {
            JOptionPane.showMessageDialog(null, listaDeErrores.obtenerDescripcionDeError(103), "Error en archivo", JOptionPane.WARNING_MESSAGE);
            System.out.println(" 10 CLASE COMPILADOR BORRAR Archivo NO CONTIENE INFORMACION");
            return true;
        } else {
            return false;
        }

    }

    //Recorre una lista de string y la imprime BORRAR
    public static void imprimirListas(List<String> contenidoArchivo) {
        for (String linea : contenidoArchivo) {
            System.out.println(linea);
        }
    }

}
