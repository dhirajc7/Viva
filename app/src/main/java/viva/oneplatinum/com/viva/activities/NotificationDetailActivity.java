package viva.oneplatinum.com.viva.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.adapters.Hori_ListAdapter;
import viva.oneplatinum.com.viva.models.NotificationDetail;
import viva.oneplatinum.com.viva.models.Notification_Image;
import viva.oneplatinum.com.viva.models.Notifications;
import viva.oneplatinum.com.viva.parser.ParseUtility;
import viva.oneplatinum.com.viva.utils.DataHolder;
import viva.oneplatinum.com.viva.utils.GeneralUtil;
import viva.oneplatinum.com.viva.widgets.CircularImageView;
import viva.oneplatinum.com.viva.widgets.FontIcon;
import viva.oneplatinum.com.viva.widgets.HorizontalListView;
import viva.oneplatinum.com.viva.widgets.RobotoBoldTextView;
import viva.oneplatinum.com.viva.widgets.RobotoRegularTextView;

/**
 * Created by D-MAX on 4/24/2016.
 */
public class NotificationDetailActivity extends ParentActivity {
    Toolbar mToolBar;
    RelativeLayout cancel, report, block;
    RobotoRegularTextView block_permit, reportUs;
    Hori_ListAdapter hori_ListAdapter;
    HorizontalListView lv;
    CircularImageView postUserImage, eventImage;
    RobotoRegularTextView notificationDescription;
    RobotoBoldTextView event_name, eventLocation;
    Context context;
    FontIcon backBtnToolbar, nextBtnToolbar;
    RobotoBoldTextView titleToolbar;
    String post_id, deleteGroupId;
    NotificationDetail mNotificationDetail;
    ArrayList<Notification_Image> event;
    ArrayList<NotificationDetail> list;
    ArrayList<Notifications> notificationsArrayList;
    JSONObject postDetailResult;
    LinearLayout galleryImageContainer;
    private int currentFirstVisibleItem, currentVisibleItemCount, currentScrollState;
    int totalpageCount, CurrentPageCount = 1;
    Boolean isFetching = false, isFirstFetch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_detail);
        initViews();
        super.initViews(findViewById(R.id.main));
        notificationsArrayList = new ArrayList<Notifications>();
        post_id = getIntent().getStringExtra("post_id");
        getNotificationDetail(DataHolder.NOTIFICATION_DETAIL);
        context = this;

        initToolBar();


    }

    public void initViews() {

        postUserImage = (CircularImageView) findViewById(R.id.postUserImage);
        eventImage = (CircularImageView) findViewById(R.id.eventImage);
        notificationDescription = (RobotoRegularTextView) findViewById(R.id.notificationDescription);
        event_name = (RobotoBoldTextView) findViewById(R.id.event_name);
        eventLocation = (RobotoBoldTextView) findViewById(R.id.eventLocation);
        galleryImageContainer = (LinearLayout) findViewById(R.id.galleryImageContainer);



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
                R.layout.toolbar_item, null);
        Toolbar.LayoutParams layoutParam = new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        customView.setLayoutParams(layoutParam);
        backBtnToolbar = (FontIcon) customView.findViewById(R.id.backBtnToolbar);
        titleToolbar = (RobotoBoldTextView) customView.findViewById(R.id.titleToolbar);
        nextBtnToolbar = (FontIcon) customView.findViewById(R.id.nextBtnToolbar);

