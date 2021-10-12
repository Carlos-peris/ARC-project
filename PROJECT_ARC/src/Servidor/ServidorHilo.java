/**
 * Clase ServidorHilo
 * 
 *      Esta clase espera siemrpe a recibir paquetes de un cliente
 *      ya que hay un cliente por instancia de esta clase.
 */
package Servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import static java.lang.Integer.parseInt;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
    private int numClie;                //Numero de clientes en la simulación
    private int mi_ide;                 //Identificador de este cliente/hilo
    private ArrayList<Socket> sc;       //Array de sockets
    private ArrayList<Integer> ide;     //Array de ides
    private DataOutputStream out;       //Creación del canal de salida  al cliente
    private DataInputStream in;         //Creación del canal de entrada al cliente
    private float latencia = 0;         //es static porque se comparte la variable entre todos los hilos
    private static int contador_clientes = 0; //Lo he hecho static para que todos los hilos cuenten a la vez en este contador cuando le lleguen clientes
    private static float media;
    private boolean acabado = false;
    byte[] buffer = new byte[1024];
    //UDP
    DatagramSocket datagrama;           //Se usa para enviar mensajes UDP
    DatagramPacket recibir, enviar;     //Donde se almacena el datagrama a enviar/recibir
    InetAddress direccion;              //Dirección a donde enviar el datagrama UDP
    int puertoCliente;                  //Puerto que van a usar los mensajes del proyecto
    
    /**
     * Constructor de la clase ServidorHilo
     * 
     * @param PUERTO    Puerto que van a usar los mensajes del proyecto
     * @param so        Socket del cliente con el que comunicarse
     * @param id        Identificador del cliente con el que comunicarse
     * @param numClie   Numero de clientes de la simulación
     * @param i         Array de identificadores de todos los clientes
     * @param s         Array de sockets de todos los clientes
     * @throws SocketException 
     */
    public ServidorHilo(int PUERTO,Socket so, int id, int numClie, ArrayList<Integer> i, ArrayList<Socket>s) throws SocketException{
        //Almaceno los datos en las variables de la clase
        this.mi_ide = id;
        this.numClie = numClie;
        sc = (ArrayList<Socket>) s.clone();
        ide = (ArrayList<Integer>) i.clone();
        datagrama = new DatagramSocket(PUERTO);
        recibir = new DatagramPacket(buffer,buffer.length);
        
        //Instancio los canales de comunicación por red
        try {
            in = new DataInputStream(so.getInputStream());
            out = new DataOutputStream(so.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * El metodo run hace ...??
     * 
     * CÓDIGO de NUMEROS para el TIPO DE MENSAJE
     *  1 - Servidor dice OK y devuelve identificador "ide" al cliente
     *  2 - Nuevo desplazamiento
     *  3 - OK desplazamiento
     *  4 - Server dice: comenzad
     */
    @Override
    public void run() {
        String mensaje ="";
        try {
            //Mensaje de iniciacion de los Hilos de los Clientes
            env_mensajeTCP(4, mi_ide, null, null);
          
            //Acción para cada iteración
            while(contador_clientes < numClie)  //Lo hace hasta que el cliente acaba de hacer sus iteraciones
            {
                //mensaje = in.readUTF();
                
                /*datagrama.receive(recibir);
                  mensaje = peticion.getData();
                  puertoCliente = peticion.getPort();
                  direccion = peticion.getAddress();
                */
                
                //rec_mensaje(mensaje); //Aqui hay un problema. Y es que el servidor no sabe cuando le dejarán de mandar mensajes TCP o UDP
                                        //Se me ha ocurrido descifrar antes el mensaje (sacar el codigo) y dependiendo de si es perteneciente
                                        //a UDP o TCP ese tipo de mensaje lanzar una funcion u otra. El problema de eso es que para recibir el
                                        //propio mensaje ya tienes que definir si será UDP o TCP lo que recibiras
            }
            
            media = media / numClie;
            System.out.println("La media de todos los clientes es: " + media + " ms.");
            //env_mensaje(5, mi_ide, null, null);
            
            //Tenemos que cerrar los sockets con todos los clientes.
              
        } catch (IOException ex) {
            Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Recibir mensajes TCP del servidor
     * 
     * @param mensaje       Mensaje de tipo string.
     * @throws IOException 
     */
    public void rec_mensajeTCP(String mensaje) throws IOException {
        
        int codigo;
        int id_rec;
        int x,y,z;
        //Leo mensaje del buffer
        //System.out.println("Mensaje de cliente recibido.");
        //Separo el mensaje que me han enviado por el separador |
        String[] parts = mensaje.split("\\|");
        codigo = Integer.parseInt(parts[0]);
        id_rec = Integer.parseInt(parts[1]);
        //Dependiendo de cada codigo el programa debera realizar unas cosas distintas
        //System.out.println(codigo);
        
        //Aqui digo lo mismo que en cliente Hilo. Pondria o bien un if para comprobarlo o directamente asumiria que el mensaje
        //será de tipo 5
        switch(codigo){                             
            case 5:
                media += Float.parseFloat(parts[2]);         
                contador_clientes++;
                //if(contador_clientes == numClie)
                    //acabado = true;
                break;
            default:
                System.out.println("(rec_mensaje)CODIGO DE PAQUETE ERRONEO: " + codigo);
        }
        
    }
    
    /**
     * Recibir mensaje UDP del servidor
     * 
     * @param mensaje       Mensaje tipo string recibido del servidor.
     * @throws IOException 
     */
    public void rec_mensajeUDP(String mensaje) throws IOException {
        
        int codigo;
        int id_rec;
        int x,y,z;
        //Leo mensaje del buffer
        //System.out.println("Mensaje de cliente recibido.");
        //Separo el mensaje que me han enviado por el separador |
        String[] parts = mensaje.split("\\|");
        codigo = Integer.parseInt(parts[0]);
        id_rec = Integer.parseInt(parts[1]);
        String coor = null;
        //Dependiendo de cada codigo el programa debera realizar unas cosas distintas
        //System.out.println(codigo);
        switch(codigo){    
            case 2: //Enviar a todos nuevo desplazamiento
                coor = parts[2]+"|"+parts[3]+"|"+parts[4];
                System.out.println("SERVER: Cordenadas que envio " + coor);
                
                //Aqui tendria que haber algun array con InetAddress o algo por el estilo de todos los clientes 
                for(int i = 0; i < numClie; i++){
                    if(ide.get(i) != id_rec){
                        //No se como sacar el InetAddress y el puerto si lo que hay es un socket de tcp
                        //env_mensajeUDP(2,id_rec, , ,coor); //Ni idea de como enviarselo a todos con lo del UDP
                    }
                }

                break;
                
            case 3: //Enviar a destinatario
                for(int j = 0; j < numClie; j++){
                    if(ide.get(j) == id_rec); //Luego quitar este punto y coma que ahora esta puesto para que no de error el for
                        //No se como sacar el InetAddress y el puerto si lo que hay es un socket de tcp
                        //env_mensajeUDP(3,id_rec, , ,coor); //Faltan campos, como el puerto y el InetAddress
                }
                break;
           
            default:
                System.out.println("(rec_mensaje)CODIGO DE PAQUETE ERRONEO: " + codigo);
        }
        
    }
    
    /**
     * Enviar mensajes TCP al servidor
     * 
     * CÓDIGO de NUMEROS para el TIPO DE MENSAJE
     *  1 - Servidor dice OK y devuelve identificador "ide" al cliente
     *  2 - Nuevo desplazamiento
     *  3 - OK desplazamiento
     *  4 - Server dice: comenzad
     * 
     * @param op        Tipo de mensaje a enviar
     * @param ide       Identificador del hilo que lo envía(este hilo)
     * @param s         Socket del servidor, es decir, al que enviar el mensaje.(¿?¿?¿??¿)
     * @param coor      Coordenadas del cliente (datos a enviar)
     * @throws IOException 
     */
    public void env_mensajeTCP(int op, int ide, Socket s, String coor) throws IOException{
        String mensaje;
        switch(op){
            case 1://Enviar su ide
                mensaje = "1" + "|" + ide;

                //System.out.println("Servidor: " + mensaje);
                out.writeUTF(mensaje);
                break;
           
            case 4:
                mensaje = "4";
                out.writeUTF(mensaje);
                break;
                
            /*case 5:
                mensaje = "5" + "|" + ide;
                out.writeUTF(mensaje);
                break;*/
                
            default:
                System.out.println("(env_mensaje servidor)ERROR LEYENDO EL TIPO");      
        }
    }
    
    
    /**
     * Enviar mensajes UDP al servidor
     * 
     * CÓDIGO de NUMEROS para el TIPO DE MENSAJE
     *  1 - Servidor dice OK y devuelve identificador "ide" al cliente
     *  2 - Nuevo desplazamiento
     *  3 - OK desplazamiento
     *  4 - Server dice: comenzad
     * 
     * @param op                Tipo de mensaje a enviar
     * @param ide               Identificador del hilo que lo envía(este hilo)
     * @param datagrama         DatagramSocket
     * @param puertoCliente     Puerto que van a usar los mensajes del proyecto
     * @param direccion         Dirección del servidor.
     * @param coor              Coordenadas del cliente (datos a enviar)
     * @throws IOException 
     */
    public void env_mensajeUDP(int op, int ide, DatagramSocket datagrama, int puertoCliente, InetAddress direccion , String coor) throws IOException{ //Se le tiene que pasar la direccion y el puerto del cliente
        String mensaje;

        switch(op){
            
            case 2://Como servidor tenemos que enviar el mensaje
                mensaje = "2" + "|" + ide +"|" + coor;
                buffer = mensaje.getBytes();
                enviar = new DatagramPacket(buffer, buffer.length, direccion, puertoCliente);
                datagrama.send(enviar);
                //System.out.println("Servidor reenvia mensaje: " + mensaje);
                break;
                
            case 3:
                mensaje = "3" + "|" + ide + "|" + coor;
                buffer = mensaje.getBytes();
                enviar = new DatagramPacket(buffer, buffer.length, direccion, puertoCliente);
                datagrama.send(enviar);
                break;
            
            default:
                System.out.println("(env_mensaje servidor)ERROR LEYENDO EL TIPO");      
        }
    }
}