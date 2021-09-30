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
    private ArrayList<Thread> listaServidor;
    private int numClie;
    private float latenciaMedia = 0, latencia;
    private DataInputStream in;
    //Constructor de la clase Servidor
    public Servidor() throws IOException {
        s = new ServerSocket(PUERTO_R);
    }
    
    public void start() throws IOException, InterruptedException{
        sc = new ArrayList<Socket>();
        ide = new ArrayList<Integer>();
        listaServidor = new ArrayList<Thread>();
        int contador = 0;
        Socket socket;
        System.out.println("Servidor iniciado");
        
        System.out.print("Inserte numero de Clientes: ");
        Scanner scanner = new Scanner(System.in);
            scanner.useDelimiter("\n");
            numClie = scanner.nextInt();
        
        while(contador < numClie){
            System.out.println("Esperando Clientes...");
            socket = s.accept();
            sc.add(socket);
            ide.add(contador);//El ide de los sockets sera su indice
            env_mensaje(1,contador,socket);
            contador++;
            System.out.println("Cliente: " + contador+"" + " conectado.");
        }
        
        //contador = 0;
        
        for (int i = 0; i < numClie; i++){
            listaServidor.add(new ServidorHilo(sc.get(i), ide.get(i), numClie, ide, sc));
        }
        //Lanzamos todos los hilos de los servidores y avisamos de que pueden empezar los mensajes
        for(Thread thread : listaServidor)
                thread.start();
        
        for(Thread thread : listaServidor){ //Esta el problema de que esto puede ocurrir sin que el ServidorHilo haya acabado
            //latencia += servidorHilo.getLatencia();  //Mandaria la latencia
            thread.join();
        }
        
        /*while(contador < numClie){
            if(recibirLatencia(sc)){
                contador++;
                latenciaMedia += latencia;
            }
        }*/
        
        System.out.println("La latencia media es: " + latenciaMedia/numClie);
    }
    
    /*public boolean recibirLatencia(ArrayList<Socket> socket){
        //in = new DataInputStream(socket.);
        //iniciar timer con unos segundos, si se acaba, se devuelve false
        
        //cuando reciba un mensaje de ServidorHilo, suma la latencia y devolveria true
        //mensaje = in.readUTF();
        return false;
    }*/
    
    public void env_mensaje(int op, int ide, Socket s) throws IOException{
        String mensaje;
        switch(op){
            case 1://Enviar su ide
                mensaje = 1+"" + "|" + ide+"";
                out = new DataOutputStream(s.getOutputStream());
                System.out.println("Mensaje del Servidor: "+mensaje);
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
