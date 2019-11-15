package Server.Model;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.lang.*;


public class Game extends Thread {
    private String usedLetters;
    private boolean alive=true;
    private boolean hasWon=false;
    private int score=0;
    private int remainingAttempts;
    private String word = "";
    private String currentHiddenWord=" ";
    private String token= "token";
    private WordHandler wordHandler;
    private JavaToken javaToken = new JavaToken();
    private String usernameDB="jakob";
    private String passwordDB="molin";
    private boolean lastRequestNewWord=false;

    private String request;
    String[] requestArray;

    public void run() {
    }
    public void setWord(String word){
        this.word=word;
    }
    public void setCurrentHiddenWord(String currentHiddenWord){
        this.currentHiddenWord = currentHiddenWord;
    }
    public void setWordHandler(WordHandler wordHandler){
        this.wordHandler = wordHandler;
    }
    public boolean getLastRequestNewWord(){
        return this.lastRequestNewWord;
    }
    public void makeMessageToClient(String request, String token, SelectionKey key, Selector selector)throws InterruptedException{
        makeRequestHandler(request,token,key,selector);
        this.request=request;
        this.token=token;
    }
    public String getMessageToClient(){
        return extractResponse();
    }
    private String extractResponse(){

        if(this.token.length()<1){
            this.token = "token";
        }
        switch(this.requestArray[0]) {
            case "newWord":
                if(javaToken.validateKey(this.token,this.usernameDB)) {
                    return "newWord," + this.currentHiddenWord + "," + this.remainingAttempts + "," + this.score + "," + this.alive + "," + this.usedLetters + "," + this.hasWon + "," + this.token;
                } else {
                    return "loginError," + this.currentHiddenWord + "," + this.remainingAttempts + "," + this.score + "," + this.alive + "," + this.usedLetters + "," + this.hasWon + "," + this.token;
                }
            case "guess":
                if(javaToken.validateKey(this.token,this.usernameDB)) {
                    return "guess," + this.currentHiddenWord + "," + this.remainingAttempts + "," + this.score + "," + this.alive + "," + this.usedLetters + "," + this.hasWon + "," + this.token;
                } else {
                    return "loginError," + this.currentHiddenWord + "," + this.remainingAttempts + "," + this.score + "," + this.alive + "," + this.usedLetters + "," + this.hasWon + "," + this.token;
                }
            case "guessWord":
                if(javaToken.validateKey(this.token,this.usernameDB)) {
                    return "guessWord," + this.currentHiddenWord + "," + this.remainingAttempts + "," + this.score + "," + this.alive + "," + this.usedLetters + "," + this.hasWon + "," + this.token;
                } else {
                    return "loginError," + this.currentHiddenWord + "," + this.remainingAttempts + "," + this.score + "," + this.alive + "," + this.usedLetters + "," + this.hasWon + "," + this.token;
                }
            case "login":
                if(this.login(requestArray[1],requestArray[2])) {
                    return "login," + this.currentHiddenWord + "," + this.remainingAttempts + "," + this.score + "," + this.alive + "," + this.usedLetters + "," + this.hasWon + "," + this.token;
                } else {
                    return "loginError," + this.currentHiddenWord + "," + this.remainingAttempts + "," + this.score + "," + this.alive + "," + this.usedLetters + "," + this.hasWon + "," + this.token;
                }
        }
        return "error2";
    }
    // The request string will look like: "request,letter/word"
    // The response string will look like: "request,requestInfo,remainingAttempts,Score,Alive,usedLetters,Win"
    public void makeRequestHandler(String request, String token, SelectionKey key, Selector selector)throws InterruptedException{
        request = request.trim();
        this.requestArray = request.split(",");
        this.token=token;

        switch(requestArray[0]) {
            case "newWord":
                    newWord(key,selector);
                break;
            case "guess":
                    guess(requestArray[1].toCharArray());
                    checkVictoryOrLoss();
                break;
            case "guessWord":
                    this.guessWord(requestArray[1]);
                    checkVictoryOrLoss();
                break;
            }
    }

    private void newWord(SelectionKey key,Selector selector){
        if(wordHandler == null){
            WordHandler wordHandler = new WordHandler(key,this,selector);
            wordHandler.start();
        } else{
            wordHandler.getNewWord();
        }

        this.usedLetters="";
        this.hasWon=false;
        this.remainingAttempts = 7;
        this.alive=true;
        this.currentHiddenWord ="";
        this.lastRequestNewWord=true;
    }

    private void guess(char[] letters){
        if(!hasWon) {
            this.lastRequestNewWord=false;
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
        this.lastRequestNewWord=false;
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

    private boolean login(String username,String password){
        this.lastRequestNewWord=false;
        if(checkCredentials(username,password)) {
            this.token=javaToken.createKey(username);
            return true;
        } else{
            return false;
        }
    }

    private boolean checkCredentials(String username,String password){
        if(username.equals(this.usernameDB)&&password.equals(this.passwordDB)){
            return true;
        }
        return false;
    }
}
