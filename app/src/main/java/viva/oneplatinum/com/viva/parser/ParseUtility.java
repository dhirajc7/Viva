package viva.oneplatinum.com.viva.parser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import viva.oneplatinum.com.viva.models.Attendees;
import viva.oneplatinum.com.viva.models.CategoryInfo;
import viva.oneplatinum.com.viva.models.CategoryType;
import viva.oneplatinum.com.viva.models.Event;
import viva.oneplatinum.com.viva.models.EventDetail;
import viva.oneplatinum.com.viva.models.Friend;
import viva.oneplatinum.com.viva.models.FriendDetail;
import viva.oneplatinum.com.viva.models.GoingMemberDetail;
import viva.oneplatinum.com.viva.models.MemeberList;
import viva.oneplatinum.com.viva.models.NotificationDetail;
import viva.oneplatinum.com.viva.models.Notifications;
import viva.oneplatinum.com.viva.models.SubCategory;
import viva.oneplatinum.com.viva.models.UserDetail;

/**
 * Created by D-MAX on 3/30/2016.
 */
public class ParseUtility {
    public static ArrayList<CategoryInfo> parseCategoryDetail(JSONObject object) throws JSONException {
        JSONObject output = object.getJSONObject("output");
        int status = output.optInt("status");
        if (status == 1) {
            JSONArray response = output.getJSONArray("response");
            ArrayList<CategoryInfo> mCategoriesList = new ArrayList<CategoryInfo>();
            for (int i = 0; i < response.length(); i++) {
                CategoryInfo mCategoryInfo = new CategoryInfo();
                JSONObject categoriesObject = response.getJSONObject(i);
                mCategoryInfo.category_id = categoriesObject.optString("category_id");
                mCategoryInfo.category_name = categoriesObject.optString("name");
                JSONArray subCategoriesArray = categoriesObject.getJSONArray("subcategory");
                for (int j = 0; j < subCategoriesArray.length(); j++) {
                    SubCategory mSubCategory = new SubCategory();
                    JSONObject subCategoriesObject = subCategoriesArray.getJSONObject(j);
                    mSubCategory.sub_category_id = subCategoriesObject.optString("sub_category_id");
                    mSubCategory.sub_category_name = subCategoriesObject.optString("sub_category_name");
                    mCategoryInfo.subCategoriesList.add(mSubCategory);
                }
                mCategoriesList.add(mCategoryInfo);

            }
            return mCategoriesList;
        }
        return null;
    }

    public static UserDetail parseUserDetail(JSONObject object) throws JSONException {
        JSONObject outputObject = object.getJSONObject("output");
        JSONObject responseObject = outputObject.getJSONObject("response");
        UserDetail mUserDetail = new UserDetail();
        mUserDetail.id = responseObject.optString("id");
        mUserDetail.first_name = responseObject.optString("first_name");
        mUserDetail.profile_picture = responseObject.optString("profile_picture");
        mUserDetail.website = responseObject.optString("website");
        mUserDetail.address = responseObject.optString("address");
        mUserDetail.email = responseObject.optString("email");
        mUserDetail.dob = responseObject.optString("dob");
        mUserDetail.last_name = responseObject.optString("last_name");
        mUserDetail.gender = responseObject.optString("gender");
        mUserDetail.is_private = responseObject.optString("is_private");
        return mUserDetail;
    }

    public static GoingMemberDetail parseMemeberDetail(JSONObject object) throws JSONException {
        JSONObject outputObject = object.getJSONObject("output");
        JSONObject responseObject = outputObject.getJSONObject("response");
        GoingMemberDetail mGoingMemberDetail = new GoingMemberDetail();
        mGoingMemberDetail.interested = responseObject.optString("interested");
        mGoingMemberDetail.going = responseObject.optString("going");

        return mGoingMemberDetail;
    }

