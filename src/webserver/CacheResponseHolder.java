package webserver;

import java.util.Date;
import radio3.Util;

/**
 *
 * @author Kris
 */
public class CacheResponseHolder {
    
   private final byte[] responsedata;
   private final String mimetype;
   private final long created;
   private int called = 0;

    private CacheResponseHolder(String mimetype, byte[] responsedata) {
        this.responsedata = responsedata;
        this.mimetype = mimetype;
        this.created = System.currentTimeMillis();
    }

    public byte[] getResponseData() {
        called++;
        return responsedata;
    }

    public String getMimetype() {
        return mimetype;
    }

    public int getCalled() {
        return called;
    }

    public Date getCreated() {
        return new Date(this.created);
    }
   
    public static CacheResponseHolder createCacheResponseHolderFromURL(String requestURL, byte[] responsedata){
        
        String mimeTypeFromRessourceName = Util.getMimeTypeFromRessourceName(requestURL);
       
        //default
        String mimeType = (mimeTypeFromRessourceName == null ? "text/html" : mimeTypeFromRessourceName);
       
        return createCacheResponseHolderWithMimeType(mimeType, responsedata);
    }
   
    public static CacheResponseHolder createCacheResponseHolderWithMimeType(String mimetype, byte[] responsedata){
        return new CacheResponseHolder(mimetype, responsedata);
    }
    
}
