package View;

public class Gameboard {
    private String gameboard;

    public Gameboard() {
        this.gameboard = "";
    }
    public String getGameboard() {
        return this.gameboard;
    }

    public String makeHead(){
        return  "\n\n\n\n\n\n\n\n\n\n"+
                "-------------------------------------------------------------------------------------------------\n" +
                "|    Used Letters     |       Word      | Remaining failed attempts |       Score               |\n" +
                "-------------------------------------------------------------------------------------------------\n";
    }
    public String gameInfo(){
        return  "Commands : Description\n" +
                "----------------------\n" +
                "new      : Starts a new hangman\n" +
                "quit     : Quits the game\n" +
                "a letter : Guesses a letter\n" +
                "a word   : Guesses the word\n" +
                "Enter a command:";
    }

    public void makeLine(String[] response){
        this.gameboard = makeHead();
        this.gameboard +=
                "|" + response[5]+"|  "+response[1]+"  |          "+response[2]+"          |       " + response[3] + "       |\n"+
                "------------------------------------------------------------------------------------------------\n";
    }
    public void endGame(boolean win,String[] response){
        if(!win){
            this.gameboard = makeHead();
            this.gameboard +=
                    "| You tried to many attempts and lost! To try a new word write new.           "+response[3]+"                |\n"+
                    "-------------------------------------------------------------------------------------------------\n";
        } else{
            this.gameboard = makeHead();
            this.gameboard +=
                    "| You won and one point has been added to your score! To try a new word write new. Score: "+response[3]+"    |\n"+
                    "------------------------------------------------------------------------------------------------\n";
        }
    }
}