    public static ArrayList<Event> parseEventsList(JSONObject object) throws JSONException {
        ArrayList<Event> eventArrayList = new ArrayList<Event>();
        JSONObject outputObject = object.getJSONObject("output");
        JSONArray responseArrayList = outputObject.getJSONArray("response");
        for (int i = 0; i < responseArrayList.length(); i++) {

            Event mEvent = new Event();
            JSONObject eventObject = responseArrayList.getJSONObject(i);
            mEvent.eventId = eventObject.optString("eventId");
            mEvent.eventTitle = eventObject.optString("eventTitle");
            mEvent.eventType = eventObject.optString("eventType");
            mEvent.eventTypeId = eventObject.optString("eventTypeId");
            mEvent.eventStartdate = eventObject.optString("eventStartdate");
            mEvent.eventEnddate = eventObject.optString("eventEnddate");
            mEvent.eventLocation = eventObject.optString("eventLocation");
            mEvent.website = eventObject.optString("website");
            mEvent.phone = eventObject.optString("phone");
            mEvent.price = eventObject.optString("price");
            mEvent.imageCount = eventObject.optInt("imageCount");
            if (eventObject.optInt("imageCount") > 0) {
                JSONArray eventImage = eventObject.optJSONArray("eventImage");
                for (int j = 0; j < eventImage.length(); j++) {
                    mEvent.eventImages.add(eventImage.optString(j));
                }
            }
            JSONArray categoriesList = eventObject.optJSONArray("Category");
            if(categoriesList!=null)
            {
                for (int k = 0; k < categoriesList.length(); k++) {
                    JSONObject categoryObject = categoriesList.getJSONObject(k);
                    CategoryInfo mCategoryInfo = new CategoryInfo();
                    mCategoryInfo.category_id = categoryObject.optString("categoryId");
                    mCategoryInfo.sub_category_id = categoryObject.optString("subCategoryId");
                    mEvent.categoryInfoArrayList.add(mCategoryInfo);
                }
            }

            eventArrayList.add(mEvent);
        }

        return eventArrayList;
    }

    public static ArrayList<Event> parseLinkedEventsList(JSONObject object) throws JSONException {
        ArrayList<Event> eventArrayList = new ArrayList<Event>();
        JSONObject outputObject = object.getJSONObject("output");
        JSONArray responseArrayList = outputObject.getJSONArray("response");
        for (int i = 0; i < responseArrayList.length(); i++) {

            Event mEvent = new Event();
            JSONObject eventObject = responseArrayList.getJSONObject(i);
            mEvent.eventId = eventObject.optString("eventId");
            mEvent.eventTitle = eventObject.optString("eventTitle");
            mEvent.eventStartdate = eventObject.optString("startDate");
            mEvent.eventEnddate = eventObject.optString("endDate");
            mEvent.eventLocation = eventObject.optString("eventLocation");
            mEvent.mainImage = eventObject.optString("eventImage");

            eventArrayList.add(mEvent);
        }

        return eventArrayList;
    }

//    public static ArrayList<Event> ParseLinkEvents(JSONObject object) throws JSONException {
//        ArrayList<Event> eventArrayList = new ArrayList<Event>();
//        JSONObject outputObject = object.getJSONObject("output");
//        JSONArray responseArrayList = outputObject.getJSONArray("response");
//        for (int i = 0; i < responseArrayList.length(); i++) {
//
//            Event mEvent = new Event();
//            JSONObject eventObject = responseArrayList.getJSONObject(i);
//            mEvent.name = eventObject.optString("name");
//            mEvent.eve = eventObject.optString("eventTitle");
//            mEvent.eventType = eventObject.optString("eventType");
//            mEvent.eventTypeId = eventObject.optString("eventTypeId");
//            mEvent.eventStartdate = eventObject.optString("eventStartdate");
//            mEvent.eventEnddate = eventObject.optString("eventEnddate");
//            mEvent.eventLocation = eventObject.optString("eventLocation");
//            mEvent.website = eventObject.optString("website");
//            mEvent.phone = eventObject.optString("phone");
//            mEvent.price = eventObject.optString("price");
//            mEvent.imageCount = eventObject.optInt("imageCount");
//            if (eventObject.optInt("imageCount") > 0) {
//                JSONArray eventImage = eventObject.getJSONArray("eventImage");
//                for (int j = 0; j < eventImage.length(); j++) {
//                    mEvent.eventImages.add(eventImage.optString(j));
//                }
//            }
//            JSONArray categoriesList = eventObject.getJSONArray("Category");
//            for (int k = 0; k < categoriesList.length(); k++) {
//                JSONObject categoryObject = categoriesList.getJSONObject(k);
//                CategoryInfo mCategoryInfo = new CategoryInfo();
//                mCategoryInfo.category_id = categoryObject.optString("categoryId");
//                mCategoryInfo.sub_category_id = categoryObject.optString("subCategoryId");
//                mEvent.categoryInfoArrayList.add(mCategoryInfo);
//            }
//            eventArrayList.add(mEvent);
//        }
//
//        return eventArrayList;
//    }


