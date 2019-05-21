package viva.oneplatinum.com.viva.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.maps.ShowEventLocationActivity;
import viva.oneplatinum.com.viva.models.EventDetail;
import viva.oneplatinum.com.viva.parser.ParseUtility;
import viva.oneplatinum.com.viva.utils.DataHolder;
import viva.oneplatinum.com.viva.utils.GeneralUtil;
import viva.oneplatinum.com.viva.widgets.CircularImageView;
import viva.oneplatinum.com.viva.widgets.FontIcon;
import viva.oneplatinum.com.viva.widgets.RobotoBoldTextView;
import viva.oneplatinum.com.viva.widgets.RobotoLightTextView;

/**
 * Created by D-MAX on 3/29/2016.
 */
public class EventDetailActivity extends ParentActivity {
    Toolbar mToolBar;
    FontIcon backBtnToolbar, newPostToolbar;
    ImageView editBtnToolbar;
    ImageView eventImage;
    private String eventId;
    RobotoBoldTextView followingOrganiser, goingMember, interestedMember, inviteFriends, eventName;
    RobotoLightTextView eventDescription, eventTime, eventDate, evenStreetLocation, eventCityLocation, eventPhone, eventMail,
            eventCost, eventLocation, orgasniserLastName, orgasniserFirstName, atendeesFirstName, attendeesLastName,
            atendeesFirstName1, attendeesLastName1, atendeesFirstName2, attendeesLastName2, eventStartDate;
    Button viewAllMembers;
    CircularImageView organiserImage, attendeesImage, attendeesImage1, attendeesImage2;
    LinearLayout eventLocationMainView, emailMainView,attendees,attendees1,attendees2,callEvent;
    private Bitmap userImageBitmap;
    EventDetail mEventDetail;
    AQuery aq;
    Boolean isInterested;
    JSONObject eventDetailResult;
    Boolean isFromCreate;
    Boolean isFromNotification;
    ArrayList<EventDetail> friendArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_active_buttons);
        eventId = getIntent().getStringExtra("eventId");
        isFromCreate = getIntent().getBooleanExtra("isFromCreate",false);
        isFromNotification = getIntent().getBooleanExtra("isFromNotification",true);
        friendArrayList=new ArrayList<EventDetail>();
        Log.e("eventId", eventId + "");
        aq = new AQuery(getApplicationContext());
        initToolBar();
        initViews();
        super.initViews(findViewById(R.id.main));
        getEventDetail();
        onClicks();

    }

    private void onClicks() {
        inviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EventDetailActivity.this, InviteFriendsActivity.class);
                i.putExtra("event_id", mEventDetail.eventId.toString());
                startActivity(i);
            }
        });
        eventLocationMainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EventDetailActivity.this, ShowEventLocationActivity.class);
                i.putExtra("latitude", mEventDetail.latitude);
                i.putExtra("eventLocationtitle", mEventDetail.eventLocation);
                i.putExtra("longitude", mEventDetail.longitude);
                startActivity(i);
            }
        });
        emailMainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://" + mEventDetail.eventWebsite));
                startActivity(i);
            }
        });
        viewAllMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EventDetailActivity.this, ViewAllMemberAvtivity.class);
                i.putExtra("event_id", eventId);
                startActivity(i);
            }
        });
        eventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailIntent = new Intent(EventDetailActivity.this,
                        ImageGalleryActivity.class);
                detailIntent.putExtra("position", 0);
                detailIntent.putStringArrayListExtra("IMAGES", mEventDetail.eventImages);
                startActivity(detailIntent);
            }
        });
        callEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = "tel:" + eventPhone.getText().toString().trim();
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                startActivity(callIntent);
            }
        });


    }

    private void initViews() {
        callEvent = (LinearLayout) findViewById(R.id.callEvent);
        eventImage = (ImageView) findViewById(R.id.eventImage);
        followingOrganiser = (RobotoBoldTextView) findViewById(R.id.followingOrganiser);
        goingMember = (RobotoBoldTextView) findViewById(R.id.goingMember);
        interestedMember = (RobotoBoldTextView) findViewById(R.id.interestedMember);
        inviteFriends = (RobotoBoldTextView) findViewById(R.id.inviteFriends);
        eventName = (RobotoBoldTextView) findViewById(R.id.eventName);
        eventDescription = (RobotoLightTextView) findViewById(R.id.eventDescription);
        eventDate = (RobotoLightTextView) findViewById(R.id.eventDate);
        eventTime = (RobotoLightTextView) findViewById(R.id.eventTime);
        eventLocation = (RobotoLightTextView) findViewById(R.id.eventLocation);
        evenStreetLocation = (RobotoLightTextView) findViewById(R.id.evenStreetLocation);
        eventCityLocation = (RobotoLightTextView) findViewById(R.id.eventCityLocation);
        eventPhone = (RobotoLightTextView) findViewById(R.id.eventPhone);
        eventCost = (RobotoLightTextView) findViewById(R.id.eventCost);
        eventMail = (RobotoLightTextView) findViewById(R.id.eventMail);
        viewAllMembers = (Button) findViewById(R.id.viewAllMembers);
        organiserImage = (CircularImageView) findViewById(R.id.organiserImage);
        orgasniserFirstName = (RobotoLightTextView) findViewById(R.id.orgasniserFirstName);
        orgasniserLastName = (RobotoLightTextView) findViewById(R.id.orgasniserLastName);
        attendeesImage = (CircularImageView) findViewById(R.id.attendeesImage);
        attendeesLastName = (RobotoLightTextView) findViewById(R.id.attendeesLastName);
        atendeesFirstName = (RobotoLightTextView) findViewById(R.id.attendeesFirstName);
        attendeesImage1 = (CircularImageView) findViewById(R.id.attendeesImage1);
        attendeesLastName1 = (RobotoLightTextView) findViewById(R.id.attendeeslastName1);
        atendeesFirstName1 = (RobotoLightTextView) findViewById(R.id.attendeesfirstName1);
        attendeesImage2 = (CircularImageView) findViewById(R.id.attendeesImage2);
        attendeesLastName2 = (RobotoLightTextView) findViewById(R.id.attendeeslastName2);
        atendeesFirstName2 = (RobotoLightTextView) findViewById(R.id.attendeesfirstName2);
        eventLocationMainView = (LinearLayout) findViewById(R.id.eventLocationMainView);
        emailMainView = (LinearLayout) findViewById(R.id.emailMainView);
        attendees = (LinearLayout) findViewById(R.id.attendees);
        attendees1 = (LinearLayout) findViewById(R.id.attendees1);
        attendees2 = (LinearLayout) findViewById(R.id.attendees2);


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
                R.layout.toolbar_profile_item, null);
        Toolbar.LayoutParams layoutParam = new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        customView.setLayoutParams(layoutParam);
        backBtnToolbar = (FontIcon) customView.findViewById(R.id.backBtnToolbar);
        editBtnToolbar = (ImageView) customView.findViewById(R.id.editBtnToolbar);
        if(isFromCreate)
        {
            editBtnToolbar.setVisibility(View.VISIBLE);
        }
        else{
            editBtnToolbar.setVisibility(View.GONE);
        }

        newPostToolbar = (FontIcon) customView.findViewById(R.id.newPostToolbar);

        backBtnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EventDetailActivity.this,MyEvents.class);
                startActivity(i);
                finish();
            }
        });
        editBtnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EventDetailActivity.this, EditEventActivity.class);
                i.putExtra("postDetailResult", eventDetailResult.toString());
                Log.e("postDetailResult", eventDetailResult + "");

                startActivity(i);
                finish();
            }
        });

        newPostToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EventDetailActivity.this, NewPostActivity.class);
                startActivity(i);
            }
        });


        return customView;
    }

    public void getEventDetail() {
        if (isNetworkAvailable()) {
            Log.d("getCategoriesresponse", "chek");
            showLoading();
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));
            params.put("event_id", eventId);
            mAqueryTask.sendRequest(DataHolder.EVENT_DETAIL, params, JSONObject.class, getEventDetailCallback());

        }
        showErrorLoading("No internet connection.",R.mipmap.ic_launcher);
    }


    private AjaxCallback<JSONObject> getEventDetailCallback() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);
                Log.d("getCategoriesresponse", result.toString());

                if (result != null) {

                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        int userStatus = outpuJsonObject.optInt("status");
                        if (userStatus == 1) {
                            eventDetailResult = result;
                            mEventDetail = ParseUtility.parseEventDetail(result);
                            setData();
                            showDataView();
                        } else {
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


    public void going() {
        if (isNetworkAvailable()) {
            Log.d("getCategoriesresponse", "chek");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.show();
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));
            params.put("event_id", eventId);
            mAqueryTask.sendRequest(DataHolder.UPDATE_USER_GOING, params, JSONObject.class, goingCallback());

        }
    }

    private AjaxCallback<JSONObject> goingCallback() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);
                Log.d("getCategoriesresponse", result.toString());
                mProgressDialog.dismiss();
                if (result != null) {

                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        int userStatus = outpuJsonObject.optInt("status");
                        if (userStatus == 1) {
                            showToast(outpuJsonObject.optString("message"));
                            goingMember.setSelected(true);
                            interestedMember.setSelected(true);
                            getEventDetail();

                        } else if (userStatus == 2) {
                            showToast(outpuJsonObject.optString("message"));
                            goingMember.setSelected(false);
                            getEventDetail();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            ;
        };
    }

    public void followingOrganiser() {
        if (isNetworkAvailable()) {
            Log.d("getCategoriesresponse", "chek");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.show();
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));
            params.put("following_user_id",mEventDetail.organiserId);
            Log.d("params",params.toString());
            mAqueryTask.sendRequest(DataHolder.FOLLOW_USER, params, JSONObject.class, followingOrganiserCallback());

        }
    }

    private AjaxCallback<JSONObject> followingOrganiserCallback() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);
                Log.d("getCategoriesresponse", result.toString());
                mProgressDialog.dismiss();
                if (result != null) {

                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        int userStatus = outpuJsonObject.optInt("status");
                        if (userStatus == 1) {
                            showToast(outpuJsonObject.optString("message"));
                            followingOrganiser.setSelected(true);
//                            followingOrganiser.setText("Follow request sent");
                            getEventDetail();

                        } else if (userStatus == 2) {
                            showToast(outpuJsonObject.optString("message"));
                            followingOrganiser.setSelected(true);
//                            followingOrganiser.setText("Already following a user");
                            getEventDetail();
                        } else if (userStatus == 3) {
                            showToast(outpuJsonObject.optString("message"));
                            followingOrganiser.setSelected(true);
//                            followingOrganiser.setText("Follow request sent but not accepted");
                            getEventDetail();
                        } else {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            ;
        };
    }

    public void interested() {
        if (isNetworkAvailable()) {
            Log.d("getCategoriesresponse", "chek");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.show();
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));
            params.put("event_id", eventId);
            mAqueryTask.sendRequest(DataHolder.UPDATE_USER_INTEREST, params, JSONObject.class, interestedCallback());

        }
    }

    private AjaxCallback<JSONObject> interestedCallback() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);
                Log.d("getCategoriesresponse", result.toString());
                mProgressDialog.dismiss();
                if (result != null) {

                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        int userStatus = outpuJsonObject.optInt("status");
                        if (userStatus == 1) {
                            showToast(outpuJsonObject.optString("message"));
                            interestedMember.setSelected(true);
                            getEventDetail();
                        } else if (userStatus == 2) {
                            interestedMember.setSelected(false);
                            getEventDetail();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            ;
        };
    }

    public void setData() {
        Calendar c = Calendar.getInstance();
        c.getTimeInMillis();

//        if(isFromCreate)
//        {
//            editBtnToolbar.setVisibility(View.VISIBLE);
//        }
//        else{
//            editBtnToolbar.setVisibility(View.GONE);
//        }
        if(mEventDetail.isCreatedByMe.equals("1"))
        {
            editBtnToolbar.setVisibility(View.VISIBLE);
        }
        else
        {
            editBtnToolbar.setVisibility(View.GONE);
        }

        eventName.setText(mEventDetail.eventTitle.toUpperCase());
        eventDescription.setText(mEventDetail.eventDescription);
        eventMail.setText(mEventDetail.eventWebsite);
        eventLocation.setText(mEventDetail.eventLocation);
        eventCityLocation.setText("");
        eventDate.setText(mEventDetail.eventStartdate);
        if (mEventDetail.eventStartdate != null) {
            if (mEventDetail.eventStartdate.length() > 0) {
                String dateandtime = GeneralUtil.convertDate(mEventDetail.eventStartdate);
                String[] date = dateandtime.split(",");
                eventDate.setText(date[0]);
                eventTime.setText(date[1].trim());
            }
        }
        if(mEventDetail.price.equals("0"))
        {
            eventCost.setText("Free");
        }
        else{
            eventCost.setText(mEventDetail.price);
        }

        eventPhone.setText(mEventDetail.phone);
        orgasniserFirstName.setText(mEventDetail.organiserFirsName);
        orgasniserLastName.setText(mEventDetail.organiserLastName);
        mAqueryTask.setImage(mEventDetail.organiserPicture.trim(), organiserImage, null);
        if (mEventDetail.eventImages.size() > 0) {
            mAqueryTask.setImage(mEventDetail.eventImages.get(0), eventImage, null);
        }

        for (int i = 0; i <mEventDetail.attendessList.size(); i++) {
            if (i == 0) {
                mAqueryTask.setImage(mEventDetail.attendessList.get(0).picture.trim(), attendeesImage, null);
                atendeesFirstName.setText(mEventDetail.attendessList.get(0).first_name);
                attendeesLastName.setText(mEventDetail.attendessList.get(0).last_name);
                atendeesFirstName.setVisibility(View.VISIBLE);
                attendeesLastName.setVisibility(View.VISIBLE);
                attendeesImage.setVisibility(View.VISIBLE);
                attendees.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(EventDetailActivity.this, FriendDetailActivity.class);
                        intent.putExtra("friendId",mEventDetail.attendessList.get(0).id );
                        startActivity(intent);
                    }
                });
            } if (i == 1) {
                mAqueryTask.setImage(mEventDetail.attendessList.get(1).picture.trim(), attendeesImage1, null);
                atendeesFirstName1.setText(mEventDetail.attendessList.get(1).first_name);
                attendeesLastName1.setText(mEventDetail.attendessList.get(1).last_name);
                attendeesImage1.setVisibility(View.VISIBLE);
                atendeesFirstName1.setVisibility(View.VISIBLE);
                attendeesLastName1.setVisibility(View.VISIBLE);
                attendees1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(EventDetailActivity.this, FriendDetailActivity.class);
                        intent.putExtra("friendId",mEventDetail.attendessList.get(1).id );
                        startActivity(intent);
                    }
                });
            }  if (i == 2) {
                mAqueryTask.setImage(mEventDetail.attendessList.get(2).picture.trim(), attendeesImage2, null);
                atendeesFirstName2.setText(mEventDetail.attendessList.get(2).first_name);
                attendeesLastName2.setText(mEventDetail.attendessList.get(2).last_name);
                attendeesImage2.setVisibility(View.VISIBLE);
                atendeesFirstName2.setVisibility(View.VISIBLE);
                attendeesLastName2.setVisibility(View.VISIBLE);
                attendees2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(EventDetailActivity.this, FriendDetailActivity.class);
                        intent.putExtra("friendId",mEventDetail.attendessList.get(2).id );
                        startActivity(intent);
                    }
                });
            }
        }
        if (mEventDetail.isGoing == 1) {
            goingMember.setSelected(true);
        } else {
            goingMember.setSelected(false);
        }
        if (mEventDetail.isInterested == 1) {
            interestedMember.setSelected(true);
        } else {
            interestedMember.setSelected(false);
        }
        if (mEventDetail.isFollowingOrganizer== 1) {
            followingOrganiser.setSelected(true);

        } else if(mEventDetail.isFollowingOrganizer == 2){
            followingOrganiser.setSelected(false);
        }
         else if(mEventDetail.isFollowingOrganizer == 0){
            followingOrganiser.setSelected(true);
        }
        if (mEventDetail.hasMoreAttendess == 1) {
            viewAllMembers.setVisibility(View.VISIBLE);
        } else {
            viewAllMembers.setVisibility(View.GONE);
        }
        if (mEventDetail.isCreatedByMe.equals("1")) {
            goingMember.setClickable(false);
            interestedMember.setClickable(false);
            followingOrganiser.setClickable(false);
            followingOrganiser.setSelected(false);

        } else {
            goingMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    going();
                }
            });
            interestedMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    interested();
                }
            });
            followingOrganiser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    followingOrganiser();
                }
            });
        }
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent i = new Intent(EventDetailActivity.this,MyEvents.class);
//        startActivity(i);
//    }
}
