package Server.Model;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Random;

public class WordExtractor extends Thread {
    private FileReaderServer fileReaderServer;
    private Random randomGenerator = new Random();
    private String userHome = System.getProperty("user.dir");
    private final String WORD_FILE = userHome + "/src/main/java/Server/Model/words.txt";
    private SelectionKey key;
    private Selector selector;

    public WordExtractor(FileReaderServer fileReaderServer, SelectionKey key,Selector selector){
        this.fileReaderServer = fileReaderServer;
        this.key=key;
        this.selector = selector;
    }
    public void run(){
        try {
            int randomSizeArray = randomGenerator.nextInt(460863);
            RandomAccessFile file = new RandomAccessFile(WORD_FILE, "r");
            FileChannel inChannel = file.getChannel();

            ByteBuffer buffer = ByteBuffer.allocateDirect(randomSizeArray);
            inChannel.read(buffer);
            String[] words = extractMessageFromBuffer(buffer).split("\n");
            inChannel.close();
            fileReaderServer.setWord(words[words.length -2]);
            key.interestOps(SelectionKey.OP_WRITE);
            selector.wakeup();
        } catch (Exception e ){
            e.printStackTrace();
        }
    }

    private String extractMessageFromBuffer(ByteBuffer buffer) {
        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return new String(bytes);
    }
}
