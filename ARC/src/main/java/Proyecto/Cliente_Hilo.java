/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Proyecto;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
    
    public Cliente_Hilo(DataInputStream in,DataOutputStream out){
        this.in = in;
        this.out = out;
    }
    
    @Override
    public void run(){
        Scanner scanner = new Scanner(System.in);
        String mensaje;
        int opcion;
        boolean salir = false;
        try {
        while(!salir){
            System.out.println("1: Almacenar numero en el archivo");
            System.out.println("2: Numeros almacenados hasta el momento");
            System.out.println("3: Lista de numeros almacenados");
            System.out.println("4: La cantidad de numeros almacenados por el cliente");
            System.out.println("5: Archivo con numeros del cliente");
            System.out.println("6. Salir");
            
            opcion = scanner.nextInt();
            out.writeInt(opcion);
            
            switch(opcion){
                case 1:
                    int numeroAleatorio = generarNumeroAleatorio(1,100);
                    out.writeInt(numeroAleatorio);
                    break;
                case 2:
                    
                    break;
                case 3:
                    
                    break;
                case 4:
                    
                    break;
                case 5:
                    
                    break;
                case 6:
                    
                    break;
                default:
                    mensaje = in.readUTF();
                    System.out.println(mensaje);
                    break;
       
            }
        }
        } catch (IOException ex) {
            Logger.getLogger(Cliente_Hilo.class.getName()).log(Level.SEVERE, null, ex);
           }
    }
    public int generarNumeroAleatorio(int minimo, int maximo){
        return (int) Math.floor(Math.random()*(maximo-minimo+1)+minimo);
    }
}
