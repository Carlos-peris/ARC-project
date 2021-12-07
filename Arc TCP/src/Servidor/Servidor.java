/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;


import Interfaces.Graficos;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Integer.parseInt;
import static java.lang.Thread.sleep;
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
    
    private double [] latenciasGrupos, throughputGrupos;
    private double latenciaMediaGrupo = 0, latenciaGlobal = 0, throughputMedioGrupos = 0;
    
    ArrayList<ServidorHilo> listaServidor = new ArrayList<ServidorHilo>();
    
    public Servidor() throws IOException {}
    
    public void setNumClientesYGrupos(int numClie, int numGrup) throws IOException
    {
        s = new ServerSocket(PUERTO_R);
        
        this.numClie = numClie;
        this.numGrup = numGrup;
        latenciasGrupos = new double[numGrup];
        throughputGrupos = new double[numGrup];
        
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

        System.out.println("Esperando Clientes...");
        
        while (cGrup < numGrup){
            ArrayList<Socket> sc = new ArrayList<Socket>();
            ArrayList<Integer> ide = new ArrayList<Integer>();
            
            contador = 0;
            
            while(contador < numClie/numGrup){
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
        
        for (int j = 0; j < numGrup; j++){
            for (int i = 0; i < numClie/numGrup; i++){
                ServidorHilo servidorHilo = new ServidorHilo(i, numClie/numGrup, numGrup, lide.get(j), lsc.get(j), j);
                listaServidor.add(servidorHilo);
                servidorHilo.start();
                }   
        }
 
        for(ServidorHilo servidorHilo : listaServidor){
            servidorHilo.join();
            latenciasGrupos[servidorHilo.getGrupo()] += servidorHilo.getLatencia();
            throughputGrupos[servidorHilo.getGrupo()] += servidorHilo.getThroughput();
        }   
        
        for(int j = 0; j < numGrup; j++){
            latenciaMediaGrupo = latenciasGrupos[j]/(numClie/numGrup);
            throughputMedioGrupos += throughputGrupos[j]/(numClie/numGrup);
            System.out.println("La latencia del grupo " + j + " es: " + latenciaMediaGrupo + "ms");
            latenciaGlobal += latenciaMediaGrupo;
        }
        
        latenciaGlobal = latenciaGlobal/numGrup;
        
        System.out.println("\nLa latencia global es de: " + latenciaGlobal + "ms");
        
        lanzarGraficos();
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
    
    public void lanzarGraficos() throws FileNotFoundException, IOException{
        FileReader escalabilidad = new FileReader("escalabilidad.txt");
        FileReader aumento_vecinos = new FileReader("C:\\Users\\Carlos\\Documents\\GitHub\\ARC-project\\Arc TCP\\aumento_vecinos");
        FileReader analisis_throughput = new FileReader("C:\\Users\\Carlos\\Documents\\GitHub\\ARC-project\\Arc TCP\\analisis_throughput");
        
        ArrayList<Integer> datosXEcalabilidad = new ArrayList<Integer>();
        ArrayList<Integer> datosYEscalabilidad = new ArrayList<Integer>();
        ArrayList<Integer> datosXVecino = new ArrayList<Integer>();
        ArrayList<Integer> datosYVecino = new ArrayList<Integer>();
        ArrayList<Integer> datosYThroughput = new ArrayList<Integer>();
        ArrayList<Integer> datosXCuello = new ArrayList<Integer>();
        ArrayList<Integer> datosYCuello = new ArrayList<Integer>();
        
        int dato, i = 0;
        
        if(numClie/numGrup == 10 && (numClie == 500 || numClie == 1000 || numClie == 1500 || numClie == 2000)){
            
            datosXEcalabilidad.add(500);
            datosXEcalabilidad.add(1000);
            datosXEcalabilidad.add(1500);
            datosXEcalabilidad.add(2000);

            
            dato = escalabilidad.read();
            while(dato != -1){
                if(datosXEcalabilidad.get(i) != numClie)
                    datosYEscalabilidad.add(dato);
                else
                    datosYEscalabilidad.add((int) latenciaGlobal);  

                dato = escalabilidad.read();
                i++;
            }
            
            String datosE = ""; 
        
            FileWriter e = new FileWriter("escalabilidad");


            for(int j = 0; j < datosXEcalabilidad.size(); j++)
                datosE += datosXEcalabilidad.get(j) + "\n";

            e.write(datosE);
        }
    
        if(numClie == 1200 && (numClie/numGrup == 5 || numClie/numGrup == 8 || numClie/numGrup == 10 || 
                numClie/numGrup == 12)){
            
            datosXVecino.add(5);
            datosXVecino.add(8);
            datosXVecino.add(10);
            datosXVecino.add(12);

            i = 0;

            
            dato = aumento_vecinos.read();
            while(dato != -1){
                if(datosXVecino.get(i) != numClie/numGrup)
                    datosXVecino.add(dato);
                else
                    datosYVecino.add((int) latenciaGlobal);   

                dato = aumento_vecinos.read();
                i++;
            }
            
            i = 0;
            
            
            dato = analisis_throughput.read();
            while(dato != -1){
                if(datosXVecino.get(i) != numClie/numGrup)
                    datosYThroughput.add(dato);
                else
                    datosYThroughput.add((int) throughputMedioGrupos/numGrup);   

                dato = analisis_throughput.read();
                i++;
            }  
            
            FileWriter av = new FileWriter("aumento_vecinos");
            FileWriter at = new FileWriter("analisis_throughput");
            
            String datosAv = "", datosAt = "";
            
            for(int j = 0; j < datosYVecino.size(); j++)
            datosAv += datosYVecino.get(j) + "\n";
        
            for(int j = 0; j < datosYThroughput.size(); j++)
                datosAt += datosYThroughput.get(j) + "\n";
            
            av.write(datosAv);
            at.write(datosAt);                
        }

        
        for(int j = 0; j < numGrup; j++)
            datosXCuello.add(j);
        
        
        for(int j = 0; j < numGrup; j++)
            datosYCuello.add((int) (latenciasGrupos[j]/(numClie/numGrup)));
                     
        
        
               
        Graficos graficos = new Graficos(datosXEcalabilidad, datosYEscalabilidad, datosXVecino, datosYVecino, 
                datosXCuello, datosYCuello, datosYThroughput);
        
        graficos.setVisible(true);
    }
}