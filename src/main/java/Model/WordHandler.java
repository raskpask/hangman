package Model;

import java.io.*;
import java.util.*;

public class WordHandler {
    String userHome = System.getProperty("user.dir");
    public final String WORD_FILE = userHome + "/src/main/java/Model/words.txt";
    Random randomGenerator = new Random();
    public String getWord(){
        String word = "";
        try {
            System.out.println(userHome);
            File file = new File(WORD_FILE);
            Scanner scanner = new Scanner(file);
            System.out.println(scanner.next());
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
