/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Proyecto;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Carlos
 */
public class Servidor_Hilo extends Thread {
    private DataInputStream in;
    private DataOutputStream out;
    private String nombreCliente;
    
    public Servidor_Hilo(DataInputStream in, DataOutputStream out, String nombreCliente){
        this.in = in;
        this.out = out;
        this.nombreCliente = nombreCliente;
    }
    
    //Esto debería gestionar que cuando le llega un aviso de cambio de coordendas, 
    //avise a todos los del grupo
    @Override
    public void run(){
        
    
    }
    
}
