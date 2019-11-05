package Model;

import java.util.ArrayList;
import java.lang.*;


public class Game extends Thread {
    private String usedLetters;
    private boolean alive=true;
    private boolean hasWon=false;
    private int score=0;
    private int remainingAttempts;
    private String word = "";
    private String currentHiddenWord="";
    WordHandler wordHandler = new WordHandler();
    public void run() {
    }

    // The request string will look like: "request,letter/word"
    // The response string will look like: "request,requestInfo,remainingAttempts,Score,Alive,usedLetters,Win"
    public String requestHandler(String request)throws InterruptedException{
        String[] requestArray = request.split(",");

        switch(requestArray[0]) {
            case "newWord":
                this.newWord();
                return "newWord,"+ this.currentHiddenWord +","+this.remainingAttempts+","+this.score+","+this.alive+","+this.usedLetters+","+this.hasWon;

            case "guess":
                guess(requestArray[1].toCharArray());
                checkVictoryOrLoss();
                return "guess,"+ this.currentHiddenWord +","+this.remainingAttempts+","+this.score+","+this.alive+","+this.usedLetters+","+this.hasWon;

            case "guessWord":
                this.guessWord(requestArray[1]);
                checkVictoryOrLoss();
                return "guessWord,"+this.currentHiddenWord+","+this.remainingAttempts+","+this.score+","+this.alive+","+this.usedLetters+","+this.hasWon;
        }
        return "error";
    }

    public void newWord(){
        this.usedLetters="";
        this.hasWon=false;
        this.remainingAttempts = 7;
        this.alive=true;
        this.word=wordHandler.getWord();
        this.currentHiddenWord ="";
        for(int i=0; i<this.word.length();i++){
            this.currentHiddenWord +="_ ";
        }
    }

    public void guess(char[] letters){
        if(!hasWon) {
            char guessedLetter = Character.toLowerCase(letters[0]);
            this.usedLetters += guessedLetter+" ";
            char[] currentHiddenWord = this.currentHiddenWord.toCharArray();
            ArrayList indexOfLetters = this.wordHandler.checkLetter(this.word, guessedLetter);
            System.out.println("Word: " + this.word);
            if (indexOfLetters.isEmpty()) {
                this.remainingAttempts--;
            } else {
                for (int i = 1; i < currentHiddenWord.length; i += 2) {
                    for (int j = 0; j < indexOfLetters.size(); j++) {
                        if (i / 2 == (int) indexOfLetters.get(j)) {
                            currentHiddenWord[i - 1] = guessedLetter;
                        }
                    }
                }
            }
            this.currentHiddenWord = new String(currentHiddenWord);
        }
    }

    private void guessWord(String guess){
        if(this.currentHiddenWord.equals(this.wordHandler.checkWord(this.word,guess,this.currentHiddenWord))){
            this.remainingAttempts--;
        }
        this.currentHiddenWord = this.wordHandler.checkWord(this.word,guess,this.currentHiddenWord);
    }

    private void checkVictoryOrLoss(){
        if(!this.currentHiddenWord.contains("_")){
            if(!hasWon) {
                this.score++;
            }
            this.hasWon=true;
        }
        if(this.remainingAttempts<0){
            this.alive=false;
        }
    }
}
