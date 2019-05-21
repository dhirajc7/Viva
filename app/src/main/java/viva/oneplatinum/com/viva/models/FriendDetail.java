package viva.oneplatinum.com.viva.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Dell on 4/7/2016.
 */
public class FriendDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    public String website, first_name, address, email, dob, last_name,


    gender, message, user_id, profile_picture;
    public int isProfilePrivate, UpCommingEventCount, isFriend, isFollowing, upComingEvents_hasPreviousPage,
            upComingEvents_currentPage, upComingEvents_pagecount, canViewProfile, upComingEvents_hasNextPage,isMyProfile;
    public ArrayList<Event> attendessList = new ArrayList<Event>();
}
