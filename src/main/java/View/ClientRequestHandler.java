package View;


import java.io.*;
public class ClientRequestHandler extends Thread {
private String token="";
    public void run(){

    }
    public void sendRequest(PrintWriter outToServer, BufferedReader inFromServer,Gameboard printer,String message){
        String newMessage;
        message = token+";"+getLength(message)+":"+message;
        try {
            outToServer.println(message + '\n');
            newMessage = inFromServer.readLine();
            String[] response = newMessage.split(",");
            this.token = response[7];
            if(response[0].equals("loginError")){
                printer.loginErrorLine();
            }else {
                checkAliveAndWin(printer, response);
            }
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
