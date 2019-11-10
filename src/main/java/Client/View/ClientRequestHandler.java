package Client.View;


//import View.Client;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class ClientRequestHandler extends Thread {
private String token;
private SocketChannel channel;
private Selector selector;
private Gameboard printer;
private String message;
private Client client;
    public ClientRequestHandler(SocketChannel channel, Gameboard printer, String message, String token, Client client){
        this.channel=channel;
        this.printer=printer;
        this.message=message;
        this.token=token;
        this.client=client;
    }
    public void run(){
        this.message = token+";"+getLength(this.message)+":"+this.message+"                                                                                                                                           ";

        init();


        while(true){
            channel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
            try {
                selector.select();
                Iterator keysIterator = selector.selectedKeys().iterator();
                while (keysIterator.hasNext()) {
                    SelectionKey key = (SelectionKey) keysIterator.next();
                    if (key.isConnectable()) {
                        System.out.println("Goes inside loop");
                        channel.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        System.out.println("Goes inside loop");
                        receiveMessage(key);
                        key.interestOps(SelectionKey.OP_WRITE);
                    } else if (key.isWritable()) {
                        System.out.println("Goes inside loop");
                        sendMessage(this.message);
                        key.interestOps(SelectionKey.OP_READ);
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            System.out.println(this.printer.getGameboard()+this.printer.gameInfo());
        }

    }

    private void checkAliveAndWin(Gameboard printer,String[] response){
        if(response[6].equals("true")){
            if(response[6].equals("false")){
                printer.makeLine(response); //Print the word once
            }
            printer.endGame(true,response);
            return;
        }
        if(response[4].equals("false")){
            printer.endGame(false,response);
        } else {
            printer.makeLine(response);
        }

    }
    private String getLength(String message){
        try {
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);
            objectOutputStream.writeBytes(message);
            objectOutputStream.flush();
            objectOutputStream.close();
            int length = byteOutputStream.toByteArray().length;
            return String.valueOf(length);
        } catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
    public void init(){
        try{
            this.selector = Selector.open();
            this.channel.register(selector, SelectionKey.OP_CONNECT);

            this.channel = SocketChannel.open();
            this.channel.configureBlocking(false);
            this.channel.connect(new InetSocketAddress("localhost",4444));

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public void receiveMessage(SelectionKey key){
        try {
            ByteBuffer buffer = (ByteBuffer) key.attachment();
            this.channel.read(buffer);

            String newMessage = new String(buffer.array()).trim();
            System.out.println("Client got: " + newMessage);
            String[] response = newMessage.split(",");
            checkAliveAndWin(printer, response);
            this.client.setToken(response[7]);
            if (response[0].equals("loginError")) {
                this.printer.loginErrorLine();
            } else {
                checkAliveAndWin(this.printer, response);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void sendMessage(String message){
        try {
            byte[] messageToServer = message.getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(messageToServer);
            this.channel.write(buffer);
            System.out.println("Sending message: " + message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