    public static ArrayList<Event> parseAllEventsList(JSONObject object) throws JSONException {
        ArrayList<Event> eventArrayList = new ArrayList<Event>();
        JSONObject outputObject = object.getJSONObject("output");
        JSONArray responseArrayList = outputObject.getJSONArray("response");
        for (int i = 0; i < responseArrayList.length(); i++) {

            Event mEvent = new Event();
            JSONObject eventObject = responseArrayList.getJSONObject(i);
            mEvent.eventId = eventObject.optString("event_id");
            mEvent.latitude = eventObject.optString("latitude");
            mEvent.longitude = eventObject.optString("longitude");
            mEvent.start_date = eventObject.optString("start_date");
            mEvent.end_date = eventObject.optString("end_date");
            mEvent.eventName = eventObject.optString("eventName");
            mEvent.location = eventObject.optString("location");
            mEvent.mainImage = eventObject.optString("mainImage");

            eventArrayList.add(mEvent);
        }

        return eventArrayList;
    }


    public static ArrayList<Notifications> parseNotificationList(JSONObject object) throws JSONException {
        ArrayList<Notifications> notificationArrayList = new ArrayList<Notifications>();
        JSONObject outputObject = object.getJSONObject("output");
        JSONArray responseArrayList = outputObject.getJSONArray("response");
        for (int i = 0; i < responseArrayList.length(); i++) {

            Notifications mEvent = new Notifications();
            JSONObject eventObject = responseArrayList.getJSONObject(i);
            mEvent.id = eventObject.optString("id");
            mEvent.message = eventObject.optString("message");
            mEvent.model = eventObject.optString("model");
            mEvent.senderId = eventObject.optString("senderId");
            mEvent.receiverId = eventObject.optString("receiverId");
            mEvent.suggestedUserId = eventObject.optString("suggestedUserId");
            mEvent.eventLocation = eventObject.optString("eventLocation");
            mEvent.type = eventObject.optString("type");
            mEvent.eventImage = eventObject.optString("eventImage");
            mEvent.link_type = eventObject.optString("link_type");
            mEvent.sender_picture = eventObject.optString("sender_picture");
            mEvent.notificationId = eventObject.optString("notificationId");
            mEvent.has_link = eventObject.optInt("has_link");
            mEvent.sticky = eventObject.optInt("sticky");
            mEvent.action = eventObject.optInt("action");
            mEvent.hasDisplayImageOnNotification = eventObject.optInt("hasDisplayImageOnNotification");


            notificationArrayList.add(mEvent);
        }

        return notificationArrayList;
    }


    public static ArrayList<MemeberList> parseMemberList(JSONObject object) throws JSONException {
        ArrayList<MemeberList> eventMemeberList = new ArrayList<MemeberList>();
        JSONObject outputObject = object.getJSONObject("output");
        JSONArray responseArrayList = outputObject.getJSONArray("response");
        for (int i = 0; i < responseArrayList.length(); i++) {

            MemeberList mMemberList = new MemeberList();
            JSONObject eventObject = responseArrayList.getJSONObject(i);
            mMemberList.firstName = eventObject.optString("firstName");
            mMemberList.lastName = eventObject.optString("lastName");
            mMemberList.profile_picture = eventObject.optString("profile_picture");
            mMemberList.user_id = eventObject.optString("user_id");
            eventMemeberList.add(mMemberList);
        }

        return eventMemeberList;
    }

