/**
 * Clase Sercidor
 * 
 *      Esta clase hace lo siguiente:
 *          -Pide un múmero de clientes en la simulacion
 *          -Crea un hilo por cada cliente hasta que estan todos
 *          -Espera a que le envíen datos(simulación empezada)
 */
package Servidor;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import static java.lang.Integer.parseInt;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


/**
 *
 * @author pc_es
 */
public class Servidor {
    private DataOutputStream out;       //Creación del canal de salida al cliente
    private final int PUERTO_R = 1234;  //Puerto que van a usar los mensajes del proyecto
    private ServerSocket s;             //Socket del servidor (solo hay uno)
    private ArrayList<Socket> sc;       //Array de sockets de clientes
    private ArrayList<Integer> ide;     //Array de ides de clientes
    private ArrayList<ServidorHilo> listaServidor;  //Array de los hilos (1 por cliente)
    private int numClie;                //Numero de clientes para la simulaicon
    private float latenciaMedia = 0, latencia;
    private DataInputStream in;         //Creación del canal de entrada al cliente
    
    //UDP
    DatagramSocket datagrama;               //Se usa para enviar mensajes UDP
    DatagramPacket recibir, enviar;         //Donde se almacena el datagrama a enviar/recibir
    InetAddress direccion;                  //Dirección a donde enviar el datagrama UDP
    
    /**
     *  Constructor de la clase Servidor
     * @throws IOException 
     */
    public Servidor() throws IOException {
        s = new ServerSocket(PUERTO_R);
    }
    
    /**
     * Método start, inicia la espera para que se conecten
     * todos los clientes y empezar la simulacion.
     * 
     * @throws IOException
     * @throws InterruptedException 
     */
    public void start() throws IOException, InterruptedException{
        
        //Instancio arrays de los sockets y los identificadores de los clientes.
        sc = new ArrayList<Socket>();
        ide = new ArrayList<Integer>();
        
        //Instancio array de los hilos para los clientes(1 hilo por cliente)
        listaServidor = new ArrayList<ServidorHilo>();
        
        int contador = 0;
        Socket socket;          //Variable para almacenar los sockets de los clientes
        System.out.println("Servidor iniciado");
        
        System.out.print("Inserte numero de Clientes: ");
        Scanner scanner = new Scanner(System.in);
            scanner.useDelimiter("\n");
            numClie = scanner.nextInt();
        
        //Espera a que se conecten todos los clientes
        while(contador < numClie){
            System.out.println("Esperando Clientes...");
            socket = s.accept();
            //Añade el socket y el ide del nuevo cliente a los arrays
            //Aqui podría haber un array de arrays en el que el primero array fuesen los grupos y el segundo ya los clientes pertenecientes a el
            sc.add(socket);
            ide.add(contador);//El ide de los sockets sera su indice
            env_mensaje(1,contador,socket);
            contador++;
            System.out.println("Cliente: " + contador+"" + " conectado.");
        }
        
        //Creo un hilo por cliente y lo añado al array de hilos
        for (int i = 0; i < numClie; i++){
            listaServidor.add(new ServidorHilo(PUERTO_R,sc.get(i), ide.get(i), numClie, ide, sc));
        }

        //Cuando ya tengo todos los hilos creados empiezo la simulación.
        for(ServidorHilo servidorHilo : listaServidor)
                servidorHilo.start();
        
        String mensaje;
        //Recibir y reenviar los mesnajes UDP
        //El while true es de momento, para que este siempre recibiendo mensajes UDP
        //Problema gordo, no se como hacer que escuche a lo que sea que viene y a raiz de ahi identifique el socket
        while(true){
            //in = new DataInputStream(s.getInputStream());
            //mensaje = in.readUTF();
            
            //Aqui tendria que reenviar a todos los de sc menos a el mismo
        }       
    }
    
    /******************************************
    * 
    * CÓDIGO de NUMEROS para el TIPO DE MENSAJE
    *  1 - Servidor dice OK y devuelve identificador "ide" al cliente
    *  2 - Nuevo desplazamiento
    *  3 - OK desplazamiento
    *  4 - Server dice: comenzad
    * 
    * @param op    Tipo de operación(códigos justo encima)
    * @param ide   Identificador del hilo/cliente
    * @param s     Socket del cliente (Destino del mensaje)
    * @throws IOException 
    */
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
