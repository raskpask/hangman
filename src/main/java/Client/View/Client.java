package Client.View;

import java.io.*;
import java.net.*;
import java.nio.channels.SocketChannel;

public class Client extends Thread{
private String token;

    public void run(){

        String message;
        try {
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            SocketChannel channel = SocketChannel.open(new InetSocketAddress("localhost",4444));
            channel.configureBlocking(false);
            Gameboard printer= new Gameboard();

            System.out.println(printer.getGameboard()+printer.gameInfo());
            while(true) {
                message = inputHandler(inFromUser.readLine(),inFromUser);
                if(!message.equals(":WrongInput:")) {
                    ClientRequestHandler clientRequestHandler = new ClientRequestHandler(channel, printer, message, this.token, this);
                    clientRequestHandler.start();
                } else {
                    System.out.println(printer.getGameboard()+printer.gameInfo());
                }
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
            System.out.println("Write username and press enter");
            String credentials="";
            try {
                credentials = inFromUser.readLine();
                System.out.println("Write password and press enter");
                credentials += ","+ inFromUser.readLine();
            } catch (Exception e){
                e.printStackTrace();
            }
            return "login,"+ credentials;
        } else if(input.length()<1) {
            //this.header.setRequest(":guess, ");
            System.out.println("You have to write something!");
            return ":WrongInput:";
        } else if (input.equals("quit")) {

            System.exit(0);
            return "";
        } else {
            //this.header.setRequest(":guessWord,"+input);
            return "guessWord,"+input;
        }

    }




}
