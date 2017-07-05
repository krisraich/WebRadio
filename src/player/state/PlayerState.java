package player.state;

import java.util.Observable;
import radio3.Util;

/**
 *
 * @author Kris
 */
public class PlayerState extends Observable{

    private String streamTitle;

    //defaults
    private byte volume = 50;
    private boolean mute = false;
    private boolean playing = true;
    private int id = 1;


    private PlayerState() {}
    
    private static PlayerState instance = null;
    public synchronized static PlayerState getInstacnce(){
        if(PlayerState.instance == null){
            PlayerState.instance = new PlayerState();
        }
        return instance;
    }
    
    
    public void checkedNotifyObserver(){
        System.out.println("--- State changed ---");
        System.out.println(this);
        this.setChanged();
        this.notifyObservers(getStateForWebRequest());
        this.clearChanged();
    }
    

    public String getStreamTitle() {
        return streamTitle;
    }

    public boolean setStreamTitle(String streamTitle) {
        if(! streamTitle.equals(this.streamTitle)){
            this.streamTitle = streamTitle;
            checkedNotifyObserver();
            return true;
        }
        return false;
    }

    public byte getVolume() {
        return volume;
    }

    public boolean setVolume(byte volume) {
        if(volume > 0){
            mute = false;
        }
        
        if(this.volume != volume){
            this.volume = volume;
            checkedNotifyObserver();
            return true;
        }
        return false;
    }

    public boolean isMute() {
        return mute;
    }

    public boolean setMute(boolean mute) {
        if(mute){
            this.volume = 0;
        }
        
        if(this.mute != mute){
            this.mute = mute;
            checkedNotifyObserver();
            return true;
        }
        return false;
    }

    public boolean isPlaying() {
        return playing;
    }

    public boolean setPlaying(boolean playing) {
        if(this.playing != playing){
            this.playing = playing;
            checkedNotifyObserver();
            return true;
        }
        return false;
    }

    public int getId() {
        return id;
    }

    public boolean setId(int id) {
        if(this.id != id){
            this.id = id;
            checkedNotifyObserver();
            return true;
        }
        return false;
    }
    
    

    public byte[] getStateForWebRequest(){
        return (" { \"title\": " + Util.sanitizeJson(this.streamTitle) + ", " + 
                "\"id\": " + this.id + ", " + 
                "\"volume\": " + this.volume + ", " + 
                "\"mute\": " + this.mute + ", " + 
                "\"playing\": " + this.playing + "}").getBytes();
    }

    @Override
    public String toString() {
        return "Current Title: " + this.streamTitle + 
                "\r\nID: " + this.id +
                "\r\nVolume: " + this.volume +
                "\r\nMute: " + this.mute +
                "\r\nPlaying: " + this.playing;
    }
    

}
