package webserver;

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
        int lastindex =  requestURL.lastIndexOf(".");
        String mimeType = "text/html";
        
        if(requestURL.length() - lastindex < 5){
            //fileextension gültig
            String fileExtension = requestURL.substring(lastindex + 1);
            
            switch(fileExtension){
                case "css": 
                    mimeType =  "text/css";
                    break;
                case "js": 
                    mimeType =  "application/javascript";
                    break;
            }
        }
        
        return createCacheResponseHolderWithMimeType(mimeType, responsedata);
    }
   
    public static CacheResponseHolder createCacheResponseHolderWithMimeType(String mimetype, byte[] responsedata){
        return new CacheResponseHolder(mimetype, responsedata);
    }
    
}
