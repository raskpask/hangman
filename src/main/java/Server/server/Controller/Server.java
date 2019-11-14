package Server.server.Controller;

import Server.Model.Game;

import  java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server extends Thread {
    private String newMessage;
    private ByteBuffer msgFromClient = ByteBuffer.allocateDirect(1024);
    private ByteBuffer msgToClient = ByteBuffer.allocate(1024);
    private ServerSocketChannel listeningSocketChannel;
    //private int gameID=1;
    //private Game[] games= new Game[50];
    public void run(){
        try {

            System.out.println("Server is starting...");
            Selector selector = Selector.open();
            listeningSocketChannel = ServerSocketChannel.open();
            listeningSocketChannel.bind(new InetSocketAddress("localhost",4444));
            listeningSocketChannel.configureBlocking(false);
            listeningSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while(true) {
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()){
                    try {
                        SelectionKey key = keys.next();
                        keys.remove();
                        if (!key.isValid()) {
                            continue;
                        }
                        if (key.isAcceptable()) {
                            ServerSocketChannel server = (ServerSocketChannel) key.channel();
                            Game game1 = new Game();
                            game1.start();
                            System.out.println("The key: "+key.toString());
                            SocketChannel channel = server.accept();
                            System.out.println("New client connected");
                            channel.configureBlocking(false);
                            channel.register(selector, SelectionKey.OP_READ,game1);
                            channel.setOption(StandardSocketOptions.SO_LINGER, 5000);

                        } else if (key.isReadable()) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            msgFromClient.clear();
                            channel.read(msgFromClient);
                            String clientMessage = extractMessageFromBuffer();
                            System.out.println("Message received: " + clientMessage);
                            String[] token = clientMessage.split(";");
                            String[] requests = token[1].split(":");

                            Game game = (Game) key.attachment();
                            newMessage = game.requestHandler(requests[1],token[0]);
                            newMessage = newMessage + "," + requests[0];
                            key.interestOps(SelectionKey.OP_WRITE);


                        } else if (key.isWritable()) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            byte[] messageToClient = this.newMessage.getBytes();
                            msgToClient = ByteBuffer.wrap(messageToClient);
                            channel.write(msgToClient);
                            if (msgToClient.hasRemaining()) {
                                msgToClient.compact();
                            } else {
                                msgToClient.clear();
                            }
                            key.interestOps(SelectionKey.OP_READ);
                            System.out.println("Message sent: " + this.newMessage);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    private String extractMessageFromBuffer() {
        msgFromClient.flip();
        byte[] bytes = new byte[msgFromClient.remaining()];
        msgFromClient.get(bytes);
        return new String(bytes);
    }
    private void readMessage(){

    }

}