    public static EventDetail parseEventDetail(JSONObject object) throws JSONException {
        JSONObject outputObject = object.getJSONObject("output");
        JSONObject responseObject = outputObject.getJSONObject("response");
        EventDetail mEventDetail = new EventDetail();
        mEventDetail.eventId = responseObject.optString("eventId");
        mEventDetail.eventTitle = responseObject.optString("eventTitle");
        mEventDetail.eventType = responseObject.optString("eventType");
        mEventDetail.eventTypeId = responseObject.optInt("eventTypeId");
        mEventDetail.eventStartdate = responseObject.optString("eventStartdate");
        mEventDetail.eventEnddate = responseObject.optString("eventEnddate");
        mEventDetail.eventLocation = responseObject.optString("eventLocation");
        mEventDetail.eventWebsite = responseObject.optString("eventWebsite");
        mEventDetail.eventCapacity = responseObject.optString("eventCapacity");
        mEventDetail.eventDescription = responseObject.optString("eventDescription");
        mEventDetail.price = responseObject.optString("price");
        mEventDetail.longitude = responseObject.optString("longitude");
        mEventDetail.latitude = responseObject.optString("latitude");
        mEventDetail.phone = responseObject.optString("phone");
        mEventDetail.isInterested = responseObject.optInt("isInterested");
        mEventDetail.hasMoreAttendess = responseObject.getInt("hasMoreAttendess");
        mEventDetail.isGoing = responseObject.optInt("isGoing");
        mEventDetail.isFollowingOrganizer = responseObject.optInt("isFollowingOrganizer_description");
        mEventDetail.isCreatedByMe = responseObject.optString("isCreatedByMe");
        mEventDetail.isSponsered = responseObject.optString("isSponsered");
        mEventDetail.imageCount = responseObject.optInt("imageCount");
        if (responseObject.optInt("imageCount") > 0) {
            JSONArray eventImage = responseObject.getJSONArray("eventImage");
            for (int i = 0; i < eventImage.length(); i++) {
                mEventDetail.eventImages.add(eventImage.optString(i));
            }
        }
        JSONObject organiserObjet = responseObject.getJSONObject("organiser");
        mEventDetail.organiserId = organiserObjet.optString("id");
        mEventDetail.organiserFirsName = organiserObjet.optString("first_name");
        mEventDetail.organiserLastName = organiserObjet.optString("last_name");
        mEventDetail.organiserPicture = organiserObjet.optString("picture");
        mEventDetail.attendessCount = responseObject.optInt("attendessCount");
        JSONArray attendees = responseObject.getJSONArray("attendess");
        for (int j = 0; j < attendees.length(); j++) {
            JSONObject attendeesObject = attendees.getJSONObject(j);
            Attendees mAttendees = new Attendees();
            mAttendees.id = attendeesObject.optString("id");
            mAttendees.first_name = attendeesObject.optString("first_name");
            mAttendees.last_name = attendeesObject.optString("last_name");
            mAttendees.picture = attendeesObject.optString("picture");
            mEventDetail.attendessList.add(mAttendees);

        }
        JSONArray categories = responseObject.getJSONArray("categories");
        for (int j = 0; j < categories.length(); j++) {
            JSONObject attendeesObject = categories.getJSONObject(j);
            CategoryType mCategoryType = new CategoryType();
            mCategoryType.categoryId = attendeesObject.optString("category_id");
            mCategoryType.subCategoryId = attendeesObject.optString("sub_category_id");
            mEventDetail.categoriesList.add(mCategoryType);

        }


        return mEventDetail;
    }

    //Notification
    public static NotificationDetail parseNotificationDetail(JSONObject object) throws JSONException {
        JSONObject outputObject = object.getJSONObject("output");
        JSONObject responseObject = outputObject.getJSONObject("response");
        NotificationDetail mEventDetail = new NotificationDetail();
        mEventDetail.eventId = responseObject.optString("eventId");
        mEventDetail.postId = responseObject.optString("postId");
        mEventDetail.post = responseObject.optString("post");
        mEventDetail.postCreated = responseObject.optString("postCreated");
        mEventDetail.eventId = responseObject.optString("eventId");
        mEventDetail.eventName = responseObject.optString("eventName");
        mEventDetail.eventStartDate = responseObject.optString("eventStartDate");
        mEventDetail.eventEndDate = responseObject.optString("eventEndDate");
        mEventDetail.eventImage = responseObject.optString("eventImage");
        mEventDetail.userId = responseObject.optString("userId");
        mEventDetail.userName = responseObject.optString("userName");
        mEventDetail.location = responseObject.optString("location");
        mEventDetail.userProfile = responseObject.optString("userProfile");
        mEventDetail.canUpdateOrDeletePost = responseObject.optInt("canUpdateOrDeletePost");
            JSONArray image = responseObject.getJSONArray("image");
            for (int i = 0; i < image.length(); i++) {
                mEventDetail.image.add((String) image.get(i));
            }



        return mEventDetail;
    }


    public static ArrayList<Friend> parseFriendList(JSONObject object) throws JSONException {
        JSONObject outputObject = object.getJSONObject("output");
        JSONArray responseArray = outputObject.getJSONArray("response");

        ArrayList<Friend> mFriendList = new ArrayList<Friend>();
        for (int i = 0; i < responseArray.length(); i++) {
            JSONObject data = responseArray.getJSONObject(i);
            Friend mFriend = new Friend();
            mFriend.id = data.optString("user_id");
            mFriend.first_name = data.optString("firstName");
            mFriend.profile_picture = data.optString("profile_picture");
            mFriend.last_name = data.optString("lastName");
            mFriendList.add(mFriend);
        }
        return mFriendList;
    }

