/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Proyecto;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Carlos
 */
public class Servidor_Hilo extends Thread {
    private DataInputStream in;
    private DataOutputStream out;
    private String nombreCliente;
    
    public Servidor_Hilo(DataInputStream in, DataOutputStream out, String nombreCliente){
        this.in = in;
        this.out = out;
        this.nombreCliente = nombreCliente;
    }
    
    @Override
    public void run(){
        int opcion;
        String mensaje;
        File f = new File("numeros.txt");
        
        while(true){
            try {
                opcion = in.readInt();
                out.writeInt(opcion);
                switch(opcion){
                    case 1:
                    int numeroAleatorio = in.readInt();
                    escribirNumeroAleatorio(f, numeroAleatorio);
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
                    out.writeUTF("Solo numeros del 1 al 6");
                    break;
                }
            } catch (IOException ex) {
                Logger.getLogger(Servidor_Hilo.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    }
    public void escribirNumeroAleatorio(File f, int numeroAleatorio){
        //funcion
    }
    
}
