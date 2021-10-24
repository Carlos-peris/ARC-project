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
    private int mi_ide, mi_grupo;
    private static int numCliexGrup;
    private static int numGrup;
    private ArrayList<Socket> sc;  //Array de sockets
    private ArrayList<Integer> ide; //Array de ides
    private DataOutputStream out;
    private DataInputStream in;
    private static float latencia_global = 0; //es static porque se comparte la variable entre todos los hilos
    private static int contador_clientes = 0; //Lo he hecho static para que todos los hilos cuenten a la vez en este contador cuando le lleguen clientes
    private static float media;
    private static float [] media_grupo;
    private static boolean global_sacada = false;
    private static boolean [] grupos_sacada;
    private boolean acabado = false;
    private Socket so;
    private int id_rec;
    
    public ServidorHilo(int p, int numCliexGrup, int numGrup, ArrayList<Integer> i, ArrayList<Socket>s, int mi_grupo){
        try {
            this.mi_grupo = mi_grupo;
            this.grupos_sacada = new boolean[numGrup];
            this.media_grupo = new float[numGrup];
            
            for(int j = 0; j < numGrup; j++){
                grupos_sacada[j] = false;
            }
            
            this.mi_ide = i.get(p);
            this.numCliexGrup = numCliexGrup;
            this.numGrup = numGrup;
            so = s.get(p);
            so.setSoTimeout(1000);//20 * 1000);
            sc = (ArrayList<Socket>) s.clone();
            ide = (ArrayList<Integer>) i.clone();
            try {
                in = new DataInputStream(so.getInputStream());
                out = new DataOutputStream(so.getOutputStream());
            } catch (IOException ex) {
                Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SocketException ex) {
            Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public void run() {
        String mensaje ="";
        try {
            //Mensaje de iniciacion de los Hilos de los Clientes
            env_mensaje(4, mi_ide, null, null);
        } catch (IOException ex) {
            Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);
        }
          
              //Lo hace hasta que el cliente acaba de hacer sus iteraciones
            do{
                try{
                mensaje = in.readUTF();
                rec_mensaje(mensaje);
                }catch(IOException ex){/*System.out.println("TIME OUT.");*/}
                
            } while(contador_clientes < numCliexGrup);
              
            //float tiempomedio = calc_med();
              //System.out.println("La media de todos los clientes es: " + tiempomedio + " ms.");
        try {
            env_mensaje(5, mi_ide, null, null);
            //System.out.println("");
            //so.close();
            //Tenemos que cerrar los sockets con todos los clientes.
        } catch (IOException ex) {
            Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);
        }
         
        bloquear(1);
        bloquear(2);
    }
    
    public synchronized void bloquear(int tipo){
        if(tipo == 1){
            if(!grupos_sacada[mi_grupo]){
            grupos_sacada[mi_grupo] = true;
            System.out.println("Grupo " + mi_grupo + " acaba");  
            System.out.println("Latencia del grupo " + mi_grupo + ": " + (media_grupo[mi_grupo] / numCliexGrup)); 
            }
        }
        else
            if(!global_sacada){
            global_sacada = true;
            System.out.println("Latencia global: " + calc_med()); 
            }  
    }
    
    public static synchronized float calc_med(){
        return (media / (numCliexGrup*numGrup));
    }
    
    public static synchronized void sumar(float t){
        media += t;
    }
    
    public void sumar_grupo(float t){
        media_grupo[mi_grupo] += t;
    }
    public static synchronized void fin_cliente(){contador_clientes++;}
    public void rec_mensaje(String mensaje) throws IOException {
        
        int codigo;
        int x,y,z;
        //Leo mensaje del buffer
        //System.out.println("Mensaje de cliente recibido.");
        //Separo el mensaje que me han enviado por el separador |
        String[] parts = mensaje.split("\\|");
        codigo = Integer.parseInt(parts[0]);
        id_rec = Integer.parseInt(parts[1]);
        String coor = "";
        //Dependiendo de cada codigo el programa debera realizar unas cosas distintas
        //System.out.println(codigo);
        switch(codigo){    
            case 2: //Enviar a todos nuevo desplazamiento
                coor = parts[2]+"|"+parts[3]+"|"+parts[4];
                //System.out.println("SERVER: Cordenadas que envio " + coor);
                for(int i = 0; i < numCliexGrup; i++){
                    if(ide.get(i) != id_rec){
                        env_mensaje(2,id_rec,sc.get(i), coor);
                    }
                }

                break;
                
            case 3: //Enviar a destinatario
                for(int j = 0; j < numCliexGrup; j++){
                    if(ide.get(j) == id_rec)
                        env_mensaje(3,id_rec,sc.get(j),coor);
                }
                break;
            case 5:
                //System.out.println ("SERVER: " + mi_ide + " ha terminado: " + mensaje);
                sumar(Float.parseFloat(parts[2]));
                sumar_grupo(Float.parseFloat(parts[2]));
                fin_cliente();
                //if(contador_clientes == numCliexGrup)
                    //acabado = true;
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
                //out = new DataOutputStream(s.getOutputStream());
                mensaje = "2" + "|" + ide +"|" + coor;
                out.writeUTF(mensaje);
                //System.out.println("Servidor reenvia mensaje: " + mensaje);
                break;
            case 3:
                //out = new DataOutputStream(s.getOutputStream());
                mensaje = "3" + "|" + ide + "|" + coor;
                out.writeUTF(mensaje);
                //System.out.println("Servidor envia OK: " + mensaje);
                break;
            case 4:
                mensaje = "4";
                out.writeUTF(mensaje);
                break;
            case 5:
                mensaje = "5" + "|" + ide;
                out.writeUTF(mensaje);
                break;
            default:
                System.out.println("(env_mensaje servidor)ERROR LEYENDO EL TIPO");      
        }
    }
}