package viva.oneplatinum.com.viva.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.adapters.ListAdapter;
import viva.oneplatinum.com.viva.models.Event;
import viva.oneplatinum.com.viva.models.FriendDetail;
import viva.oneplatinum.com.viva.parser.ParseUtility;
import viva.oneplatinum.com.viva.utils.DataHolder;
import viva.oneplatinum.com.viva.utils.GeneralUtil;
import viva.oneplatinum.com.viva.widgets.CircularImageView;
import viva.oneplatinum.com.viva.widgets.FontIcon;
import viva.oneplatinum.com.viva.widgets.RobotoBoldTextView;

/**
 * Created by Dell on 4/4/2016.
 */
public class FriendDetailActivity extends ParentActivity {
    Toolbar mToolBar;
    FontIcon backBtnToolbar, nextBtnToolbar;
    final Context context = this;
    RelativeLayout cancel, report, block;
    ListView lv;
    ListAdapter listAdapter;
    ArrayList<Event> eventArrayList;
    private int currentFirstVisibleItem, currentVisibleItemCount, currentScrollState;
    int totalpageCount, CurrentPageCount = 1;
    Boolean isFetching = false, isFirstFetch = true;
    String userId, strPost;
    FriendDetail mFriendDetail;
    TextView firstName, lastName, location, email;
    CircularImageView imageView;
    ImageView blurImage;
    LinearLayout dataView, privateView;
    RobotoBoldTextView followBtn;
    private Bitmap userImageBitmap;
    AQuery aq;
    String imageUrl = "";
    Dialog ReportDialog, BlockDialog;
    EditText post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_profile);
        initToolBar();
        final Context context = this;
        userId = getIntent().getStringExtra("friendId");
        Log.d("userid", userId.toString());
        super.initViews(findViewById(R.id.main));
        getFriendDetail();
//        getMyEvents(DataHolder.UPCOMING_EVENTS + "1.json");

        initData();

//        getVivaFreinds(DataHolder.MY_EVENTS);
    }

    private void initData() {
        firstName = (TextView) findViewById(R.id.firstName);
        lastName = (TextView) findViewById(R.id.lastName);
        location = (TextView) findViewById(R.id.friendLocation);
        email = (TextView) findViewById(R.id.friendInfo);
        imageView = (CircularImageView) findViewById(R.id.friendImage);
        blurImage = (ImageView) findViewById(R.id.profileImageBack);
        lv = (ListView) findViewById(R.id.event_list);
        dataView = (LinearLayout) findViewById(R.id.dataView);
        privateView = (LinearLayout) findViewById(R.id.privateView);
        followBtn = (RobotoBoldTextView) findViewById(R.id.followBtn);


        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFriendDetail.isFollowing == 1) {
                    unfollowUser(DataHolder.UNFOLLOW_USER);
                } else if (mFriendDetail.isFollowing == 2) {
                    followUser(DataHolder.FOLLOW_USER);
                } else if (mFriendDetail.isFollowing == 0) {
                    followBtn.setText("Pending");
                }
                //finish();
            }
        });
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                currentScrollState = scrollState;
                if (currentVisibleItemCount > 0 && currentScrollState == SCROLL_STATE_IDLE) {
                    isScrollCompleted();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                currentFirstVisibleItem = firstVisibleItem;
                currentVisibleItemCount = visibleItemCount;
            }

        });
        listAdapter = new ListAdapter(getApplicationContext(), 0, eventArrayList, mVivaApplicatiion.getUserData(DataHolder.TOKEN), false);
    }

    Dialog loadingDialog;

    private void showLoadingDialog() {
        loadingDialog = new Dialog(this,
                android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_loading,
                null);
        loadingDialog.addContentView(view, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();
    }

    private void hideLoadingDialog() {
        loadingDialog.dismiss();
        //finish();
    }

    private void followUser(String url) {

        if (isNetworkAvailable()) {
            mProgressDialog.setMessage("Following...");
            mProgressDialog.show();
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));
            params.put("following_user_id", userId);

            mAqueryTask.sendRequest(url, params, JSONObject.class, getFollowUserCallback());

        }
    }

    private void unfollowUser(String url) {

        if (isNetworkAvailable()) {
            mProgressDialog.setMessage("Unfollowing...");
            mProgressDialog.show();

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));
            params.put("user_id", userId);

            mAqueryTask.sendRequest(url, params, JSONObject.class, getunFollowUserCallback());

        }
    }

    private AjaxCallback<JSONObject> getFollowUserCallback() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                Log.e("checkFriendlist", "" + result);
                mProgressDialog.dismiss();

                if (result != null) {
                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        int followStatus = outpuJsonObject.optInt("status");
                        //mFriendDetail =ParseUtility.parseFriendDetail(obj);
                        if (followStatus == 1) {
                            mFriendDetail.isFollowing = 1;
                            showToast(outpuJsonObject.optString("message"));
                            followBtn.setText("+UNFOLLOW");
                        } else if (followStatus == 2) {
                            showToast(outpuJsonObject.optString("message"));
                            followBtn.setText("following");
                        } else if (followStatus == 3) {
                            showToast(outpuJsonObject.optString("message"));
                            followBtn.setText("pending");
                        } else {
                            showToast(outpuJsonObject.optString("message"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            ;
        };
    }

    private AjaxCallback<JSONObject> getunFollowUserCallback() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                Log.e("checkFriendlist", "" + result);
                mProgressDialog.dismiss();

                if (result != null) {
                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        int unfollowStatus = outpuJsonObject.optInt("status");
                        //mFriendDetail =ParseUtility.parseFriendDetail(obj);
                        if (unfollowStatus == 1) {
                            mFriendDetail.isFollowing = 2;
                            showDataView();
                            showToast(outpuJsonObject.optString("message"));
                            followBtn.setText("+FOLLOW");


                        } else if (unfollowStatus == 2) {
                            showToast(outpuJsonObject.optString("message"));
                        } else {
                            showToast(outpuJsonObject.optString("message"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            ;
        };
    }


    public void isScrollCompleted() {
        if (!isFetching) {
            if (CurrentPageCount < totalpageCount) {
                int pageCount = (CurrentPageCount + 1);
                getMyEvents(DataHolder.UPCOMING_EVENTS + pageCount + ".json");
                isFetching = true;
                Log.d("getCategoriesresponse", "getCategoriesresponse");
            }
        }
    }

    public void getFriendDetail() {
        if (isNetworkAvailable()) {
            showLoading();
            Log.e("getCategoriesresponse", "getCategoriesresponse");
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));

            params.put("user_id", userId);
            mAqueryTask.sendRequest(DataHolder.USER_PROFILE, params, JSONObject.class, getMyFriendsCallback());
            Log.i("checkToken", "" + mVivaApplicatiion.getUserData(DataHolder.TOKEN));
        }
        showErrorLoading("No internet connection.",R.mipmap.ic_launcher);
    }

    private AjaxCallback<JSONObject> getMyFriendsCallback() {
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
                            mFriendDetail = ParseUtility.parseFriendDetail(result);
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

    public void getMyEvents(String url) {
        if (isNetworkAvailable()) {
            if (isFirstFetch) {
                showLoading();
            }
            Log.e("getCategoriesresponse", "getCategoriesresponse");
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));
            params.put("user_id", userId);
            mAqueryTask.sendRequest(url, params, JSONObject.class, getMyEventsCallback());

        }
    }

    private AjaxCallback<JSONObject> getMyEventsCallback() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);
                Log.e("getCategoriesresponseevent", result.toString());
