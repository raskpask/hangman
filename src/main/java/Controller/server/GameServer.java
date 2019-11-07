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

            while (true) {
                try {
                clientMessage = this.inFromClient.readLine();
                String[] token = clientMessage.split(";");
                String[] requests = token[1].split(":");

                //As long as the recived message is smaller than the full message it will continue to read.
                while (getLength(requests[1])<Integer.parseInt(requests[0])){
                    clientMessage += this.inFromClient.readLine();
                }

                if (requests.length > 1) {
                    //System.out.println("SERVER received: " + clientMessage);
                    newMessage = game.requestHandler(requests[1],token[0]);
                    this.outToClient.println(newMessage);
                }
                } catch (Exception e) {

                }
            }

    }
    // Needs to be duplicated because of the code has to be in the client and the server. The server should be fine without the code of client.
    private int getLength(String message){
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);
            objectOutputStream.writeBytes(message);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        return byteOutputStream.toByteArray().length;
    }
}