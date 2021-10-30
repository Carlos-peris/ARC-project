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
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
        /**
         * Variable ->    "control_GUI"
         * true  para control por vista gráfica
         * false para control por consola normal
         */
        boolean control_GUI = true;// info^^
        boolean unavez = false;         //Para reenviar datos correctos
        boolean continuar = false;
        int PUERTO_CONTROL  = 7685;
        int PUERTO_CLIENTES = 10740;
        int numClie = 0;
        int numGrup = 0;
                
        //Creo el socket para comunicar con el control
        ServerSocket servSoc = new ServerSocket(PUERTO_CONTROL);
        //Creo el socket de la vista.
        Socket socketContol = null;
        //Creo canal para recibir datos:
        DataInputStream controlIN;
        //Variable del mensaje
        String mensajeDelControl;
        
        System.out.println("Proyecto ARC 2021.");
        System.out.println("");
        System.out.println("Prototipo 1.");   
        System.out.println("Realizado por: Carlos, David, Alex, Sergio y Raúl.");
        System.out.println("");
        System.out.println("");
        System.out.println("Servidor iniciado");
        
        do{
            if(control_GUI)
            {
                System.out.println("Esperando datos. . .");
                //Creo el socket para comunicar con el control
                socketContol = servSoc.accept();
                //Instancio la via de comunicación
                controlIN  = new DataInputStream(socketContol.getInputStream());
                //Espero a que llegue mensaje del control.
                mensajeDelControl = controlIN.readUTF(); //Espera a que llegue mensaje
                do{
                    if(unavez) {System.out.println("Recibiendo datos. . .");}
                    unavez = true;
                    String[] infoIni = mensajeDelControl.split("\\|");
                    numClie = Integer.parseInt(infoIni[0]);
                    numGrup = Integer.parseInt(infoIni[1]);
                    if(unavez) {System.out.println("Listo para empezar.");}
                    mensajeDelControl = controlIN.readUTF(); //Espera a que llegue mensaje
                }while(!"".equals(mensajeDelControl));
                socketContol.close();
            }
            else
            {
                System.out.print("Inserte numero de Clientes: ");
                Scanner scanner = new Scanner(System.in);
                scanner.useDelimiter("\n");
                numClie = scanner.nextInt();

                System.out.print("Inserte numero de Grupos: ");
                scanner = new Scanner(System.in);
                scanner.useDelimiter("\n");
                numGrup = scanner.nextInt();
            }

            if(numClie%numGrup != 0)
                    System.out.println("¡Diablos senyor! Los clientes han de ser divisibles con los grupos");
            else if (numClie/numGrup < 5 || numClie/numGrup > 15)
                    System.out.println("¡Al carajo! ¡Solo puede haber entre 5 y 15 clientes por grupos!");
            else{
                Servidor server = new Servidor(numClie, numGrup);
                server.start();
            }

            if(control_GUI)
            {
                controlIN  = new DataInputStream(socketContol.getInputStream());
                mensajeDelControl = controlIN.readUTF();
                if( "".equalsIgnoreCase(mensajeDelControl) )
                    continuar = true;
                else
                    continuar = false;
            }
        }while(continuar);
        socketContol.close();
    }
}
