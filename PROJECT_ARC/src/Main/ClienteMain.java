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
        //Nada mas iniciar el cliente pedimos el numero de Iteraciones que va a realizar
            System.out.print("Inserte numero de Iteraciones: ");
            Scanner scanner = new Scanner(System.in);
            scanner.useDelimiter("\n");
            int numIte = scanner.nextInt();
            //Despues le pedimos el numero de clientes que va a ver
            System.out.print("Inserte numero de Clientes: ");
            scanner = new Scanner(System.in);
            scanner.useDelimiter("\n");
            int numClie = scanner.nextInt();
            
            for (int i = 0; i < numClie; i++)
                clientes.add(new ClienteHilo(numIte,numClie));
            
            for(Thread thread : clientes)
                thread.start();
    }
    
}
