package controller;

import java.net.Socket;

public class Worker extends Thread {
    private Socket cliente;

    public Worker() {
        this.start();
    }

    public Worker(Socket cliente) {
        this.start();
        this.cliente = cliente;
    }

    @Override
    public void run() {
        super.run();
        work();
    }

    private void work(){
        //TODO


    }


}
