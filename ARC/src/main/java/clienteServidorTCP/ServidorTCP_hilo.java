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
public class ServidorTCP_hilo extends Observable implements Runnable {

    private int puerto;
    
    public ServidorTCP_hilo(int puerto){
        this.puerto = puerto;
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
               
               in = new DataInputStream(sc.getInputStream());
               
               String mensaje = in.readUTF();
               System.out.println(mensaje);
               
               this.setChanged();
               this.notifyObservers(mensaje);
               this.clearChanged();
               
               sc.close();
               System.out.println("Cliente desconectado");
           }
        }catch (IOException ex){
            Logger.getLogger(ServidorTCP_hilo.class.getName()).log(Level.SEVERE,null,ex);
        }
    }
}
