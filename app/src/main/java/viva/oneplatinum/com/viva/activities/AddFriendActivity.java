package viva.oneplatinum.com.viva.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.HashMap;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.fb.FacebookLogin;
import viva.oneplatinum.com.viva.fb.SessionStore;
import viva.oneplatinum.com.viva.widgets.FontIcon;
import viva.oneplatinum.com.viva.widgets.RobotoBoldTextView;

/**
 * Created by D-MAX on 4/12/2016.
 */
public class AddFriendActivity extends ParentActivity {
    LinearLayout addFriendViva, addFreindGoogle, addFriendFacebook, addFreindAddressBook;
    FontIcon backBtnToolbar, searchIcon;
    ImageView nextBtnToolbar;
    RobotoBoldTextView titleToolbar;
    Toolbar mToolBar;
    GoogleApiClient google_api_client;
    GoogleApiAvailability google_api_availability;
    SignInButton signIn_btn;
    private static final int SIGN_IN_CODE = 0;
    private static final int PROFILE_PIC_SIZE = 120;
    private ConnectionResult connection_result;
    private boolean is_intent_inprogress;
    private boolean is_signInBtn_clicked;
    private int request_code;
    ProgressDialog progress_dialog;
    private static final String TAG = "LoginActivity";
    Boolean isFromGooglePlus;
    public static final int RQUEST_FACEBOOK_LOGIN = 1001;
    public static final int RQUEST_GOOGLE_LOGIN = 1002;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friend);
        isFromGooglePlus = getIntent().getBooleanExtra("isFromGooglePlus", true);
        initViews();
        initToolBar();

    }


    public void initViews() {

        addFreindGoogle = (LinearLayout) findViewById(R.id.addFriendGoogle);
        addFreindAddressBook = (LinearLayout) findViewById(R.id.addFriendAddressBook);
        addFriendFacebook = (LinearLayout) findViewById(R.id.addFriendFacebook);
        addFriendViva = (LinearLayout) findViewById(R.id.addFriendViva);
//        if (mVivaApplicatiion.getUserData(DataHolder.ISFROMFACEBOOK).equals("true")) {
//            addFriendFacebook.setVisibility(View.VISIBLE);
//
//        } else {
//            addFriendFacebook.setVisibility(View.GONE);
//        }
//        if (mVivaApplicatiion.getUserData(DataHolder.ISFROMGOOGLEPLUS).equals("true")) {
//            addFreindGoogle.setVisibility(View.VISIBLE);
//            addFreindGoogle.setOnClickListener(this);
//        } else {
//            addFreindGoogle.setVisibility(View.GONE);
//        }
//        if (mVivaApplicatiion.getUserData(DataHolder.ISFROMREGISTER).equals("true")) {
//            addFreindGoogle.setVisibility(View.GONE);
//            addFriendFacebook.setVisibility(View.GONE);
//        }
        addFriendViva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddFriendActivity.this, VivaFriendActivity.class);
                startActivity(i);
            }
        });
        addFreindAddressBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddFriendActivity.this, AddressBookActivity.class);
                startActivity(i);
            }
        });
        addFriendFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initFbLogin();
            }
        });
        addFreindGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initGoogleLogin();
            }
        });


    }
    private void initGoogleLogin() {
        Intent googleLoginIntent = new Intent(this, FriendsList.class);

//        Bundle params = new Bundle();
//        params.putInt(GoogleLoginActivity.gglLOGTYPE, GoogleLoginActivity.gglLOGIN);
//        googleLoginIntent.putExtras(params);
        startActivityForResult(googleLoginIntent, RQUEST_GOOGLE_LOGIN);
    }

    private void initFbLogin() {
        Intent fbLoginIntent = new Intent(this, FacebookLogin.class);
        Bundle params = new Bundle();
        params.putInt(FacebookLogin.LOGTYPE, FacebookLogin.LOGIN);
        fbLoginIntent.putExtras(params);
        startActivityForResult(fbLoginIntent, RQUEST_FACEBOOK_LOGIN);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == RQUEST_FACEBOOK_LOGIN) {
            HashMap<String, String> map = (HashMap<String, String>) SessionStore.fetchFacebookInformation(AddFriendActivity.this);
            Intent i = new Intent(AddFriendActivity.this, FriendsListForFacebook.class);
            i.putExtra("jsondata", map.get(SessionStore.FB_FRIENDS));
            Log.d("jsondata", map.get(SessionStore.FB_FRIENDS).toString());
            startActivity(i);

        }
    }



    private void initToolBar() {
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        mToolBar.addView(getCustomView());
    }

    private View getCustomView() {

        LayoutInflater linflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View customView = linflater.inflate(
                R.layout.toolbar_item_addfriend, null);
        Toolbar.LayoutParams layoutParam = new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        customView.setLayoutParams(layoutParam);
        backBtnToolbar = (FontIcon) customView.findViewById(R.id.backBtnToolbar);
        titleToolbar = (RobotoBoldTextView) customView.findViewById(R.id.titleToolbar);
        nextBtnToolbar = (ImageView) customView.findViewById(R.id.nextBtnToolbar);

        titleToolbar.setText("ADD FRIEND");
        nextBtnToolbar.setVisibility(View.VISIBLE);

        titleToolbar.setTextColor(getResources().getColor(R.color.txt_color_default));
        backBtnToolbar.setText("j");
        backBtnToolbar.setTextColor(getResources().getColor(R.color.red));
        backBtnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        backBtnToolbar.setTextColor(getResources().getColor(R.color.red));
        backBtnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        return customView;
    }

