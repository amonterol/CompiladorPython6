/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auxiliares;

/**
 *
 * @author A Montero
 */
public class MiError {
    private int numeroLinea;
    private int key;
    private String descripcion;

    public MiError(int numeroLinea, int key, String descripcion) {
        this.numeroLinea = numeroLinea;
        this.key = key;
        this.descripcion = descripcion;
    }

    public int getNumeroLinea() {
        return numeroLinea;
    }

    public void setNumeroLinea(int numeroLinea) {
        this.numeroLinea = numeroLinea;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

   
    
}