    public static ArrayList<Friend> parseAddFriendList(JSONObject object) throws JSONException {
        JSONObject outputObject = object.getJSONObject("output");
        JSONArray responseArray = outputObject.getJSONArray("response");

        ArrayList<Friend> mFriendList = new ArrayList<Friend>();
        for (int i = 0; i < responseArray.length(); i++) {
            JSONObject data = responseArray.getJSONObject(i);
            Friend mFriend = new Friend();
            mFriend.id = data.optString("user_id");
            mFriend.first_name = data.optString("firstName");
            mFriend.profile_picture = data.optString("profile_picture");
            mFriend.last_name = data.optString("lastName");
            mFriendList.add(mFriend);
        }
        return mFriendList;
    }

    public static FriendDetail parseFriendDetail(JSONObject object) throws JSONException {
        JSONObject outputObject = object.getJSONObject("output");
        JSONObject responseObject = outputObject.getJSONObject("response");
        FriendDetail mFriend = new FriendDetail();

        mFriend.isProfilePrivate = responseObject.optInt("isProfilePrivate");
        mFriend.profile_picture = responseObject.optString("profile_picture");
        mFriend.website = responseObject.optString("website");
        mFriend.UpCommingEventCount = responseObject.optInt("UpCommingEvenCount");
        mFriend.isFriend = responseObject.optInt("isFriend");
        mFriend.first_name = responseObject.optString("first_name");
        mFriend.address = responseObject.optString("address");
        mFriend.email = responseObject.optString("email");
        mFriend.last_name = responseObject.optString("last_name");
        mFriend.gender = responseObject.optString("gender");
        mFriend.dob = responseObject.optString("dob");
        mFriend.user_id = responseObject.optString("user_id");
        mFriend.isFollowing=responseObject.optInt("isFollowing");
        mFriend.isMyProfile = responseObject.optInt("isMyProfile");
        JSONArray UpComingEvents = responseObject.getJSONArray("UpComingEvents");
        for (int j = 0; j < UpComingEvents.length(); j++) {
            JSONObject upcomingEventObject = UpComingEvents.getJSONObject(j);
            Event mAttendees = new Event();
            mAttendees.eventStartdate = upcomingEventObject.optString("startDate");
            mAttendees.eventId = upcomingEventObject.optString("event_id");
            mAttendees.eventEnddate = upcomingEventObject.optString("endDate");
            mAttendees.eventLocation = upcomingEventObject.optString("location");
            mAttendees.eventImages.add(upcomingEventObject.optString("mainImage"));
            mAttendees.eventTitle = upcomingEventObject.optString("name");
            mFriend.attendessList.add(mAttendees);

        }

        mFriend.upComingEvents_hasPreviousPage=outputObject.optInt("upComingEvents_hasPreviousPage");
        mFriend.upComingEvents_currentPage=outputObject.optInt("upComingEvents_currentPage");
        mFriend.message=outputObject.optString("message");
        mFriend.canViewProfile=outputObject.optInt("canViewProfile");
        mFriend.upComingEvents_pagecount=outputObject.optInt("upComingEvents_pagecount");

        return mFriend;
    }

    public static ArrayList<Friend> parseFriendListForInvite(JSONObject object) throws JSONException {
        JSONObject outputObject = object.getJSONObject("output");
        int status = outputObject.optInt("status");
        if (status == 1) {
            //Log.i("checkParse",""+status);
            JSONArray response = outputObject.getJSONArray("response");
            ArrayList<Friend> mFriendListForInvite = new ArrayList<Friend>();
            for (int i = 0; i < response.length(); i++) {
                Friend mFriendInvite = new Friend();
                JSONObject data = response.getJSONObject(i);
                Log.i("checkParse", "" + status);
                mFriendInvite.id = data.optString("user_id");
                mFriendInvite.first_name = data.optString("firstName");
                mFriendInvite.profile_picture = data.optString("profile_picture");
                mFriendInvite.last_name = data.optString("lastName");
                mFriendInvite.invitation_status = data.optInt("invitation_status");
                mFriendInvite.invitation_status_msg = data.optString("invitation_status_msg");
                mFriendListForInvite.add(mFriendInvite);
            }
            return mFriendListForInvite;

        }
        return null;
    }

}

