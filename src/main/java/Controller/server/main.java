package Controller.server;

import View.Client;

public class main {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();

        Client client = new Client();
        client.start();
    }
}

