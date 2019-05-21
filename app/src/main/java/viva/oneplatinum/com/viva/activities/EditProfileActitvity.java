package viva.oneplatinum.com.viva.activities;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.gcm.GcmManager;
import viva.oneplatinum.com.viva.models.UserDetail;
import viva.oneplatinum.com.viva.utils.DataHolder;
import viva.oneplatinum.com.viva.utils.GeneralUtil;
import viva.oneplatinum.com.viva.widgets.FontIcon;
import viva.oneplatinum.com.viva.widgets.RobotoBoldTextView;

/**
 * Created by D-MAX on 3/31/2016.
 */
public class EditProfileActitvity extends ParentActivity {

    EditText website;
    AutoCompleteTextView location;
    LinearLayout googleLoginView, facebookLoginView;
    String imageUrl, familyName, givenName, gender, displayName, email, loginType, googleId, dob, address, websitedata,is_Private;
    TextView textViewMale, textViewFemale, textViewOther;
    EditText textViewfname, textViewlname;
    EditText editTextEmail, editYear;
    TextView textLocation, textWebsite;
    private static final int PROFILE_PIC_SIZE = 120;
    ImageView userImg, imageEdit,profileImageBack;
    Spinner spinDay, spinMonth;
    //Cloudinary cloudinary;
    AQuery aq;
    RobotoBoldTextView titleToolbar;
    Toolbar mToolBar;
    FontIcon backBtnToolbar, nextBtnToolbar;
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
    UserProfile mUserProfile;
    UserDetail mUserDetail;
    RobotoBoldTextView textViewPublic, textViewPrivate;
    String profileType = "public";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        aq = new AQuery(getApplicationContext());
        mUserProfile=(UserProfile)UserProfile.mActivity;
        gcmManager = new GcmManager(this);
        places = new ArrayList<String>();
        initCloudinary();
        initView();
        initData();
        initToolbar();
        onClick();



    }

    private void initCloudinary() {
        Map config = new HashMap();
        config.put("cloud_name", "dcj2wdnva");
        config.put("api_key", "197437687815994");
        config.put("api_secret", "BNWeI5mER5dgcdy_0ewO7m1VF2c");
        cloudinary = new Cloudinary(config);
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

    public void initView() {
        textViewPublic = (RobotoBoldTextView) findViewById(R.id.textViewPublic);
        textViewPrivate = (RobotoBoldTextView) findViewById(R.id.textViewPrivate);
        textViewPublic.setSelected(true);
//        textViewPrivate.setSelected(false);
        profileImageBack = (ImageView) findViewById(R.id.profileImageBack);
        loginType = getIntent().getStringExtra("loginType");
        if (loginType.equals("google")) {
            googleId = getIntent().getStringExtra("googleId");
        }
        imageUrl = getIntent().getStringExtra("picture");
        is_Private= getIntent().getStringExtra("is_Private");
        familyName = getIntent().getStringExtra("last_name");
        givenName = getIntent().getStringExtra("first_name");
        gender = getIntent().getStringExtra("gender");
        gender=gender.toLowerCase();
        dob = getIntent().getStringExtra("birthday");
        websitedata = getIntent().getStringExtra("website");
        address = getIntent().getStringExtra("address");
        displayName = getIntent().getStringExtra("displayName");
        email = getIntent().getStringExtra("email");
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
        editTextEmail.setClickable(false);
        editTextEmail.setSelected(false);
        editTextEmail.setFocusable(false);
        editTextEmail.setFocusableInTouchMode(false);
        editTextEmail.setCursorVisible(false);
        editTextEmail.setPressed(false);
//        editTextEmail.setFilters(new InputFilter[]{
//                new InputFilter() {
//                    public CharSequence filter(CharSequence src, int start,
//                                               int end, Spanned dst, int dstart, int dend) {
//                        return src.length() < 1 ? dst.subSequence(dstart, dend) : "";
//                    }
//                }
//        });

        textViewMale = (TextView) findViewById(R.id.male);
        textViewFemale = (TextView) findViewById(R.id.female);
        textViewOther = (TextView) findViewById(R.id.other);
        userImg = (ImageView) findViewById(R.id.userImage);
        //setProfilePic(imageUrl);

        spinDay = (Spinner) findViewById(R.id.day);
        spinMonth = (Spinner) findViewById(R.id.month);
        editYear = (EditText) findViewById(R.id.year);

        placeslistAdapter = new ArrayAdapter<String>(EditProfileActitvity.this,
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
                    AQuery aq = new AQuery(EditProfileActitvity.this);
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
                                EditProfileActitvity.this,
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
        if(is_Private.equals("1")){
            textViewPublic.setSelected(false);
            textViewPrivate.setSelected(true);
        }else{
            textViewPublic.setSelected(true);
            textViewPrivate.setSelected(false);
        }
        editTextEmail.setText(email);
        editTextEmail.setClickable(false);
        textViewfname.setText(givenName);
        textViewlname.setText(familyName);
        location.setText(address);
        website.setText(websitedata);
        final Bitmap defaultImage = BitmapFactory.decodeResource(getApplicationContext()
                .getResources(), R.drawable.ic_launcher);
        userImageBitmap = GeneralUtil.getRoundedBitmap(defaultImage);
        // aq.id(userImage).image(profileInfo.myPhoto);
        aq.id(userImg).image(imageUrl.trim(), true, true, 400, 0,
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
        Log.d("image",imageUrl);
        GeneralUtil.setBlurBackground(imageUrl.trim(), profileImageBack, EditProfileActitvity.this);

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

            }
        });
        textViewMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewMale.setSelected(true);
                textViewFemale.setSelected(false);
                textViewOther.setSelected(false);
                gender = "male";
            }
        });
        textViewOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewMale.setSelected(false);
                textViewFemale.setSelected(false);
                textViewOther.setSelected(true);
                gender="others";
            }
        });

        spinMonth = (Spinner) findViewById(R.id.month);
        spinDay = (Spinner) findViewById(R.id.day);
        spinDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                day = position + "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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
        String[] mdob = dob.split("-");
        spinDay.setSelection(Integer.parseInt(mdob[2]));
        spinMonth.setSelection(Integer.parseInt(mdob[1]));
        editYear.setText(mdob[0]);
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

