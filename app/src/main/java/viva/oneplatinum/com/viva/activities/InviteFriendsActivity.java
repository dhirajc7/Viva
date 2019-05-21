package viva.oneplatinum.com.viva.activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.adapters.GridAdapter;
import viva.oneplatinum.com.viva.models.Friend;
import viva.oneplatinum.com.viva.parser.ParseUtility;
import viva.oneplatinum.com.viva.utils.DataHolder;
import viva.oneplatinum.com.viva.widgets.FontIcon;
import viva.oneplatinum.com.viva.widgets.RobotoBoldTextView;

/**
 * Created by D-MAX on 4/1/2016.
 */
public class InviteFriendsActivity extends ParentActivity {
    GridView gv;
    GridAdapter gridAdapter;
    Context context;
    AQuery aq;
    ArrayList<Friend> friend;
    Toolbar mToolBar;
    FontIcon backBtnToolbar, nextBtnToolbar, searchIcon;
    RobotoBoldTextView titleToolbar;
    ArrayList<Friend> friendInviteArrayList;
    RobotoBoldTextView selectAll, deselectAll;
    private int currentFirstVisibleItem, currentVisibleItemCount, currentScrollState;
    int totalpageCount, CurrentPageCount = 1;
    Boolean isFetching = false, isFirstFetch = true;
    LinearLayout inviteBackground;
    String eventId;
    ArrayList<Friend> selectedFriend = new ArrayList<Friend>();
    ArrayList<Friend> friendsArray = new ArrayList<>();
    EditText searchEvent;
    LinearLayout containerLayout;
    Friend mFriendDetail;
    Boolean isEventList = true;
    JSONArray users;
    String event_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_friends);
        event_id = getIntent().getStringExtra("event_id");
        friendInviteArrayList = new ArrayList<Friend>();
        super.initViews(findViewById(R.id.main));
        initData();
        context = this;
        getInviteFriends(DataHolder.FRIENDLIST_FOR_INVITE + "1.json");
        gridAdapter = new GridAdapter(getApplicationContext(), 0, friendInviteArrayList);
        gv.setAdapter(gridAdapter);
        initToolBar();
        aq = new AQuery(InviteFriendsActivity.this);

    }

    private void initData() {
        selectAll = (RobotoBoldTextView) findViewById(R.id.selectAll);
        deselectAll = (RobotoBoldTextView) findViewById(R.id.deselectAll);

        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < friendInviteArrayList.size(); i++) {
                    if(friendInviteArrayList.get(i).invitation_status == 1) {
                        friendInviteArrayList.get(i).isSelected = true;
                    }
                }
                gridAdapter.notifyDataSetChanged();
            }

        });

        deselectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < friendInviteArrayList.size(); i++) {
                    friendInviteArrayList.get(i).isSelected = false;
                }
                gridAdapter.notifyDataSetChanged();
            }

        });
        gv = (GridView) findViewById(R.id.event_list);
        searchEvent = (EditText) findViewById(R.id.searchEvents);
        containerLayout = (LinearLayout) findViewById(R.id.container);
        searchIcon = (FontIcon) findViewById(R.id.searchIcon);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFriendList();
            }
        });

        searchEvent.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent key) {
                // TODO Auto-generated method stub
                if (arg1 == EditorInfo.IME_ACTION_SEARCH) {
                    searchFriendList();
                    addDoneinKeyboard();
                }

                return false;
            }
        });
        searchEvent.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                if (s.length() == 0) {
                    if (!isEventList) {
//                        fetchEventList();
                        getInviteFriends(DataHolder.FRIENDLIST_FOR_INVITE + "1.json");
                        containerLayout.setVisibility(View.GONE);
                        gv.setVisibility(View.VISIBLE);

                    }
                } else if (s.length() > 0) {
                    //do your searches here
                    searchFriendList();
                    containerLayout.setVisibility(View.GONE);
                    gv.setVisibility(View.VISIBLE);
                }
            }


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        gv.setOnScrollListener(new AbsListView.OnScrollListener() {

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
        gridAdapter = new GridAdapter(getApplicationContext(), 0, friendInviteArrayList);


//        containerLayout = (LinearLayout) findViewById(R.id.container);
//        searchIcon = (FontIcon) findViewById(R.id.searchIcon);
//        searchIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                searchFriendList();
//            }
//        });
//        searchEvent = (EditText) findViewById(R.id.searchEvents);
//        searchEvent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//
//            @Override
//            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent key) {
//                // TODO Auto-generated method stub
//                if (arg1 == EditorInfo.IME_ACTION_SEARCH) {
//                    searchFriendList();
//                    addDoneinKeyboard();
//                }
//
//                return false;
//            }
//        });
//        searchEvent.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before,
//                                      int count) {
//                // TODO Auto-generated method stub
//                if (s.length() == 0) {
//                    if (!isEventList) {
////                        fetchEventList();
//                        getInviteFriends(DataHolder.FRIENDLIST_FOR_INVITE + "1.json");
//                        containerLayout.setVisibility(View.GONE);
//                        gv.setVisibility(View.VISIBLE);
//                        hideSoftKeyboard();
//
//                    }
//                } else if (s.length() > 0) {
//                    //do your searches here
//                    searchFriendList();
//                    containerLayout.setVisibility(View.GONE);
//                    gv.setVisibility(View.VISIBLE);
//                }
//            }
//
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count,
//                                          int after) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                // TODO Auto-generated method stub
//
//            }
//        });
    }

    private void searchFriendList() {
        if (isNetworkAvailable()) {

            Map<String, Object> params;
            try {
                JSONObject mainJson = new JSONObject();
                mainJson.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));
                mainJson.put("search_keyword", searchEvent.getText().toString());
                mainJson.put("event_id", event_id.toString());
                StringEntity s;

                s = new StringEntity(mainJson.toString(), HTTP.UTF_8);

                params = new HashMap<String, Object>();
                params.put(AQuery.POST_ENTITY, s);
                System.out.println(DataHolder.FRIENDLIST_FOR_INVITE + mainJson);
                Log.d("check", "" + DataHolder.FRIENDLIST_FOR_INVITE + mainJson);
                aq.ajax(DataHolder.FRIENDLIST_FOR_INVITE+"1.json", params, JSONObject.class,
                        fetchDataCallbackForSearch());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {
            System.out.println(DataHolder.FRIENDLIST_FOR_INVITE + "no internet");

        }
    }

    private AjaxCallback<JSONObject> fetchDataCallbackForSearch() {
        return new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object,
                                 AjaxStatus status) {

                System.out.println("allFriends" + object);
                Log.d("checkobj", "" + object);
//                hideLoadingDialog();
                if (object == null) {

                } else {

                    try {
                        JSONObject outpuJsonObject = object.getJSONObject("output");
                        int userStatus = outpuJsonObject.optInt("status");
                        if (userStatus == 1) {
                            gridAdapter = new GridAdapter(getApplicationContext(), 0, friendInviteArrayList);
                            gv.setAdapter(gridAdapter);
                            friendInviteArrayList = ParseUtility.parseFriendListForInvite(object);
                            Log.i("responseArray", "" + friendInviteArrayList);


                            setData();
                            showDataView();
                        } else {
//                            showToast(outpuJsonObject.optString("message"));
                            // getMyEvents(DataHolder.MY_EVENTS);
                            View empty = getLayoutInflater().inflate(
                                    R.layout.layout_error_loading, null);
                            TextView text = (TextView) empty
                                    .findViewById(R.id.txt_error_loading_message);
                            text.setText("No Friends found");
                            TextView btn = (TextView) empty
                                    .findViewById(R.id.txt_error_loading);
                            showToast(outpuJsonObject.optString("message"));
                            btn.setVisibility(View.GONE);
                            ImageView im = (ImageView) empty.findViewById(R.id.errorImage);
                            im.setVisibility(View.GONE);
                            containerLayout.addView(empty);
                            gv.setVisibility(View.GONE);

                        }
                        // progressLayout.setVisibility(View.GONE);

                        // populateViews(cruecoFriends);
                        // listview.setAdapter(new FriendsAdapter(getActivity(),
                        // 0, cruecoFriends));
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();

                    }
                    isEventList = false;
                }
            }
        }.header("Content-Type", "application/json");
    }

    public void isScrollCompleted() {
        if (!isFetching) {
            if (CurrentPageCount < totalpageCount) {
                int pageCount = (CurrentPageCount + 1);
                getInviteFriends(DataHolder.FRIENDLIST_FOR_INVITE + pageCount + ".json");
                isFetching = true;
                Log.d("checkFriends", "getCategoriesresponse");
            }
            else {
                if (!isEventList) {
                    if (CurrentPageCount < totalpageCount) {
                        int pageCount = (CurrentPageCount + 1);
                        getInviteFriends(DataHolder.FRIENDLIST_FOR_INVITE + pageCount + ".json");
                        isFetching = true;
                    }
                }
            }
        }
    }

    public void getInviteFriends(String url) {
        if (isNetworkAvailable()) {
            if (isFirstFetch) {
                showLoading();
            }

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));
            params.put("event_id", event_id);
            Log.i("checkEventId", "" + event_id);
            Log.e("getCategoriesresponse", "" + params);
            mAqueryTask.sendRequest(url, params, JSONObject.class, getInviteFriendsCallback());

        }
    }

    private AjaxCallback<JSONObject> getInviteFriendsCallback() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                Log.e("checkFriendlist", "" + result);