//        titleToolbar.setText("NOTIFICATIONS");
        nextBtnToolbar.setText("a");
        nextBtnToolbar.setTextColor(getResources().getColor(R.color.red));
        nextBtnToolbar.setVisibility(View.GONE);
        nextBtnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                View view = getLayoutInflater().inflate(R.layout.friends_profile_menu_dialog, null);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(view);
                Window window = dialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.BOTTOM;
                wlp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                wlp.dimAmount = 0.7f;
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                window.setAttributes(wlp);
                block_permit = (RobotoRegularTextView) view.findViewById(R.id.block_permit);
                reportUs = (RobotoRegularTextView) view.findViewById(R.id.reportUs);
                block_permit.setText("Edit");
                reportUs.setText("Delete");
                cancel = (RelativeLayout) view.findViewById(R.id.cancel);
                cancel.setVisibility(View.GONE);

                block = (RelativeLayout) view.findViewById(R.id.block);
                block.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(NotificationDetailActivity.this, NewPostActivity.class);
                        i.putExtra("postDetailResult", postDetailResult.toString());
                        i.putExtra("post_id", post_id);
                        i.putExtra("isFromNotification",true);
                        startActivity(i);
                        dialog.dismiss();
                        finish();
                        //codes to block user
//                        editPost();
                    }
                });
                report = (RelativeLayout) view.findViewById(R.id.report);
                report.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //codes to report user
                        deletePost();
                        dialog.dismiss();
                        finish();
                    }
                });
                dialog.show();
            }
        });

        titleToolbar.setTextColor(getResources().getColor(R.color.txt_color_default));
        backBtnToolbar.setText("j");
        backBtnToolbar.setTextColor(getResources().getColor(R.color.red));
        backBtnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        return customView;
    }


    public void getNotificationDetail(String url) {
        if (isNetworkAvailable()) {

            showLoading();

            Log.e("getCategoriesresponse", "getCategoriesresponse");
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));
            params.put("post_id", post_id.toString());
            mAqueryTask.sendRequest(url, params, JSONObject.class, getNotificationDetailCallback());

        }
    }

    private AjaxCallback<JSONObject> getNotificationDetailCallback() {
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
                            postDetailResult = result;
                            mNotificationDetail = ParseUtility.parseNotificationDetail(result);
                            setData();
                            showDataView();
                        }
                        if (userStatus == 2) {
//                            Toast.makeText(NotificationDetailActivity.this, "This post has been deleted by user.", Toast.LENGTH_SHORT).show();

                            showToast(outpuJsonObject.optString("message"));
                            Intent i = new Intent(NotificationDetailActivity.this,HomeActivity.class);
                            startActivity(i);
                            finish();
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
        titleToolbar.setText(GeneralUtil.convertPostDate(mNotificationDetail.postCreated));
        if (mNotificationDetail.canUpdateOrDeletePost == 1) {
            nextBtnToolbar.setVisibility(View.VISIBLE);
        } else {
            nextBtnToolbar.setVisibility(View.GONE);
        }
        galleryImageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailIntent = new Intent(NotificationDetailActivity.this,
                        ImageGalleryActivity.class);
                detailIntent.putExtra("position", 0);
                detailIntent.putStringArrayListExtra("IMAGES", mNotificationDetail.image);
                startActivity(detailIntent);
            }
        });
        galleryImageContainer.removeAllViews();
        for (int i = 0; i < mNotificationDetail.image.size(); i++) {
            Log.d("image", mNotificationDetail.image.toString());
            final View imageViewLayout = getLayoutInflater().inflate(
                    R.layout.image_layout, null);
            imageViewLayout.setLayoutParams(
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

            ImageView imageView = (ImageView) imageViewLayout.findViewById(R.id.Clickedimage);
            final FontIcon deleteIcon = (FontIcon) imageViewLayout.findViewById(R.id.delete);
            deleteIcon.setVisibility(View.GONE);
            mAqueryTask.setImage(mNotificationDetail.image.get(i),imageView,null);
            galleryImageContainer.addView(imageViewLayout);
        }

//        lv = (HorizontalListView) findViewById(R.id.HlistView);
//        hori_ListAdapter = new Hori_ListAdapter(context, event);
//        lv.setAdapter(hori_ListAdapter);
        event_name.setText(mNotificationDetail.eventName.toUpperCase());
        notificationDescription.setText(mNotificationDetail.post);
        eventLocation.setText(mNotificationDetail.location);
//        if (mEventDetail.eventStartdate != null) {
//            if (mEventDetail.eventStartdate.length() > 0) {
//                String dateandtime = GeneralUtil.convertDate(mEventDetail.eventStartdate);
//                String[] date = dateandtime.split(",");
//                eventDate.setText(date[0]);
//                eventTime.setText(date[1].trim());
//            }
//        }
        mAqueryTask.setImage(mNotificationDetail.userProfile.trim(), postUserImage, null);
        mAqueryTask.setImage(mNotificationDetail.eventImage.trim(), eventImage, null);


    }

    public void deletePost() {
        AQuery aquery = new AQuery(this);
        if (GeneralUtil.isNetworkAvailable(context)) {
            showLoadingDialog();
            Map<String, Object> params;
            try {
                JSONObject mainJson = new JSONObject();
                mainJson.put("token",  mVivaApplicatiion.getUserData(DataHolder.TOKEN));
                mainJson.put("post_id", post_id.toString());


                StringEntity s;

                s = new StringEntity(mainJson.toString(), HTTP.UTF_8);

                params = new HashMap<String, Object>();
                params.put(AQuery.POST_ENTITY, s);
                System.out.println(DataHolder.DELETE_POST + mainJson);
                aquery.ajax(DataHolder.DELETE_POST, params, JSONObject.class, deleteFriendCallback());

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {
            System.out.println(DataHolder.DELETE_POST + "no internet");

        }

    }

    private AjaxCallback<JSONObject> deleteFriendCallback() {
        return new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {

                System.out.println(object);
                System.out.println(result);
                Log.e("getCategoriesresponse", result.toString());
                hideLoadingDialog();
                if (result != null) {
                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        int userStatus = outpuJsonObject.optInt("status");
                        if (userStatus == 1) {
                            showToast(outpuJsonObject.optString("message"));
                            Intent i = new Intent(NotificationDetailActivity.this, HomeActivity.class);
                            startActivity(i);
                            finish();
                        } else {
//                            showErrorLoading(outpuJsonObject.optString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.header("Content-Type", "application/json");
    }

    public void editPost() {
        AQuery aquery = new AQuery(this);
        if (GeneralUtil.isNetworkAvailable(context)) {
            showLoadingDialog();
            Map<String, Object> params;
            try {
                JSONObject mainJson = new JSONObject();
                mainJson.put("token", "cdojghf6cchb2g06ot2hnfrcgaqfe5vxbl612orq");
                mainJson.put("post_id", post_id.toString());


                StringEntity s;

                s = new StringEntity(mainJson.toString(), HTTP.UTF_8);

                params = new HashMap<String, Object>();
                params.put(AQuery.POST_ENTITY, s);
                System.out.println(DataHolder.EDIT_POST + mainJson);
                aquery.ajax(DataHolder.EDIT_POST, params, JSONObject.class, editFriendCallback());

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {
            System.out.println(DataHolder.EDIT_POST + "no internet");

        }

    }

    private AjaxCallback<JSONObject> editFriendCallback() {
        return new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {

                System.out.println(object);
                System.out.println(result);
                Log.e("getCategoriesresponse", result.toString());
                hideLoadingDialog();
                if (result != null) {
                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        int userStatus = outpuJsonObject.optInt("status");
                        if (userStatus == 1) {
                            showToast(outpuJsonObject.optString("message"));
                            Intent i = new Intent(NotificationDetailActivity.this, NotificationActivity.class);
                            startActivity(i);
                        } else {
//                            showErrorLoading(outpuJsonObject.optString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.header("Content-Type", "application/json");
    }

    Dialog loadingDialog;

    public void showLoadingDialog() {
        loadingDialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_loading, null);
        loadingDialog.addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

    }

    public void hideLoadingDialog() {
        loadingDialog.dismiss();
    }

}