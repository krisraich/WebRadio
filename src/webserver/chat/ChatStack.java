package webserver.chat;

import java.util.LinkedList;

/**
 *
 * @author Kris
 */
public class ChatStack extends LinkedList<ChatMessage> {
    

    
    private final int maxSize;
    private final String LINE_DELIMITER ="\n"; // "</ br>";

    public ChatStack(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(ChatMessage e) {
        if(this.size() == maxSize){
            this.removeFirst();
        }
        return super.add(e); 
    }

    
    public String getChatAsHTML(){
        StringBuilder sb = new StringBuilder();
        for (ChatMessage current : this) {
            sb.append(current);
            sb.append(LINE_DELIMITER);
        }
        sb.setLength(sb.length() - LINE_DELIMITER.length());
       
        return sb.toString();
    }
    
}
