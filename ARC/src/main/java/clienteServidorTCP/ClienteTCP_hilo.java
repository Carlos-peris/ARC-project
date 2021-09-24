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
public class ClienteTCP_hilo implements Runnable {

    //para conectar con otros ordenadores es lo comentado
    //private String host;
    private int puerto;
    private String mensaje;
    
    public ClienteTCP_hilo(int puerto, String mensaje){ //otro campo, host
        this.puerto = puerto;
        this.mensaje = mensaje;
        //this.host = host;
    }
    
    @Override
    public void run() {
        final String Host = "127.0.0.1"; //se quitaría
        DataOutputStream out;
        
        try{
          Socket sc = new Socket(Host,puerto); //se pone el host del private
          
          out = new DataOutputStream(sc.getOutputStream());
          out.writeUTF(mensaje);
          
          sc.close();
          
        } catch (IOException ex){
            Logger.getLogger(ServidorTCP_hilo.class.getName()).log(Level.SEVERE,null,ex);
        }
    }
}
