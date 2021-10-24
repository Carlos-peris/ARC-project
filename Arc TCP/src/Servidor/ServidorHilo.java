/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pc_es
 */
public class ServidorHilo extends Thread{
<<<<<<< Updated upstream
    private int mi_ide;
    private static int numCliexGrup;
    private static int numGrup;
    private ArrayList<Socket> sc;  //Array de sockets
    private ArrayList<Integer> ide; //Array de ides
    private DataOutputStream out;
    private DataInputStream in;
    private float latencia = 0; //es static porque se comparte la variable entre todos los hilos
=======
    private int mi_ide = 0;
    private static int numCliexGrup = 0;
    private static int numGrup = 0;
    private Socket s = null;
    private ArrayList<Socket> sc = null;  //Array de sockets
    private ArrayList<Integer> ide = null; //Array de ides
    private OutputStreamWriter out = null;
    private InputStreamReader in = null;
    private BufferedWriter buffW = null;
    private BufferedReader buffR = null;
>>>>>>> Stashed changes
    private static int contador_clientes = 0; //Lo he hecho static para que todos los hilos cuenten a la vez en este contador cuando le lleguen clientes
    private static float media = 0;
    
    public ServidorHilo(int p, int numCliexGrup, int numGrup, ArrayList<Integer> i, ArrayList<Socket>ls){
        try {
            this.mi_ide = i.get(p);
            this.numCliexGrup = numCliexGrup;
            this.numGrup = numGrup;
<<<<<<< Updated upstream
            Socket so = s.get(p);
            so.setSoTimeout(20 * 1000);
            sc = (ArrayList<Socket>) s.clone();
            ide = (ArrayList<Integer>) i.clone();
            try {
                in = new DataInputStream(so.getInputStream());
                out = new DataOutputStream(so.getOutputStream());
            } catch (IOException ex) {
                Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);
            }
=======
            s = ls.get(p);
            s.setSoTimeout(90 * 100);
            sc = (ArrayList<Socket>) ls.clone();
            ide = (ArrayList<Integer>) i.clone();
            in = new InputStreamReader(s.getInputStream());
            buffR = new BufferedReader(in);
>>>>>>> Stashed changes
        } catch (SocketException ex) {
            Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public void run() {
        String mensaje ="";
        try {
            //Mensaje de iniciacion de los Hilos de los Clientes
            env_mensaje(4, mi_ide, s, null, null);
        } catch (IOException ex) {
            Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);
        }
          
              //Lo hace hasta que el cliente acaba de hacer sus iteraciones
            do{
                try{
                mensaje = buffR.readLine();
                rec_mensaje(mensaje);
<<<<<<< Updated upstream
                }catch(IOException ex){System.out.println("TIME OUT.");}
=======
                }catch(IOException ex){
                    Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);

                }
>>>>>>> Stashed changes
                
            } while(contador_clientes < (numCliexGrup * numGrup));
              
            float tiempomedio = calc_med();
              System.out.println("La media de todos los clientes es: " + tiempomedio + " ms.");
        try {
<<<<<<< Updated upstream
            env_mensaje(5, mi_ide, null, null);
            
            //Tenemos que cerrar los sockets con todos los clientes.
=======
            env_mensaje(5, mi_ide, s, null, null);    
>>>>>>> Stashed changes
        } catch (IOException ex) {
            Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);
        }
              
            
    }
    
    public static synchronized float calc_med(){
    
        return (media / (numCliexGrup*numGrup));
    }
    public static synchronized void sumar(float t){
    
        media += t;
    }
    public static synchronized void fin_cliente(){
        contador_clientes++;
    }
    public void rec_mensaje(String mensaje) throws IOException {
        
        int codigo;
        int id_rec;
        int x,y,z;
        String ite;
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
                ite = parts[5];
                
                for(int i = 0; i < numCliexGrup; i++){
                    if(ide.get(i) != id_rec){
                        env_mensaje(2,ide.get(i),sc.get(i), coor, ite);
                    }
                }

                break;
                
            case 3: //Enviar a destinatario
                coor = parts[2]+"|"+parts[3]+"|"+parts[4];
                ite = parts[5];
                for(int j = 0; j < numCliexGrup; j++){
                    if(ide.get(j) == id_rec)
                        env_mensaje(3,id_rec,sc.get(j),coor,ite);
                }
                break;
                
            case 5:
                System.out.println ("SERVER: " + mi_ide + " ha terminado: " + mensaje);
                sumar(Float.parseFloat(parts[2]));
                fin_cliente();
                //if(contador_clientes == numClie)
                    //acabado = true;
                break;
            default:
                System.out.println("(rec_mensaje)CODIGO DE PAQUETE ERRONEO: " + codigo);
        }
        
    }
    public void env_mensaje(int op, int ide, Socket soc, String coor, String ite) throws IOException{
        String mensaje;
        out = new OutputStreamWriter(soc.getOutputStream());
        buffW = new BufferedWriter (out);
        switch(op){
            case 1://Enviar su ide
<<<<<<< Updated upstream
                mensaje = "1" + "|" + ide;

=======
                mensaje = "1" + "|" + ide+"";
               
>>>>>>> Stashed changes
                //System.out.println("Servidor: " + mensaje);
                buffW.write(mensaje);
                buffW.newLine();
                break;
            case 2://Como servidor tenemos que enviar el mensaje
                
                mensaje = "2" + "|" + ide +"|" + coor + "|" + ite+"";
                System.out.println("SERVER: Mensaje que envio " + mensaje);
                buffW.write(mensaje);
                buffW.newLine();
                //System.out.println("Servidor reenvia mensaje: " + mensaje);
                break;
            case 3:
                
                mensaje = "3" + "|" + ide + "|" + coor + "|" + ite+"";
                buffW.write(mensaje);
                buffW.newLine();
                System.out.println("Servidor envia OK: " + mensaje);
                break;
            case 4:
<<<<<<< Updated upstream
                mensaje = "4";
                out.writeUTF(mensaje);
                break;
            /*case 5:
                mensaje = "5" + "|" + ide;
                out.writeUTF(mensaje);
                break;*/
=======
                
                mensaje = "4"+"";
                buffW.write(mensaje);
                buffW.newLine();
                break;
            case 5:
                
                mensaje = "5" + "|" + ide+"";
                buffW.write(mensaje);
                buffW.newLine();
                break;
>>>>>>> Stashed changes
            default:
                System.out.println("(env_mensaje servidor)ERROR LEYENDO EL TIPO");      
        }
        buffW.flush();
    }
    
}