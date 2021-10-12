/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cliente;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Carlos
 */
public class Temporizador {

    private boolean continua = true;
    
    public Temporizador(){

        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            int i = 20;

            public void run() {
                i--;

                if (i < 0) {
                    timer.cancel();
                    continua = false;
                }
            }
        }, 0, 20);
    }   
    
    public boolean continua(){
        return continua;
    }
}
