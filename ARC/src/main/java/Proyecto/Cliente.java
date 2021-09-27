/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Proyecto;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Carlos
 */
public class Cliente {
    
    public static void main (String[] args){
        
        try {
            Scanner scanner = new Scanner(System.in);
            scanner.useDelimiter("\n");
        
            Socket socket;
            socket = new Socket("127.0.0.1",5000);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            
            String mensaje = in.readUTF();
            System.out.println(mensaje);
            
            
            //Aqui en lugar del nombre, lo suyo es que avise al servidor de su grupo creo yo
            String nombre = scanner.next();
            out.writeUTF(nombre);
            
            int iteraciones = scanner.nextInt();
            
            Cliente_Hilo clienteH = new Cliente_Hilo(in,out,iteraciones);
            clienteH.start();
            clienteH.join();
            
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
}
