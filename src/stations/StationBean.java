package stations;

import radio3.Util;

/**
 *
 * @author Kris
 */
public class StationBean {

    private int id;
    private String img;
    private String desc;
    private String path;

    @Override
    public String toString() {
                return "ID: " + this.id + 
                "\r\nURL: " + this.path +
                "\r\nBeschreibung: " + this.desc +
                "\r\nBild URL: " + this.img;
    }

    public String toJsonString(){
        return (" { \"path\": " + Util.sanitizeJson(this.path) + ", " + 
                "\"id\": " + this.id + ", " + 
                "\"desc\": " + Util.sanitizeJson(this.desc) + ", " + 
                "\"img\": " +  Util.sanitizeJson(this.img) + "}");
    }
    
    
    
    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    
    
    
}
