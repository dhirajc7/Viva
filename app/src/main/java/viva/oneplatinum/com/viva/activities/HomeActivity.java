package viva.oneplatinum.com.viva.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.adapters.MyCircleLayoutAdapter;
import viva.oneplatinum.com.viva.circleview.CircleLayout;
import viva.oneplatinum.com.viva.interfaces.EventListner;
import viva.oneplatinum.com.viva.maps.MapActivity;
import viva.oneplatinum.com.viva.models.Event;
import viva.oneplatinum.com.viva.parser.ParseUtility;
import viva.oneplatinum.com.viva.utils.DataHolder;
import viva.oneplatinum.com.viva.utils.GeneralUtil;
import viva.oneplatinum.com.viva.utils.OnSwipeTouchListener;
import viva.oneplatinum.com.viva.widgets.FontIcon;
import viva.oneplatinum.com.viva.widgets.RobotoBoldTextView;

/**
 * Created by Dell on 3/23/2016.
 */
public class HomeActivity extends ParentActivity implements EventListner,SensorEventListener {

    FontIcon userProfile, notification,homeSearch,location;
    RobotoBoldTextView events;
    TextView eventName,eventLocation,eventDescription,eventTime,eventDetailByTap;
    ImageView eventDetailButton;
    View eventView;
    CircleLayout circularLayout ;
    ArrayList<Event> eventArrayList;
    String eventId,eventsData;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float vibrateThreshold = 0;
    public Vibrator v;
    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        eventArrayList = new ArrayList<Event>();
        eventView=(LinearLayout)findViewById(R.id.mainView);
      eventsData=  mVivaApplicatiion.getUserData(DataHolder.EVENTS);
        initData();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            vibrateThreshold = accelerometer.getMaximumRange() / 2;
        } else {
            // fai! we dont have an accelerometer!
        }

        //initialize vibration
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
//        initView();
//        getMyEvents(DataHolder.EVENT_LISTING);
    }

    private void initView() {

        eventName=(TextView)findViewById(R.id.eventName);
        eventLocation=(TextView)findViewById(R.id.event_location);
        eventDescription=(TextView)findViewById(R.id.eventDescription);
        eventTime=(TextView)findViewById(R.id.eventDateTime);
        eventDetailButton=(ImageView)findViewById(R.id.eventDetail);
        eventDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEventDetail();
            }
        });
        eventDetailByTap=(TextView)findViewById(R.id.moreInfo);
        eventDetailByTap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEventDetail();

            }
        });
        circularLayout =(CircleLayout) findViewById(R.id.circularLayout);
        circularLayout.removeAllViews();
        MyCircleLayoutAdapter ad=new MyCircleLayoutAdapter();
        for(int i=0;i<10;i++){
            if(i==0){
                updateEvent(0);
            }
            ad.add(R.drawable.ic_launcher,eventArrayList.get(i));
        }
//        ad.add(R.drawable.animation1);
//        ad.add(R.drawable.animation2);
//        ad.add(R.drawable.animation3);
//        ad.add(R.drawable.animation4);
//        ad.add(R.drawable.animation5);
//        ad.add(R.drawable.animation6);
//        ad.add(R.drawable.animation7);
//        ad.add(R.drawable.animation8);
//        ad.add(R.drawable.animation9);
//        ad.add(R.drawable.animation10);
//        ad.add(R.drawable.animation11);
        circularLayout.setAdapter(ad);
        circularLayout.setChildrenCount(10);
        circularLayout.setRadius(85);
        circularLayout.setOffsetY(-450);

        circularLayout.resumeAutoScroll();
        eventView.setVisibility(View.VISIBLE);
        homeSearch = (FontIcon) findViewById(R.id.homeSearch);
        location = (FontIcon) findViewById(R.id.location);
        location.setOnTouchListener(new OnSwipeTouchListener(HomeActivity.this) {
            public void onSwipeRight() {
                super.onSwipeRight();
                Intent intent = new Intent(HomeActivity.this, MapActivity.class);
                intent.putExtra("eventData",mVivaApplicatiion.getUserData(DataHolder.EVENTS));
                startActivity(intent);
            }
            public void onClick() {
                super.onClick();
                Intent intent = new Intent(HomeActivity.this, MapActivity.class);
                intent.putExtra("eventData",mVivaApplicatiion.getUserData(DataHolder.EVENTS));
                startActivity(intent);
            }

        });
