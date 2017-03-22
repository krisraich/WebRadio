package stations;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.SortedMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import radio3.Radio3;

/**
 *
 * @author Kris
 */
public class StationManager {

    private static StationManager instance;
    private static String filePathToStationsXML = null;
    
    public static void setFilePathToStationsXML(String filepath){
        filePathToStationsXML = filepath;
    }
    
    public static synchronized StationManager getInstance() {
        if (instance == null) {
            instance = new StationManager();
        }
        return instance;
    }

    private SortedMap<Integer, StationBean> stationBeans; 
    
    private StationManager() {
        InputStream is;
        if(StationManager.filePathToStationsXML == null){
            is = getDefaultInputStream();
        }else{
            try {
                is = new FileInputStream(filePathToStationsXML);
            } catch (FileNotFoundException ex) {
                System.err.println("Cant find Stations XML.. using Default");
                is = getDefaultInputStream();
            }
        }

        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            if(Radio3.DEV_MODE) System.out.println("Parsing stations xml...");
            SAXParser saxParser = factory.newSAXParser();
            StationHandler handler = new StationHandler();
            saxParser.parse(is, handler);
            stationBeans = handler.getStationBeans();
            
        } catch (IOException err) {
            System.err.println("Cant read stations xml");
        } catch (ParserConfigurationException | SAXException ex) {
             System.err.println("Cant parse stations xml");
        } 

    }

    private InputStream getDefaultInputStream(){
        return getClass().getResourceAsStream("stations.xml");
    }
    
    public synchronized byte[] getBeansAsJsonArray(){
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Map.Entry<Integer, StationBean> entry : stationBeans.entrySet()) {
            sb.append(entry.getValue().toJsonString());
            sb.append(",");
        }
        sb.setLength(sb.length()-1);
        sb.append("]");
        return sb.toString().getBytes();
    }
    
    public synchronized StationBean getStationByID(int stationid){
        return stationBeans.get(stationid);
    }
    
    public synchronized String getFirstEntryPath(){
        return stationBeans.get(stationBeans.firstKey()).getPath();
    }
    public synchronized int getFirstEntryID(){
        return stationBeans.firstKey();
    }
    
}
