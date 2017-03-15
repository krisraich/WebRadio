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
    
    
    private void checkedNotifyObserver(){
        System.out.println("--- State changed ---");
        System.out.println(this);
        this.setChanged();
        this.notifyObservers(getStateForWebRequest());
        this.clearChanged();
    }
    

    public String getStreamTitle() {
        return streamTitle;
    }

    public void setStreamTitle(String streamTitle) {
        if(! streamTitle.equals(this.streamTitle)){
            this.streamTitle = streamTitle;
            checkedNotifyObserver();
        }
    }

    public byte getVolume() {
        return volume;
    }

    public void setVolume(byte volume) {
        if(volume > 0){
            mute = false;
        }
        
        if(this.volume != volume){
            this.volume = volume;
            checkedNotifyObserver();
        }
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        if(mute){
            this.volume = 0;
        }
        
        if(this.mute != mute){
            this.mute = mute;
            checkedNotifyObserver();
        }
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        if(this.playing != playing){
            this.playing = playing;
            checkedNotifyObserver();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if(this.id != id){
            this.id = id;
            checkedNotifyObserver();
        }
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
