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
    private final int PUERTO_R = 1234;
    private ServerSocket s;
    private ArrayList<Socket> sc;  //Array de sockets
    private ArrayList<Integer> ide; //Array de ides
    private DataInputStream in;
    private DataOutputStream out;
    private int numClie;
    //Constructor de la clase Servidor
    public Servidor() throws IOException {
        s = new ServerSocket(PUERTO_R);
    }
    
    public void start() throws IOException{
        sc = new ArrayList<Socket>();
        ide = new ArrayList<Integer>();
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
        
        //Avisamos a todos los clientes que pueden comenzar
        for(int i = 0; i < numClie; i++)
            env_mensaje(4, ide.get(i), sc.get(i));
        
        while(true){
            rec_mensaje();
        }
    }
    public void rec_mensaje() throws IOException{
        int codigo;
        int id_rec;
        int x,y,z;
        String mensaje;
        //Leo mensaje del buffer
        in = new DataInputStream(s.getInputStream());
        mensaje = in.readUTF();
        
        //Separo el mensaje que me han enviado por el separador |
        String[] parts = mensaje.split("|");
        codigo = Integer.parseInt(parts[0]);
        
        //Dependiendo de cada codigo el programa debera realizar unas cosas distintas
        switch(codigo){
            case 1: //Me han pasado mi ide desde el Server
                
                break;
                
            case 2: //Me han pasado un nuevo desplazamiento
                //Extraigo los datos del paquete
                id_rec = parseInt(parts[2]);
                x = parseInt(parts[4]);
                y = parseInt(parts[6]);
                z = parseInt(parts[8]);
                //Y los paso a la funcion para que envie el okay
                env_mensaje(4,id_rec,x,y,z);
                break;
            
            case 3: //Has recibido un okay
               
                break;
                
            case 4://Hemos recibido el OK del server que podemos empezar
                //Llamamos a la funcion que genere numeros random
                
            default:
                System.out.println("(rec_mensaje)CODIGO DE PAQUETE ERRONEO: " + codigo);
        }
        
    }
    public void env_mensaje(int op, int ide, Socket s) throws IOException{
        String mensaje;
        switch(op){
            case 1://Enviar su ide
                mensaje = 1+"" + "|" + ide+"";
                out = new DataOutputStream(s.getOutputStream());
                System.out.println(mensaje);
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
