/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clienteServidorTCP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Carlos
 */
public class ClienteTCP {
    public static void main (String[] args) throws IOException{
        final String Host = "127.0.0.1";
        final int Puerto = 5000;
        DataInputStream in;
        DataOutputStream out;
        
        try{
          Socket sc = new Socket(Host,Puerto); 
          
          in = new DataInputStream(sc.getInputStream());
          out = new DataOutputStream(sc.getOutputStream());
          
          out.writeUTF("Hola mundo desde el cliente");
          
          String mensaje = in.readUTF();
          
          System.out.println(mensaje);
          
          sc.close();
          
        } catch (IOException ex){
            Logger.getLogger(ServidorTCP_hilo.class.getName()).log(Level.SEVERE,null,ex);
        }
    }
}
