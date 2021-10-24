/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
     */
    public static void main(String[] args) throws IOException {
        ArrayList<Thread> clientes = new ArrayList<Thread>();
        
            System.out.println("Proyecto ARC 2021.");
            System.out.println("");
            System.out.println("Prototipo 1.");   
            System.out.println("Realizado por: Carlos, David, Alex, Sergio y Raúl.");
            System.out.println("");
            System.out.println("");
            //Despues le pedimos el numero de clientes que va a ver
            System.out.print("Inserte numero de Clientes: ");
            Scanner scanner = new Scanner(System.in);
            scanner.useDelimiter("\n");
            int numClie = scanner.nextInt();
            
            System.out.print("Inserte numero de Grupos: ");
            int numGrup = scanner.nextInt();
            
            System.out.print("Inserte numero de Iteraciones: ");
            int numIte = scanner.nextInt();
            

            
            for (int i = 0; i < numClie; i++)
                clientes.add(new ClienteHilo(numIte, numClie, numGrup));
            
            for(Thread thread : clientes)
                thread.start();
    }
    
}
