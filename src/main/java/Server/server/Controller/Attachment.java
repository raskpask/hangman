package Server.server.Controller;

import Server.Model.Game;

import java.nio.ByteBuffer;

public class Attachment {
    private Game game;
    private ByteBuffer buffer;
    private String newMessage;

    public Attachment(Game game ,ByteBuffer buffer){
        this.buffer=buffer;
        this.game=game;
    }

    public Game getGame() {
        return game;
    }

    public ByteBuffer getBuffer(){

        return buffer;
    }

    public void setNewMessage(String message) {
        this.newMessage = message;
    }
    public String getNewMessage(){
        return newMessage;
    }
}
