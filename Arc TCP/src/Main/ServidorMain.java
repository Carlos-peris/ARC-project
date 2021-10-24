/**
 * Clase ServidoMain
 * 
 *      Esta clase sirve para:
 *          -Mostrar una pequeña información por pantalla.
 *          -Instanciar la clase Servidor
 *          -Llamar al método para lanzar el servidor.
 */
package Main;


import Servidor.Servidor;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author pc_es
 */
public class ServidorMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        System.out.println("Proyecto ARC 2021.");
        System.out.println("");
        System.out.println("Prototipo 1.");   
        System.out.println("Realizado por: Carlos, David, Alex, Sergio y Raúl.");
        System.out.println("");
        System.out.println("");
        
        Servidor server;
        try {
            server = new Servidor();
            server.start();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(ServidorMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        

    }
    
}
