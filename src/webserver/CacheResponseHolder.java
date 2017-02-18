package webserver;

import radio3.Util;

/**
 *
 * @author Kris
 */
public class CacheResponseHolder {
    
   private final byte[] responsedata;
   private final String mimetype;



    private CacheResponseHolder(String mimetype, byte[] responsedata) {
        this.responsedata = responsedata;
        this.mimetype = mimetype;
    }

    public byte[] getResponseData() {
        return responsedata;
    }

    public String getMimetype() {
        return mimetype;
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
