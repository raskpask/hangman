package Server.server.Controller;

import Server.Model.Game;

import javax.sound.midi.SysexMessage;
import java.io.*;
import  java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server extends Thread {
    private String newMessage;
    private String clientMessage;
    public void run(){
        try {
            //SocketChannel channel = SocketChannel.open(new InetSocketAddress('localhost',4444));
            //channel.configureBlocking(false);
            System.out.println("Server is starting...");
            Selector selector = Selector.open();
            Game game = new Game();
            game.start();


            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            ServerSocket serverSocket = serverChannel.socket();
            serverSocket.bind(new InetSocketAddress("localhost",4444));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            //ServerSocket ss = new ServerSocket(4444);
            //System.out.println("SERVER: Server starting...");

            while(true) {
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()){
                    try {
                        SelectionKey key = keys.next();
                        keys.remove();

                        if (key.isAcceptable()) {
                            ServerSocketChannel server = (ServerSocketChannel) key.channel();
                            SocketChannel channel = server.accept();
                            System.out.println("New client connected");
                            channel.configureBlocking(false);
                            channel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));

                        } else if (key.isReadable()) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            ByteBuffer buffer = (ByteBuffer) key.attachment();
                            channel.read(buffer);
                            this.clientMessage = new String(buffer.array()).trim();
                            System.out.println("Message received: " + this.clientMessage);
                            String[] token = clientMessage.split(";");
                            String[] requests = token[1].split(":");
                            this.newMessage = game.requestHandler(requests[1], token[0]);
                            key.interestOps(SelectionKey.OP_WRITE);


                        } else if (key.isWritable()) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            ByteBuffer buffer = (ByteBuffer) key.attachment();

                            byte[] messageToClient = this.newMessage.getBytes();
                            buffer = ByteBuffer.wrap(messageToClient);
                            channel.write(buffer);
                            if (buffer.hasRemaining()) {
                                buffer.compact();
                            } else {
                                buffer.clear();
                            }
                            key.interestOps(SelectionKey.OP_READ);
                            System.out.println("Message sent: " + this.newMessage);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                }
                }
                //System.out.println("SERVER: Waiting for new client");
                //Socket socket = ss.accept();
                //System.out.println("SERVER: New client connected");
                //BufferedReader inFromClient = new BufferedReader((new InputStreamReader(socket.getInputStream())));
                //PrintWriter outToClient = new PrintWriter(socket.getOutputStream(), true);
                //GameServer gameServer = new GameServer(inFromClient, outToClient);
                //gameServer.start();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

}
