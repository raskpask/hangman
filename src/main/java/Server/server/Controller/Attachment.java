package Server.server.Controller;

import Server.Model.Game;

import java.nio.ByteBuffer;

public class Attachment {
    private Game game;
    private ByteBuffer buffer;

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
}
