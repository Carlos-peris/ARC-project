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

/**while
 *
 * @author pc_es
 */
public class ClienteHilo extends Thread {
    private final int PUERTO = 10740; 
    private final String HOST = "localHost";
    
    private DataOutputStream out;
    private DataInputStream in;
    private Socket s;                         
   
    private int numIte, numCliexGrup, contador, id_rec, ide;
    private float latencia = 0;
    private double inicio, tiempo;
    private boolean acabado = false; 

    public ClienteHilo(int numIte, int numCliexGrup) {
        this.numIte = numIte;
        this.numCliexGrup = numCliexGrup;
    }
    
    @Override
    public void run() {
        try { 
            s = new Socket (HOST, PUERTO);
            s.setSoTimeout(20*1000);
            rec_mensaje();
            rec_mensaje(); 
            
        } catch (IOException ex) {
            Logger.getLogger(ClienteHilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void rec_mensaje() {
        int codigo;
        String x, y, z, mensaje;
        
        try{
        in = new DataInputStream(s.getInputStream());
        mensaje = in.readUTF();
        
        String[] parts = mensaje.split("\\|");
        codigo = Integer.parseInt(parts[0]);
        
        switch(codigo){
            case 1:
                ide = parseInt(parts[1]);
                break;
                
            case 2:
                id_rec = parseInt(parts[1]);
                
                x = parts[2];
                y = parts[3];
                z = parts[4];
                
                env_mensaje(3,id_rec,x,y,z);
                break;
            
            case 3:
                contador++;
                break;
                
            case 4:
                empezar();
                break;
                
            case 5: 
                acabado = true;
                break;
            default:
                System.out.println("(rec_mensaje)CODIGO DE PAQUETE ERRONEO: " + codigo);
        }
        } catch (IOException e){System.out.println("TIME OUT."); contador++;}
    }
    
    public void env_mensaje(int codigo, int id, String x, String y, String z) throws IOException{
        out = new DataOutputStream(s.getOutputStream());
        
        String mensaje;
        
        switch(codigo){
            case 2:
                mensaje = codigo + "|" + id + "|" + x + "|" + y + "|" + z;
                out.writeUTF(mensaje);
                break;
                
            case 3:
                mensaje = codigo + "|" + id + "|" + x + "|" + y + "|" + z;
                out.writeUTF(mensaje);
                break;
                
            case 5:
                mensaje = codigo + "|" + id + "|" + latencia;;
                out.writeUTF(mensaje);
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
        
        for(int i = 0; i < numIte; i++){
            x = generarNumeroAleatorio(0,100)+"";
            y = generarNumeroAleatorio(0,100)+"";
            z = generarNumeroAleatorio(0,100)+"";
            
            inicio = System.currentTimeMillis();
            
            env_mensaje(2,ide,x,y,z);
            contador = 0;
          
            while(contador < (numCliexGrup - 1))
                rec_mensaje();

            tiempo = System.currentTimeMillis();
            
            tiempo -= inicio;
            latencia += tiempo;
        }

        latencia = latencia/numIte;
        
        System.out.println("Latencia del Cliente " + ide + ":----------------------------------------" + latencia + " ms.");
        env_mensaje(5,ide,latencia+"",null,null);

        while(!acabado)
            rec_mensaje();
        
        s.close();
    
        System.out.println("Acabo el hilo " + ide);
    }
    
    public int generarNumeroAleatorio(int minimo, int maximo){
        return (int) Math.floor(Math.random()*(maximo-minimo+1)+minimo);
    }
}
