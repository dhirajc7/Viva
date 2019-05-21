package viva.oneplatinum.com.viva.utils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import java.util.ArrayList;
import java.util.HashMap;

import viva.oneplatinum.com.viva.models.ImageItem;


/**
 * @author shiva
 */
public class DataHolder {

    public static final String USER_PERFERENCE = "";
    public static String GETALLMETHOD = "getallMethod";

    public static String INVITE_COUNT = "inviteCount";
    public static String PENDING_COUNT = "pendintCount";

    public static String USERINFO = "userInfo";
    public static String TOKEN = "token";
    public static String ISFROMGOOGLEPLUS = "googlePlus";
    public static String ISFROMREGISTER = "register";
    public static String ISFROMFACEBOOK = "facebook";
    public static String USERNAME = "username";
    public static String USERIMAGE = "picture";
    public static String USEREMAIL = "useremail";
    public static String USERID = "userid";
    public static String EVENTS = "events";
    public static String PRF_IMAGE = "image";
    public static String CURRENT_LOCATION_LATITUDE = "latitude";
    public static String CURRENT_LOCATION_LONGITUDE = "longitude";
    public static String ISPINSELECTED = "false";


    public static final int LIMIT = 10;
    public static final int RESULT_LOAD_IMAGE = 101;
    public static final int INTENT_CAPTURE_IMAGE = 102;
    public static final int INTENT_BROWSE_GALLERY = 103;
    //local server url
//    public static final String ROOT_URL = "http://192.168.0.117/viva/";
    //live server url
	public static final String ROOT_URL="http://api.yeahviva.com/";
//    public static final String ROOT_URL="http://naren.local/viva_local/";
    public static final String LOGIN = ROOT_URL + "Users/login.json";

    public static final String FACEBOOK_LOGIN = ROOT_URL + "Users/fb_login.json";
    public static final String GOOGLE_LOGIN = ROOT_URL + "Users/google_login.json";
    public static final String REGISTER = ROOT_URL + "Users/register.json";
    public static final String CREATE_EVENT = "http://api.yeahviva.com/" + "Events/create.json";
    public static final String UPDATE_EVENT = "http://api.yeahviva.com/" + "Events/updateEvent.json";
    public static final String GET_CATEGORIES = "http://api.yeahviva.com/" + "Categories/getCategories.json";
    public static final String USER_PROFILE = ROOT_URL + "Users/userProfile.json";
    public static final String MY_PROFILE = ROOT_URL + "Users/profile.json";
    public static final String ADD_FRIEND_CONTACT = ROOT_URL + "Userfollows/addFriendFromContact.json";
    public static final String EDIT_PROFILE = "http://api.yeahviva.com/" + "Users/editProfile.json";
    public static final String MY_EVENTS = "http://api.yeahviva.com/" + "Events/myEvents/page:";
    public static final String LINKED_EVENTS = "http://api.yeahviva.com/" + "Events/myEventWithGoingEvent/page:";
    public static final String UPCOMING_EVENTS = "http://api.yeahviva.com/" + "Events/userUpcomingEvents/page:";