//                mProgressDialog.dismiss();

                if (result != null) {
                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        int userStatus = outpuJsonObject.optInt("status");
                        if (userStatus == 1) {
                            CurrentPageCount = outpuJsonObject.optInt("currentPage");
                            totalpageCount = outpuJsonObject.optInt("pagecount");
                            ArrayList<Event> arrayList = new ArrayList<Event>();
                            arrayList = ParseUtility.parseEventsList(result);
                            eventArrayList.addAll(arrayList);
                            isFetching = false;
                            isFirstFetch = false;
                            setData();
                            showDataView();
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

    public void setData() {

        if (mFriendDetail.isMyProfile == 0) {
            followBtn.setVisibility(View.VISIBLE);
        }
        else{
            followBtn.setVisibility(View.GONE);
        }
        firstName.setText(mFriendDetail.first_name);
        lastName.setText(mFriendDetail.last_name);
        if (mFriendDetail.profile_picture.length() > 0) {
            mAqueryTask.setImage(mFriendDetail.profile_picture.trim(), imageView, null);
            Log.d("imageUrl", mFriendDetail.profile_picture);
            GeneralUtil.setBlurBackground(mFriendDetail.profile_picture, blurImage, FriendDetailActivity.this);
//            mAqueryTask.setImage(mFriendDetail.profile_picture.trim(), blurImage, null);

//            userImageBitmap = GeneralUtil.setBlurBackground(mFriendDetail.profile_picture.trim(), blurImage, FriendDetailActivity.this);
//            if (userImageBitmap != null) {
//                blurImage.setImageBitmap(GeneralUtil.setBlurBackground(mFriendDetail.profile_picture.trim(), blurImage, FriendDetailActivity.this));
//                blurImage.setTag(GeneralUtil.setBlurBackground(mFriendDetail.profile_picture.trim(), blurImage, FriendDetailActivity.this));
//
//            } else {
//                imageView.setImageResource(R.drawable.ic_launcher);
//                blurImage.setImageResource(R.drawable.ic_launcher);
//
//            }
        }


        if (mFriendDetail.canViewProfile == 1) {
            location.setText(mFriendDetail.address);
            email.setText(mFriendDetail.email);
            dataView.setVisibility(View.VISIBLE);
            privateView.setVisibility(View.GONE);
            listAdapter = new ListAdapter(getApplicationContext(), 0, mFriendDetail.attendessList, mVivaApplicatiion.getUserData(DataHolder.TOKEN), false);
            lv.setAdapter(listAdapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(FriendDetailActivity.this, EventDetailActivity.class);
                    intent.putExtra("eventId", mFriendDetail.attendessList.get(position).eventId);
                    Log.i("checkEventId", "" + mFriendDetail.attendessList.get(position).eventId);
                    startActivity(intent);
                }
            });
            listAdapter.notifyDataSetChanged();
        } else {
            privateView.setVisibility(View.VISIBLE);
        }
        if (mFriendDetail.isFollowing == 1) {
            followBtn.setText("+UNFOLLOW");
        } else if (mFriendDetail.isFollowing == 2) {
            followBtn.setText("+FOLLOW");
        } else if (mFriendDetail.isFollowing == 0) {
            followBtn.setText("Pending");
        }
//            } else {
//                followBtn.setText("following");
//            }

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
                R.layout.toolbar_friends, null);
        Toolbar.LayoutParams layoutParam = new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        customView.setLayoutParams(layoutParam);
        backBtnToolbar = (FontIcon) customView.findViewById(R.id.backBtnToolbar);
        nextBtnToolbar = (FontIcon) customView.findViewById(R.id.openDialogToolbar);


        //nextBtnToolbar.setVisibility(View.GONE);

        backBtnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(FriendDetailActivity.this, MyFriendActivity.class);
                startActivity(i);
                finish();
            }
        });
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
                cancel = (RelativeLayout) view.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                block = (RelativeLayout) view.findViewById(R.id.block);
                block.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //codes to block user
                        showBlockDialog();
                        dialog.dismiss();


                    }
                });
                report = (RelativeLayout) view.findViewById(R.id.report);
                report.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //codes to report user
                        //reportUser(DataHolder.);
                        showReportDialog();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });


        return customView;

    }

    private void showBlockDialog() {
        BlockDialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        View view = null;
        view = LayoutInflater.from(this).inflate(R.layout.block_dialog, null);
        TextView addpost = (TextView) view.findViewById(R.id.yes);
        TextView cancel = (TextView) view.findViewById(R.id.no);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                BlockDialog.dismiss();
            }
        });
        addpost.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                BlockDialog.dismiss();
                blockFriend(DataHolder.BLOCK_USER);


            }
        });
        BlockDialog.setContentView(view);
        BlockDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        BlockDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        BlockDialog.show();


    }

    private void blockFriend(String strPost) {
        if (isNetworkAvailable()) {
            HashMap<String, String> params = new HashMap<String, String>();
            Log.d("getCategories", "check");
            params.put("user_id", userId);
            params.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));
            mAqueryTask.sendRequest(strPost, params, JSONObject.class, postBlockCallBack());
            // TODO Auto-generated catch block
        }

    }


    private AjaxCallback<JSONObject> postBlockCallBack() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);
                Log.i("checkBlockCall", "" + result);

                if (result != null) {
                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        int userStatus = outpuJsonObject.optInt("status");
                        if (userStatus == 1) {
                            showToast(outpuJsonObject.optString("message"));
                            Intent i = new Intent(FriendDetailActivity.this, MyFriendActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            showToast(outpuJsonObject.optString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            ;
        };

    }

    private void showReportDialog() {
        ReportDialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        View view = null;
        view = LayoutInflater.from(this).inflate(R.layout.report_dialog, null);
        post = (EditText) view.findViewById(R.id.post);
        TextView addpost = (TextView) view.findViewById(R.id.addPost);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ReportDialog.dismiss();
            }
        });
        addpost.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                strPost = post.getText().toString();
                if (strPost.length() == 0) {
                    post.setError("Enter post");
                } else {
                    ReportDialog.dismiss();
                    reportUser(DataHolder.REPORT_USER);
                }

            }
        });
        ReportDialog.setContentView(view);
        ReportDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        ReportDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ReportDialog.show();


    }

    private void reportUser(String reportUser) {
        if (isNetworkAvailable()) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));
            params.put("user_id", userId);
            params.put("message", strPost);
            Log.i("checkParams", "" + params);
            mAqueryTask.sendRequest(reportUser, params, JSONObject.class, postReportCallback());
            // TODO Auto-generated catch block
        }

    }

    private AjaxCallback<JSONObject> postReportCallback() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);
                Log.i("checkReportCall", "" + result);

                if (result != null) {
                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        int userStatus = outpuJsonObject.optInt("status");
                        if (userStatus == 1) {
                            showToast(outpuJsonObject.optString("message"));
                        } else {
                            showToast(outpuJsonObject.optString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            ;
        };
    }


}
