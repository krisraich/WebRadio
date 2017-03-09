package webserver.chat;

import java.util.LinkedList;

/**
 *
 * @author Kris
 */
public class ChatStack extends LinkedList<ChatMessage> {
    

    
    private final int maxSize;
    private final String LINE_DELIMITER ="\n"; // "</ br>";

    private volatile byte[] outCache;
    public final static String EMPTY_CHAT = "-- empty chat --";
    
    public ChatStack(int maxSize) {
        this.maxSize = maxSize;
        updateCache();
    }

    @Override
    public boolean add(ChatMessage e) {
        if(this.size() == maxSize){
            this.removeFirst();
        }
        super.add(e);
        updateCache();
        
        return true;
    }

    @Override
    public void clear() {
        super.clear();
        updateCache();
    }

    public byte[] getOutCache() {
        return outCache;
    }
    
    private void updateCache(){
        if(this.isEmpty()){
            outCache = EMPTY_CHAT.getBytes();
        }else{
            StringBuilder sb = new StringBuilder();
            for (ChatMessage current : this) {
                sb.append(current);
                sb.append(LINE_DELIMITER);
            }
            sb.setLength(sb.length() - LINE_DELIMITER.length());

            outCache =  sb.toString().getBytes();
        }
    }
    
}
