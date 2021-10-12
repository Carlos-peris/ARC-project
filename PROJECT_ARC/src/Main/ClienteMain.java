/**
 * Clase ClienteMain
 * 
 *      Esta clase hace esto:
 *          -Pregunta cuantas iteraciones va a tener la simulación
 *          -Pregunta cuantos clientes van a haber (para generarlos)
 *          -Crea un hilo por cliente y los activa.
 */
package Main;

import Cliente.ClienteHilo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author pc_es
 */
public class ClienteMain {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        
        //Creo un array de hilos (cada cliente usará un hilo)
        ArrayList<Thread> clientes = new ArrayList<Thread>();
        
        //Información al usuario
        System.out.println("Proyecto ARC 2021.");
        System.out.println("");
        System.out.println("Prototipo 1.");   
        System.out.println("Realizado por: Carlos, David, Alex, Sergio y Raúl.");
        System.out.println("");
        System.out.println("");
        
        //Introducir numero de iteraciones
        System.out.print("Inserte numero de Iteraciones: ");
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");
        int numIte = scanner.nextInt();
        
        //Despues le pedimos el numero de clientes que va a haber
        System.out.print("Inserte numero de Clientes: ");
        scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");
        int numClie = scanner.nextInt();

        //Instancio un hilo por cliente
        for (int i = 0; i < numClie; i++)
            clientes.add(new ClienteHilo(numIte,numClie));

        //Inicio todos los hilos/clientes (empieza la siumulación)
        for(Thread thread : clientes)
            thread.start();
    }
    
}
