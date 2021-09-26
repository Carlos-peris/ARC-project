/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Proyecto;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Carlos
 */
public class Servidor {
    public static void main(String[] args){
        try {
            ServerSocket server = new ServerSocket(5000);
            Socket socket;
            
            System.out.println("Servidor Iniciado");
            
            while(true){
                socket = server.accept();
                
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                
                out.writeUTF("Indica tu nombre");
                String nombreCliente = in.readUTF();
                
                Servidor_Hilo serverH = new Servidor_Hilo(in, out, nombreCliente);
                serverH.start();
                
                System.out.println("Creada la conexion con el cliente" + nombreCliente);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
