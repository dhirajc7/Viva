package viva.oneplatinum.com.viva.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by D-MAX on 3/31/2016.
 */
public class EventDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    public String eventId, eventTitle, eventType,organiserId,organiserFirsName,organiserLastName,organiserPicture, eventStartdate, eventEnddate, eventLocation, eventWebsite, isSponsered,isCreatedByMe, price,eventDescription,eventCapacity,phone,longitude,latitude;
    public int imageCount,isGoing,isInterested,attendessCount,isFollowingOrganizer,hasMoreAttendess,eventTypeId;
    public ArrayList<String> eventImages=new ArrayList<String>();
    public ArrayList<Attendees> attendessList=new ArrayList<Attendees>();
    public ArrayList<CategoryType> categoriesList=new ArrayList<CategoryType>();
}
