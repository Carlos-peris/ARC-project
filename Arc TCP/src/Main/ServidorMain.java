/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import Cliente.ClienteHilo;
import Servidor.Servidor;
import java.io.IOException;
import java.util.ArrayList;

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
        
        Servidor server = new Servidor();
        
        server.start();

    }
    
}
