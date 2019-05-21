package viva.oneplatinum.com.viva.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.cloudinary.Cloudinary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.fb.SessionStore;
import viva.oneplatinum.com.viva.gcm.GcmManager;
import viva.oneplatinum.com.viva.utils.DataHolder;
import viva.oneplatinum.com.viva.utils.GeneralUtil;
import viva.oneplatinum.com.viva.widgets.FontIcon;
import viva.oneplatinum.com.viva.widgets.RobotoBoldTextView;

/**
 * Created by Dell on 3/15/2016.
 */
public class SignupActivity extends ParentActivity {
    EditText website;
    AutoCompleteTextView location;
    LinearLayout googleLoginView, facebookLoginView;
    String imageUrl = "", familyName, givenName, gender, displayName, email, loginType, googleId, password, facebookId, dob,is_private;
    TextView textViewMale, textViewFemale, textViewOther;
    EditText textViewfname, textViewlname;
    EditText editTextEmail, editYear;
    TextView textLocation, textWebsite;
    private static final int PROFILE_PIC_SIZE = 120;
    ImageView userImg, imageEdit,profileImageBack;
    Spinner spinDay, spinMonth;
    //Cloudinary cloudinary;
    AQuery aq;
    Toolbar mToolBar;
    FontIcon backBtnToolbar, nextBtnToolbar;
    RobotoBoldTextView titleToolbar;
    private String year, month, day;
    private Calendar calendar;
    private Dialog chooseImageDialog;
    private Bitmap userImageBitmap;
    private Button btnRemoveImage;
    ArrayAdapter<String> placeslistAdapter;
    String PlaceArray[] = {};
    ArrayList<String> places;
    Cloudinary cloudinary;
    File file;
    String images = "";
    GcmManager gcmManager;
    Boolean isFromFacebook, isFromGoogle;
    private static final String DATE_PATTERN =
            "(0?[1-9]|1[012]) [/.-] (0?[1-9]|[12][0-9]|3[01]) [/.-] ((19|20)\\d\\d)";
    private Pattern pattern;
    private Matcher matcher;
    RobotoBoldTextView textViewPublic, textViewPrivate;
    String profileType = "public";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        aq = new AQuery(getApplicationContext());
        gcmManager = new GcmManager(this);
        places = new ArrayList<String>();
        initCloudinary();
        initView();
        initData();
        initToolbar();
        onClick();

    }

    private void onClick() {
        textViewPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewPublic.setSelected(true);
                textViewPrivate.setSelected(false);
                hideSoftKeyboard();
                profileType = "public";

            }
        });
        textViewPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewPrivate.setSelected(true);
                textViewPublic.setSelected(false);
                hideSoftKeyboard();
                profileType = "private";
            }
        });
    }

    private void initCloudinary() {
        Map config = new HashMap();
        config.put("cloud_name", "dcj2wdnva");
        config.put("api_key", "197437687815994");
        config.put("api_secret", "BNWeI5mER5dgcdy_0ewO7m1VF2c");
        cloudinary = new Cloudinary(config);
    }

    public void initView() {

        textViewPublic = (RobotoBoldTextView) findViewById(R.id.textViewPublic);
        textViewPrivate = (RobotoBoldTextView) findViewById(R.id.textViewPrivate);
        textViewPublic.setSelected(true);
        profileType="public";
        isFromFacebook = getIntent().getBooleanExtra("isFromFacebook", true);
        isFromGoogle = getIntent().getBooleanExtra("isFromGoogle", true);
        loginType = getIntent().getStringExtra("loginType");
        Log.i("checkLogin", "checkLogin" + loginType);
        if (loginType.equals("google")) {
            googleId = getIntent().getStringExtra("id");
        }

        if (loginType.equals("facebook")) {
            facebookId = getIntent().getStringExtra("id");
        }

        imageUrl = getIntent().getStringExtra("image");
        profileImageBack = (ImageView) findViewById(R.id.profileImageBack);

        familyName = getIntent().getStringExtra("familyName");
        givenName = getIntent().getStringExtra("givenName");
        gender = getIntent().getStringExtra("gender");
        if (gender != null) {
            gender = gender.toLowerCase();
        }


        dob = getIntent().getStringExtra("birthday");


        displayName = getIntent().getStringExtra("displayName");
        email = getIntent().getStringExtra("email");

        if (loginType.equals("emailReg")) {
            gender = "male";
        }
        location = (AutoCompleteTextView) findViewById(R.id.Au_AddressLocation);
        website = (EditText) findViewById(R.id.Au_webSite);

        imageEdit = (ImageView) findViewById(R.id.imageEdit);
        imageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chooseImageDialog == null) {
                    chooseImageDialog = createChooseImageDialog();
                }

                chooseImageDialog.show();
            }
        });

        textViewfname = (EditText) findViewById(R.id.firstName);
        textViewlname = (EditText) findViewById(R.id.lastName);
        editTextEmail = (EditText) findViewById(R.id.email);

        textViewMale = (TextView) findViewById(R.id.male);
        textViewFemale = (TextView) findViewById(R.id.female);
        textViewOther = (TextView) findViewById(R.id.other);
        userImg = (ImageView) findViewById(R.id.userImage);
        //setProfilePic(imageUrl);

        spinDay = (Spinner) findViewById(R.id.day);
        spinMonth = (Spinner) findViewById(R.id.month);
        editYear = (EditText) findViewById(R.id.year);

        placeslistAdapter = new ArrayAdapter<String>(SignupActivity.this,
                android.R.layout.simple_list_item_1, PlaceArray);
        location.setAdapter(placeslistAdapter);
        location.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
                // SearchUserByAddress();
                // call the google api