//                mProgressDialog.dismiss();

                if (result != null) {
                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");

                        int userStatus = outpuJsonObject.optInt("status");
                        if (userStatus == 1) {
                            CurrentPageCount = outpuJsonObject.optInt("currentPage");
                            totalpageCount = outpuJsonObject.optInt("pagecount");
                            ArrayList<Friend> arrayList = new ArrayList<Friend>();
                            arrayList = ParseUtility.parseFriendListForInvite(result);
                            friendInviteArrayList.addAll(arrayList);
                            isFetching = false;
                            isFirstFetch = false;
                            Log.i("responseArray", "" + friendInviteArrayList);
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
        };
    }

    public void setData() {

       gridAdapter.notifyDataSetChanged();


    }

    //
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
        nextBtnToolbar.setText("s");
        nextBtnToolbar.setTextColor(Color.BLUE);

        titleToolbar.setText("INVITE FRIENDS");

        titleToolbar.setTextColor(getResources().getColor(R.color.txt_color_default));
        backBtnToolbar.setText("j");
        backBtnToolbar.setTextColor(getResources().getColor(R.color.red));
        backBtnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        nextBtnToolbar.setVisibility(View.VISIBLE);
        nextBtnToolbar.setTextColor(getResources().getColor(R.color.Blue));
        nextBtnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                users = new JSONArray();
                for (int i = 0; i < friendInviteArrayList.size(); i++) {
                    if (friendInviteArrayList.get(i).isSelected) {
                        users.put(friendInviteArrayList.get(i).id);
                        if (friendInviteArrayList.get(i).invitation_status == 0) {
                            showToast(friendInviteArrayList.get(i).invitation_status_msg);
                        }
                        if (friendInviteArrayList.get(i).invitation_status == 1) {
                            showToast(friendInviteArrayList.get(i).invitation_status_msg);
                            sendInvitation(DataHolder.INVITE_FRIEND);
                        }
                        if (friendInviteArrayList.get(i).invitation_status == 2) {
                            showToast(friendInviteArrayList.get(i).invitation_status_msg);
                        }
                        if (friendInviteArrayList.get(i).invitation_status == 3) {
                            showToast(friendInviteArrayList.get(i).invitation_status_msg);
                        }
                        if (friendInviteArrayList.get(i).invitation_status == 4) {
                            showToast(friendInviteArrayList.get(i).invitation_status_msg);
                        }
                        if (friendInviteArrayList.get(i).invitation_status == 5) {
                            showToast(friendInviteArrayList.get(i).invitation_status_msg);
                        }
                    }
                }
