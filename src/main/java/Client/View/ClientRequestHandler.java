package Client.View;


//import View.Client;

import java.io.*;
public class ClientRequestHandler extends Thread {
private String token;
private PrintWriter outToServer;
private BufferedReader inFromServer;
private Gameboard printer;
private String message;
private Client client;
    public ClientRequestHandler(PrintWriter outToServer, BufferedReader inFromServer,Gameboard printer,String message,String token,Client client){
        this.outToServer=outToServer;
        this.inFromServer=inFromServer;
        this.printer=printer;
        this.message=message;
        this.token=token;
        this.client=client;
    }
    public void run(){
        String newMessage;
        this.message = token+";"+getLength(this.message)+":"+this.message;
        try {
            this.outToServer.println(this.message + '\n');
            newMessage = this.inFromServer.readLine();
            String[] response = newMessage.split(",");
            client.setToken(response[7]);
            if(response[0].equals("loginError")){
                this.printer.loginErrorLine();
            }else {
                checkAliveAndWin(this.printer, response);
            }
        } catch (Exception e){
            System.out.println(e.getStackTrace());

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
}
