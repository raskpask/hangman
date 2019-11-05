package View;

import java.io.*;
public class ClientRequestHandler extends Thread {
    public void run(){

    }
    public void sendRequest(PrintWriter outToServer, BufferedReader inFromServer,Gameboard printer,String message){
        String newMessage;
        try {
            outToServer.println(message + '\n');
            newMessage = inFromServer.readLine();
            String[] response = newMessage.split(",");
            checkAliveAndWin(printer, response);
        } catch (Exception e){
            System.out.println(e.getStackTrace());
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
}
