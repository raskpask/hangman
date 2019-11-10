package Server.server.Controller;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import Server.Model.Game;


public class GameServer extends Thread {
    private Selector selector;
    private String newMessage;
    private String clientMessage;
    public GameServer(){

    }
    @Override
    public void run() {
        Game game = new Game();
        game.start();


    }

}