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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Carlos
 */
public class ServidorUDP {
    public static void main (String[] args) throws IOException{
        final int Puerto = 5000;
        byte[] buffer = new byte[1024];
        
        try {
            DatagramSocket socketUDP = new DatagramSocket(Puerto);
            
            DatagramPacket peticion = new DatagramPacket(buffer, buffer.length);
            
            
            socketUDP.receive(peticion);
            System.out.println("Recibo de cliente");
            
            String mensaje = new String(peticion.getData());
            System.out.println(mensaje);
            
            int puertoCliente = peticion.getPort();
            InetAddress direccion = peticion.getAddress();
            
            mensaje = "Hola mundo desde el servidor";
            buffer = mensaje.getBytes();
            
            DatagramPacket respuesta = new DatagramPacket(buffer, buffer.length, direccion, Puerto);
            System.out.println("Envio a cliente");
            
            socketUDP.send(respuesta);
        } catch (SocketException ex) {
            Logger.getLogger(ServidorUDP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
