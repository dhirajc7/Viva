package viva.oneplatinum.com.viva.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Dell on 4/4/2016.
 */
public class FriendList implements Serializable {

    private static final long serialVersionUID = 1L;

    public String  firstName, lastName;
    public ArrayList<String> friendImages=new ArrayList<String>();
}
