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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
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
    private float tiempo;
    private boolean acabado = false;  //Servira para cuando el servidor sea quien nos indica cuando se acaba

    public ClienteHilo(int numIte, int numClie) {
        this.numIte = numIte;
        this.numClie = numClie;
    }
    
    @Override
    public void run() {
        try { 
            s = new Socket (HOST, PUERTO);
            rec_mensaje(); //Recibo mi ide
            System.out.println("Esperando confirmacion del Servidor...");
            rec_mensaje(); //Recibo el ok para comenzar el programa
            
        } catch (IOException ex) {
            Logger.getLogger(ClienteHilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void rec_mensaje() throws IOException{
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
                System.out.println("Me acaban de asignar el ide: "+ide);
                break;
                
            case 2: //Me han pasado un nuevo desplazamiento
                //Extraigo los datos del paquete
                id_rec = parseInt(parts[1]);
                x = parts[2];
                y = parts[3];
                z = parts[4];
                //Y los paso a la funcion para que envie el okay
                //System.out.println("Cliente: "+ide + "Recibe nuevo movimiento.");
                env_mensaje(3,id_rec,x,y,z);
                break;
            
            case 3:             //Has recibido un okay
                //System.out.println("Cliente: "+ide + "Recibe OK.");
                contador++;
                break;
                
            case 4://Hemos recibido el OK del server que podemos empezar
                //Llamamos a la funcion que genere numeros random
                empezar();
                break;
                
            case 5: //El servidor indica que es hora de desconectarse. (implementar más adelante)
                acabado = true;
                break;
            default:
                System.out.println("(rec_mensaje)CODIGO DE PAQUETE ERRONEO: " + codigo);
        }
        
    }
    
    public void env_mensaje(int codigo, int id, String x, String y, String z) throws IOException{
        out = new DataOutputStream(s.getOutputStream());
        switch(codigo){
            case 2: //Creo un mensaje de tipo Nuevo desplazamiento
                mensaje = codigo + "|" + id + "|" + x + "|" + y + "|" + z;
                //System.out.println("Cliente " + ide +": "+"Envio nueva posicion.");
                out.writeUTF(mensaje);
                break;
                
            case 3: //Creo un mensaje de tipo Recibido desplazamiento de vecino
                mensaje = codigo + "|" + id + "|" + x + "|" + y + "|" + z;
                //System.out.println("Cliente " + ide +": "+"Envio un OK.");
                out.writeUTF(mensaje);
                break;
                
            case 5: //El cliente acaba sus iteraciones y va a mandar la latencia
                mensaje = codigo + "|" + id + "|" + latencia/numIte;
                
                out.writeUTF(mensaje);
                System.out.println("Envio el puto case 5");
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
            
            env_mensaje(2,ide,x,y,z);//Creamos el mensaje y lo enviamos
            
            float tiempo = System.currentTimeMillis(); //Se inicia el contador
            //System.out.println("Cliente a la espera de confirmacion... " + ide);
            while(contador < (numClie - 1))//Bucle de espera la confirmacion de todos los clientes. Esto se intercambiará por un timer de 20 seg, tras el cual pasaremos a la siguiente iteracion
                rec_mensaje();

            //System.out.println("Cliente recibe todos los OK " + ide);
            tiempo = System.currentTimeMillis() - tiempo; //La diferencia entre el tiempo desde que empezo hasta ahora
            
            latencia += tiempo;
            latencia += 1;
                
            contador = 0;
        }
        System.out.println("Voy a mandar el mensaje 5, atencion!!!!");
        env_mensaje(5,ide,latencia+"","","");
        System.out.println("Latencia del Cliente " + ide + ":----------------------------------------" + latencia);
        //Cuando sea el servidor el que nos diga cuando se acaba, cambiar la linea por:
        // while (!acabado)
        while(true){
            rec_mensaje();
        }
    }
    
    public int generarNumeroAleatorio(int minimo, int maximo){
        return (int) Math.floor(Math.random()*(maximo-minimo+1)+minimo);
    }
}
