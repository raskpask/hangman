package Server.Model;

import java.io.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;

public class WordHandler extends Thread{
    private String userHome = System.getProperty("user.dir");
    private final String WORD_FILE = userHome + "/src/main/java/Server/Model/words.txt";
    private Random randomGenerator = new Random();
    private SelectionKey key;
    private Game game;
    private Selector selector;

   public WordHandler(SelectionKey key, Game game,Selector selector){
    this.key=key;
    this.game=game;
    this.selector = selector;
   }

    public void run(){
       String word = getWord();
       String currentHiddenWord="";
       game.setWord(word);
       for(int i=0; i<word.length();i++){
           currentHiddenWord +="_ ";
       }
       game.setCurrentHiddenWord(currentHiddenWord);
       game.setWordHandler(this);
       key.interestOps(SelectionKey.OP_WRITE);
       selector.wakeup();
    }

    public String getWord(){
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
    public ArrayList<Integer> checkLetter(String word, char letter){
        ArrayList<Integer> position= new ArrayList<>();
        int j=0;
        char[] wordList = word.toCharArray();
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
