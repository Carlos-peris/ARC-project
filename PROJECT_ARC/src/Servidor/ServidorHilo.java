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
import java.net.Socket;
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
    
    public ServidorHilo(Socket so, int id, int numClie, ArrayList<Integer> i, ArrayList<Socket>s){
        this.mi_ide = id;
        this.numClie = numClie;
        sc = (ArrayList<Socket>) s.clone();
        ide = (ArrayList<Integer>) i.clone();
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
            //Mensaje de iniciacion de los Hilos de los CLientes
            env_mensaje(4, mi_ide, null, null);
          
            while(contador_clientes < numClie)  //Lo hace hasta que el cliente acaba de hacer sus iteraciones
            {
                mensaje = in.readUTF();
                rec_mensaje(mensaje);
            }
              media = media / numClie;
              System.out.println("La media de todos los clientes es: " + media);
              env_mensaje(5, mi_ide, null, null);
              
              //Tenemos que cerrar los sockets con todos los clientes.
              
            } catch (IOException ex) {
                Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    
    public void rec_mensaje(String mensaje) throws IOException {
        
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
                System.out.println("SERVER: Cordenadas que envio" + coor);
                for(int i = 0; i < numClie; i++){
                    if(ide.get(i) != id_rec){
                        env_mensaje(2,id_rec,sc.get(i), coor);
                    }
                }

                break;
                
            case 3: //Enviar a destinatario
                for(int j = 0; j < numClie; j++){
                    if(ide.get(j) == id_rec)
                        env_mensaje(3,id_rec,sc.get(j),coor);
                }
                break;
                
            case 5:  //No recibe este mensaje en ningun momento
                System.out.println(codigo);
                latencia = Float.parseFloat(parts[2]);
                //acabado = true;            
                media +=latencia;
                contador_clientes ++;
                System.out.println("Contador comun de latencia: " + contador_clientes);
                break;
            default:
                System.out.println("(rec_mensaje)CODIGO DE PAQUETE ERRONEO: " + codigo);
        }
        
    }
    public void env_mensaje(int op, int ide, Socket s, String coor) throws IOException{
        String mensaje;
        switch(op){
            case 1://Enviar su ide
                mensaje = "1" + "|" + ide;

                //System.out.println("Servidor: " + mensaje);
                out.writeUTF(mensaje);
                break;
            case 2://Como servidor tenemos que enviar el mensaje
                DataOutputStream o = new DataOutputStream(s.getOutputStream());
                mensaje = "2" + "|" + ide +"|" + coor;
                o.writeUTF(mensaje);
                //System.out.println("Servidor reenvia mensaje: " + mensaje);
                break;
            case 3:
                DataOutputStream ou = new DataOutputStream(s.getOutputStream());
                mensaje = "3" + "|" + ide + "|" + coor;
                ou.writeUTF(mensaje);
                //System.out.println("Servidor envia OK: " + mensaje);
                break;
            case 4:
                mensaje = "4";
                out.writeUTF(mensaje);
                break;
            case 5:
                //Falta enviar un mensaje a todos los clientes para que cierren la conexion con su socket.
                //Asi todos los clientes cierran conexion con su hilo xD y fin.
                break;
            default:
                System.out.println("(env_mensaje servidor)ERROR LEYENDO EL TIPO");      
        }
    }
    
    public float getLatencia(){
        return latencia;
    }
}