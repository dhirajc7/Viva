package viva.oneplatinum.com.viva.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import viva.oneplatinum.com.viva.adapters.AddFriendAdapter;
import viva.oneplatinum.com.viva.models.Friend;
import viva.oneplatinum.com.viva.parser.ParseUtility;
import viva.oneplatinum.com.viva.utils.DataHolder;
import viva.oneplatinum.com.viva.utils.GeneralUtil;
import viva.oneplatinum.com.viva.widgets.FontIcon;
import viva.oneplatinum.com.viva.widgets.RobotoBoldTextView;

/**
 * Created by D-MAX on 4/12/2016.
 */
public class VivaFriendActivity extends ParentActivity implements AdapterView.OnItemClickListener {
    ListView lv;
    AddFriendAdapter mAddFriendAdapter;
    Context context;
    Toolbar mToolBar;
    FontIcon backBtnToolbar, nextBtnToolbar;
    RobotoBoldTextView titleToolbar;
    ArrayList<Friend> FreindArrayList;
    private int currentFirstVisibleItem, currentVisibleItemCount, currentScrollState;
    int totalpageCount, CurrentPageCount = 1;
    Boolean isFetching = false, isFirstFetch = true;
    EditText searchEvent;
    LinearLayout containerLayout;
    Boolean isEventList = true;
    AQuery aq;
    ArrayList<Friend> selectedFriend = new ArrayList<Friend>();
    ArrayList<String> FreindArray = new ArrayList<String>();
    JSONArray users;
    RobotoBoldTextView selectAll, deselectAll;
    ArrayList<Friend> arrayList;
    LayoutInflater mInflater;
    private View listViewFooter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viva_friends);
        FreindArrayList = new ArrayList<Friend>();
        initViews();
        super.initViews(findViewById(R.id.main));
        initData();
        context = this;
        initToolBar();
        getVivaFreinds(DataHolder.VIVA_FRIENDS + "1.json");
        aq = new AQuery(this);
