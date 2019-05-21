package viva.oneplatinum.com.viva.activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.maps.GPSTracker;
import viva.oneplatinum.com.viva.utils.DataHolder;


/**
 * Created by D-MAX on 3/16/2016.
 */
public class SplashActivity extends ParentActivity {
    private ImageView img;
    ImageView userImage, error_image;
    TextView username, inviteCount, pendingCount, text_error_loading, txt_error_loading_message;
    LinearLayout error_connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        txt_error_loading_message = (TextView) findViewById(R.id.txt_error_loading_message);
        error_image = (ImageView) findViewById(R.id.errorImage);
        error_connection = (LinearLayout) findViewById(R.id.error_Connection);
        text_error_loading = (TextView) findViewById(R.id.txt_error_loading);
        img = (ImageView) findViewById(R.id.img);
        img.clearAnimation();
        ((AnimationDrawable) img.getBackground()).start();

        text_error_loading.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                getMyEvents(DataHolder.EVENT_LISTING);
            }


        });
        img.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (mVivaApplicatiion.getUserData(DataHolder.TOKEN).length() > 0) {
//                    Intent i = new Intent(SplashActivity.this, HomeActivity.class);
//                    startActivity(i);
//                    finish();
//                }else {
//                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
//                    startActivity(i);
//                    finish();
//                }
                    GPSTracker mGpsTracker = new GPSTracker(getApplicationContext());
                    Location mLocation = null;
//                if (mVivaApplicatiion.getUserData(DataHolder.ISPINSELECTED) != null) {
                    if (mVivaApplicatiion.getUserData(DataHolder.ISPINSELECTED).length() == 0 || mVivaApplicatiion.getUserData(DataHolder.ISPINSELECTED).equals("false")) {
                        mLocation = mGpsTracker.getLocation();
                        mVivaApplicatiion.setUserData(DataHolder.CURRENT_LOCATION_LATITUDE, mLocation.getLatitude() + "");
                        mVivaApplicatiion.setUserData(DataHolder.CURRENT_LOCATION_LONGITUDE, mLocation.getLongitude() + "");
                        if (mLocation != null) {
                            Log.v("chek1", "dfdfd");
                            getMyEvents(DataHolder.EVENT_LISTING);
                        }
                    } else {
                        getMyEvents(DataHolder.EVENT_LISTING);

                    }
                }else{
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
//            else {
//                    mLocation = mGpsTracker.getLocation();
//                    mVivaApplicatiion.setUserData(DataHolder.CURRENT_LOCATION_LATITUDE, mLocation.getLatitude() + "");
//                    mVivaApplicatiion.setUserData(DataHolder.CURRENT_LOCATION_LONGITUDE, mLocation.getLongitude() + "");
//                    if (mLocation != null) {
//
//                        getMyEvents(DataHolder.EVENT_LISTING);
//                    }
//                }
//            }
        }, 1000);
    }

    public void getMyEvents(String url) {
        if (isNetworkAvailable()) {
            Log.e("getCategoriesresponse", "getCategoriesresponse");
//            mProgressDialog.setMessage("Loading events..");
//            mProgressDialog.show();
            Map<String, Object> params;
            try {
                JSONObject mainJson = new JSONObject();
                mainJson.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));
                mainJson.put("lat", mVivaApplicatiion.getUserData(DataHolder.CURRENT_LOCATION_LATITUDE));
                mainJson.put("lng", mVivaApplicatiion.getUserData(DataHolder.CURRENT_LOCATION_LONGITUDE));
                StringEntity s;

                s = new StringEntity(mainJson.toString(), HTTP.UTF_8);

                params = new HashMap<String, Object>();
                params.put(AQuery.POST_ENTITY, s);
                Log.v("chek",mainJson.toString());
                mAqueryTask.sendPostObjectRequest(url, params, JSONObject.class, getMyEventsCallback());
                error_connection.setVisibility(View.GONE);
                img.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            error_connection.setVisibility(View.VISIBLE);
            img.setVisibility(View.GONE);
        }
    }

    private AjaxCallback<JSONObject> getMyEventsCallback() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);
                Log.e("getCategoriesresponse", result.toString());
//                mProgressDialog.dismiss();

                if (result != null) {
                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        int userStatus = outpuJsonObject.optInt("status");
                        if (userStatus == 1) {
                            mVivaApplicatiion.setUserData(DataHolder.EVENTS, result.toString());
                            if (mVivaApplicatiion.getUserData(DataHolder.TOKEN).length() > 0) {
                                Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                                startActivity(i);
                                finish();
                            }
                        } else {
                            showToast(outpuJsonObject.optString("message"));
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
//                    showErrorLoading("Something went wrong", R.drawable.ic_no_data);
                }
            }

            ;
        };
    }

}
