package viva.oneplatinum.com.viva.models;

import java.io.Serializable;

/**
 * Created by D-MAX on 3/31/2016.
 */
public class UserDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    public String id;
    public String first_name;
    public String profile_picture;
    public String website;
    public String address;
    public String email;
    public String dob;
    public String last_name;
    public String gender;
    public String is_private;
}
