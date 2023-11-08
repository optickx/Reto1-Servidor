package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import controller.Worker;

/**
 * Main class for the Server side, that handles all the requests from clients
 * 
 * @author Alexander Epelde
 */
public abstract class Server {

    /**
     * Declares a global LOG file that will be used for the whole server side
     */
    public static final Logger LOGGER = Logger.getLogger("package server\":");

    /**
     * Bundle for constants that are succeptible of change
     */
    private static final String propertyBundle = "resources.constants";
    /**
     * Keeps track of the client petitions being processed at the same time
     */
    private static int CONCURRENT_USERS = 0;
    /**
     * The port where the server will be listening at
     */
    private static final int PORT = Integer.parseInt(ResourceBundle.getBundle(propertyBundle)
            .getString("PORT"));
    /**
     * Server socket that will create connections
     */
    private static ServerSocket serverSocket;
    /**
     * Stablished connections that will be served to the workers
     */
    private static Socket client;

    /**
     * Maximum ammount of concurrent users
     */
    private static final int MAX_USERS = Integer.parseInt(ResourceBundle.getBundle(propertyBundle)
            .getString("MAX_USERS"));

    public static void main(String[] args) {
        setExitKeyListener(); // we stablish a key that if we enter the server will stop
        LOGGER.info("Server connected, Press 'p' to exit");
        //stablishConnection();
        // infinite loop that is listening for new clients' requests
        while (true) {
            client = null;
            try {
                // build server socket that listens at PORT
                serverSocket = new ServerSocket(PORT);
                LOGGER.info("Expecting clients");
                client = serverSocket.accept();
                LOGGER.info("Client connected");
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

    /**
     * Method in charge of stablishing a key that will make the server stop
     */
    private static void setExitKeyListener() {
        ExitKeyListener ekl = new ExitKeyListener();
        ekl.start();
    }

    /**
     * Method that is in charge of connecting to the database server
     *

    private static void stablishConnection() {
        Session session;
        try {
            // set parameters to connect to the host that will port forward the database
            // server
            session = new JSch().getSession(
                    ResourceBundle.getBundle(propertyBundle).getString("HOSTNAME"),
                    ResourceBundle.getBundle(propertyBundle).getString("ODOO_HOST"));
            session.setPassword(
                    ResourceBundle.getBundle(propertyBundle).getString("ODOO_PASSWORD"));
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            // we set a port forward on that host to the database server
            session.setPortForwardingL(
                    Integer.parseInt(ResourceBundle.getBundle(propertyBundle).getString("FORWARDED_PORT")),
                    ResourceBundle.getBundle(propertyBundle).getString("DESTINATION_HOST"),
                    Integer.parseInt(ResourceBundle.getBundle(propertyBundle).getString("DESTINATION_PORT")));
        } catch (JSchException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }
    */
}
