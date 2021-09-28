/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import Cliente.Cliente;
import java.io.IOException;

/**
 *
 * @author pc_es
 */
public class ClienteMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        Cliente c = new Cliente();
        c.start();
    }
    
}
