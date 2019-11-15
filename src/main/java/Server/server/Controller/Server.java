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
    private ServerSocketChannel listeningSocketChannel;
    private int gameID=1;
    private Game[] games= new Game[50];
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
                    SelectionKey key = keys.next();
                    keys.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) {
                        startConnection(key,selector);
                    } else if (key.isReadable()) {
                        reciveMessage(key,selector);

                    } else if (key.isWritable()) {
                        sendMessage(key);
                    }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    private String extractMessageFromBuffer(ByteBuffer buffer) {
        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return new String(bytes);
    }
    private void startConnection(SelectionKey key,Selector selector){
        try{
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            System.out.println("The key: "+key.toString());
            SocketChannel channel = server.accept();
            System.out.println("New client connected");

            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ, new Attachment(new Game(),ByteBuffer.allocate(1024)));
            channel.setOption(StandardSocketOptions.SO_LINGER, 5000);
            //key.attach(new Attachment(new Game(),(ByteBuffer) key.attachment()));
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error connecting");
        }
    }
    private void sendMessage(SelectionKey key){
        try{
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buffer;// = (ByteBuffer) key.attachment();
            Attachment attachment = (Attachment) key.attachment();

            byte[] messageToClient = attachment.getNewMessage().getBytes();
            buffer = ByteBuffer.wrap(messageToClient);
            channel.write(buffer);
            if (buffer.hasRemaining()) {
                buffer.compact();
            } else {
                buffer.clear();
            }
            key.interestOps(SelectionKey.OP_READ);
            System.out.println("Message sent: " + attachment.getNewMessage());
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error sending message");
        }
    }
    private void reciveMessage(SelectionKey key,Selector selector){
        try{
            SocketChannel channel = (SocketChannel) key.channel();
            Attachment attachment = (Attachment) key.attachment();
            ByteBuffer buffer = attachment.getBuffer();
            buffer.clear();
            channel.read(buffer);
            String clientMessage = extractMessageFromBuffer(buffer);
            System.out.println("Message received: " + clientMessage);
            String[] token = clientMessage.split(";");
            String[] requests = token[1].split(":");
            attachment.setNewMessage(attachment.getGame().requestHandler(requests[0],token[0],key,selector));
            if(!attachment.getGame().getLastRequestNewWord()) {
                key.interestOps(SelectionKey.OP_WRITE);
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error reciving message");
        }
    }


}
