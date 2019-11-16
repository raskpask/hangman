package Server.Model;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class WordHandler extends Thread{
    private SelectionKey key;
    private Game game;
    private Selector serverSelector;
    private Selector selector;
    private String word;
    private SocketChannel wordChannel;

    private boolean timeToSend;
    private boolean messageSent;
    private FileReaderServer fileReaderServer;



   public WordHandler(SelectionKey key, Game game,Selector selector){
    this.key=key;
    this.game=game;
    this.serverSelector = selector;
   }

    public void run(){
       fileReaderServer = new FileReaderServer(game,key,serverSelector,this);
       fileReaderServer.start();
       startConnection();
    }
    public void setWord(String word){
       this.word=word;
    }
    public void getNewWord(){
       try {
           fileReaderServer.removeWordExtractor();
          sendRequest();
       }catch(Exception e){
           e.printStackTrace();
       }
    }
    private void startConnection(){
       try {
           wordChannel = SocketChannel.open(new InetSocketAddress("localhost", 5555));
           wordChannel.configureBlocking(false);
           sendRequest();
       }catch (Exception e){
           e.printStackTrace();
       }
    }

    private void sendRequest() throws IOException{

        selector = Selector.open();
        wordChannel.register(selector, SelectionKey.OP_READ);
        timeToSend=true;
        messageSent=false;
        while(!messageSent) {
            if (timeToSend) {
                wordChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
                timeToSend = false;
            }

            selector.select();
            for (SelectionKey key : selector.selectedKeys()) {
                selector.selectedKeys().remove(key);
                if (key.isReadable()) {

                } else if (key.isWritable()) {
                    try {
                        byte[] messageToServer = "Get the Word".getBytes();
                        ByteBuffer buffer = ByteBuffer.wrap(messageToServer);
                        wordChannel.write(buffer);
                        key.interestOps(SelectionKey.OP_READ);
                        messageSent = true;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public ArrayList<Integer> checkLetter(String word, char letter){
        ArrayList<Integer> position= new ArrayList<>();
        int j=0;
        char[] wordList = this.word.toCharArray();
        for(int i=0;i<wordList.length;i++){
            if(wordList[i]==letter){
                position.add(i);
                j++;
            }
        }
            return position;

    }
    public String checkWord(String word, String guess,String currentWord){
        if(word.equals(guess)){
            return guess;
        }
        return currentWord;

    }
}