//                        Intent intent = new Intent(EditProfileActitvity.this, ForgetPassword.class);
//                        intent.putExtra("image", imageUrl);
//                        intent.putExtra("givenName", textViewfname.getText().toString().trim());
//                        intent.putExtra("familyName", textViewlname.getText().toString().trim());
//                        intent.putExtra("email", editTextEmail.getText().toString().trim());
//                        intent.putExtra("gender", gender);
//                        intent.putExtra("dob", editYear.getText().toString().trim() + "-" + month + "-" + day);
//                        intent.putExtra("address", location.getText().toString().trim());
//                        intent.putExtra("website", website.getText().toString().trim());
//                        startActivity(intent);
                    editUserProfile();
                }


            }
        });


        return customView;
    }

    public void editUserProfile() {
        if (isNetworkAvailable()) {
            mProgressDialog.setMessage("Updating...");
            mProgressDialog.show();
            Log.d("getCategories", "check");
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("token",  mVivaApplicatiion.getUserData(DataHolder.TOKEN));
            params.put("first_name", textViewfname.getText().toString().trim());
            params.put("last_name", textViewlname.getText().toString().trim());
            params.put("dob", editYear.getText().toString().trim() + "-" + month + "-" + day);
            params.put("gender", gender);
            params.put("address", location.getText().toString().trim());
            params.put("website", website.getText().toString().trim());
            params.put("picture", imageUrl);
            params.put("email",editTextEmail.getText().toString().trim());
            if (profileType.equals("public")) {
                params.put("is_private", 0);
            } else {
                params.put("is_private", 1);
            }
            Log.d("getCategories", DataHolder.EDIT_PROFILE);
            Log.d("getCategories", params+"");
            mAqueryTask.sendPostObjectRequest(DataHolder.EDIT_PROFILE, params, JSONObject.class, getUserProfileCallback());

        }
    }

    private AjaxCallback<JSONObject> getUserProfileCallback() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);
                Log.d("getCategories", result.toString());
                mProgressDialog.dismiss();
                if (result != null) {
                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                            int userStatus = outpuJsonObject.optInt("status");
                            if (userStatus == 1) {
                            showToast(outpuJsonObject.optString("message"));
                            Intent i = new Intent(EditProfileActitvity.this, UserProfile.class);
                            startActivity(i);
                            finish();
                            mUserProfile.finish();
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
            editYear.setError("day is required.");
//            Toast.makeText(this, "Please enter your date of birth correctly", Toast.LENGTH_LONG).show();
        }

        if (month.equals("0")) {
            val = false;
            editYear.setError("month is required.");
//            Toast.makeText(this, "Please enter your date of birth", Toast.LENGTH_LONG).show();
        }

        if (editYear.getText().length() == 0) {
            val = false;
            editYear.setError("Year is required.");
        }
        else if(editYear.getText().length()>=cyear)
        {
            val = false;
            editYear.setError("year not valid");
        }


        if (location.getText().length() == 0) {
            val = false;
            location.setError("Location is required.");

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
            userImg.setImageBitmap(GeneralUtil.getRoundedBitmap(userImageBitmap));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(EditProfileActitvity.this,UserProfile.class);
        startActivity(i);
    }
}


