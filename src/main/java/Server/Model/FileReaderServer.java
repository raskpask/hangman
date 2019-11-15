package Server.Model;


import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class FileReaderServer extends Thread {
    private ServerSocketChannel listeningSocketChannel;
    private String word;
    private Game game;
    private SelectionKey serverKey;
    private boolean wordSent= false;
    private Selector selector;
    private WordHandler wordHandler;

    public FileReaderServer(Game game,SelectionKey serverKey,Selector selector,WordHandler wordHandler){
        this.game=game;
        this.serverKey = serverKey;
        this.selector = selector;
        this.wordHandler=wordHandler;
    }
    public void run(){
        try {
            Selector selector = Selector.open();
            listeningSocketChannel = ServerSocketChannel.open();
            listeningSocketChannel.bind(new InetSocketAddress("localhost",5555));
            listeningSocketChannel.configureBlocking(false);
            listeningSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while(!wordSent) {
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()){
                    SelectionKey key = keys.next();
                    keys.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel channel = server.accept();
                        channel.configureBlocking(false);
                        channel.register(selector, SelectionKey.OP_READ);
                        System.out.println("New client to fileReaderServer");
                    } else if (key.isReadable()) {
                        WordExtractor wordExtractor = new WordExtractor(this,key,selector);
                        wordExtractor.start();
                    } else if (key.isWritable()) {
                        write(key);
                    }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }

    }
    private void write(SelectionKey key){
        game.setWord(word);
        String currentHiddenWord="";
        for(int i=0; i<word.length();i++){
            currentHiddenWord +="_ ";
        }
        game.setCurrentHiddenWord(currentHiddenWord);
        game.setWordHandler(wordHandler);
        wordHandler.setWord(word);
        System.out.println("write has been set in server: "+word);
        serverKey.interestOps(SelectionKey.OP_WRITE);
        key.interestOps(SelectionKey.OP_READ);
        wordSent=true;
        selector.wakeup();
        //write=false;
    }
    public void setWord(String word){
        this.word = word;
    }

}
