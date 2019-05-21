package viva.oneplatinum.com.viva.models;

import java.io.Serializable;

/**
 * Created by D-MAX on 4/24/2016.
 */
public class Notifications implements Serializable {

    private static final long serialVersionUID = 1L;

    public String id, model,link_type, message,senderId, receiverId, suggestedUserId, type, eventLocation, sender_picture,eventImage,notificationId;
    public int sticky,has_link,hasDisplayImageOnNotification,action;

}