//    private class DownloadFacebookFriendsTask extends AsyncTask<FacebookFriend.Type, Boolean, Boolean> {
//        private final String TAG = DownloadFacebookFriendsTask.class.getSimpleName();
//        GraphObject graphObject;
//        ArrayList<FacebookFriend> myList = new ArrayList<FacebookFriend>();
//
//        @Override
//        protected Boolean doInBackground(FacebookFriend.Type... pickType) {
//            //
//            //Determine Type
//            //
//            String facebookRequest;
//            if (pickType[0] == FacebookFriend.Type.AVAILABLE) {
//                facebookRequest = "/me/friends";
//            } else {
//                facebookRequest = "/me/invitable_friends";
//            }
//
//            //
//            //Launch Facebook request and WAIT.
//            //
//            new Request(
//                    Session.getActiveSession(),
//                    facebookRequest,
//                    null,
//                    HttpMethod.GET,
//                    new Request.Callback() {
//                        public void onCompleted(Response response) {
//                            FacebookRequestError error = response.getError();
//                            if (error != null && response != null) {
//                                Log.e(TAG, error.toString());
//                            } else {
//                                graphObject = response.getGraphObject();
//                            }
//                        }
//                    }
//            ).executeAndWait();
//
//            //
//            //Process Facebook response
//            //
//            //
//            if (graphObject == null) {
//                return false;
//            }
//
//            int numberOfRecords = 0;
//            JSONArray dataArray = (JSONArray) graphObject.getProperty("data");
//            if (dataArray.length() > 0) {
//
//                // Ensure the user has at least one friend ...
//                for (int i = 0; i < dataArray.length(); i++) {
//
//                    JSONObject jsonObject = dataArray.optJSONObject(i);
//                    FacebookFriend facebookFriend = new FacebookFriend(jsonObject, pickType[0]);
//
//                    if (facebookFriend.isValid()) {
//                        numberOfRecords++;
//
//                        myList.add(facebookFriend);
//                    }
//                }
//            }
//
//            //make sure there are records to process
//            if (numberOfRecords > 0) {
//                return true;
//            } else {
//                return false;
//            }
//        }
//
//        @Override
//        protected void onProgressUpdate(Boolean... booleans) {
//            //no need to update this, wait until the whole thread finishes.
//        }
//
//        @Override
//        protected void onPostExecute(Boolean result) {
//            if (result) {
//                /*
//                User the array "myList" to create the adapter which will control showing items in the list.
//                 */
//
//            } else {
//            }
//        }
//    }
//
//    public static class FacebookFriend {
//        String facebookId;
//        String name;
//        String pictureUrl;
//        boolean invitable;
//        boolean available;
//        boolean isValid;
//
//        public boolean isValid() {
//            return true;
//        }
//
//        public enum Type {AVAILABLE, INVITABLE}
//
//        ;
//
//        public FacebookFriend(JSONObject jsonObject, Type type) {
//            //
//            //Parse the Facebook Data from the JSON object.
//            //
//            try {
//                if (type == Type.INVITABLE) {
//                    //parse /me/invitable_friend
//                    this.facebookId = jsonObject.getString("id");
//                    this.name = jsonObject.getString("name");
//
//                    //Handle the picture data.
//                    JSONObject pictureJsonObject = jsonObject.getJSONObject("picture").getJSONObject("data");
//                    boolean isSilhouette = pictureJsonObject.getBoolean("is_silhouette");
//                    if (!isSilhouette) {
//                        this.pictureUrl = pictureJsonObject.getString("url");
//
//                    } else {
//                        this.pictureUrl = "";
//                    }
//
//                    this.invitable = true;
//                } else {
//                    //parse /me/friends
//                    this.facebookId = jsonObject.getString("id");
//                    this.name = jsonObject.getString("name");
//                    this.available = true;
//                    this.pictureUrl = "";
//                }
//
//                isValid = true;
//            } catch (JSONException e) {
//                Log.w("#", "Warnings - unable to process FB JSON: " + e.getLocalizedMessage());
//            }
//        }
//
//    }
}
