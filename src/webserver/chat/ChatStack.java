/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver.chat;

import java.util.LinkedList;

/**
 *
 * @author Kris Raich
 */
public class ChatStack extends LinkedList<String> {
    
    private final int maxSize;
    private final String LINE_DELIMITER = "</ br>";

    public ChatStack(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(String e) {
        if(this.size() == maxSize){
            this.removeFirst();
        }
        return super.add(e); 
    }

    
    public String getChatAsHTML(){
        StringBuilder sb = new StringBuilder();
        for (String current : this) {
            sb.append("--> ");
            sb.append(sb);
            sb.append(LINE_DELIMITER);
        }
        sb.setLength(sb.length() - LINE_DELIMITER.length());
       
        return sb.toString();
    }
    
}
