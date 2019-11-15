package Server.Model;


import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class WordHandler extends Thread{
    private String userHome = System.getProperty("user.dir");
    private final String WORD_FILE = userHome + "/src/main/java/Server/Model/words.txt";
    private Random randomGenerator = new Random();
    private SelectionKey key;
    private Game game;
    private Selector selector;
    private String word;

    private boolean write;


   public WordHandler(SelectionKey key, Game game,Selector selector){
    this.key=key;
    this.game=game;
    this.selector = selector;
   }

    public void run(){
       FileReaderServer fileReaderServer = new FileReaderServer(game,key,selector,this);
       fileReaderServer.start();
       startConnection();
    }
    public void setWrite(boolean write){
       this.write = write;
    }
    public void setWord(String word){
       this.word=word;
    }
    public boolean getWrite(){
       return write;
    }
    public void getNewWord(){
        startConnection();
    }
    private void startConnection(){
       try {
           SocketChannel wordChannel = SocketChannel.open(new InetSocketAddress("localhost", 5555));
           wordChannel.configureBlocking(false);
           byte[] messageToServer = "Get a word".getBytes();
           ByteBuffer buffer = ByteBuffer.wrap(messageToServer);
           wordChannel.write(buffer);
           System.out.println("Sending message to filereader ");
           key.interestOps(SelectionKey.OP_READ);
           wordChannel.register(selector, SelectionKey.OP_READ);

           key.interestOps(SelectionKey.OP_READ);
       }catch (Exception e){
           e.printStackTrace();
       }
    }



    private String getWord(){
        String word = "";
        try {
            File file = new File(WORD_FILE);
            Scanner scanner = new Scanner(file);
            int random = randomGenerator.nextInt(51528);
            for(int i=0;i< random;i++){
                scanner.next();
            }
            word = scanner.next();


        } catch(FileNotFoundException e){
            e.printStackTrace();
        }
        return word;
    }
    private void getWordC(){
       try {
           //int random = randomGenerator.nextInt(51528);
           //int randomSizeArray = randomGenerator.nextInt(460863);
           //RandomAccessFile file = new RandomAccessFile(WORD_FILE, "r");
           //FileChannel inChannel = file.getChannel();

           //ByteBuffer buffer = ByteBuffer.allocateDirect(randomSizeArray);
           //inChannel.read(buffer);
           //String[] words = extractMessageFromBuffer(buffer).split("\n");
           //inChannel.close();
           //this.word = words[words.length -2];
           //write=true;
           //return words[words.length -2];
       } catch (Exception e ){
           e.printStackTrace();
       }
       //return "No word found";
    }
    private String extractMessageFromBuffer(ByteBuffer buffer) {
        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return new String(bytes);
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
