package server;

import pool.Pool;

/**
 * Small auxiliary class that handles the exit mechanism of the server
 * 
 * @author Alexander Epelde
 */
public class ExitKeyListener extends Thread {
    @Override
    public void run() {
        try {
            while (true) {
                int input = System.in.read();
                if (input == 112) { // Check for the 'p' key (ASCII value 112)
                    System.out.println("Exit code entered. Exiting...");
                    Pool.removeAll();
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
