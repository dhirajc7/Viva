package viva.oneplatinum.com.viva.fb;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.facebook.Response;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import viva.oneplatinum.com.viva.models.FbFriends;

public class SessionStore {
	private static final String FACEBOOK_OUTH_TOKEN = "access_token";
	private static final String KEY = "facebook-session";
	public static final String FACEBOOK_ID = "fbId", FACEBOOK_UNAME = "uname";
	public static final String USER_INFO = "userInfo";
	public static final String STATUS = "status";

	public static final String USER_TOKEN = "userToken";
	public static final String USER_ID = "userId";
	public static final String USER_EMAIL = "userEmail";
	public static final String USER_IMAGE = "userImage";
	public static final String USER_NAME = "userName";

	public static void clear(Context context) {
		Editor editor = context.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();
		
	}

	public static void addUserInfo(Context context, String facebookId,
			String facebookUsername) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
				.edit();
		editor.putString(FACEBOOK_ID, facebookId);
		editor.putString(FACEBOOK_UNAME, facebookUsername);
		editor.commit();
	}

	public static void storeAuthTOken(Context context, String token) {
		Editor editor = context.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE).edit();
		editor.putString(FACEBOOK_OUTH_TOKEN, token);
		editor.commit();
	}

	public static String getFacebookAuthToken(Context context) {
		SharedPreferences userInfo = context.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		return userInfo.getString(FACEBOOK_OUTH_TOKEN, "");

	}

	public static void clearUserInfo(Context context) {
		Editor editor = context.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();
	}

	public static void setUserProfileImage(Context context, String imagePath) {
		Editor editor = context.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE).edit();
		editor.putString(USER_IMAGE, imagePath);
		editor.commit();
	}

	public static String getUserProfile(Context context) {
		SharedPreferences userInfo = context.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		return userInfo.getString(USER_IMAGE, "");

	}

	public static String getUserName(Context context) {
		SharedPreferences userInfo = context.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		return userInfo.getString(USER_NAME, "");

	}

	public static String getUserId(Context context) {
		SharedPreferences userInfo = context.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		return userInfo.getString(USER_ID, "");
	}

	public static final String FNAME = "firstname", LNAME = "lastname",
			BDAY = "birthday", GENDER = "gender", USERID = "uid",
			UNAME = "uname", LOCATION = "location", IMAGE_URL = "image",
			NAME = "name", FB_EMAIL = "email",FB_FRIENDS="friends";

	public static void storeFacebookInformation(Context context, GraphUser user,Response response) {
		final Editor editor = context.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE).edit();
		Log.v("fbData", ""+user);
