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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**while
 *
 * @author pc_es
 */
public class ClienteHilo extends Thread {
    private final int PUERTO = 1234;        //Puerto al que enviamos
    protected String mensaje;                   //String con el que vamos a leer y enviar los mensajes
    private final String HOST = "localHost";    //Direccion del host
    private Socket s;                       //Sockets para enviar(se) y recibir(sr)
    private DataOutputStream out;
    private DataInputStream in;
    private final int numIte;
    private final int numClie;
    private float latencia = 0;
    private int ide;
    private int contador; //Cuenta el numero de respuestas recibidas
    private long inicio, fin;
    private double tiempo;
    private boolean acabado = false;  //Servira para cuando el servidor sea quien nos indica cuando se acaba
    byte[] buffer = new byte[1024];
    DatagramSocket datagrama;
    DatagramPacket recibir, enviar;
    InetAddress direccion;
    int puertoCliente;

    public ClienteHilo(int numIte, int numClie) {
        this.numIte = numIte;
        this.numClie = numClie;
    }
    
    @Override
    public void run() {
        try { 
            s = new Socket (HOST, PUERTO);
            datagrama = new DatagramSocket(PUERTO);
            rec_mensajeTCP(); //Recibo mi ide
            //System.out.println("Esperando confirmacion del Servidor...");
            rec_mensajeTCP(); //Recibo el ok para comenzar el programa
            
        } catch (IOException ex) {
            Logger.getLogger(ClienteHilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Se encarga de recibir los mensajes relativos a la fase 1,3
    //case 1,4,5
    public void rec_mensajeTCP() throws IOException{
        int codigo;
        int id_rec;
        String x,y,z;
        
        //Leo mensaje del buffer
        in = new DataInputStream(s.getInputStream());
        mensaje = in.readUTF();
        
        //Separo el mensaje que me han enviado por el separador |
        String[] parts = mensaje.split("\\|");
        codigo = Integer.parseInt(parts[0]);
        
        //Dependiendo de cada codigo el programa debera realizar unas cosas distintas
        switch(codigo){
            case 1: //Me han pasado mi ide desde el Server
                ide = parseInt(parts[1]);
                //System.out.println("Me acaban de asignar el ide: "+ide);
                break;
                
            case 4://Hemos recibido el OK del server que podemos empezar
                //Llamamos a la funcion que genere numeros random
                empezar();
                break;
                
            case 5: //El servidor indica que es hora de desconectarse. (implementar más adelante)
                acabado = true;
                System.out.println("Recibido mensaje 5");
                break;
            default:
                System.out.println("(rec_mensaje)CODIGO DE PAQUETE ERRONEO: " + codigo);
        }  
    }
    
    //Se encarga de recibir los mensajes relativos a la 2 fase
    //case 2,3
    public void rec_mensajeUDP() throws IOException{
        int codigo;
        int id_rec;
        String x,y,z;
        
        //Leo mensaje del buffer
        in = new DataInputStream(s.getInputStream());
        mensaje = in.readUTF();
        
        //Separo el mensaje que me han enviado por el separador |
        String[] parts = mensaje.split("\\|");
        codigo = Integer.parseInt(parts[0]);
        
        //Dependiendo de cada codigo el programa debera realizar unas cosas distintas
        switch(codigo){                
            case 2: //Me han pasado un nuevo desplazamiento
                //Extraigo los datos del paquete
                id_rec = parseInt(parts[1]);
                x = parts[2];
                y = parts[3];
                z = parts[4];
                //Y los paso a la funcion para que envie el okay
                //System.out.println("Cliente: "+ide + "Recibe nuevo movimiento.");
                env_mensajeTCP(3,id_rec,x,y,z);
                break;
            
            case 3:             //Has recibido un okay
                //System.out.println("Cliente: "+ide + "Recibe OK.");
                contador++;
                break;
                
            default:
                System.out.println("(rec_mensaje)CODIGO DE PAQUETE ERRONEO: " + codigo);
        }
        
    }
    
    //Envia los mensajes relativos a la fase 1 y 3:
    //5
    //Veo inutil un switch para un solo case, habría que cambiarlo a un if o algo por el estilo (por si acaso el mensaje no fuese de tipo 5) 
    //o directamente asumir que el mensaje que se reciba en ese momento va a ser de tipo 5 y ya (lo cual aumentaria la eficiencia al haber menos
    //pasos. Cuando haya muchos clientes sería interesante.
    public void env_mensajeTCP(int codigo, int id, String x, String y, String z) throws IOException{
        out = new DataOutputStream(s.getOutputStream());
        switch(codigo){                
            case 5: //El cliente acaba sus iteraciones y va a mandar la latencia
                mensaje = codigo + "|" + id + "|" + latencia;
                
                out.writeUTF(mensaje);
                break;
            default:
                System.out.println("(env_mensaje)CODIGO DE PAQUETE ERRONEO: " + codigo);
        }   
    }
    
    //Envia los mensajes relativos a la fase 2:
    //case 2,3
    //Aqui digo lo mismo que en el de arriba. Un switch me parece demasiado poderoso para solo 2 opciones. Propongo un if(codigo = 2) y luego
    //else if(codigo = 3) por si acaso
    public void env_mensajeUDP(int codigo, int id, int puertoCliente, InetAddress direccion, String x, String y, String z) throws IOException{
        out = new DataOutputStream(s.getOutputStream());
        switch(codigo){
            case 2: //Creo un mensaje de tipo Nuevo desplazamiento
                mensaje = codigo + "|" + id + "|" + x + "|" + y + "|" + z;
                buffer = mensaje.getBytes();
                enviar = new DatagramPacket(buffer, buffer.length, direccion, puertoCliente);
                datagrama.send(enviar);
                break;
                
            case 3: //Creo un mensaje de tipo Recibido desplazamiento de vecino
                mensaje = codigo + "|" + id + "|" + x + "|" + y + "|" + z;
                buffer = mensaje.getBytes();
                enviar = new DatagramPacket(buffer, buffer.length, direccion, puertoCliente);
                datagrama.send(enviar);
                break;

            default:
                System.out.println("(env_mensaje)CODIGO DE PAQUETE ERRONEO: " + codigo);
        }   
    }

    /**
     *
     * @throws IOException
     */
    
    
    public void empezar() throws IOException{
        String x,y,z;
        //System.out.println("Ya estamos todos conectados, podemos comenzar...");
        for(int i = 0; i < numIte; i++){  //  realizamos el numero de iteraciones que tenemos que hacer
            x = generarNumeroAleatorio(0,100)+"";
            y = generarNumeroAleatorio(0,100)+"";
            z = generarNumeroAleatorio(0,100)+"";
            
            
            
            //env_mensajeUDP(2,ide, ,PUERTO,x,y,z); //Ni idea de como pasar lo del InetAddress que tocaria
            
            inicio = System.currentTimeMillis(); //Se inicia el contador
            
            
            Temporizador timer = new Temporizador();
            
            while(contador < (numClie - 1) && timer.continua())//Bucle de espera la confirmacion de todos los clientes. Esto se intercambiará por un timer de 20 seg, tras el cual pasaremos a la siguiente iteracion
                rec_mensajeUDP();

            fin = System.currentTimeMillis(); //La diferencia entre el tiempo desde que empezo hasta ahora
            tiempo = (double) ((fin - inicio));
            
            latencia += tiempo;
                
            contador = 0;
        }

        latencia = latencia /numIte;
        System.out.println("Latencia del Cliente " + ide + ":----------------------------------------" + latencia + " ms.");
        env_mensajeTCP(5,ide,latencia+"",null,null);

        while(!acabado){   //De este bucle solo sale uno de los hilos
            rec_mensajeTCP();
        } 
        
        System.out.println("Acabo el hilo " + ide);
    }
    
    public int generarNumeroAleatorio(int minimo, int maximo){
        return (int) Math.floor(Math.random()*(maximo-minimo+1)+minimo);
    }
}
