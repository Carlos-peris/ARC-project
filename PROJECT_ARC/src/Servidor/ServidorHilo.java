/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import static java.lang.Integer.parseInt;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pc_es
 */
public class ServidorHilo extends Thread{
    private int numClie;
    private int mi_ide;
    private ArrayList<Socket> sc;  //Array de sockets
    private ArrayList<Integer> ide; //Array de ides
    private DataOutputStream out;
    private DataInputStream in;
    private float latencia = 0; //es static porque se comparte la variable entre todos los hilos
    private static int contador_clientes = 0; //Lo he hecho static para que todos los hilos cuenten a la vez en este contador cuando le lleguen clientes
    private static float media;
    private boolean acabado = false;
    byte[] buffer = new byte[1024];
    DatagramSocket datagrama;
    DatagramPacket recibir, enviar;
    InetAddress direccion;
    int puertoCliente;
    
    public ServidorHilo(int PUERTO,Socket so, int id, int numClie, ArrayList<Integer> i, ArrayList<Socket>s) throws SocketException{
        this.mi_ide = id;
        this.numClie = numClie;
        sc = (ArrayList<Socket>) s.clone();
        ide = (ArrayList<Integer>) i.clone();
        datagrama = new DatagramSocket(PUERTO);
        recibir = new DatagramPacket(buffer,buffer.length);
        
        try {
            in = new DataInputStream(so.getInputStream());
            out = new DataOutputStream(so.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public void run() {
        String mensaje ="";
        try {
            //Mensaje de iniciacion de los Hilos de los Clientes
            env_mensajeTCP(4, mi_ide, null, null);
          
            while(contador_clientes < numClie)  //Lo hace hasta que el cliente acaba de hacer sus iteraciones
            {
                //mensaje = in.readUTF();
                
                /*datagrama.receive(recibir);
                  mensaje = peticion.getData();
                  puertoCliente = peticion.getPort();
                  direccion = peticion.getAddress();
                */
                
                //rec_mensaje(mensaje); //Aqui hay un problema. Y es que el servidor no sabe cuando le dejarán de mandar mensajes TCP o UDP
                                        //Se me ha ocurrido descifrar antes el mensaje (sacar el codigo) y dependiendo de si es perteneciente
                                        //a UDP o TCP ese tipo de mensaje lanzar una funcion u otra. El problema de eso es que para recibir el
                                        //propio mensaje ya tienes que definir si será UDP o TCP lo que recibiras
            }
              media = media / numClie;
              System.out.println("La media de todos los clientes es: " + media + " ms.");
              //env_mensaje(5, mi_ide, null, null);
              
              //Tenemos que cerrar los sockets con todos los clientes.
              
            } catch (IOException ex) {
                Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    
    public void rec_mensajeTCP(String mensaje) throws IOException {
        
        int codigo;
        int id_rec;
        int x,y,z;
        //Leo mensaje del buffer
        //System.out.println("Mensaje de cliente recibido.");
        //Separo el mensaje que me han enviado por el separador |
        String[] parts = mensaje.split("\\|");
        codigo = Integer.parseInt(parts[0]);
        id_rec = Integer.parseInt(parts[1]);
        //Dependiendo de cada codigo el programa debera realizar unas cosas distintas
        //System.out.println(codigo);
        
        //Aqui digo lo mismo que en cliente Hilo. Pondria o bien un if para comprobarlo o directamente asumiria que el mensaje
        //será de tipo 5
        switch(codigo){                             
            case 5:
                media += Float.parseFloat(parts[2]);         
                contador_clientes++;
                //if(contador_clientes == numClie)
                    //acabado = true;
                break;
            default:
                System.out.println("(rec_mensaje)CODIGO DE PAQUETE ERRONEO: " + codigo);
        }
        
    }
    
    public void rec_mensajeUDP(String mensaje) throws IOException {
        
        int codigo;
        int id_rec;
        int x,y,z;
        //Leo mensaje del buffer
        //System.out.println("Mensaje de cliente recibido.");
        //Separo el mensaje que me han enviado por el separador |
        String[] parts = mensaje.split("\\|");
        codigo = Integer.parseInt(parts[0]);
        id_rec = Integer.parseInt(parts[1]);
        String coor = null;
        //Dependiendo de cada codigo el programa debera realizar unas cosas distintas
        //System.out.println(codigo);
        switch(codigo){    
            case 2: //Enviar a todos nuevo desplazamiento
                coor = parts[2]+"|"+parts[3]+"|"+parts[4];
                System.out.println("SERVER: Cordenadas que envio " + coor);
                for(int i = 0; i < numClie; i++){
                    if(ide.get(i) != id_rec){
                        env_mensajeUDP(2,id_rec,sc.get(i), coor); //Ni idea de como enviarselo a todos con lo del UDP
                    }
                }

                break;
                
            case 3: //Enviar a destinatario
                for(int j = 0; j < numClie; j++){
                    if(ide.get(j) == id_rec)
                        env_mensajeUDP(3,id_rec,sc.get(j),coor); //Faltan campos, como el puerto y el InetAddress
                }
                break;
           
            default:
                System.out.println("(rec_mensaje)CODIGO DE PAQUETE ERRONEO: " + codigo);
        }
        
    }
    
    
    public void env_mensajeTCP(int op, int ide, Socket s, String coor) throws IOException{
        String mensaje;
        switch(op){
            case 1://Enviar su ide
                mensaje = "1" + "|" + ide;

                //System.out.println("Servidor: " + mensaje);
                out.writeUTF(mensaje);
                break;
           
            case 4:
                mensaje = "4";
                out.writeUTF(mensaje);
                break;
                
            /*case 5:
                mensaje = "5" + "|" + ide;
                out.writeUTF(mensaje);
                break;*/
                
            default:
                System.out.println("(env_mensaje servidor)ERROR LEYENDO EL TIPO");      
        }
    }
    
    public void env_mensajeUDP(int op, int ide, Socket s, String coor) throws IOException{ //Se le tiene que pasar la direccion y el puerto del cliente
        String mensaje;
        
        
        switch(op){
            
            case 2://Como servidor tenemos que enviar el mensaje
                mensaje = "2" + "|" + ide +"|" + coor;
                buffer = mensaje.getBytes();
                enviar = new DatagramPacket(buffer, buffer.length, direccion, puertoCliente);
                datagrama.send(enviar);
                //System.out.println("Servidor reenvia mensaje: " + mensaje);
                break;
                
            case 3:
                mensaje = "3" + "|" + ide + "|" + coor;
                buffer = mensaje.getBytes();
                enviar = new DatagramPacket(buffer, buffer.length, direccion, puertoCliente);
                datagrama.send(enviar);
                break;
            
            default:
                System.out.println("(env_mensaje servidor)ERROR LEYENDO EL TIPO");      
        }
    }
}