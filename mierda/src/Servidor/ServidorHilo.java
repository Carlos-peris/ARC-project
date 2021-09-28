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
    private static float latencia = 0; //es static porque se comparte la variable entre todos los hilos
    private int contador_clientes = 0;
    private float media;
    private boolean acabado = false;
    
    public ServidorHilo(Socket so, int ide, int numClie){
        this.mi_ide = ide;
        this.numClie = numClie;
        try {
            in = new DataInputStream(so.getInputStream());
            out = new DataOutputStream(so.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void setArray(ArrayList s, ArrayList i){
        sc = (ArrayList<Socket>) s.clone();
        ide = (ArrayList<Integer>) i.clone();
    }
    @Override
    public void run() {
        String mensaje ="";
        try {
            //Mensaje de iniciacion de los Hilos de los CLientes
            env_mensaje(4, mi_ide);
                
             mensaje = in.readUTF();
            
            while(!acabado)  //Lo hace hasta que el cliente acaba de hacer sus iteraciones
                rec_mensaje(mensaje);
                
            } catch (IOException ex) {
                Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    
    public void rec_mensaje(String mensaje) {
        
        int codigo;
        int id_rec;
        int x,y,z;
        //Leo mensaje del buffer
        System.out.println("Mensaje de cliente recibido.");
        //Separo el mensaje que me han enviado por el separador |
        String[] parts = mensaje.split("\\|");
        codigo = Integer.parseInt(parts[0]);
        //Dependiendo de cada codigo el programa debera realizar unas cosas distintas
        switch(codigo){
            case 1: //Me han pasado mi ide desde el Server
                
                break;
                
            case 2: //Enviar a todos nuevo desplazamiento
                //Extraigo los datos del paquete
                id_rec = parseInt(parts[1]);
                x = parseInt(parts[2]);
                y = parseInt(parts[3]);
                z = parseInt(parts[4]);
                //Y los paso a la funcion para que envie el okay
                //env_mensaje(4,id_rec,x,y,z);
                break;
                
            case 3: //Enviar OK
                
                break;
                
            case 5: //No estoy seguro porque este ServidorHilo conecta con un cliente, no varios, entonces el contador no tiene sentido
                id_rec = parseInt(parts[1]);
                latencia += Float.parseFloat(parts[2]);
                contador_clientes++;
                
                if(contador_clientes == numClie){
                    media = latencia/contador_clientes;
                    System.out.println("La media es: " + media);
                    acabado = true;
                }
                
                break;
            default:
                System.out.println("(rec_mensaje)CODIGO DE PAQUETE ERRONEO: " + codigo);
        }
        
    }
    public void env_mensaje(int op, int ide) throws IOException{
        String mensaje;
        switch(op){
            case 1://Enviar su ide
                mensaje = 1 + "|" + ide;

                System.out.println("Servidor: " + mensaje);
                out.writeUTF(mensaje);
                break;
            
            case 4:
                mensaje = "4";
                out.writeUTF(mensaje);
                break;
                
            default:
                System.out.println("(env_mensaje servidor)ERROR LEYENDO EL TIPO");      
        }
    }
}
