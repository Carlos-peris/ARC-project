/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import static java.lang.Integer.parseInt;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author pc_es
 */
public class Cliente {
    private final int PUERTO = 1234;        //Puerto al que enviamos
    protected String mensaje;                   //String con el que vamos a leer y enviar los mensajes
    private final String HOST = "localHost";    //Direccion del host
    private Socket s;                       //Sockets para enviar(se) y recibir(sr)
    private DataOutputStream out;
    private DataInputStream in;
    private int numIte;
    private int numClie;
    private int ide;
    private int contador; //Cuenta el numero de respuestas recibidas
    
    public Cliente() throws IOException {
    }
    
    public void start() throws IOException{
        //Nada mas iniciar el cliente pedimos el numero de Iteraciones que va a realizar
        System.out.print("Inserte numero de Iteraciones: ");
        Scanner scanner = new Scanner(System.in);
            scanner.useDelimiter("\n");
            numIte = scanner.nextInt();
        //Despues le pedimos el numero de clientes que va a ver   
        System.out.print("Inserte numero de Clientes: ");
        scanner = new Scanner(System.in);
            scanner.useDelimiter("\n");
            numClie = scanner.nextInt();
            
        s = new Socket (HOST, PUERTO);
        rec_mensaje(); //Recibo mi ide
        System.out.print("Esperando confirmacion del Servidor...");
        rec_mensaje(); //Recibo el ok para comenzar el programa
        run();
    }
    
    public void rec_mensaje() throws IOException{
        int codigo;
        int id_rec;
        int x,y,z;
        
        //Leo mensaje del buffer
        in = new DataInputStream(s.getInputStream());
        mensaje = in.readUTF();
        
        //Separo el mensaje que me han enviado por el separador |
        String[] parts = mensaje.split("|");
        codigo = Integer.parseInt(parts[0]);
        
        //Dependiendo de cada codigo el programa debera realizar unas cosas distintas
        switch(codigo){
            case 1: //Me han pasado mi ide desde el Server
                ide = parseInt(parts[2]);
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
                contador++;
                break;
                
            case 4://Hemos recibido el OK del server que podemos empezar
                //Llamamos a la funcion que genere numeros random
                run();
            default:
                System.out.println("(rec_mensaje)CODIGO DE PAQUETE ERRONEO: " + codigo);
        }
        
    }
    
    public void env_mensaje(int codigo, int id, int x, int y, int z) throws IOException{
        out = new DataOutputStream(s.getOutputStream());
        switch(codigo){
            case 2: //Creo un mensaje de tipo Nuevo desplazamiento
                mensaje = codigo+"" + "|" + id+"" + "|" + x+"" + "|" + y+"" + "|" + z+"";
                out.writeUTF(mensaje);
                break;
                
            case 3: //Creo un mensaje de tipo Recibido desplazamiento de vecino
                mensaje = codigo+"" + "|" + id+"" + "|" + x+"" + "|" + y+"" + "|" + z+"";
                out.writeUTF(mensaje);
                break;
            default:
                System.out.println("(env_mensaje)CODIGO DE PAQUETE ERRONEO: " + codigo);
        
        }   
    }
    public void run() throws IOException{
        int x = 0,y = 0,z = 0;
        for(int i = 0; i < numIte; i++){  //  realizamos el numero de iteraciones que tenemos que hacer
            //Generamos los numeros random
            env_mensaje(2,ide,x,y,z);//Creamos el mensaje y lo enviamos
            while(contador < numClie)//Bucle de espera la confirmacion de todos los clientes
                rec_mensaje();
            contador = 0;
        }
    }
    
}
