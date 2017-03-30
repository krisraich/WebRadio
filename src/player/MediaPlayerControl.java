package player;

/**
 *
 * @author Kris
 */
public interface MediaPlayerControl {
    
    public void play();
    public void pause();
    public void stop();
    
    public void mute();
    public void unmute();
    public void togglemute();
    public void volumeUp();
    public void volumeDown();
    
    public void setStationID(int stationid);    
    
}
