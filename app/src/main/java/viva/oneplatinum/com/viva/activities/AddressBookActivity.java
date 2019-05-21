package viva.oneplatinum.com.viva.activities;

import android.app.Dialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.adapters.SelectUserAdapter;
import viva.oneplatinum.com.viva.models.SelectUser;
import viva.oneplatinum.com.viva.utils.DataHolder;
import viva.oneplatinum.com.viva.widgets.FontIcon;
import viva.oneplatinum.com.viva.widgets.RobotoBoldTextView;

public class AddressBookActivity extends ParentActivity {
    Toolbar mToolBar;
    FontIcon backBtnToolbar, nextBtnToolbar, searchIcon;
    RobotoBoldTextView titleToolbar;
    // ArrayList
    ArrayList<SelectUser> selectUsers;
    List<SelectUser> temp;
    // Contact List
    ListView listView;
    // Cursor to load contacts list
    Cursor phones, email;
    JSONArray contacts;
    EditText searchEvent;
    // Pop up
    ContentResolver resolver;
    SearchView search;
    SelectUserAdapter adapter;
    SelectUser selectUser;
    Boolean isEventList = true;
    RobotoBoldTextView selectAll, deselectAll;
    LoadContact loadContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addressbook_friends_listview);
        super.initViews(findViewById(R.id.main));
        selectUsers = new ArrayList<SelectUser>();
        resolver = getContentResolver();
        listView = (ListView) findViewById(R.id.contacts_list);
        initToolBar();
        mProgressDialog.setMessage("Fetching Contacts ...");
        mProgressDialog.show();
        loadContact = new LoadContact();
        loadContact.execute();
        Log.i("checkAddressBook", "" + phones);

        searchEvent = (EditText) findViewById(R.id.searchEvents);
        searchEvent.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent key) {
                // TODO Auto-generated method stub
                if (arg1 == EditorInfo.IME_ACTION_SEARCH) {
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
                adapter.filter(String.valueOf(s));
//                if (s.length() == 0) {
//                    if (!isEventList) {
//                        loadContact.execute();
//                        listView.setVisibility(View.VISIBLE);
//                        hideSoftKeyboard();
//                    }
//                } else if (s.length() > 0) {
//                    //do your searches here
//                    adapter.filter((String) s);
//                    listView.setVisibility(View.VISIBLE);
//                }
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

//        search = (SearchView) findViewById(R.id.searchView);
//
//        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                // TODO Auto-generated method stub
//
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                // TODO Auto-generated method stub
//                adapter.filter(newText);
//                return false;
//            }
//        });
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
        nextBtnToolbar.setVisibility(View.VISIBLE);
        nextBtnToolbar.setTextColor(getResources().getColor(R.color.Blue));

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
                contacts = new JSONArray();
                for (int i = 0; i < selectUsers.size(); i++) {
                    if (selectUsers.get(i).isSelected) {
                        JSONObject obj = new JSONObject();

                        try {
                            obj.put("name", selectUsers.get(i).getName());
                            obj.put("phone", selectUsers.get(i).getPhone());
                            obj.put("email", selectUsers.get(i).getEmail());
                            contacts.put(obj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                    }
                }
                hideSoftKeyboard();
                sendInvitation(DataHolder.ADD_FRIEND_CONTACT);

            }
        });


        return customView;
    }

    private void sendInvitation(String url) {
        if (isNetworkAvailable()) {
            mProgressDialog.setMessage("Sending request..");
            mProgressDialog.show();
            Map<String, Object> params;
            try {
                JSONObject mainJson = new JSONObject();
                mainJson.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));
                mainJson.put("contacts", contacts);
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
                        int userStatus = outpuJsonObject.optInt("status");
                        if (userStatus == 1) {
                            showToast(outpuJsonObject.optString("message"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

        }.header("Content-Type", "application/json");
    }

    // Load data on background
    class LoadContact extends AsyncTask<Void, Void, Void> {
        @Override

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Get Contact list from Phone
            String phoneNumber = null;

            String email = null;

            String MobilePattern = "[0-9]{10}";
            Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;

            String _ID = ContactsContract.Contacts._ID;

            String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;


            Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

            String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;

            String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
            String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

            Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;

            String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;

            String DATA = ContactsContract.CommonDataKinds.Email.DATA;


            ContentResolver contentResolver = getContentResolver();


            Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                    String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
                    if (hasPhoneNumber > 0) {
                        Bitmap bit_thumb = null;
                        Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                        while (phoneCursor.moveToNext()) {
                            phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                            Log.d("phonenumber", "" + phoneNumber);

                        }
                        phoneCursor.close();
                        Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?", new String[]{contact_id}, null);
                        while (emailCursor.moveToNext()) {


                            email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                            Log.d("email", "" + email);

                        }


                        emailCursor.close();

                        String image_thumb = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
                        try {
                            if (image_thumb != null) {
                                bit_thumb = MediaStore.Images.Media.getBitmap(resolver, Uri.parse(image_thumb));
                            } else {
                                Log.e("No Image Thumb", "--------------");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        SelectUser selectUser = new SelectUser();
                        selectUser.setThumb(bit_thumb);
                        selectUser.setName(name);
                        selectUser.setEmail(email);
//                                if(phoneNumber.length()>10) {
                        selectUser.setPhone(phoneNumber);
//                                }
                        selectUsers.add(selectUser);
                        Log.e("selected users", selectUser + "--------------");
                    }
                }
            } else {
                Log.e("Cursor close 1", "----------------");
            }
            //phones.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            mProgressDialog.dismiss();
            adapter = new SelectUserAdapter(selectUsers, AddressBookActivity.this);
            mProgressDialog.dismiss();
            listView.setAdapter(adapter);
            selectAll = (RobotoBoldTextView) findViewById(R.id.selectAll);
            deselectAll = (RobotoBoldTextView) findViewById(R.id.deselectAll);

            selectAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < selectUsers.size(); i++) {
                        selectUsers.get(i).isSelected = true;
                    }
                    adapter.notifyDataSetChanged();
                }

            });

            deselectAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < selectUsers.size(); i++) {
                        selectUsers.get(i).isSelected = false;
                    }
                    adapter.notifyDataSetChanged();
                }

            });
            // Select item on listclick
//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                    View v = listView.getChildAt(i);
//
//
//                    //int desiredBackgroundColor = android.graphics.Color.rgb(125, 125, 125);
//
//                    ColorDrawable viewColor = (ColorDrawable) view.getBackground();
//
//                    if (viewColor == null) {
//                        view.setBackgroundColor(Color.GRAY);
//                        return;
//                    }
//
//                    int currentColorId = viewColor.getColor();
//
//                    if (currentColorId == Color.GRAY) {
//                        view.setBackgroundColor(Color.TRANSPARENT);
//                    } else {
//                        view.setBackgroundColor(Color.GRAY);
//                    }
//
//                    Log.e("search", "here---------------- listener");
//
//                    SelectUser data = selectUsers.get(i);
//                }
//            });
//
//            listView.setFastScrollEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
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

//    private static boolean isValidPhoneNumber(String mobile) {
//        String regEx = "^(?:0091|\\\\+91|0)[7-9][0-9]{9}$";
//        return mobile.matches(regEx);
//    }
}