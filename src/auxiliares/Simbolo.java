/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auxiliares;

/**
 *
 * @author A Montero
 */
public class Simbolo {

    private String tipo; //Almacena variables y funciones
    private String valor; //Nombre de la variable o de la función
    private int numeroLinea; //Número de línea donde se declaró por primera vez
    private String argumento; //Nombre del argumento

    
    public Simbolo(String tipo, String valor, int numeroLinea, String argumento) {
        this.tipo = tipo;
        this.valor = valor;
        this.numeroLinea = numeroLinea;
        this.argumento = argumento;
    }

    
    public String getArgumento() {
        return argumento;
    }

    public void setArgumento(String argumento) {
        this.argumento = argumento;
    }

    
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Object getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

   
    public int getNumeroLinea() {
        return numeroLinea;
    }

    public void setNumeroLinea(int numeroLinea) {
        this.numeroLinea = numeroLinea;
    }
}
