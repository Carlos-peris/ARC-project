/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
    
    private final int PUERTO_R = 12345;
    private ServerSocket s;
    private ArrayList<Socket> sc;  //Array de sockets
    private ArrayList<Integer> ide; //Array de ides
    private ArrayList<ArrayList> lsc; //Array de lista de sockets
    private ArrayList<ArrayList> lide;//Array de lista de ide
    private ArrayList<ServidorHilo> listaServidor;
    private int numClie;
    private float latenciaMedia = 0, latencia;
    private InputStreamReader in;
    private OutputStreamWriter out;
    private BufferedReader buffR;
    private BufferedWriter buffW;
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
        int numGrup = scanner.nextInt();
        
        //Bucle para controlar los grupos que tenemos
        while (cGrup < numGrup){
            //Bucle que controla los clientes que se conectan por grupo
            sc = new ArrayList<>();
            ide = new ArrayList<>();
            while(contador < numClie/numGrup){
                System.out.println("Esperando Clientes...");
                socket = s.accept();
                sc.add(socket);
                do{
                    aux_ide = (int) (Math.random() * 20000);
                }while(ide.contains(aux_ide));

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
                listaServidor.add(new ServidorHilo(i, (numClie/numGrup), numGrup, lide.get(j), lsc.get(j)));
            }

        listaServidor.forEach((ServidorHilo servidorHilo) -> {
            servidorHilo.start();
        });
 
    }
       
    public void env_mensaje(int op, int ide, Socket s) throws IOException{
        String mensaje;
        out = new OutputStreamWriter(s.getOutputStream());
        buffW = new BufferedWriter(out);
        switch(op){
            case 1://Enviar su ide
                mensaje = 1+"" + "|" + ide+"";
                
                //System.out.println("Mensaje del Servidor: "+mensaje);
                buffW.write(mensaje);
                buffW.newLine();
                break;               
            default:
                System.out.println("(env_mensaje servidor)ERROR LEYENDO EL TIPO");
                
        }
        buffW.flush();
    }
}
