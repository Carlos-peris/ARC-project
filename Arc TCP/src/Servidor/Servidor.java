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
    private DataOutputStream out;
    private final int PUERTO_R = 1234;
    private ServerSocket s;
    private ArrayList<Socket> sc;  //Array de sockets
    private ArrayList<Integer> ide; //Array de ides
    private ArrayList<ArrayList> lsc; //Array de lista de sockets
    private ArrayList<ArrayList> lide;//Array de lista de ide
    private ArrayList<ServidorHilo> listaServidor;
    private int numClie;
    private float latenciaMedia = 0, latencia;
    private DataInputStream in;
    //Constructor de la clase Servidor
    public Servidor() throws IOException {
        s = new ServerSocket(PUERTO_R);
    }
    
    public void start() throws IOException, InterruptedException{
        
        lsc = new ArrayList<ArrayList>();
        lide = new ArrayList<ArrayList>();
        
        listaServidor = new ArrayList<ServidorHilo>();
        int cGrup = 0;
        int contador = 0;
        int aux_ide = 0;
        Socket socket;
        System.out.println("Servidor iniciado");
        
        System.out.print("Inserte numero de Clientes: ");
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");
        numClie = scanner.nextInt();
            
        System.out.print("Inserte numero de Grupos: ");
        scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");
        int numGrup = scanner.nextInt();
        
        //Bucle para controlar los grupos que tenemos
        while (cGrup < numGrup){
        //Bucle que controla los clientes que se conectan por grupo
        sc = new ArrayList<Socket>();
        ide = new ArrayList<Integer>();
        while(contador < numClie/numGrup){
            System.out.println("Esperando Clientes...");
            socket = s.accept();
            sc.add(socket);
            aux_ide = (int) (Math.random() * 20000);
            ide.add(aux_ide);//El ide de los sockets sera su indice
            env_mensaje(1,aux_ide,socket);
            contador++;
            System.out.println("Cliente: " + aux_ide+"" + " conectado.");
        }
            /*Ya se han conectado todos los clientes de un grupo. 
            ahora lo que hacemos es guardar el array de sockets y el de ides
            para despues pasarlo a cada servidor en funcion de en que grupo este*/
            lsc.add(sc);
            lide.add(ide);
            cGrup++;
            contador = 0;
        }
        
        for (int j = 0; j < numGrup; j++)
        for (int i = 0; i < numClie/numGrup; i++){
            listaServidor.add(new ServidorHilo(i, numClie/numGrup, numGrup, lide.get(j), lsc.get(j)));
        }

        for(ServidorHilo servidorHilo : listaServidor)
                servidorHilo.start();
 
    }
       
    public void env_mensaje(int op, int ide, Socket s) throws IOException{
        String mensaje;
        switch(op){
            case 1://Enviar su ide
                mensaje = 1+"" + "|" + ide+"";
                out = new DataOutputStream(s.getOutputStream());
                //System.out.println("Mensaje del Servidor: "+mensaje);
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
}
