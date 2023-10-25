package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import controller.Worker;

/**
 *
 * @author Alexander Epelde
 */
public abstract class Server {

    /**
     * Declares a global LOG file that will be used for the whole server side
     */
    public static final Logger LOGGER = Logger.getLogger("server.LOG");

    /**
     * Keeps track of the client petitions being processed at the same time
     */
    private static int CONCURRENT_USERS = 0;
    /**
     * The port where the server will be listening at
     */
    private static final int PORT = 42069;
    /**
     * Server socket that will create connections
     */
    private static ServerSocket serverSocket;
    /**
     * Stablished connections that will be served to the workers
     */
    private static Socket client;
    /**
     * Bundle for constants that are succeptible of change
     */
    private static final String propertyBundle = "resources.constants";

    /**
     * Maximum ammount of concurrent users
     */
    private static final int MAX_USERS = Integer.parseInt(ResourceBundle.getBundle(propertyBundle)
            .getString("MAX_USERS"));

    public static void main(String[] args) {
        // infinite loop that is listening for new clients' requests
        while (true) {
            client = null;
            try {
                // build server socket that listens at PORT
                serverSocket = new ServerSocket(PORT);
                System.out.println("Esperando conexiones del cliente...");
                client = serverSocket.accept();
                System.out.println("Cliente conectado");
                if (CONCURRENT_USERS != MAX_USERS) // check if the server is at max capacity
                    new Worker(client);
                else // send the user a message to saying this
                    Worker.handleMaxCapacity(client);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            } finally {
                // closing all necessary objects
                if (serverSocket != null) {
                    try {
                        serverSocket.close();
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, e.getMessage());
                    }
                }

            }

        }

    }

    /**
     * Method that handles the amount of workers at any given point.
     * Intended use: pass '1' to add 1 to the worker count, '0' to subtract 1
     * 
     * @param num
     */
    public static synchronized void changeWorkerCount(int num) {
        if (num == 1)
            CONCURRENT_USERS++;
        else if (num == 0)
            CONCURRENT_USERS--;
    }
}
