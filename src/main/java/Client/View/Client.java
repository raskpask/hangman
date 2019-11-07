package Client.View;

import java.io.*;
import java.net.*;

public class Client extends Thread{
private String token;
    public void run(){

        String message;
        try {
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            Socket socket = new Socket("localhost", 4444);
            PrintWriter outToServer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Gameboard printer= new Gameboard();

            System.out.println(printer.getGameboard()+printer.gameInfo());
            while(true) {
                message = inputHandler(inFromUser.readLine(),inFromUser);
                ClientRequestHandler clientRequestHandler = new ClientRequestHandler(outToServer,inFromServer,printer,message,this.token,this);
                clientRequestHandler.start();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void setToken(String token) {
        this.token = token;
    }

    private String inputHandler(String input, BufferedReader inFromUser){
        if(0<input.length() && input.length()<2){
            return "guess,"+input;
        } else if(input.equals("new")){
            //this.header.setRequest(":newWord,"+input);
            return "newWord";
        } else if(input.equals("login")){
            System.out.println("Write username and password separated with ',' like: user,pass ");
            String credentials="";
            try {
                credentials = inFromUser.readLine();
            } catch (Exception e){
                e.printStackTrace();
            }
            return "login,"+ credentials;
        } else if(input.length()<1) {
            //this.header.setRequest(":guess, ");
            System.out.println("You have to write something a attempt will be lost");
            return "guess, ";
        } else if (input.equals("quit")) {

            System.exit(0);
            return "";
        } else {
            //this.header.setRequest(":guessWord,"+input);
            return "guessWord,"+input;
        }

    }




}