//		Toast.makeText(context, ""+user, Toast.LENGTH_LONG).show();
		if (user.getProperty("email") != null)
			editor.putString(FB_EMAIL, user.getProperty("email").toString());
		// Log.v("Facebook Email", user.asMap().get("email").toString()
		// +" check email");
		editor.putString(FACEBOOK_ID, user.getId());
		editor.putString(FNAME, user.getFirstName());
		editor.putString(LNAME, user.getLastName());
		editor.putString(NAME, user.getName());
		editor.putString(USER_IMAGE, "https://graph.facebook.com/"
                + user.getProperty("id").toString() + "/picture?type=large");
		if (user.asMap().get("gender") != null)
			editor.putString(GENDER, user.asMap().get("gender").toString());

		if (user.getLocation() != null) {
			editor.putString(LOCATION, user.getLocation().getName());
		}



		try {
			Log.d("editor","cczxczxczcz1");
			GraphObject graphObj = response.getGraphObject();
			Log.d("editor","cczxczxczcz2");
			if (graphObj != null) {
				Log.d("editor","cczxczxczcz3");
				JSONObject jsonObj = graphObj.getInnerJSONObject();
				JSONObject friends = jsonObj.getJSONObject("friends");
				Log.d("editor","cczxczxczcz4");
				editor.putString(FB_FRIENDS,friends.toString());
				Log.d("editor","cczxczxczcz5");
				JSONArray data = friends.getJSONArray("data");
				Log.d("editor","cczxczxczcz6");
				for(int  i= 0 ;i<data.length();i++)
				{
					Log.d("editor","cczxczxczcz");
					ArrayList<FbFriends> fbArrayList = new ArrayList<FbFriends>();
					FbFriends mfreinds = new FbFriends();
					JSONObject fbObject = data.getJSONObject((i));
					mfreinds.id = fbObject.optString("id");
					mfreinds.name = fbObject.optString("name");

					fbArrayList.add(mfreinds);
					editor.putString(FB_FRIENDS,data.toString());
					Log.d("editor",fbArrayList.toString());
				}
//				Log.d("editor","cczxczxczcz4");
//				JSONArray array = jsonObj.getJSONArray("data");
//				for (int i = 0; i < array.length(); i++) {
//					Log.d("editor","cczxczxczcz5");
//					JSONObject Jobj = (JSONObject) array.get(i);
//					String id = Jobj.getString("id");
//					String name = Jobj.getString("name");
//					FbFriends f = new FbFriends();
//					friendArrayList.add(f);
//					editor.putString(FB_FRIENDS,friendArrayList.toString());
//					Log.d("editor","cczxczxczcz6");
//				}
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}

//		GraphObject graphObject;
//
//		graphObject = response.getGraphObject();
//		JSONArray dataArray = (JSONArray) graphObject.getProperty("data");
//                for (int i = 0; i < dataArray.length(); i++) {
//
//					ArrayList<FbFriends> myList = new ArrayList<FbFriends>();
//					JSONObject jsonObject = dataArray.optJSONObject(i);
//					FbFriends mfreinds = new FbFriends(id, name);
//					mfreinds.id = jsonObject.optString("id");
//					mfreinds.name = jsonObject.optString("name");
//
//					myList.add(mfreinds);
//					editor.putString(FB_FRIENDS,myList.toString());
//                }
//			try {
//				Log.d("editor","cczxczxczcz1");
//				JSONObject userObject=new JSONObject(user+"");
//				Log.d("editor","cczxczxczcz3");
//				JSONObject friends = userObject.getJSONObject("friends");
//				Log.d("editor","cczxczxczcz4");
//				editor.putString(FB_FRIENDS,friends.toString());
//				Log.d("editor","cczxczxczcz5");
//				JSONArray data = friends.getJSONArray("data");
//				Log.d("editor","cczxczxczcz6");
//				for(int  i= 0 ;i<data.length();i++)
//				{
//					Log.d("editor","cczxczxczcz");
//					ArrayList<FbFriends> fbArrayList = new ArrayList<FbFriends>();
//					FbFriends mfreinds = new FbFriends();
//					JSONObject fbObject = data.getJSONObject((i));
//					mfreinds.id = fbObject.optString("id");
//					mfreinds.name = fbObject.optString("name");
//
//					fbArrayList.add(mfreinds);
//					editor.putString(FB_FRIENDS,fbArrayList.toString());
//					Log.d("editor",fbArrayList.toString());
//				}
//////				JSONObject objectdata = data.getJSONObject(0);
//////				String friend_name = objectdata.getString("name");
//////				String friend_id = objectdata.getString("id");
//////				editor.putString(FB_FRIENDS,objectdata.toString());
//			} catch (JSONException e) {
//				e.printStackTrace();
//				Log.d("editor","cczxczxczcz");
//			}
//		try {
//			JSONObject userObject=new JSONObject(user+"");
//			JSONObject jsonState=userObject. getJSONObject("state");
//			for(int i = 0 ; i<=jsonState.length();i++)
//			{
//				JSONObject stateObj = jsonState.getJSONObject(String.valueOf((i)));
//				JSONObject frinendsObj = stateObj.getJSONObject("friends");
//				JSONArray dataArray = frinendsObj.getJSONArray("data");
//				for(int j = 0 ;i<=dataArray.length();j++)
//				{
//					dataArray.getJSONObject(j);
//				}
//			}
//			JSONArray jarray = userObject.getJSONArray("data");
//
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
		editor.commit();
	}

	public static Map<String, String> fetchFacebookInformation(Context context) {
		Map<String, String> params = new HashMap<String, String>();
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				USER_INFO, Context.MODE_PRIVATE);

		params.put(USER_ID, sharedPreferences.getString(FACEBOOK_ID, null));
		params.put(NAME, sharedPreferences.getString(NAME, ""));
		params.put(FB_EMAIL, sharedPreferences.getString(FB_EMAIL, ""));
		params.put(FNAME, sharedPreferences.getString(FNAME, ""));
		params.put(LNAME, sharedPreferences.getString(LNAME, ""));
		params.put(LOCATION, sharedPreferences.getString(LOCATION, ""));
		params.put(GENDER, sharedPreferences.getString(GENDER, ""));
		params.put(USER_IMAGE, sharedPreferences.getString(USER_IMAGE, ""));
		params.put(FB_FRIENDS,sharedPreferences.getString(FB_FRIENDS,""));
		Log.d("params",params+"");
		return params;

	}

	public static void setEmail(String Email, Context context) {
		Editor editor = context.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE).edit();
		editor.putString(FB_EMAIL, Email);
		editor.commit();
	}
}
