/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Proyecto_pruebas;

import clienteServidorTCP.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Carlos
 */
public class Cliente extends Observable implements Runnable {

    //para conectar con otros ordenadores es lo comentado
    //private String host;
    private int puerto;
    private String mensaje;
    
    public Cliente(int puerto, String mensaje){ //otro campo, host
        this.puerto = puerto;
        this.mensaje = mensaje;
        //this.host = host;
    }
    
    @Override
    public void run() {
        final String Host = "127.0.0.1";     
        try{
          Socket sc = new Socket(Host,puerto); //se pone el host del private
          ObjectInputStream ois = new ObjectInputStream(sc.getInputStream());
          while(true){
             Coordenadas coordenadas = (Coordenadas) ois.readObject();
             
             this.setChanged();
             this.notifyObservers(coordenadas);
             this.clearChanged();
          }
          
        } catch (IOException ex){
            Logger.getLogger(ServidorTCP_hilo.class.getName()).log(Level.SEVERE,null,ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
