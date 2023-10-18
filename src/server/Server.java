package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import controller.Worker;

/**
 *
 * @author Alexander Epelde
 */
public abstract class Server {

    private static final int PORT = 42069;
    private static ServerSocket serverSocket;
    private static Socket client;

    public static void main(String[] args) {

        while (true) {
            client = null;
            try {
                serverSocket = new ServerSocket(PORT);
                System.out.println("Esperando conexiones del cliente...");
                client = serverSocket.accept();
                System.out.println("Cliente conectado");
                new Worker(client);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (serverSocket != null) {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

        }

    }
}
