package Server.server.Controller;
import Client.View.Client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientHandler extends Thread {
    private Server server;
    private SocketChannel channel;
    ClientHandler(Server server, SocketChannel channel){
        this.server = server;
        this.channel = channel;
    }


}
