/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clienteServidorUDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Carlos
 */
public class ClienteUDP {
    public static void main (String[] args){
        final int puertoServidor = 5000;
        byte[] buffer = new byte[1024];
       
        
        try {
            DatagramSocket socketUDP = new DatagramSocket();
            InetAddress direccionServidor = InetAddress.getByName("localhost");
            
            String mensaje = "Hola mundo desde el cliente";
            
            buffer = mensaje.getBytes();
            
            DatagramPacket pregunta = new DatagramPacket(buffer, buffer.length, direccionServidor, puertoServidor);
            
            
            socketUDP.send(pregunta);
            System.out.println("Envio datagrama");
            
            DatagramPacket peticion = new DatagramPacket(buffer, buffer.length);
            socketUDP.receive(peticion);
            System.out.println("Recibo peticion");
            
            mensaje = new String(peticion.getData());
            System.out.println(mensaje);
            
            socketUDP.close();
            
        } catch (SocketException ex) {
            Logger.getLogger(ClienteUDP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(ClienteUDP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClienteUDP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
