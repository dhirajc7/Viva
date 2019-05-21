package viva.oneplatinum.com.viva.models;

import java.io.Serializable;

/**
 * Created by Dell on 4/7/2016.
 */
public class Friend implements Serializable{

    private static final long serialVersionUID = 1L;

    public String id,first_name,profile_picture,website,address,email,dob,last_name,gender,invitation_status_msg;
    public int invitation_status;
    public Boolean isSelected=false;
    public String toString()
    {
        return id;
    }
}