//                sendInvitation(DataHolder.INVITE_FRIEND);
            }
        });
        return customView;

    }

    public void sendInvitation(String url) {
        if (isNetworkAvailable()) {
            mProgressDialog.setMessage("Sending request..");
            mProgressDialog.show();
            Map<String, Object> params;
            try {
                JSONObject mainJson = new JSONObject();
                mainJson.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));
                mainJson.put("event_id", event_id);
                mainJson.put("friends", users);
                StringEntity s;

                s = new StringEntity(mainJson.toString(), HTTP.UTF_8);

                params = new HashMap<String, Object>();
                params.put(AQuery.POST_ENTITY, s);

                Log.i("checkParams", "" + mainJson);
                //params.put("friends",);

                mAqueryTask.sendPostObjectRequest(url, params, JSONObject.class, getInvitationCallback());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private AjaxCallback<JSONObject> getInvitationCallback() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {
                Log.i("checkResult", "" + result);
                mProgressDialog.dismiss();
                if (result != null) {
                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        showToast(outpuJsonObject.optString("message"));
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

        }.header("Content-Type", "application/json");
    }

    public void addUser(String url) {
        if (isNetworkAvailable()) {
            showLoadingDialog();
            Map<String, Object> params;
            try {
                JSONObject mainJson = new JSONObject();
                mainJson.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));
                users = new JSONArray();
                for (int i = 0; i < selectedFriend.size(); i++) {
                    users.put(selectedFriend.get(i).id);
                }
                mainJson.put("friends", users);
                mainJson.put("event_id", event_id);
                StringEntity s;
                s = new StringEntity(mainJson.toString(), HTTP.UTF_8);
                params = new HashMap<String, Object>();
                params.put(AQuery.POST_ENTITY, s);

                Log.d("params", params + "");
                System.out.println(DataHolder.INVITE_FRIEND + mainJson);
                Log.d("checkfriendslist", "" + DataHolder.INVITE_FRIEND + mainJson);
                aq.ajax(DataHolder.INVITE_FRIEND, params, JSONObject.class,
                        addUsersCallback());
                Log.d("friendslsit", "calling data");

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Log.d("friendslist", "no internet");
            System.out.println(DataHolder.INVITE_FRIEND + "no internet");
        }
    }

    private AjaxCallback<JSONObject> addUsersCallback() {
        return new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object,
                                 AjaxStatus status) {

                System.out.println("allFriends" + object);
                Log.i("checkAllFriends", "" + object);
                if (object == null) {
                    hideLoadingDialog();
                    Toast.makeText(InviteFriendsActivity.this, "Something went wrong.", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    hideLoadingDialog();
                    try {

                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        showToast(outpuJsonObject.optString("message"));


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.header("Content-Type", "application/json");
    }

    Dialog loadingDialog;

    private void showLoadingDialog() {
        loadingDialog = new Dialog(this,
                android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        View view = LayoutInflater.from(this).inflate(R.layout.loading_layout,
                null);
        loadingDialog.addContentView(view, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

    }

    private void hideLoadingDialog() {
        loadingDialog.dismiss();
    }

    private boolean validationSuccess() {
        boolean val = true;
        if (gv.getChildCount() < 1) {
            showToast("Please select an item");
            val = false;
        }
        return val;
    }
    //


}