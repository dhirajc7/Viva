package viva.oneplatinum.com.viva.models;

import java.io.Serializable;

/**
 * Created by D-MAX on 5/11/2016.
 */
public class FbFriends implements Serializable {

    private static final long serialVersionUID = 1L;

    public String id,name,image;
    public Boolean isSelected=false;

    public FbFriends() {
    }
}
