/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Proyecto;

/**
 *
 * @author Carlos
 */
public class Coordenadas {
    private int x, y, z;
    
    public Coordenadas(){
        x = generarNumeroAleatorio(0,100);
        y = generarNumeroAleatorio(0,100);
        z = generarNumeroAleatorio(0,100);
    }
    
    public int generarNumeroAleatorio(int minimo, int maximo){
        return (int) Math.floor(Math.random()*(maximo-minimo+1)+minimo);
    }
    
    public void moverse(){
        x += generarNumeroAleatorio(0,100);
        x -= generarNumeroAleatorio(0,100);
        y += generarNumeroAleatorio(0,100);
        y -= generarNumeroAleatorio(0,100);
        z += generarNumeroAleatorio(0,100);
        z -= generarNumeroAleatorio(0,100);
    }
}
