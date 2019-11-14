package Client.View;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class ClientRequestHandler extends Thread {
private String token;
private SocketChannel channel;
private Selector selector;
private Gameboard printer;
private String message;
private Client client;
private boolean messageRecived=false;
private boolean timeToSend=false;
private ByteBuffer msgFromServer = ByteBuffer.allocateDirect(1024);
private int id;

    public ClientRequestHandler(SocketChannel channel, Gameboard printer, String message, String token, Client client, int id){
        this.channel=channel;
        this.printer=printer;
        this.message=message;
        this.token=token;
        this.client=client;
        this.id=id;
    }
    public void run(){
        this.message = token+";"+id+":"+this.message+"                                                                                                                                           ";
        timeToSend=true;

            try {
                selector = Selector.open();
                channel.register(selector, SelectionKey.OP_READ);
                while(!messageRecived) {
                    if (timeToSend) {
                        channel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
                        timeToSend = false;
                    }

                    selector.select();
                    for (SelectionKey key : selector.selectedKeys()) {
                        selector.selectedKeys().remove(key);
                        if (!key.isValid()) {
                            continue;
                        }
                        if (key.isReadable()) {
                            receiveMessage(key);
                        } else if (key.isWritable()) {
                            sendMessage(this.message, key);
                        }
                    }
                }
                }catch(Exception e){
                    e.printStackTrace();
                }
        try {
            //disconnect();
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(this.printer.getGameboard()+this.printer.gameInfo());
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

    public void receiveMessage(SelectionKey key){
        try {
            msgFromServer.clear();
            channel.read(msgFromServer);
            String newMessage = extractMessageFromBuffer();
            String[] response = newMessage.split(",");
            checkAliveAndWin(printer, response);
            this.client.setId(Integer.parseInt(response[8]));
            this.client.setToken(response[7]);
            if (response[0].equals("loginError")) {
                this.printer.loginErrorLine();
            } else {
                checkAliveAndWin(this.printer, response);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        this.messageRecived = true;
        key.interestOps(SelectionKey.OP_WRITE);
    }
    private String extractMessageFromBuffer(){
        msgFromServer.flip();
        byte[] bytes = new byte[msgFromServer.remaining()];
        msgFromServer.get(bytes);
        return new String(bytes);
    }
    public void sendMessage(String message,SelectionKey key){
        try {
            byte[] messageToServer = message.getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(messageToServer);
            this.channel.write(buffer);
            System.out.println("Sending message: " + message);
            key.interestOps(SelectionKey.OP_READ);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
