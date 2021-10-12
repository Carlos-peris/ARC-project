/**
 * Clase ClienteHilo
 * 
 *      Cada instancia de esta clase es un cliente que
 *      se conecta al servidor.
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

/**
 *
 * @author pc_es
 */
public class ClienteHilo extends Thread {
    private final int PUERTO = 1234;            //Puerto que van a usar los mensajes del proyecto
    protected String mensaje;                   //String con el que vamos a leer y enviar los mensajes
    private final String HOST = "localHost";    //Direccion del host (servidor)
    private Socket s;                           //Sockets para enviar(se) y recibir(sr)
    private DataOutputStream out;               //Creación del canal de salida  al servidor   (cliente -> servidor)
    private DataInputStream in;                 //Creación del canal de entrada al servidor   (cliente <- servidor)
    private final int numIte;                   //Numero de iteraciones de la simulacion
    private final int numClie;                  //Numero de clientes de la simulacion
    private float latencia = 0;
    private int ide;
    private int contador;                   //Cuenta el numero de respuestas recibidas
    private long inicio, fin;
    private double tiempo;
    private boolean acabado = false;  //Servira para cuando el servidor sea quien nos indica cuando se acaba
    byte[] buffer = new byte[1024];
    //UDP
    DatagramSocket datagrama;               //Se usa para enviar mensajes UDP
    DatagramPacket recibir, enviar;         //Donde se almacena el datagrama a enviar/recibir
    InetAddress direccion;                  //Dirección a donde enviar el datagrama UDP
    int puertoCliente;                      //Puerto que van a usar los mensajes del proyecto

    
    /**
     * Constructor de la clase CLienteHilo
     * 
     *      Guarda los datos introducidos en las variables locales de la clase
     * 
     * @param numIte    Numero de iteraciones de la simulacion
     * @param numClie   Numero de clientes de la simulacion
     */
    public ClienteHilo(int numIte, int numClie) {
        this.numIte = numIte;
        this.numClie = numClie;
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
    

    /**
     * Recibir mensajes TCP del servidor
     *      
     *      Se encarga de recibir los mensajes relativos a la fase 1,3
     *      (case 1,4,5)
     * 
     * @throws IOException 
     */
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
    
    
    /**
     * Recibir mensajes UDP del servidor
     *      
     *      Se encarga de recibir los mensajes relativos a la fase 2
     *      (case 2,3)
     * @throws IOException 
     */
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
        
        //Transforma dirección en InetAddress
        //Estas 3 lineas no las borreis. Cuando usemos el servidor las usaremos.
            //InetAddress inetHOST = InetAddress.getByName("www.alexms.es");
            //InetAddress inetHOST = InetAddress.getByName("8.8.8.8");
            InetAddress inetHOST = InetAddress.getLocalHost();
        
        //System.out.println("Ya estamos todos conectados, podemos comenzar...");
        for(int i = 0; i < numIte; i++){  //  realizamos el numero de iteraciones que tenemos que hacer
            x = generarNumeroAleatorio(0,100)+"";
            y = generarNumeroAleatorio(0,100)+"";
            z = generarNumeroAleatorio(0,100)+"";
            
            
            //Envía coordenadas al servidor para reenviarlas a todos los clientes del grupo
            env_mensajeUDP(2,ide,PUERTO,inetHOST,x,y,z);
            
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
