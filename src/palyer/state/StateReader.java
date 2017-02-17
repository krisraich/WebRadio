package palyer.state;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * TODO Regex by BOB implementieren
 * 
 * @author Kris
 */
public class StateReader extends Thread{

    private final static PlayerState playerState = PlayerState.getInstacnce();
    private static StateReader currentInstance = null;
    
    
    
    /**
     * Kann mehrmals aufgerufen werden und muss in einem Thread sein...
     * @param inputStream 
     */
    public static synchronized void bindInputStreamAndProcess(InputStream inputStream){
        stopProcessing();
        currentInstance = new StateReader(inputStream);
        currentInstance.start();
    }
    
    public static void stopProcessing(){
        if(currentInstance != null){
            currentInstance.dowork.set(false);
            currentInstance.interrupt();
        }
    }
    
    private final InputStream inputStream;
    private final AtomicBoolean dowork = new AtomicBoolean(true);
    
    private StateReader(InputStream inputStream){
        this.setName("Player Output Streamreader");
        this.inputStream = inputStream;
    }
    
    
    @Override
    public void run() {
        
        try(BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while (dowork.get()) {
                line = br.readLine().trim();

                // process the line.
                if(line == null || line.length() == 0){
                    //suspend Thread
                    Thread.sleep(100);
                    continue;
                }
                
                System.out.println("line: " + line);
                /*
                Outup der obigen line
                line: MPlayer svn r34540 (Raspbian), built with gcc-4.6 (C) 2000-2012 MPlayer Team
line: Playing http://mp3channels.webradio.rockantenne.de/classic-perlen.aac.
line: Resolving mp3channels.webradio.rockantenne.de for AF_INET...
line: Connecting to server mp3channels.webradio.rockantenne.de[194.97.151.145]: 80...
line: Name   : ROCK ANTENNE Classic Perlen
line: Genre  : Rock
line: Website: http://classicperlen.rockantenne.de/classicperlen
line: Public : no
line: Bitrate: 64kbit/s
line: Cache size set to 320 KBytes
line: Cache fill:  0.00% (0 bytes)
line: ICY Info: StreamTitle='Def Leppard - Women';StreamUrl='http://classicperlen.rockantenne.de/classicperlen';
received icy infoline: Cache fill: 17.50% (57344 bytes)
line: AAC file format detected.
line: ==========================================================================
line: Opening audio decoder: [ffmpeg] FFmpeg/libavcodec audio decoders
line: libavcodec version 53.35.0 (external)
line: Mismatching header version 53.32.2
line: AUDIO: 44100 Hz, 2 ch, s16le, 0.0 kbit/0.00% (ratio: 0->176400)
line: Selected audio codec: [ffaac] afm: ffmpeg (FFmpeg AAC (MPEG-2/MPEG-4 Audio))
line: ==========================================================================
line: AO: [alsa] 44100Hz 2ch s16le (2 bytes per sample)
line: Video: no video
line: Starting playback...
line: A:   0.0 (00.0) of 0.0 (unknown) ??,?% 71%
line: A:   0.1 (00.0) of 0.0 (unknown) ??,?% 71%
line: A:   0.1 (00.0) of 0.0 (unknown) ??,?% 71%
line: A:   0.1 (00.1) of 0.0 (unknown) ??,?% 71%
line: A:   0.1 (00.1) of 0.0 (unknown) ??,?% 71%
line: A:   0.2 (00.1) of 0.0 (unknown) ??,?% 71%
line: A:   0.2 (00.1) of 0.0 (unknown) 14.3% 70%
line: A:   0.2 (00.2) of 0.0 (unknown) 13.7% 70%
line: A:   0.2 (00.2) of 0.0 (unknown) 14.3% 70%
line: A:   0.3 (00.2) of 0.0 (unknown) 13.7% 73%


                */
                
                //TODO: BOB
                
                
                if(line.startsWith("A:")){
                    //player is alive
                    playerState.updateIsPlaying();
                }else if(line.startsWith("ICY Info:")){
                    System.out.print("received icy info");
                    playerState.setStreamTitle(line);
                }else if(line.contains("Mute: disabled")){
                    System.out.print("received mute info");
                    playerState.setMute(false);
                }else if(line.contains("Mute:")){
                    System.out.print("received mute2 info");
                    playerState.setMute(true);
                }else if(line.contains("Volume:")){
                    System.out.print("received volume info");
                    playerState.setVolume((byte)0);
                }

            }

        } catch (IOException iOException) {
            //read line wirft IO Exception.. Stream ist geschlossen
             System.err.println("Lost Programm output");
//             ex.printStackTrace();
        }catch (InterruptedException interruptedException){
            //Programm has terminated
        }
        
    }
}