//				Toast.makeText(getActivity(), "supported encoding" + arg0,
//						Toast.LENGTH_SHORT).show();
                String url = DataHolder.Google_Places;
                try {
                    String searchCode = url
                            + URLEncoder.encode(arg0.toString(), "utf8");
                    AQuery aq = new AQuery(SignupActivity.this);
                    aq.ajax(searchCode, JSONObject.class,
                            getplaceslistcallback());
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
//					Toast.makeText(getActivity(), "unsupported encoding",
//							Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });


    }

    private AjaxCallback<JSONObject> getplaceslistcallback() {
        return new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object,
                                 AjaxStatus status) {
                super.callback(url, object, status);
                Log.d("placesCallBack", object + "");
                if (object != null) {
                    try {
                        JSONArray predictions = object
                                .getJSONArray("predictions");
                        // get the place objects
                        // Toast.makeText(getActivity(), predictions.toString(),
                        // 1)
                        // .show();
                        places.clear();
                        for (int i = 0; i < predictions.length(); i++) {
                            JSONObject place = predictions.getJSONObject(i);
                            String description = place.getString("description");
                            places.add(description);
                            Log.d("place from google", description);
                        }

                        String[] stringArray = places.toArray(new String[places
                                .size()]);
                        PlaceArray = new String[places.size()];
                        for (int j = 0; j < places.size(); j++) {
                            PlaceArray[j] = places.get(j);
                            Log.d("placearray items",
                                    places.get(j) + "  " + PlaceArray[j] + " ,"
                                            + stringArray.toString());
                        }
                        PlaceArray = stringArray.clone();
                        Log.d("placesCallBack", PlaceArray.length
                                + "");
                        placeslistAdapter = new ArrayAdapter<String>(
                                SignupActivity.this,
                                android.R.layout.simple_list_item_1, PlaceArray);
                        location.setAdapter(placeslistAdapter);
                        placeslistAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    // showDataView();
                }
            }
        };
    }

    ;

    private void initData() {
        if (isFromFacebook || isFromGoogle) {
            editTextEmail.setClickable(false);
        }
        editTextEmail.setText(email);
        textViewfname.setText(givenName);
        textViewlname.setText(familyName);

        final Bitmap defaultImage = BitmapFactory.decodeResource(getApplicationContext()
                .getResources(), R.drawable.ic_launcher);
        userImageBitmap = GeneralUtil.getRoundedBitmap(defaultImage);
        // aq.id(userImage).image(profileInfo.myPhoto);
        aq.id(userImg).image(imageUrl, true, true, 400, 0,
                new BitmapAjaxCallback() {
                    protected void callback(String url, ImageView iv,
                                            Bitmap bm, AjaxStatus status) {
                        if (bm != null) {
                            userImageBitmap = GeneralUtil.getRoundedBitmap(bm);

                        } else {
                            userImageBitmap = GeneralUtil
                                    .getRoundedBitmap(defaultImage);
                        }
                        userImg.setImageBitmap(userImageBitmap);
                    }

                    ;
                });


        if (gender.equals("male")) {
            textViewMale.setSelected(true);
            textViewFemale.setSelected(false);
            textViewOther.setSelected(false);
        } else if (gender.equals("female")) {
            textViewMale.setSelected(false);
            textViewFemale.setSelected(true);
            textViewOther.setSelected(false);
        } else {
            textViewMale.setSelected(false);
            textViewFemale.setSelected(false);
            textViewOther.setSelected(true);
        }


        textViewFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewMale.setSelected(false);
                textViewFemale.setSelected(true);
                textViewOther.setSelected(false);
                gender = "female";
                hideSoftKeyboardAuto2();
            }
        });
        textViewMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewMale.setSelected(true);
                textViewFemale.setSelected(false);
                textViewOther.setSelected(false);
                gender = "male";
                hideSoftKeyboardAuto3();
            }
        });
        textViewOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewMale.setSelected(false);
                textViewFemale.setSelected(false);
                textViewOther.setSelected(true);
                gender = "others";
                hideSoftKeyboardAuto4();
            }
        });

        spinMonth = (Spinner) findViewById(R.id.month);
        spinDay = (Spinner) findViewById(R.id.day);
        spinDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hideSoftKeyboardAuto();
                hideSoftKeyboard();
                day = position + "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hideSoftKeyboardAuto1();
                hideSoftKeyboard();
                month = position + "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ArrayAdapter monthAdapter = ArrayAdapter.createFromResource(this, R.array.Month, R.layout.spinner_parent_layout);
        spinMonth.setAdapter(monthAdapter);

        ArrayAdapter dayAdapter = ArrayAdapter.createFromResource(this, R.array.Day, R.layout.spinner_parent_layout);
        spinDay.setAdapter(dayAdapter);

        Log.i("checkDate", "dob" + dob);
        if (dob != null) {
            if (dob.length() > 0) {
                String[] mdob = dob.split("/");
                Log.i("checkDate", "" + mdob[2]);
                spinDay.setSelection(Integer.parseInt(mdob[0]));
                spinMonth.setSelection(Integer.parseInt(mdob[1]));
                editYear.setText(mdob[2]);
                hideSoftKeyboard();
            }
        }

    }

    private void initToolbar() {
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

        titleToolbar.setVisibility(View.GONE);
        nextBtnToolbar.setText("s");
        nextBtnToolbar.setTextColor(getResources().getColor(R.color.Blue));

        //titleToolbar.setTextColor(getResources().getColor(R.color.txt_color_default));
        backBtnToolbar.setText("j");
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

                if (validation()) {
                    if (loginType.equals("facebook")) {
                        if (gcmManager.getRegistrationId(SignupActivity.this).isEmpty()) {
                            gcmManager.initGcmRegister();
                            gcmManager.setListner(new GcmManager.onCompleteFetchingGcmIdListner() {
                                @Override
                                public void onCompleteFetchingGcmId(String gcmId) {
                                    register(gcmId);
                                }
                            });

                        } else {
                            register(gcmManager.getRegistrationId(SignupActivity.this));
                        }
                    } else if (loginType.equals("google")) {
                        if (gcmManager.getRegistrationId(SignupActivity.this).isEmpty()) {
                            gcmManager.initGcmRegister();
                            gcmManager.setListner(new GcmManager.onCompleteFetchingGcmIdListner() {
                                @Override
                                public void onCompleteFetchingGcmId(String gcmId) {
                                    register(gcmId);
                                }
                            });

                        } else {
                            register(gcmManager.getRegistrationId(SignupActivity.this));
                        }
                    } else {

                        Intent intent = new Intent(SignupActivity.this, SetPassword.class);
                        intent.putExtra("picture", imageUrl);
                        intent.putExtra("first_name", textViewfname.getText().toString().trim());
                        intent.putExtra("last_name", textViewlname.getText().toString().trim());
                        intent.putExtra("email", editTextEmail.getText().toString().trim());
                        intent.putExtra("gender", gender);
                        intent.putExtra("dob", editYear.getText().toString().trim() + "-" + month + "-" + day);
                        intent.putExtra("address", location.getText().toString().trim());
                        intent.putExtra("website", website.getText().toString().trim());
                        if (profileType.equals("public")) {
                            intent.putExtra("is_private", 0);
                        } else {
                            intent.putExtra("is_private", 1);
                        }
                        Log.d("intent",intent.toString());
                        startActivity(intent);
                        finish();

                    }
                } else {

                }


            }
        });


        return customView;
    }

    private boolean validation() {
        Boolean val = true;
        Calendar c = Calendar.getInstance();
        int cyear = c.get(Calendar.YEAR);
        if (editTextEmail.getText().length() == 0) {
            val = false;
            editTextEmail.setError("Fields are required.");
        } else if (!GeneralUtil.isEmailValid(editTextEmail.getText().toString().trim())) {
            val = false;
            editTextEmail.setError("Please enter valid email address.");
        }

        if (day.equals("0")) {
            val = false;
//            Toast.makeText(this, "Please enter your date of birth correctly", Toast.LENGTH_LONG).show();
        }

        if (month.equals("0")) {
            val = false;
//            Toast.makeText(this, "Please enter your date of birth", Toast.LENGTH_LONG).show();
        }

        if (editYear.getText().length() == 0) {
            val = false;
            editYear.setError("Year is required.");
        } else if(editYear.getText().length()>=cyear)
        {
            val = false;
            editYear.setError("year not valid");
        }

        if (location.getText().length() == 0) {
            val = false;
            location.setError("Location is required.");

        }
        if (editYear.getText().length() != 4) {
            val = false;
            editYear.setError("Enter valid year.");

        } else {
//            matcher = Pattern.compile(DATE_PATTERN).matcher(editYear.getText().toString());
//
//            if (!matcher.matches()) {
//                Toast.makeText(getApplicationContext(), "Invalid Birthday!", Toast.LENGTH_SHORT).show();
//            }
        }
        if (textViewfname.getText().length() == 0) {
            val = false;
            textViewfname.setError("This field is required.");

        }
        if (textViewlname.getText().length() == 0) {
            val = false;
            textViewlname.setError("This field is required.");

        }
        if (website.getText().length() == 0) {
            val = false;
            website.setError("This field is required!");
            website.requestFocus();
        }
        else if (!GeneralUtil.isValidUrl(website.getText().toString().trim())) {
            val = false;
            website.setError("Please enter valid website.");
            website.requestFocus();
        }

        return val;
    }

    public void register(String gcmId) {
        if (isNetworkAvailable()) {
            hideSoftKeyboard();
            mProgressDialog.setMessage("Loading..");
            mProgressDialog.show();
            HashMap<String, Object>
                    mainJson = new HashMap<String, Object>();
            if (loginType.equals("facebook")) {
                HashMap<String, String> map = (HashMap<String, String>) SessionStore.fetchFacebookInformation(this);
                mainJson.put("fbid", map.get(SessionStore.FACEBOOK_ID));
                //check here....

            } else {
                //else if google???
                mainJson.put("googleid", googleId);
            }
            mainJson.put("first_name", givenName);
            mainJson.put("last_name", familyName);
            mainJson.put("email", editTextEmail.getText().toString().trim());
            mainJson.put("dob", editYear.getText().toString().trim() + "-" + month + "-" + day);
            mainJson.put("gender", gender);
            mainJson.put("website", website.getText().toString().trim());
            mainJson.put("address", location.getText().toString().trim());
            mainJson.put("picture", imageUrl);
            mainJson.put("device_token", gcmId);
            mainJson.put("device_type", "A");
            if (profileType.equals("public")) {
                mainJson.put("is_private", 0);
            } else {
                mainJson.put("is_private", 1);
            }
            Log.i("checkPic", "" + mainJson);
            mAqueryTask.sendPostObjectRequest(DataHolder.REGISTER, mainJson, JSONObject.class, dataCallBack());
        } else {
            showToast("No internet connection available.");
        }
    }

    private AjaxCallback<JSONObject> dataCallBack() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);
                Log.i("checkStatus", "checkStatus" + result);
                // {"output":{"response":"","message":"Invalid username or password, try again","action":"login","status":0}}
                // {"output":{"response":{"data":"Sharmila Khadka","token":"x21v1ft5ms1cvuol5510px9lgmebd92a"},"message":"You are authorized user.","action":"login","status":1}}
                mProgressDialog.dismiss();
                if (result != null) {
                    try {
                        JSONObject object = result.getJSONObject("output");
                        int userStatus = object.optInt("status");
                        String message = object.optString("message");
                        if (userStatus == 1) {
                            JSONObject userObject = object.getJSONObject("response");
                            String token = userObject.optString("token");
                            String email = userObject.optString("email");
                            String user_id = userObject.optString("user_id");
                            String name = userObject.optString("name");
                            String picture = userObject.optString("picture");
                            mVivaApplicatiion.setUserData(DataHolder.TOKEN, token);
                            mVivaApplicatiion.setUserData(DataHolder.USEREMAIL, email);
                            mVivaApplicatiion.setUserData(DataHolder.USERNAME, name);
                            mVivaApplicatiion.setUserData(DataHolder.USERID, user_id);
                            mVivaApplicatiion.setUserData(DataHolder.USERIMAGE, picture);
                            mVivaApplicatiion.setUserData(DataHolder.ISFROMREGISTER, true);
                            Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                            showToast(message);
                        } else if (userStatus == 0) {
                            showToast(message);
                        } else if (userStatus == 2) {
                            showToast(message);
                            editTextEmail.setError(message);
                            editTextEmail.setFocusable(true);
                        } else if (userStatus == 3) {
                            new AlertDialog.Builder(SignupActivity.this).setTitle("Viva")
                                    .setMessage(message)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // continue with delete
                                        }
                                    }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            }).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        };
    }


    private Dialog createChooseImageDialog() {

        ViewGroup addPhotoView = (ViewGroup) getLayoutInflater().inflate(
                R.layout.add_photo_dialog_layout, null);

        Button cameraClickButton = (Button) addPhotoView
                .findViewById(R.id.btnCamera);
        cameraClickButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                chooseImageDialog.dismiss();
                startActivityForResult(cameraIntent,
                        DataHolder.INTENT_CAPTURE_IMAGE);
            }
        });

        Button photoBrowseButton = (Button) addPhotoView
                .findViewById(R.id.btnBrowseGallery);
        photoBrowseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImageDialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, DataHolder.INTENT_BROWSE_GALLERY);
            }
        });
        btnRemoveImage = (Button) addPhotoView.findViewById(R.id.btnRemove);
        btnRemoveImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                chooseImageDialog.dismiss();
                userImg.setImageResource(R.drawable.ic_launcher);
                userImg.setTag(null);
                userImageBitmap = null;
            }
        });
        Dialog addPhotoDialog = new Dialog(this);
        addPhotoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addPhotoDialog.setContentView(addPhotoView);
        setDialogParams(addPhotoDialog);
        return addPhotoDialog;

    }

    private void setDialogParams(Dialog dialog) {
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        String imagePath;
        if (requestCode == DataHolder.INTENT_CAPTURE_IMAGE) {
            // for mCamera photo click result
            try {
                String imgLocation = "/sdcard/events/", fileName;
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");

                OutputStream fOut = null;
                Date date = new Date();
                File dir = new File(imgLocation);
                dir.mkdir();
                fileName = "img" + date.getTime() + ".png";
                file = new File(dir, fileName);

                try {
                    fOut = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                fOut.flush();
                fOut.close();
                imagePath = imgLocation + fileName;
                Log.i("imagepath", imagePath);
                if (imagePath != null) {
                    setSelectedImage(imagePath);
                    showLoadingDialog();
                    Upload task = new Upload();
                    task.execute();

                }
            } catch (Exception e) {
                Log.i("ImageLoading", "Error in adding image");
            }
        } else if (requestCode == DataHolder.INTENT_BROWSE_GALLERY) {
            if (data != null) {
                Uri targetUri = data.getData();
                imagePath = getRealPathFromURI(targetUri);
                if (imagePath != null) {
                    setSelectedImage(imagePath);
                    file = new File(imagePath);
                    showLoadingDialog();
                    Upload task = new Upload();
                    task.execute();

                }
            }
        }
    }

    private void setSelectedImage(String imagePath) {
        userImageBitmap = GeneralUtil.decodeImageFile(imagePath, 500);
        if (userImageBitmap != null) {
            userImg.setImageBitmap(userImageBitmap);
            userImg.setTag(imagePath);
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(contentUri, proj, null, null,
                    null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private class Upload extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String response = "";

            try {

                JSONObject result = cloudinary.uploader().upload(file,
                        Cloudinary.emptyMap());
                Log.v("data", result.toString());
                try {
                    response = result.getString("url");
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            hideLoadingDialog();
            images = result;
            imageUrl = result;
            GeneralUtil.setBlurBackground(imageUrl.trim(), profileImageBack, SignupActivity.this);
            Log.i("uploadImages", "uploadtask" + images);
        }

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

    public boolean validate(final String date) {

        matcher = pattern.matcher(date);

        if (matcher.matches()) {
            matcher.reset();

            if (matcher.find()) {
                String day = matcher.group(1);
                String month = matcher.group(2);
                int year = Integer.parseInt(matcher.group(3));

                if (day.equals("31") &&
                        (month.equals("4") || month.equals("6") || month.equals("9") ||
                                month.equals("11") || month.equals("04") || month.equals("06") ||
                                month.equals("09"))) {
                    return false; // only 1,3,5,7,8,10,12 has 31 days
                } else if (month.equals("2") || month.equals("02")) {
                    //leap year
                    if (year % 4 == 0) {
                        if (day.equals("30") || day.equals("31")) {
                            return false;
                        } else {
                            return true;
                        }
                    } else {
                        if (day.equals("29") || day.equals("30") || day.equals("31")) {
                            return false;
                        } else {
                            return true;
                        }
                    }
                } else {
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    public void hideSoftKeyboardAuto() {
        InputMethodManager imm = (InputMethodManager) getApplicationContext()
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(spinDay.getApplicationWindowToken(), 0);

    }
    public void hideSoftKeyboardAuto1() {
        InputMethodManager imm = (InputMethodManager) getApplicationContext()
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(spinMonth.getApplicationWindowToken(), 0);

    }
    public void hideSoftKeyboardAuto2() {
        InputMethodManager imm = (InputMethodManager) getApplicationContext()
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(textViewFemale.getApplicationWindowToken(), 0);

    }
    public void hideSoftKeyboardAuto3() {
        InputMethodManager imm = (InputMethodManager) getApplicationContext()
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(textViewMale.getApplicationWindowToken(), 0);

    }
    public void hideSoftKeyboardAuto4() {
        InputMethodManager imm = (InputMethodManager) getApplicationContext()
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(textViewOther.getApplicationWindowToken(), 0);

    }
}