//        fetchEventList();
        mAddFriendAdapter = new AddFriendAdapter(getApplicationContext(), 0, FreindArrayList);
        lv.setAdapter(mAddFriendAdapter);
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

        titleToolbar.setText("ADD FRIENDS");
        nextBtnToolbar.setText("s");
        nextBtnToolbar.setTextColor(getResources().getColor(R.color.Blue));

        nextBtnToolbar.setVisibility(View.VISIBLE);


        titleToolbar.setTextColor(getResources().getColor(R.color.txt_color_default));
        backBtnToolbar.setText("o");
        backBtnToolbar.setTextColor(getResources().getColor(R.color.red));
        backBtnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        nextBtnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                users = new JSONArray();
                for (int i = 0; i < FreindArrayList.size(); i++) {
                    if (FreindArrayList.get(i).isSelected) {
                        users.put(FreindArrayList.get(i).id);
                    }
                }
                hideSoftKeyboard();
                sendFriendRequest(DataHolder.SEND_FRIEND_REQUEST);
            }
        });

        return customView;
    }


    public void sendFriendRequest(String url) {
        if (isNetworkAvailable()) {
            mProgressDialog.setMessage("Sending request..");
            mProgressDialog.show();
            Map<String, Object> params;
            try {
                JSONObject mainJson = new JSONObject();
                mainJson.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));
                mainJson.put("users", users);
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
                hideSoftKeyboard();
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

    private void initViews() {
        mInflater = getLayoutInflater();
        lv = (ListView) findViewById(R.id.event_list);
        listViewFooter = mInflater.inflate(R.layout.listview_footer_layout, lv, false);

        containerLayout = (LinearLayout) findViewById(R.id.container);
        searchEvent = (EditText) findViewById(R.id.searchEvents);
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
                        hideSoftKeyboard();
                            getVivaFreinds(DataHolder.VIVA_FRIENDS + "1.json");
                            containerLayout.setVisibility(View.GONE);
                            lv.setVisibility(View.VISIBLE);
                    }
                } else if (s.length() > 0) {
                    //do your searches here
                    searchFriendList();
                    containerLayout.setVisibility(View.GONE);
                    lv.setVisibility(View.VISIBLE);
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
    }

    private void initData() {

        selectAll = (RobotoBoldTextView) findViewById(R.id.selectAll);
        deselectAll = (RobotoBoldTextView) findViewById(R.id.deselectAll);

        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < FreindArrayList.size(); i++) {
                    FreindArrayList.get(i).isSelected = true;
                }
                mAddFriendAdapter.notifyDataSetChanged();
            }

        });

        deselectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < FreindArrayList.size(); i++) {
                    FreindArrayList.get(i).isSelected = false;
                }
                mAddFriendAdapter.notifyDataSetChanged();
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
        mAddFriendAdapter = new AddFriendAdapter(getApplicationContext(), 0, FreindArrayList);
    }

    public void isScrollCompleted() {
        if (!isFetching)
            if (CurrentPageCount < totalpageCount) {
                int pageCount = (CurrentPageCount + 1);
                getVivaFreinds(DataHolder.VIVA_FRIENDS + pageCount + ".json");
                isFetching = true;
                Log.d("getCategoriesresponse", "getCategoriesresponse");
            } else {
                if (!isEventList) {
                    if (CurrentPageCount < totalpageCount) {
                        int pageCount = (CurrentPageCount + 1);
                        getVivaFreinds(DataHolder.VIVA_FRIENDS + pageCount + ".json");
                        isFetching = true;
                    }
                }
            }

    }

    public void getVivaFreinds(String url) {
        if (isNetworkAvailable()) {
            if (isFirstFetch) {
                showLoading();
            }
            Log.e("getCategoriesresponse", "getCategoriesresponse");
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));
            mAqueryTask.sendRequest(url, params, JSONObject.class, getMyEventsCallback());

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
                            CurrentPageCount = outpuJsonObject.optInt("currentPage");
                            totalpageCount = outpuJsonObject.optInt("pagecount");
                            arrayList = new ArrayList<Friend>();
                            arrayList = ParseUtility.parseAddFriendList(result);
                            FreindArrayList.addAll(arrayList);
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
        if (arrayList != null) {
            if (CurrentPageCount < totalpageCount) {
                Log.v("addFooter", "addFooter");
                if (!isFirstFetch) {
                    lv.removeFooterView(listViewFooter);
                }
                lv.addFooterView(listViewFooter);
            } else {
                Log.v("removeFooter", "removeFooter");
                lv.removeFooterView(listViewFooter);
            }
        }
        mAddFriendAdapter.notifyDataSetChanged();
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
                mainJson.put("users", users);
                StringEntity s;
                s = new StringEntity(mainJson.toString(), HTTP.UTF_8);
                params = new HashMap<String, Object>();
                Log.d("params", params + "");
                params.put(AQuery.POST_ENTITY, s);

                System.out.println(DataHolder.SEND_FRIEND_REQUEST + mainJson);
                aq.ajax(DataHolder.SEND_FRIEND_REQUEST, params, JSONObject.class,
                        addUsersCallback());
                Log.d("friendslsit", "calling data");

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Log.d("friendslist", "no internet");
            System.out.println(DataHolder.SEND_FRIEND_REQUEST + "no internet");
        }
    }

    private AjaxCallback<JSONObject> addUsersCallback() {
        return new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object,
                                 AjaxStatus status) {

                System.out.println("allFriends" + object);
                Log.d("check", "" + object);
                if (object == null) {
                    hideLoadingDialog();
                    Toast.makeText(VivaFriendActivity.this, "Something went wrong.", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    hideLoadingDialog();
                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        showToast(outpuJsonObject.optString("message"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent i = new Intent(VivaFriendActivity.this, AddFriendActivity.class);
                    startActivity(i);
                }
            }
        }.header("Content-Type", "application/json");
    }

    private void searchFriendList() {

        if (GeneralUtil.isNetworkAvailable(VivaFriendActivity.this)) {
            // view.findViewById(R.id.progress).setVisibility(View.VISIBLE);
            // HashMap<String, String>params = new HashMap<String, String>();
            // params.put("token", "1234");
            // progressLayout.setVisibility(View.VISIBLE);
//            showLoadingDialog();
            Map<String, Object> params;
            try {
                JSONObject mainJson = new JSONObject();
                mainJson.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));
                mainJson.put("search_keyword", searchEvent.getText().toString());
                StringEntity s;

                s = new StringEntity(mainJson.toString(), HTTP.UTF_8);

                params = new HashMap<String, Object>();
                params.put(AQuery.POST_ENTITY, s);
                System.out.println(DataHolder.VIVA_FRIENDS + "1.json" + mainJson);
                aq.ajax(DataHolder.VIVA_FRIENDS + "1.json", params, JSONObject.class,
                        fetchDataCallbackForSearch());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {
            System.out.println(DataHolder.VIVA_FRIENDS + "no internet");

        }
    }

//    private void fetchEventList() {
//
//        if (GeneralUtil.isNetworkAvailable(this)) {
//            // view.findViewById(R.id.progress).setVisibility(View.VISIBLE);
//            // HashMap<String, String>params = new HashMap<String, String>();
//            // params.put("token", "1234");
//            // progressLayout.setVisibility(View.VISIBLE);
//            showLoadingDialog();
//            Map<String, Object> params;
//            try {
//                JSONObject mainJson = new JSONObject();
//                mainJson.put("token", "cdojghf6cchb2g06ot2hnfrcgaqfe5vxbl612orq");
//                StringEntity s;
//
//                s = new StringEntity(mainJson.toString(), HTTP.UTF_8);
//
//                params = new HashMap<String, Object>();
//                params.put(AQuery.POST_ENTITY, s);
//                System.out.println(DataHolder.MY_EVENTS + mainJson);
//                aq.ajax(DataHolder.MY_EVENTS, params, JSONObject.class,
//                        fetchDataCallbackForFriends());
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//
//        } else {
//            System.out.println(DataHolder.MY_EVENTS + "no internet");
//
//        }
//    }

//    private AjaxCallback<JSONObject> fetchDataCallbackForFriends() {
//        return new AjaxCallback<JSONObject>() {
//            @Override
//            public void callback(String url, JSONObject object,
//                                 AjaxStatus status) {
//
//                System.out.println("allFriends" + object);
//                if (object == null) {
//
//                } else {
//
//                    try {
//                        if (object.getJSONObject("output").getString("status")
//                                .equals("1")) {
//
//                        }
//                    } catch (JSONException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//
//                    }
//                    isEventList = true;
//                }
//            }
//        }.header("Content-Type", "application/json");
//    }

    private AjaxCallback<JSONObject> fetchDataCallbackForSearch() {
        return new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object,
                                 AjaxStatus status) {

                System.out.println("allFriends" + object);
//                hideLoadingDialog();
                if (object == null) {

                } else {

                    try {
                        FreindArrayList = new ArrayList<Friend>();
                        JSONObject outpuJsonObject = object.getJSONObject("output");
                        int userStatus = outpuJsonObject.optInt("status");
                        if (userStatus == 1) {
//                            CurrentPageCount = outpuJsonObject.optInt("currentPage");
//                            totalpageCount = outpuJsonObject.optInt("pagecount");
//                            ArrayList<Friend> arrayList = new ArrayList<Friend>();
//                            arrayList = ParseUtility.parseAddFriendList(object);
//                            FreindArrayList.addAll(arrayList);
//                            isFetching = false;
//                            isFirstFetch = false;
                            FreindArrayList =  ParseUtility.parseAddFriendList(object);
                            setData();
                            mAddFriendAdapter = new AddFriendAdapter(getApplicationContext(), 0, FreindArrayList);
                            lv.setAdapter(mAddFriendAdapter);
                            showDataView();
//                            filterFriends(eventArrayList);
                        } else {
//                            showToast(outpuJsonObject.optString("message"));
                            // getVivaFreinds(DataHolder.MY_EVENTS);
                            View empty = getLayoutInflater().inflate(
                                    R.layout.layout_error_loading, null);
                            TextView text = (TextView) empty
                                    .findViewById(R.id.txt_error_loading_message);
//                            text.setText("No Events found");
                            showToast(outpuJsonObject.optString("message"));
                            TextView btn = (TextView) empty
                                    .findViewById(R.id.txt_error_loading);

                            btn.setVisibility(View.GONE);
                            ImageView im = (ImageView) empty.findViewById(R.id.errorImage);
                            im.setVisibility(View.GONE);
                            containerLayout.addView(empty);
                            lv.setVisibility(View.GONE);

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


    Dialog loadingDialog;

    private void showLoadingDialog() {
        loadingDialog = new Dialog(VivaFriendActivity.this,
                android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        View view = LayoutInflater.from(VivaFriendActivity.this).inflate(R.layout.loading_layout,
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

    public void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getWindow().getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }
    }

    public void addDoneinKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}

