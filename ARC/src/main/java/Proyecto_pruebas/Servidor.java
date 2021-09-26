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
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Carlos
 */
public class Servidor implements Runnable {

    private ArrayList<Socket> clientes;
    private int puerto;
    
    public Servidor(int puerto){
        this.puerto = puerto;
        this.clientes = new ArrayList();
    }
    
    @Override
    public void run() {
       ServerSocket servidor = null;
        Socket sc = null;
        DataInputStream in;
        
        try{
           servidor = new ServerSocket(puerto);
           
           while(true){
               sc = servidor.accept();
               
               clientes.add(sc);
           }
        }catch (IOException ex){
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE,null,ex);
        }
    }
    
    public void enviarDatos(Coordenadas coordenadas){
        for(Socket socket: clientes){
            
            try {
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(coordenadas);
                oos.close();
                } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
    }
}