    public static final String EVENT_DETAIL = "http://api.yeahviva.com/" + "Events/eventDetail.json";
    public static final String EDIT_EVENT = "http://api.yeahviva.com/" + "Events/edit.json";
    public static final String UPDATE_USER_GOING = "http://api.yeahviva.com/EventUsers/updateEventJoin.json";
    public static final String FORGOT_PASSWORD = ROOT_URL + "Users/forgetPassword.json";
    public static final String VERIFY_CODE = ROOT_URL + "Users/verifyCode.json";
    public static final String FRIEND_LIST = ROOT_URL + "Userfollows/myFriends/page:";
    public static final String ADD_POST = ROOT_URL + "EventPosts/add.json";
    public static final String UPDATE_USER_INTEREST = "http://api.yeahviva.com/UserInterests/updateInterest.json";
    public static final String FOLLOW_USER = ROOT_URL+"Userfollows/followUser.json";
    public static final String VIEW_MEMBERS = "http://api.yeahviva.com/" + "EventUsers/goingEventUsers/page:";
    public static final String VIVA_FRIENDS = "http://api.yeahviva.com/" + "Userfollows/nonFriendUserList/page:";
    public static final String SEARCH_FILTER = "http://api.yeahviva.com/" + "Events/filter.json";
    public static final String SEND_FRIEND_REQUEST = ROOT_URL + "Userfollows/followMultipleUsers.json";
    public static final String SEND_FRIEND_REQUEST_FB = ROOT_URL + "Userfollows/addFriendFromFacebook.json";
    public static final String FRIENDLIST_FOR_INVITE = ROOT_URL + "Userfollows/friendListForInvitation/page:";
    public static final String RESET_PASSWORD=ROOT_URL+"Users/resetPassword.json";
    public static final String RESEND_CODE=ROOT_URL+"Users/resendCode.json";
    public static final String INVITE_FRIEND = ROOT_URL + "Events/inviteFriendToEvent.json";
    public static final String NOTIFICATION_LIST = ROOT_URL + "Notifications/listNotifications/page:";
    public static final String NOTIFICATION_DETAIL = ROOT_URL + "EventPosts/postDetail.json";
    public static final String DELETE_POST = ROOT_URL + "EventPosts/deletePost.json";
    public static final String EDIT_POST = ROOT_URL + "EventPosts/updatePost.json";
    public static final String ALLOW_FRIEND = ROOT_URL + "Userfollows/acceptOrRejectFollowRequest.json";
    public static final String EVENT_SUGGESTION = ROOT_URL + "EventUsers/allowDisallowEventSuggestion.json";
    public static final String BLOCK_USER = ROOT_URL + "BlockedUsers/block.json";
    public static final String UNBLOCK_USER = ROOT_URL + "BlockedUsers/unBlockUser.json";
    public static final String REPORT_USER = ROOT_URL + "Reportusers/report.json";
    public static final String UNFOLLOW_USER = ROOT_URL+"Userfollows/unfollowUser.json";
    public static final String EVENT_LISTING = ROOT_URL + "Events/getEventListingForWheel.json";
    public static final String FILTER_SEARCH_EVENTS=ROOT_URL+"Events/filter_search.json";
    public static final String KEYBOARD_SEARCH=ROOT_URL+"Events/keyword_search.json";

    public static final String SETPIN_LOCATION="http://api.yeahviva.com/Users/updateCurrentLocation.json";
    public static final String UNSETPIN_LOCATION="http://api.yeahviva.com/Users/removeCurrentLocation.json";


    public static final String SEND_FRIEND_REQUEST_GOOGLE = ROOT_URL + "Userfollows/addFriendFromGooglePlus.json";
    public static final String LOGOUT = ROOT_URL + "Users/logout.json";

    public static String Google_Places = "https://maps.googleapis.com/maps/api/place" + "/autocomplete" + "/json"
            + "?key=" + "AIzaSyB9G-UMgYpBYRCPTxa-YSXlTyZZE6aLvGs" + "&input=";
    public static String USER_NAME;


    public static class Dataholder {
        public HashMap<String, String> images;
        public ArrayList<String> ids;
        public ArrayList<ImageItem> imagelist;
        static Dataholder dH;
        public Boolean reEntry = false;
        public HashMap<String, ImageItem> tempImageItemList;
        public ArrayList<String> noItemsList = new ArrayList<String>();
        public LruCache<String, Bitmap> imageCache = new LruCache<String, Bitmap>(50);
        public String feedTitle = "";

        private Dataholder() {
            images = new HashMap<String, String>();
            ids = new ArrayList<String>();
            imagelist = new ArrayList<ImageItem>();
            tempImageItemList = new HashMap<String, ImageItem>();
            noItemsList = new ArrayList<String>();

        }

        public static Dataholder getInstance() {
            if (dH == null) {
                dH = new Dataholder();
            }
            return dH;
        }

        public String getIds(int pos) {
            return ids.get(pos);
        }
    }

}