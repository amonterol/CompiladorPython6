/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auxiliares;

import java.util.List;

/**
 *
 * @author A Montero
 */
public class LineaDeContenido {

    private int numeroDeLinea;
    private String instruccion;
    private List<MiError> erroresEncontrados;

    public LineaDeContenido(int numeroDeLinea, String instruccion, List<MiError> erroresEncontrados) {
        this.numeroDeLinea = numeroDeLinea;
        this.instruccion = instruccion;
        this.erroresEncontrados = erroresEncontrados;
    }

    public LineaDeContenido(int numeroDeLinea, String instruccion) {
        this.numeroDeLinea = numeroDeLinea;
        this.instruccion = instruccion;
    }

    public LineaDeContenido() {
    }

    public int getNumeroDeLinea() {
        return numeroDeLinea;
    }

    public void setNumeroDeLinea(int numeroDeLinea) {
        this.numeroDeLinea = numeroDeLinea;
    }

    public String getInstruccion() {
        return instruccion;
    }

    public void setInstruccion(String instruccion) {
        this.instruccion = instruccion;
    }

    public List<MiError> getErroresEncontrados() {
        return erroresEncontrados;
    }

    public void setErroresEncontrados(List<MiError> erroresEncontrados) {
        this.erroresEncontrados = erroresEncontrados;
    }

    public void agregarNuevoError(MiError nuevoError) {
        erroresEncontrados.add(nuevoError);
    }

    @Override
    public String toString() {
        String errores = " ";
        if (erroresEncontrados != null) {
            for (int k = 0; k < erroresEncontrados.size(); ++k) {
                errores = errores + getErroresEncontrados().get(k).getDescripcion() + "\n";
            }
        } else {
            errores = " No hay errores en la linea ";
        }
        return "LineaDeContenido{" + "numeroDeLinea=" + numeroDeLinea + ", instruccion=" + instruccion + ", erroresEncontrados=" + errores + '}';
    }

}
