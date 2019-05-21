package viva.oneplatinum.com.viva.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    public String image,longitude,name,eventId, eventTitle, eventType, eventTypeId, eventStartdate, eventEnddate, eventLocation, website, phone, price,end_date,location,eventName,latitude,start_date,mainImage;
    public ArrayList<CategoryInfo> categoryInfoArrayList = new ArrayList<CategoryInfo>();
    public int imageCount,isMyEvent;
    public ArrayList<String> eventImages=new ArrayList<String>();


}
