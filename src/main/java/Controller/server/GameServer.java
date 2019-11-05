package Controller.server;
import java.io.*;
import Model.Game;

public class GameServer extends Thread {
    BufferedReader inFromClient;
    PrintWriter outToClient;
    public GameServer(BufferedReader inFromClient, PrintWriter outToClient){
        this.inFromClient=inFromClient;
        this.outToClient=outToClient;
    }
    @Override
    public void run() {
        Game game = new Game();
        game.start();
        String clientMessage;
        String newMessage;
        try {
            while (true) {
                clientMessage = this.inFromClient.readLine();
                String[] requests = clientMessage.split(":");
                if (requests.length > 1) {
                    System.out.println("SERVER received: " + clientMessage);
                    newMessage = game.requestHandler(requests[1]);
                    this.outToClient.println(newMessage);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }
}