/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auxiliares;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author A Montero
 */
public class LineaArchivoSalida {

    private int numeroLinea;
    private String instruccionOriginal;
    private List<Error> erroresEnLinea = new ArrayList<>();

    public LineaArchivoSalida(int numeroLinea, String instruccionOriginal) {
        this.numeroLinea = numeroLinea;
        this.instruccionOriginal = instruccionOriginal;
    }

    public LineaArchivoSalida() {
       
    }

    public int getNumeroLinea() {
        return numeroLinea;
    }

    public void setNumeroLinea(int numeroLinea) {
        this.numeroLinea = numeroLinea;
    }

    public String getInstruccionOriginal() {
        return instruccionOriginal;
    }

    public void setInstruccionOriginal(String instruccionOriginal) {
        this.instruccionOriginal = instruccionOriginal;
    }

    public List<Error> getErroresEnLinea() {
        return erroresEnLinea;
    }

    public void setErroresEnLinea(List<Error> erroresEnLinea) {
        this.erroresEnLinea = erroresEnLinea;
    }

    @Override
    public String toString() {
        return "ArchivoSalida{" + "numeroLinea=" + numeroLinea + ", instruccionOriginal=" + instruccionOriginal + ", erroresEnLinea=" + erroresEnLinea + '}';
    }
    
    

}
