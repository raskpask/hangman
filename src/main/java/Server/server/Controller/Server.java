package Server.server.Controller;

import java.io.*;
import  java.net.*;

public class Server extends Thread {
    public void run(){
        try {
            ServerSocket ss = new ServerSocket(4444);
            System.out.println("SERVER: Server starting...");
            while(true) {
                System.out.println("SERVER: Waiting for new client");
                Socket socket = ss.accept();
                System.out.println("SERVER: New client connected");
                BufferedReader inFromClient = new BufferedReader((new InputStreamReader(socket.getInputStream())));
                PrintWriter outToClient = new PrintWriter(socket.getOutputStream(), true);
                GameServer gameServer = new GameServer(inFromClient, outToClient);
                gameServer.start();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

}
