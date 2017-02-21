package stations;

import java.util.SortedMap;
import java.util.TreeMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Kris
 */
public class StationHandler extends DefaultHandler{

    private final SortedMap<Integer, StationBean> stationBeans = new TreeMap();
    
    private StationBean currentBean;
    private String currentElement;

    
    public SortedMap<Integer, StationBean> getStationBeans() {
        return stationBeans;
    }
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if("station".equals(qName)){
            currentBean = new StationBean();
        }
        currentElement = qName;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if("station".equals(qName)){
            stationBeans.put(currentBean.getId(), currentBean);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String value = new String(ch, start, length).trim();
        if(value.length() == 0) return;
        
        //nice.. java JDK 7
        switch (currentElement) {
            case "id":
                currentBean.setId(Integer.parseInt(value));
                break;
            case "img":
                currentBean.setImg(value);
                break;
            case "desc":
                currentBean.setDesc(value);
                break;
            case "path":
                currentBean.setPath(value);
                break;
        }
        
    }
    
}
