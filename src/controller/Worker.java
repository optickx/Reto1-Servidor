package controller;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;

import exceptions.BadCredentialsException;
import exceptions.NoSuchUserException;
import exceptions.ServerCapacityException;
import exceptions.ServerErrorException;
import exceptions.UserAlreadyExistsException;
import factories.SignableFactory;
import interfaces.Signable;
import packets.Request;
import packets.RequestType;
import packets.Response;
import packets.ResponseType;
import server.Server;

/**
 * Worker thread that handles the request of the user and returns the
 * appropriate response
 * 
 * @author Alexander Epelde
 */

public class Worker extends Thread {
    /**
     * Recieved client from the server class
     */
    private Socket client;

    /**
     * InputStream extracted from the client Socket
     */
    private ObjectInputStream in;
    /**
     * OutputStream extracted from the client Socket
     */
    private ObjectOutputStream out;
    /**
     * Packet sent to the user after handling his Request
     */
    private Response response;
    /**
     * Packet received from the user
     */
    private Request request;
    /**
     * Data access object
     */
    private Signable signable;
    /**
     * Flag that is set to true only if no Exception is thrown while handling the
     * request
     */
    private boolean correctExecutionFlag = false;

    public Worker() {
        this.start();
    }

    public Worker(Socket client) {
        this.start();
        this.client = client;
    }

    @Override
    public void run() {
        super.run();
        Server.changeWorkerCount(1);

        work();
    }

    /**
     * Method that resolves the petition of the user
     */
    private void work() {
        try {
            // get the data acess object
            signable = SignableFactory.getSignable();
            // unpackage the streams
            in = new ObjectInputStream(client.getInputStream());
            out = new ObjectOutputStream(client.getOutputStream());
            request = (Request) in.readObject();

            if (request.getRequest().equals(RequestType.LOGIN_REQUEST)) // handle the login request
                out.writeObject(new Response(signable.signIn(request.getUser()), ResponseType.OK_RESPONSE));
            else { // handle signup request
                signable.signUp(request.getUser());
                out.writeObject(new Response(request.getUser(), ResponseType.OK_RESPONSE));
            }
            correctExecutionFlag = true; // everything executed correctly

            // handle every exception
        } catch (ServerErrorException e) {
            response = new Response(request.getUser(), ResponseType.SERVER_ERROR);
        } catch (BadCredentialsException e) {
            response = new Response(request.getUser(), ResponseType.BAD_CREDENTIAL_ERROR);
        } catch (NoSuchUserException e) {
            response = new Response(request.getUser(), ResponseType.NO_SUCH_USER_ERROR);
        } catch (ServerCapacityException e) {
            response = new Response(request.getUser(), ResponseType.SERVER_CAPACITY_ERROR);
        } catch (UserAlreadyExistsException e) {
            response = new Response(request.getUser(), ResponseType.USER_ALREADY_EXISTS_ERROR);
        } catch (Exception e) {
            Server.LOGGER.log(Level.SEVERE, e.getMessage());
        } finally {
            try {
                if (!correctExecutionFlag) // get back to the user with the aproppriate response error
                    out.writeObject(response);
                // close objects that need closing
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
                if (client != null)
                    client.close();
            } catch (Exception e) {
                Server.LOGGER.log(Level.SEVERE, e.getMessage());
            }
            Server.changeWorkerCount(0); // signal to the Server that the thread execution is done
        }
    }

    /**
     * Method that handles the Server's max capacity, quickly giving the user an
     * appropriate response
     * 
     * @param client Received connection to the user
     */
    public static void handleMaxCapacity(Socket client) {
        ObjectInputStream in = null;
        ObjectOutputStream out = null;
        try {
            // unpackage the streams
            in = new ObjectInputStream(client.getInputStream());
            out = new ObjectOutputStream(client.getOutputStream());
            Request request = (Request) in.readObject();
            // write to the user signaling a server overload
            out.writeObject(new Response(request.getUser(), ResponseType.SERVER_CAPACITY_ERROR));
        } catch (Exception e) {
            Server.LOGGER.log(Level.SEVERE, e.getMessage());
        } finally {
            try { // close all the objects that need closing
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
                if (client != null)
                    client.close();
            } catch (Exception e) {
                Server.LOGGER.log(Level.SEVERE, e.getMessage());
            }
        }
    }
}
