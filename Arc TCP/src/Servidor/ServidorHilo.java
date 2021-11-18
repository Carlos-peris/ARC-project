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
    private Socket so;
    private DataOutputStream out;
    private DataInputStream in;
    
    private int mi_ide, mi_grupo, numCliexGrup, numGrup;
    private ArrayList<Socket> sc;
    private ArrayList<Integer> ide;
    
    private static int[] contador_mi_grupo;
    private static int contador_clientes = 0;
    private float media;
    
    /**
     * ServidorHilo se comunica con un cliente.
     * 30.10.2021 - Alex
     * 
     * @param p             número para saber el id
     * @param numCliexGrup  Número de clientes por grupo
     * @param numGrup       Número de grupos
     * @param i             Lista de IDs de clientes de mi grupo
     * @param s             Lista de sockets de clientes de mi grupo
     * @param mi_grupo      Número de mi grupo
     */
    public ServidorHilo(int p, int numCliexGrup, int numGrup, ArrayList<Integer> i, ArrayList<Socket>s, int mi_grupo){
        try {
            this.mi_grupo = mi_grupo;
            this.numGrup = numGrup;
            this.mi_ide = i.get(p);
            this.numCliexGrup = numCliexGrup;
            
            
            if(p == 0 && mi_grupo == 0){
                this.contador_mi_grupo = new int[numGrup];
                
                for(int j = 0; j < numGrup; j++){
                    contador_mi_grupo[j] = 0;
                }
            }
            
            media = 0;
            
            so = s.get(p);
            so.setSoTimeout(20*1000);
            
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
            env_mensaje(4, mi_ide, null, null);
        } catch (IOException ex) {
            Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        while(contador_mi_grupo[mi_grupo] < numCliexGrup){
            try{
                //System.out.println(contador_mi_grupo[mi_grupo] + " clientes acabados del grupo " + mi_grupo + " hilo " + mi_ide);
                //System.out.println(contador_clientes);
                mensaje = in.readUTF();
                rec_mensaje(mensaje); 
            }catch(IOException ex){}
        }
        
        System.out.println("El grupo " + mi_grupo + " se desconecta");

        try {
            env_mensaje(5, mi_ide, null, null);
        } catch (IOException ex) {
            Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public synchronized void fin_cliente(){contador_mi_grupo[mi_grupo]++;contador_clientes++;}
    
    public void rec_mensaje(String mensaje) throws IOException {
            int id_rec, codigo, x, y, z;

            String[] parts = mensaje.split("\\|");
            codigo = Integer.parseInt(parts[0]);
            id_rec = Integer.parseInt(parts[1]);
            String coor = "";

            switch(codigo){    
                case 2: //Enviar a todos nuevo desplazamiento
                    coor = parts[2]+"|"+parts[3]+"|"+parts[4];

                    for(int i = 0; i < numCliexGrup; i++)
                        if(ide.get(i) != id_rec)
                            env_mensaje(2,id_rec,sc.get(i), coor); 
                    break;

                case 3: //Enviar a destinatario
                    for(int j = 0; j < numCliexGrup; j++)
                        if(ide.get(j) == id_rec)
                            env_mensaje(3,id_rec,sc.get(j),coor);
                    break;
                case 5:
                    media = Float.parseFloat(parts[2]);
                    fin_cliente();
                    so.setSoTimeout(1000);
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
                out.writeUTF(mensaje);
                break;
            case 2://Como servidor tenemos que enviar el mensaje
                mensaje = "2" + "|" + ide +"|" + coor;               
                out.writeUTF(mensaje);
                break;
            case 3:
                mensaje = "3" + "|" + ide + "|" + coor;               
                out.writeUTF(mensaje);
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
    
    public double getLatencia(){
        return media;
    }
    
    public int getGrupo(){
        return mi_grupo;
    }
}