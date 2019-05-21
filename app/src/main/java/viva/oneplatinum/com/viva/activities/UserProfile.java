package viva.oneplatinum.com.viva.activities;

import android.app.Activity;
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

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.models.UserDetail;
import viva.oneplatinum.com.viva.parser.ParseUtility;
import viva.oneplatinum.com.viva.utils.DataHolder;
import viva.oneplatinum.com.viva.utils.GeneralUtil;
import viva.oneplatinum.com.viva.widgets.CircularImageView;
import viva.oneplatinum.com.viva.widgets.FontIcon;
import viva.oneplatinum.com.viva.widgets.RobotoBoldTextView;

/**
 * Created by D-MAX on 3/18/2016.
 */
public class UserProfile extends ParentActivity {
    Toolbar mToolBar;
    FontIcon backBtnToolbar, newPostToolbar;
    ImageView editBtnToolbar, profileImageBack;
    CircularImageView userImage;
    RobotoBoldTextView firstName, lastName;
    UserDetail mUserDetail;
    LinearLayout createEvent, myevent, myFriends, addfriend, setting;
    public static Activity mActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_layout);
        initToolBar();
        initViews();
        super.initViews(findViewById(R.id.main));
        mActivity = this;
        getUserProfile();
        setOnClick();

    }

    private void setOnClick() {

        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfile.this, CreateEventBrandNew.class);
                startActivity(i);

            }
        });
        myevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfile.this, MyEvents.class);
                startActivity(i);
            }
        });
        myFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfile.this, MyFriendActivity.class);
                startActivity(i);
            }
        });
        addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfile.this, AddFriendActivity.class);
                startActivity(i);
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfile.this, Settings.class);
                startActivity(i);
            }
        });
    }

    private void initViews() {
        userImage = (CircularImageView) findViewById(R.id.userImage);
        profileImageBack = (ImageView) findViewById(R.id.profileImageBack);
        firstName = (RobotoBoldTextView) findViewById(R.id.firstName);
        lastName = (RobotoBoldTextView) findViewById(R.id.lastName);
        createEvent = (LinearLayout) findViewById(R.id.createEvent);
        myevent = (LinearLayout)findViewById(R.id.myevent);
        myFriends = (LinearLayout)findViewById(R.id.myFriends);
        addfriend = (LinearLayout)findViewById(R.id.addfriend);
        setting = (LinearLayout)findViewById(R.id.setting);
    }

    private void initToolBar() {
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);


        mToolBar.addView(getCustomView());
    }

    private View getCustomView() {

        LayoutInflater linflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View customView = linflater.inflate(
                R.layout.toolbar_profile_item, null);
        Toolbar.LayoutParams layoutParam = new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        customView.setLayoutParams(layoutParam);
        backBtnToolbar = (FontIcon) customView.findViewById(R.id.backBtnToolbar);
        editBtnToolbar = (ImageView) customView.findViewById(R.id.editBtnToolbar);
        newPostToolbar = (FontIcon) customView.findViewById(R.id.newPostToolbar);

        backBtnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfile.this,HomeActivity.class);
                startActivity(i);
                finish();
            }
        });
        editBtnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this, EditProfileActitvity.class);
                intent.putExtra("loginType", "facebook");
                intent.putExtra("picture", mUserDetail.profile_picture);
                intent.putExtra("last_name", mUserDetail.last_name);
                intent.putExtra("first_name", mUserDetail.first_name);
                intent.putExtra("displayName", "");
                intent.putExtra("email",mUserDetail.email);
                intent.putExtra("address",mUserDetail.address);
                intent.putExtra("website",mUserDetail.website);
                intent.putExtra("gender", mUserDetail.gender);
                intent.putExtra("is_Private",mUserDetail.is_private);
                Log.d("is_Private", String.valueOf(mUserDetail.is_private));
                String[] dob=mUserDetail.dob.split("-");
                intent.putExtra("birthday", mUserDetail.dob);
                startActivity(intent);
//                finish();
            }
        });
        newPostToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfile.this, NewPostActivity.class);
                startActivity(i);
                finish();
            }
        });


        return customView;
    }

    public void getUserProfile() {
        if (isNetworkAvailable()) {
            Log.d("getCategoriesresponse", "chek");
           showLoading();
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));
            mAqueryTask.sendRequest(DataHolder.MY_PROFILE, params, JSONObject.class, getUserProfileCallback());

        }   showErrorLoading("No internet connection.",R.mipmap.ic_launcher);
    }
    private AjaxCallback<JSONObject> getUserProfileCallback() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);
                Log.d("getCategoriesresponse", result.toString());

                if (result != null) {
                    try {
                        JSONObject outpuJsonObject=result.getJSONObject("output");
                        int userStatus=outpuJsonObject.optInt("status");
                        if(userStatus==1){
                           mUserDetail= ParseUtility.parseUserDetail(result);
                            setData();
                            showDataView();
                        }else{
                        showErrorLoading(outpuJsonObject.optString("message"),R.mipmap.ic_launcher);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            ;
        };
    }
    private void setData(){
    firstName.setText(mUserDetail.first_name);
        lastName.setText(mUserDetail.last_name);
        mVivaApplicatiion.setUserData(DataHolder.USERIMAGE,mUserDetail.profile_picture.trim());
        mAqueryTask.setImage(mUserDetail.profile_picture.trim(), userImage, null);
        Log.d("imageUrl",mUserDetail.profile_picture);
        GeneralUtil.setBlurBackground(mUserDetail.profile_picture.trim(),profileImageBack,UserProfile.this);
        Log.d("imageUrl",mUserDetail.profile_picture);
//        mAqueryTask.setImage(mUserDetail.profile_picture.trim(),profileImageBack,null);
        Log.i("checkSetData",""+userImage+profileImageBack);
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent i = new Intent(UserProfile.this,HomeActivity.class);
//        mActivity.finish();
//        startActivity(i);
//    }
}

