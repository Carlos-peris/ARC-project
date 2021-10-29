/**
 * Clase ServidoMain
 * 
 *      Esta clase sirve para:
 *          -Mostrar una pequeña información por pantalla.
 *          -Instanciar la clase Servidor
 *          -Llamar al método para lanzar el servidor.
 */
package Main;

import Cliente.ClienteHilo;
import Servidor.Servidor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author pc_es
 */
public class ServidorMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Proyecto ARC 2021.");
        System.out.println("");
        System.out.println("Prototipo 1.");   
        System.out.println("Realizado por: Carlos, David, Alex, Sergio y Raúl.");
        System.out.println("");
        System.out.println("");
        System.out.println("Servidor iniciado");
        
        System.out.print("Inserte numero de Clientes: ");
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");
        int numClie = scanner.nextInt();
            
        System.out.print("Inserte numero de Grupos: ");
        scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");
        int numGrup = scanner.nextInt();
        
        if(numClie%numGrup != 0)
                System.out.println("¡Diablos senyor! Los clientes han de ser divisibles con los grupos");
        else if (numClie/numGrup < 5 || numClie/numGrup > 15)
                System.out.println("¡Al carajo! ¡Solo puede haber entre 5 y 15 clientes por grupos!");
        else{
            Servidor server = new Servidor(numClie, numGrup);
            server.start();
        }
    }
}
