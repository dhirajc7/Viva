package viva.oneplatinum.com.viva.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by D-MAX on 4/24/2016.
 */
public class NotificationDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    public String postId, post, postCreated, eventId,eventName,eventStartDate,eventEndDate,userId, userName, userProfile,location,eventImage;
    public int canUpdateOrDeletePost;
    public ArrayList<String> image=new ArrayList<String>();
}
