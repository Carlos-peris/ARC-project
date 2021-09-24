/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clienteServidorTCP;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Carlos
 */
public class ServidorTCP {
    public static void main (String[] args) throws IOException {
        
        ServerSocket servidor = null;
        Socket sc = null;
        DataInputStream in;
        DataOutputStream out;
        final int Puerto = 5000;
        
        try{
           servidor = new ServerSocket(Puerto);
           
           while(true){
               sc = servidor.accept();
               
               in = new DataInputStream(sc.getInputStream());
               out = new DataOutputStream(sc.getOutputStream());
               
               String mensaje = in.readUTF();
               System.out.println(mensaje);
               
               out.writeUTF("Hola mundo desde el servidor");
               
               sc.close();
               System.out.println("Cliente desconectado");
           }
        }catch (IOException ex){
            Logger.getLogger(ServidorTCP.class.getName()).log(Level.SEVERE,null,ex);
        }
        
    }
}
