/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auxiliares;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author abmon
 */
public class Archivo {
    //Lee el programa a analizar y convierte cada linea del programa en una entrada de una lista.
    public List leerArchivo(String nombreArchivo) throws IOException {

       List<String> lineas = new ArrayList<>();
        try {
            // Obtener el directorio actual del programa
            String directorioActual = new File(".").getCanonicalPath();
            // Crear la ruta completa del archivo
            String rutaArchivo = directorioActual + File.separator + nombreArchivo;
            
            try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    lineas.add(linea);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lineas;
    }

    public void escribirArchivo(List<String> contenido, String nombreArchivo) {
        try {
            // Obtener el directorio actual del programa
            String directorioActual = new File(".").getCanonicalPath();
            // Crear la ruta completa del archivo
            String rutaArchivo = directorioActual + File.separator + nombreArchivo;
            
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo))) {
                for (String linea : contenido) {
                    bw.write(linea);
                    bw.newLine();
                }
            }
            System.out.println("Archivo escrito exitosamente en: " + rutaArchivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void imprimirArchivo(String nombreArchivo) throws IOException {
        try {
            // Obtener el directorio actual del programa
            String directorioActual = new File(".").getCanonicalPath();
            // Crear la ruta completa del archivo
            String rutaArchivo = directorioActual + File.separator + nombreArchivo;
            
            try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    System.out.println(linea);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
