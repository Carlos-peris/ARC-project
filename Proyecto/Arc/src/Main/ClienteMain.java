/**
 * 
 * GRUPOS      -> 800
 * Iteraciones ->   5
 * 
 * Servidor 1000
 * Carlos    100
 * Alex      700
 * David    6200
 * Raul        ?
 * 
 */
package Main;

import Cliente.ClienteHilo;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author pc_es
 */
public class ClienteMain {
    //ELEGIR IP:
          private static final String HOST = "localHost";
          //private static final String HOST = "arc.alexms.es";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        ArrayList<Thread> clientes = new ArrayList<Thread>();
            System.out.println("Proyecto ARC 2021.");
            System.out.println("");
            System.out.println("Prototipo 1.");   
            System.out.println("Realizado por: Carlos, David, Alex, Sergio y Raúl.");
            System.out.println("");
            System.out.println("");
            
            System.out.print("Inserte numero de Clientes: ");
            Scanner scanner = new Scanner(System.in);
            scanner.useDelimiter("\n");
            int numClie = scanner.nextInt();
            
            System.out.print("Inserte numero de Grupos: ");
            scanner = new Scanner(System.in);
            scanner.useDelimiter("\n");
            int numGrup = scanner.nextInt();
            
            System.out.print("Inserte numero de Iteraciones: ");
            scanner = new Scanner(System.in);
            scanner.useDelimiter("\n");
            int numIte = scanner.nextInt();

            for (int i = 0; i < numClie; i++){
                ClienteHilo cliente = new ClienteHilo(numIte,numClie/numGrup,HOST);
                clientes.add(cliente);
                cliente.start();
                sleep(2);
            }       
    }
}
