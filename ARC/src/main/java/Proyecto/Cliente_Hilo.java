/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Proyecto;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Carlos
 */
public class Cliente_Hilo extends Thread {
    private DataInputStream in;
    private DataOutputStream out;
    private int iteraciones;
    private Coordenadas coordenadas;
    
    //Se crea el hilo
    public Cliente_Hilo(DataInputStream in,DataOutputStream out, int iteraciones){  
        this.in = in;
        this.out = out;
        this.iteraciones = iteraciones;
        this.coordenadas = new Coordenadas();
    }
    
    //Aqui se mueve las iteraciones que se le marque y avisa, molaría que mandase un object porque 
    //nos soluciona bastante la vida. Está puesta la forma de mandar un object en proyecto_prueba.
    @Override
    public void run(){
        for(int i = 0; i < iteraciones; i++){
            coordenadas.moverse();
            
            //out.writeObject(coordenadas);
        }

        
    }
}

