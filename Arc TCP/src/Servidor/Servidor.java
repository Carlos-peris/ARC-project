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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


/**
 *
 * @author pc_es
 */
public class Servidor {
    private final int PUERTO_R = 10740;
    private ServerSocket s;
    
    private DataOutputStream out;
    private DataInputStream in;
        
    private int numClie, numGrup;
    
    private double [] latenciasGrupos;  
    private double latenciaMediaGrupo = 0, latenciaGlobal = 0;
    
    ArrayList<ServidorHilo> listaServidor = new ArrayList<ServidorHilo>();
    
    public Servidor() throws IOException {}
    
    public void setNumClientesYGrupos(int numClie, int numGrup) throws IOException
    {
        s = new ServerSocket(PUERTO_R);
        
        this.numClie = numClie;
        this.numGrup = numGrup;
        latenciasGrupos = new double[numGrup];
        
        for(int j = 0; j < numGrup; j++)
            latenciasGrupos[j] = 0;
    }
    
    //Constructor de la clase Servidor
    public Servidor(int numClie, int numGrup) throws IOException {
        s = new ServerSocket(PUERTO_R);
        
        this.numClie = numClie;
        this.numGrup = numGrup;
        latenciasGrupos = new double[numGrup];
        
        for(int j = 0; j < numGrup; j++)
            latenciasGrupos[j] = 0;
    }
    
    public void start() throws IOException, InterruptedException{
        //ArrayList<ServidorHilo> listaServidor = new ArrayList<ServidorHilo>();
        ArrayList<ArrayList> lsc = new ArrayList<ArrayList>();
        ArrayList<ArrayList> lide = new ArrayList<ArrayList>();
        
        int cGrup = 0, contador = 0, contador_id = 0;
        
        Socket socket;

        while (cGrup < numGrup){
            ArrayList<Socket> sc = new ArrayList<Socket>();
            ArrayList<Integer> ide = new ArrayList<Integer>();
            
            contador = 0;
            
            while(contador < numClie/numGrup){
                System.out.println("Esperando Clientes...");
                socket = s.accept();
                sc.add(socket);
                ide.add(contador_id);
                
                env_mensaje(1,contador_id,socket);
                
                System.out.println("Cliente: " + contador_id + " conectado.");
                contador_id++;
                contador++;
            }

            lsc.add(sc);
            lide.add(ide);
            
            cGrup++;
        }
        
        for (int j = 0; j < numGrup; j++)
            for (int i = 0; i < numClie/numGrup; i++)
                listaServidor.add(new ServidorHilo(i, numClie/numGrup, numGrup, lide.get(j), lsc.get(j), j));

        for(ServidorHilo servidorHilo : listaServidor){
            servidorHilo.start();
            System.out.println("Empiezo hilo");
        }
                
 
        for(ServidorHilo servidorHilo : listaServidor){
            servidorHilo.join();
            latenciasGrupos[servidorHilo.getGrupo()] += servidorHilo.getLatencia();
        }   
        
        for(int j = 0; j < numGrup; j++){
            latenciaMediaGrupo = latenciasGrupos[j]/(numClie/numGrup);
            System.out.println("La latencia del grupo " + j + " es: " + latenciaMediaGrupo + "ms");
            latenciaGlobal += latenciaMediaGrupo;
        }
        
        latenciaGlobal = latenciaGlobal/numGrup;
        
        System.out.println("\nLa latencia global es de: " + latenciaGlobal + "ms");
    }
       
    public void env_mensaje(int op, int ide, Socket s) throws IOException{
        String mensaje;
        switch(op){
            case 1://Enviar su ide
                mensaje = 1+"" + "|" + ide+"";
                out = new DataOutputStream(s.getOutputStream());
                out.writeUTF(mensaje);
                break;
            
            case 4:
                mensaje = 4+"";
                out = new DataOutputStream(s.getOutputStream());
                out.writeUTF(mensaje);
                break;
                
            default:
                System.out.println("(env_mensaje servidor)ERROR LEYENDO EL TIPO");
                
        }
    }
    
    public void resetearServidor()
    {
        for(ServidorHilo servidorHilo : listaServidor)
        {
            servidorHilo.stop(); //no eliminar
        }
    }
    
}