package controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import exceptions.BadCredentialsException;
import exceptions.NoSuchUserException;
import exceptions.ServerCapacityException;
import exceptions.ServerErrorException;
import factories.SignableFactory;
import interfaces.Signable;
import packets.Request;
import packets.RequestType;
import packets.Response;
import packets.ResponseType;

public class Worker extends Thread {
    private Socket client;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Response response;
    private Request request;
    private Signable signable;
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

        work();
    }

    private void work() {
        try {
            signable = SignableFactory.getSignable();
            in = new ObjectInputStream(client.getInputStream());
            out = new ObjectOutputStream(client.getOutputStream());
            request = (Request) in.readObject();

            if (request.getRequest().equals(RequestType.LOGIN_REQUEST))
                out.writeObject(new Response(signable.signIn(request.getUser()), ResponseType.OK_RESPONSE));
            else {
                signable.signUp(request.getUser());
                out.writeObject(new Response(request.getUser(), ResponseType.OK_RESPONSE));
            }
            correctExecutionFlag = true;
        } catch (ServerErrorException | IOException | ClassNotFoundException e) {
            response = new Response(request.getUser(), ResponseType.SERVER_ERROR);
        } catch (BadCredentialsException e) {
            response = new Response(request.getUser(), ResponseType.BAD_CREDENTIAL_ERROR);
        } catch (NoSuchUserException e) {
            response = new Response(request.getUser(), ResponseType.NO_SUCH_USER_ERROR);
        } catch (ServerCapacityException e) {
            response = new Response(request.getUser(), ResponseType.SERVER_CAPACITY_ERROR);
        } finally {
            try {
                if (!correctExecutionFlag)
                    out.writeObject(response);
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
                if (client != null)
                    client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