//        homeSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(HomeActivity.this, FilterSearchActivity.class);
//                intent.putExtra("isFromHomeActiviy", true);
//                startActivity(intent);
//            }
//        });
        userProfile = (FontIcon) findViewById(R.id.userProfile);
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, UserProfile.class);
                startActivity(intent);
            }
        });

        notification = (FontIcon) findViewById(R.id.notification);
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });
        notification.setOnTouchListener(new OnSwipeTouchListener(HomeActivity.this) {
            public void onSwipeLeft() {
                super.onSwipeLeft();
                Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
            public void onClick() {
                super.onClick();
                Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
                startActivity(intent);
            }

        });
        userProfile.setOnTouchListener(new OnSwipeTouchListener(HomeActivity.this) {
            public void onSwipeTop() {
                super.onSwipeTop();
                Intent intent = new Intent(HomeActivity.this, UserProfile.class);
                startActivity(intent);
            }
            public void onClick() {
                super.onClick();
                Intent intent = new Intent(HomeActivity.this, UserProfile.class);
                startActivity(intent);
            }

        });
        homeSearch.setOnTouchListener(new OnSwipeTouchListener(HomeActivity.this) {
            public void onSwipeBottom() {
                super.onSwipeBottom();
                Intent intent = new Intent(HomeActivity.this, FilterSearchActivity.class);
                intent.putExtra("isFromHomeActivity", true);
                startActivity(intent);
                finish();
            }
            public void onClick() {
                super.onClick();
                Intent intent = new Intent(HomeActivity.this, FilterSearchActivity.class);
                intent.putExtra("isFromHomeActivity", true);
                startActivity(intent);
                finish();
            }

        });

//        userProfile.setOnTouchListener(new OnSwipeTouchListener(HomeActivity.this) {
//            public void onSwipeTop() {
//                Intent intent = new Intent(HomeActivity.this, UserProfile.class);
//                startActivity(intent);
//            }
//
//        });

        events = (RobotoBoldTextView) findViewById(R.id.events);
        events.setVisibility(View.GONE);
        events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, EventListingActivity.class);
                startActivity(intent);
            }
        });
    }
    public void getMyEvents(String url) {
        if (isNetworkAvailable()) {
            Log.e("getCategoriesresponse", "getCategoriesresponse");
            mProgressDialog.setMessage("Loading events..");
            mProgressDialog.show();
            mAqueryTask.sendRequest(url, JSONObject.class, getMyEventsCallback());

        }
    }

    private AjaxCallback<JSONObject> getMyEventsCallback() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);
                Log.e("getCategoriesresponse", result.toString());
                mProgressDialog.dismiss();

                if (result != null) {
                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        int userStatus = outpuJsonObject.optInt("status");
                        if (userStatus == 1) {
                            eventArrayList = ParseUtility.parseAllEventsList(result);
                            initView();
                        } else {
//                            showErrorLoading(outpuJsonObject.optString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            ;
        };
    }
    public void updateEvent(int i){
        int position=i%10;

        Event mEvent=eventArrayList.get(position);

        eventId=mEvent.eventId;
        eventName.setText(mEvent.eventName);
        eventLocation.setText(mEvent.location);
        eventDescription.setText("");
        eventTime.setText(GeneralUtil.convertEventDate(mEvent.start_date));

    }
    public void showEventDetail(){
        Intent intent = new Intent(HomeActivity.this, EventDetailActivity.class);
        intent.putExtra("eventId", eventId);
        startActivity(intent);
    }

    @Override
    public void eventposition(Boolean flag,int i) {
        int position=i%10;
        if(position<0){
            position=10-position;
        }
        Event mEvent=eventArrayList.get(position);

        eventId=mEvent.eventId;
        if(flag)
        updateEvent(position);
        else
            showEventDetail();
    }
    public void initData(){
        try {
            JSONObject data=new JSONObject(eventsData);
            eventArrayList = ParseUtility.parseAllEventsList(data);
            initView();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
//        showToast(event.values[0]+"");
        Log.v("angle", event.values[0] + "");
        if(event.values[0]>0.20 || event.values[0]<-0.20) {

            circularLayout.pauseAutoScroll();
            circularLayout.rotate(event.values[0]);
        }else{
            circularLayout.resumeAutoScroll();
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public void vibrate() {
        if ((deltaX > vibrateThreshold) || (deltaY > vibrateThreshold) || (deltaZ > vibrateThreshold)) {
            v.vibrate(50);
        }
    }
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Are you sure you want to exit?");
            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    finish();
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
    }
}
