package viva.oneplatinum.com.viva.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.cloudinary.Cloudinary;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.dialogs.CustomGalleryFragment;
import viva.oneplatinum.com.viva.dialogs.TypePickerDialog;
import viva.oneplatinum.com.viva.interfaces.ImageSelectorFromGallery;
import viva.oneplatinum.com.viva.interfaces.TypeSelectorListner;
import viva.oneplatinum.com.viva.models.CategoryInfo;
import viva.oneplatinum.com.viva.models.EventDetail;
import viva.oneplatinum.com.viva.parser.ParseUtility;
import viva.oneplatinum.com.viva.utils.DataHolder;
import viva.oneplatinum.com.viva.utils.GeneralUtil;
import viva.oneplatinum.com.viva.widgets.CircularImageView;
import viva.oneplatinum.com.viva.widgets.FontIcon;
import viva.oneplatinum.com.viva.widgets.RobotoBoldTextView;
import viva.oneplatinum.com.viva.widgets.RobotoLightEditText;

import static viva.oneplatinum.com.viva.utils.DataHolder.GETALLMETHOD;

/**
 * Created by D-MAX on 3/21/2016.
 */
public class StartFromScratch extends ParentActivity implements ImageSelectorFromGallery, TypeSelectorListner {
    FontIcon backBtnToolbar, nextBtnToolbar;
    RobotoBoldTextView titleToolbar;
    Toolbar mToolBar;
    static File photoFile;
    ImageView image;
    static Cloudinary cloudinary;
    static String images = "";
    RobotoLightEditText eventName;
    RobotoBoldTextView textViewPublic, textViewPrivate, eventType1, eventType2, eventType3, eventType4;
    static ImageView squareImage1, squareImage2, squareImage3, squareImage4;
    static CircularImageView circularImage;
    static RelativeLayout circle_img;
    static RelativeLayout square_img1_img;
    static RelativeLayout square_img2_img;
    static RelativeLayout square_img3_img;
    static RelativeLayout square_img4_img;
    static FontIcon fontIconPlus;
    static FontIcon fontIconPlusSquareImg1;
    static FontIcon fontIconPlusSquareImg2;
    static FontIcon fontIconPlusSquareImg3;
    static FontIcon fontIconPlusSquareImg4;
    ArrayList<String> imageList = null;
    int resultcode;
    String eventType = "public";
    String circleImageUrl = "";
    ListView lv;
    AQuery aq;
    SharedPreferences getAllMethod;
    FrameLayout mFrameLayout;
    FrameLayout imageContainer;
    AutoCompleteTextView location;
    RobotoLightEditText eventDescription, start_Date_Time, end_Date_Time, eventPhone, eventPrice;
    AutoCompleteTextView au_eventLocation, event_Au_email;
    ArrayAdapter<String> placeslistAdapter;
    String PlaceArray[] = {};
    ArrayList<String> places;
    String eventStartYear;
    String eventStartMonth;
    String eventStartDay;
    String eventStartHour;
    String eventStartMin;
    String eventEndYear;
    String eventEndMonth;
    String eventEndDay;
    String eventEndHour;
    String eventEndMin;
    SharedPreferences eventInfo;
    Calendar c1 = Calendar.getInstance(), c2 = Calendar.getInstance(), c = Calendar.getInstance();
    Boolean datePickerBoolean = true;
    android.app.AlertDialog datePickerDialog;
    static CustomGalleryFragment mCustomGalleryFragment;
    static String defaultImage;
    static String image1;
    static String image2;
    static String image3;
    static String image4;
    String eventDetailResult;
    EventDetail mEventDetail;
    JSONObject categoryObject;
    ArrayList<CategoryInfo> mCategoryInfoList;
    public static Activity mActivity;
    String data;
    Boolean isTypeSelecte = false;
    Boolean type1 = false, type2 = false, type3 = false, type4 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event_from_scratch);
        eventDetailResult = getIntent().getStringExtra("postDetailResult");
        initToolBar();
        initViews();
        onClick();
        initCloudinary();
        mCategoryInfoList = new ArrayList<CategoryInfo>();
        getCategory();
        mActivity = this;
        aq = new AQuery(this);
        eventType1.setSelected(false);
        eventType2.setSelected(false);
        eventType3.setSelected(false);
        eventType4.setSelected(false);
        getAllMethod = StartFromScratch.this.getSharedPreferences(GETALLMETHOD, 0);

    }

    public static void setEventImage(Bitmap bitmap, String path) {


    }


    ;

    private void upload() {
        showLoadingDialog();
        Upload task = new Upload();
        task.execute();
    }

    private void onClick() {
        textViewPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewPublic.setSelected(true);
                textViewPrivate.setSelected(false);
                hideSoftKeyboard();
                eventType = "public";

            }
        });
        textViewPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewPrivate.setSelected(true);
                textViewPublic.setSelected(false);
                hideSoftKeyboard();
                eventType = "private";
            }
        });
        eventType1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                type1 = true;
                type2 = false;
                type3 = false;
                type4 = false;
                hideSoftKeyboard();

                if (categoryObject == null) {
                    isTypeSelecte = true;
                    getCategory();
                } else {
                    TypePickerDialog mTypePickerDialog = new TypePickerDialog();
                    Bundle b = new Bundle();
                    b.putString("categoryData", categoryObject.toString());
                    if (eventType1.isSelected()) {
                        b.putString("type", eventType1.getTag().toString());
                    }
                    mTypePickerDialog.setArguments(b);
                    mTypePickerDialog.show(getSupportFragmentManager(), "TypePicker");
                }


            }


        });
        eventType2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type1 = false;
                type2 = true;
                type3 = false;
                type4 = false;

                hideSoftKeyboard();
                if (categoryObject == null) {
                    isTypeSelecte = true;
                    getCategory();
                } else {
                    TypePickerDialog mTypePickerDialog = new TypePickerDialog();
                    Bundle b = new Bundle();
                    b.putString("categoryData", categoryObject.toString());
                    if (eventType2.isSelected()) {
                        b.putString("type", eventType2.getTag().toString());
                    }

                    mTypePickerDialog.setArguments(b);
                    mTypePickerDialog.show(getSupportFragmentManager(), "TypePicker");
                }
            }
        });
        eventType3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type1 = false;
                type2 = false;
                type3 = true;
                type4 = false;
                hideSoftKeyboard();
                if (categoryObject == null) {
                    isTypeSelecte = true;
                    getCategory();
                } else {
                    TypePickerDialog mTypePickerDialog = new TypePickerDialog();
                    Bundle b = new Bundle();
                    b.putString("categoryData", categoryObject.toString());
                    if (eventType3.isSelected()) {
                        b.putString("type", eventType3.getTag().toString());
                    }
                    mTypePickerDialog.setArguments(b);
                    mTypePickerDialog.show(getSupportFragmentManager(), "TypePicker");
                }
            }
        });
        eventType4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                type1 = false;
                type2 = false;
                type3 = false;
                type4 = true;
                hideSoftKeyboard();
                if (categoryObject == null) {
                    isTypeSelecte = true;
                    getCategory();
                } else {
                    TypePickerDialog mTypePickerDialog = new TypePickerDialog();
                    Bundle b = new Bundle();
                    b.putString("categoryData", categoryObject.toString());
                    if (eventType4.isSelected()) {
                        b.putString("type", eventType4.getTag().toString());
                    }
                    mTypePickerDialog.setArguments(b);
                    mTypePickerDialog.show(getSupportFragmentManager(), "TypePicker");
                }
            }
        });
        fontIconPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (fontIconPlus.getText().equals("E")) {
                    View view = StartFromScratch.this.getCurrentFocus();
                    hideSoftKeyboard();
                    mCustomGalleryFragment = new CustomGalleryFragment();
                    mCustomGalleryFragment.show(getSupportFragmentManager(), "imageGallery");
                    setSelectedImage();
                    circularImage.setSelected(true);
                    fontIconPlus.setVisibility(View.VISIBLE);
                } else {
                    circularImage.setImageBitmap(null);
                    fontIconPlus.setText("E");
                }

            }
        });
        circle_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = StartFromScratch.this.getCurrentFocus();
                hideSoftKeyboard();
                mCustomGalleryFragment = new CustomGalleryFragment();
                mCustomGalleryFragment.show(getSupportFragmentManager(), "imageGallery");
                setSelectedImage();
                circularImage.setSelected(true);
                fontIconPlus.setVisibility(View.VISIBLE);


            }
        });

        eventName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageContainer.setVisibility(View.GONE);
            }
        });
        fontIconPlusSquareImg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fontIconPlusSquareImg1.getText().equals("E")) {
                    View view = StartFromScratch.this.getCurrentFocus();
                    hideSoftKeyboard();
                    mCustomGalleryFragment = new CustomGalleryFragment();
                    mCustomGalleryFragment.show(getSupportFragmentManager(), "imageGallery");
                    setSelectedImage();
                    squareImage1.setSelected(true);
                    fontIconPlusSquareImg1.setVisibility(View.VISIBLE);
                } else {
                    squareImage1.setImageBitmap(null);
                    fontIconPlusSquareImg1.setText("E");
                }


            }
        });
        square_img1_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                hideSoftKeyboard();
                mCustomGalleryFragment = new CustomGalleryFragment();
                mCustomGalleryFragment.show(getSupportFragmentManager(), "imageGallery");
                setSelectedImage();
                squareImage1.setSelected(true);
                fontIconPlusSquareImg1.setVisibility(View.VISIBLE);


            }
        });
        fontIconPlusSquareImg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fontIconPlusSquareImg2.getText().equals("E")) {
                    View view = StartFromScratch.this.getCurrentFocus();
                    hideSoftKeyboard();
                    mCustomGalleryFragment = new CustomGalleryFragment();
                    mCustomGalleryFragment.show(getSupportFragmentManager(), "imageGallery");
                    setSelectedImage();
                    squareImage2.setSelected(true);
                    fontIconPlusSquareImg2.setVisibility(View.VISIBLE);
                } else {
                    squareImage2.setImageBitmap(null);
                    fontIconPlusSquareImg2.setText("E");
                }


            }
        });
        square_img2_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                hideSoftKeyboard();
                mCustomGalleryFragment = new CustomGalleryFragment();
                mCustomGalleryFragment.show(getSupportFragmentManager(), "imageGallery");
                setSelectedImage();
                squareImage2.setSelected(true);
                fontIconPlusSquareImg2.setVisibility(View.VISIBLE);


            }
        });
        fontIconPlusSquareImg3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fontIconPlusSquareImg3.getText().equals("E")) {
                    View view = StartFromScratch.this.getCurrentFocus();
                    hideSoftKeyboard();
                    mCustomGalleryFragment = new CustomGalleryFragment();
                    mCustomGalleryFragment.show(getSupportFragmentManager(), "imageGallery");
                    setSelectedImage();
                    squareImage3.setSelected(true);
                    fontIconPlusSquareImg3.setVisibility(View.VISIBLE);
                } else {
                    squareImage3.setImageBitmap(null);
                    fontIconPlusSquareImg3.setText("E");
                }


            }
        });
        square_img3_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                hideSoftKeyboard();
                mCustomGalleryFragment = new CustomGalleryFragment();
                mCustomGalleryFragment.show(getSupportFragmentManager(), "imageGallery");
                setSelectedImage();
                squareImage3.setSelected(true);
                fontIconPlusSquareImg3.setVisibility(View.VISIBLE);


            }
        });
        fontIconPlusSquareImg4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fontIconPlusSquareImg4.getText().equals("E")) {
                    View view = StartFromScratch.this.getCurrentFocus();
                    hideSoftKeyboard();
                    mCustomGalleryFragment = new CustomGalleryFragment();
                    mCustomGalleryFragment.show(getSupportFragmentManager(), "imageGallery");
                    setSelectedImage();
                    squareImage4.setSelected(true);
                    fontIconPlusSquareImg4.setVisibility(View.VISIBLE);
                } else {
                    squareImage4.setImageBitmap(null);
                    fontIconPlusSquareImg4.setText("E");
                }


            }
        });
        square_img4_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                hideSoftKeyboard();
                mCustomGalleryFragment = new CustomGalleryFragment();
                mCustomGalleryFragment.show(getSupportFragmentManager(), "imageGallery");
                setSelectedImage();
                squareImage4.setSelected(true);
                fontIconPlusSquareImg4.setVisibility(View.VISIBLE);


            }
        });
    }


    public void getCategory() {
        if (isNetworkAvailable()) {
            if (isTypeSelecte) {
                mProgressDialog.setMessage("loading types..");
                mProgressDialog.show();
            }
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));
            mAqueryTask.sendRequest(DataHolder.GET_CATEGORIES, params, JSONObject.class, getCategoriesCallback());
            Log.i("checkCateg1 ", "" + params);
        }
    }

    public void setSelectedImage() {
        circularImage.setSelected(false);
        squareImage1.setSelected(false);
        squareImage2.setSelected(false);
        squareImage3.setSelected(false);
        squareImage4.setSelected(false);

    }

    private void initViews() {
        imageContainer = (FrameLayout) findViewById(R.id.imageContainer);
        eventName = (RobotoLightEditText) findViewById(R.id.eventName);
        textViewPublic = (RobotoBoldTextView) findViewById(R.id.textViewPublic);
        textViewPrivate = (RobotoBoldTextView) findViewById(R.id.textViewPrivate);
        eventType1 = (RobotoBoldTextView) findViewById(R.id.eventType1);
        eventType2 = (RobotoBoldTextView) findViewById(R.id.eventType2);
        eventType3 = (RobotoBoldTextView) findViewById(R.id.eventType3);
        eventType4 = (RobotoBoldTextView) findViewById(R.id.eventType4);
        circle_img = (RelativeLayout) findViewById(R.id.circle_img);
        square_img1_img = (RelativeLayout) findViewById(R.id.square_img1_img);
        square_img2_img = (RelativeLayout) findViewById(R.id.square_img2_img);
        square_img3_img = (RelativeLayout) findViewById(R.id.square_img3_img);
        square_img4_img = (RelativeLayout) findViewById(R.id.square_img4_img);
        circularImage = (CircularImageView) findViewById(R.id.circularImage);
        squareImage1 = (ImageView) findViewById(R.id.squareImage1);
        squareImage2 = (ImageView) findViewById(R.id.squareImage2);
        squareImage3 = (ImageView) findViewById(R.id.squareImage3);
        squareImage4 = (ImageView) findViewById(R.id.squareImage4);
        fontIconPlus = (FontIcon) findViewById(R.id.fontIconPlus);
        fontIconPlusSquareImg1 = (FontIcon) findViewById(R.id.fontIconPlusSquareImg1);
        fontIconPlusSquareImg2 = (FontIcon) findViewById(R.id.fontIconPlusSquareImg2);
        fontIconPlusSquareImg3 = (FontIcon) findViewById(R.id.fontIconPlusSquareImg3);
        fontIconPlusSquareImg4 = (FontIcon) findViewById(R.id.fontIconPlusSquareImg4);

        start_Date_Time = (RobotoLightEditText) findViewById(R.id.start_Date_Time);
        end_Date_Time = (RobotoLightEditText) findViewById(R.id.end_Date_Time);
        eventPhone = (RobotoLightEditText) findViewById(R.id.eventPhone);
        eventPrice = (RobotoLightEditText) findViewById(R.id.eventPrice);
        eventDescription = (RobotoLightEditText) findViewById(R.id.eventDescription);
        event_Au_email = (AutoCompleteTextView) findViewById(R.id.event_Au_email);
        au_eventLocation = (AutoCompleteTextView) findViewById(R.id.au_eventLocation);
        fontIconPlus = (FontIcon) findViewById(R.id.fontIconPlus);
        fontIconPlusSquareImg1 = (FontIcon) findViewById(R.id.fontIconPlusSquareImg1);
        fontIconPlusSquareImg2 = (FontIcon) findViewById(R.id.fontIconPlusSquareImg2);
        fontIconPlusSquareImg3 = (FontIcon) findViewById(R.id.fontIconPlusSquareImg3);
        fontIconPlusSquareImg4 = (FontIcon) findViewById(R.id.fontIconPlusSquareImg4);

        textViewPublic.setSelected(true);
        textViewPrivate.setSelected(false);


//        Log.v("image", images);
//        AQuery aq = new AQuery(this);
//        if (images.equalsIgnoreCase(
//                "http://res.cloudinary.com/oneplatinumtechnology/image/upload/v1452666635/add_image_jr0ot3.png")) {
//            aq.id(image).image(
//                    "http://res.cloudinary.com/oneplatinumtechnology/image/upload/v1452666635/add_image_jr0ot3.png");
//        } else {
//            ImageView image = (ImageView) findViewById(R.id.circularImage);
//            ImageView image1 = (ImageView) findViewById(R.id.squareImage1);
//            ImageView image2 = (ImageView) findViewById(R.id.squareImage1);
//            aq.id(image).image(images);
//        }
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

        titleToolbar.setText("CREATE EVENT");
        nextBtnToolbar.setText("s");
        nextBtnToolbar.setTextColor(getResources().getColor(R.color.Blue));

        titleToolbar.setTextColor(getResources().getColor(R.color.txt_color_default));
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

                    // postEvent();
                    Intent i = new Intent(StartFromScratch.this, CreateEvent3.class);
                    i.putExtra("eventName", eventName.getText().toString().trim());
                    i.putExtra("profileType", eventType);
                    i.putExtra("categoriesData", categoryObject.toString());
                    if (eventType1.isSelected()) {
                        i.putExtra("type1", eventType1.getTag().toString());
                        Log.d("tag1", eventType1.getTag().toString());
                    }
                    if (eventType2.isSelected()) {
                        i.putExtra("type2", eventType2.getTag().toString());
                        Log.d("tag2", eventType2.getTag().toString());
                    }
                    if (eventType3.isSelected()) {
                        i.putExtra("type3", eventType3.getTag().toString());
                        Log.d("tag3", eventType3.getTag().toString());
                    }
                    if (eventType4.isSelected()) {
                        i.putExtra("type4", eventType4.getTag().toString());
                        Log.d("tag4", eventType4.getTag().toString());
                    }

                    i.putExtra("squareImage1", image1);
                    i.putExtra("squareImage2", image2);
                    i.putExtra("squareImage3", image3);
                    i.putExtra("squareImage4", image4);
                    i.putExtra("circularImage", defaultImage);
                    Log.e("getcircularImage", defaultImage + "");
                    Log.e("getcircularImage", image1 + "");
                    Log.e("getcircularImage", image2 + "");
                    Log.e("getcircularImage", image3 + "");
                    Log.e("getcircularImage", image4 + "");
                    startActivity(i);
                } else {
                    // do nothing
                }

            }
        });


        return customView;
    }

    @Override
    public void image(Bitmap bitmap, String path) {
        mCustomGalleryFragment.dismiss();
        if (circularImage.isSelected()) {
            circularImage.setImageBitmap(bitmap);
            fontIconPlus.setText("f");
            mProgressDialog.setMessage("Uploading image...");
            photoFile = new File(path);
            if (path != null) {
                upload();

            }
        } else if (squareImage1.isSelected()) {
            squareImage1.setImageBitmap(bitmap);
            fontIconPlusSquareImg1.setText("f");
            mProgressDialog.setMessage("Uploading image...");
            photoFile = new File(path);
            if (path != null) {
                upload();

            }
        } else if (squareImage2.isSelected()) {
            squareImage2.setImageBitmap(bitmap);
            fontIconPlusSquareImg2.setText("f");
            mProgressDialog.setMessage("Uploading image...");
            photoFile = new File(path);
            if (path != null) {
                upload();

            }
        } else if (squareImage3.isSelected()) {
            squareImage3.setImageBitmap(bitmap);
            fontIconPlusSquareImg3.setText("f");
            mProgressDialog.setMessage("Uploading image...");
            photoFile = new File(path);
            if (path != null) {
                upload();

            }
        } else if (squareImage4.isSelected()) {
            squareImage4.setImageBitmap(bitmap);
            fontIconPlusSquareImg4.setText("f");
            mProgressDialog.setMessage("Uploading image...");
            photoFile = new File(path);
            if (path != null) {
                upload();

            }
        }

    }

    @Override
    public void selectedType(String data) {
        Log.d("data", data);
        if (type1 == true) {
            eventType1.setSelected(true);
            eventType1.setTag(data);

            eventType1.setBackgroundResource(R.color.red);
            eventType1.setTextColor(getResources().getColor(R.color.white));
        } else if (type2 == true) {
            eventType2.setSelected(true);
            eventType2.setTag(data);
            eventType2.setBackgroundResource(R.color.red);
            eventType2.setTextColor(getResources().getColor(R.color.white));
        } else if (type3 == true) {
            eventType3.setSelected(true);
            eventType3.setTag(data);
            eventType3.setBackgroundResource(R.color.red);
            eventType3.setTextColor(getResources().getColor(R.color.white));
        } else if (type4 == true) {
            eventType4.setSelected(true);
            eventType4.setTag(data);
            eventType4.setBackgroundResource(R.color.red);
            eventType4.setTextColor(getResources().getColor(R.color.white));
        }
    }


    private class Upload extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String response = "";

            try {

                JSONObject result = cloudinary.uploader().upload(photoFile,
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
            if (circularImage.isSelected()) {
                defaultImage = result;
            } else if (squareImage1.isSelected()) {
                image1 = result;
            } else if (squareImage2.isSelected()) {
                image2 = result;
            } else if (squareImage3.isSelected()) {
                image3 = result;
            } else if (squareImage4.isSelected()) {
                image4 = result;
            }

            Log.i("uploadImages", "uploadtask" + images);
        }

    }

    private void initCloudinary() {
        Map config = new HashMap();
        config.put("cloud_name", "oneplatinumtechnology");
        config.put("api_key", "962396486575677");
        config.put("api_secret", "_w3nczIKdA3yBYyzHL2aRbSnKpY");
        cloudinary = new Cloudinary(config);
    }

//    private File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "capturedImage";
//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(imageFileName, /* prefix */
//                ".jpg", /* suffix */
//                storageDir /* directory */
//        );
//        return image;
//    }

    public Boolean validation() {
        Boolean val = true;
        if (eventName.getText().length() == 0) {
            val = false;
            eventName.setError("This field is required!");
            eventName.requestFocus();
        }
        if (eventType1.isSelected()==false&&eventType2.isSelected()==false&&eventType3.isSelected()==false&&eventType4.isSelected()==false) {
            val = false;
            showToast("please select one type to continue");
        }


        return val;
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


    private AjaxCallback<JSONObject> getCategoriesCallback() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {
                mProgressDialog.dismiss();
                System.out.println(result);
                Log.d("getCategoriesresponse", result.toString());
                if (result != null) {
                    try {
                        JSONObject output = result.getJSONObject("output");
                        int finalStatus = output.optInt("status");
                        if (finalStatus == 1) {
                            categoryObject = result;
                            mCategoryInfoList = ParseUtility.parseCategoryDetail(result);
                            if (isTypeSelecte) {
                                TypePickerDialog mTypePickerDialog = new TypePickerDialog();
                                Bundle b = new Bundle();
                                b.putString("categoryData", categoryObject.toString());
                                if (type1 && eventType1.getTag() != null) {
                                    b.putString("type", eventType1.getTag().toString());
                                } else if (type2 && eventType2.getTag() != null) {
                                    b.putString("type", eventType2.getTag().toString());
                                } else if (type3 && eventType3.getTag() != null) {
                                    b.putString("type", eventType3.getTag().toString());
                                } else if (type4 && eventType4.getTag() != null) {
                                    b.putString("type", eventType4.getTag().toString());
                                }
                                mTypePickerDialog.setArguments(b);
                                mTypePickerDialog.show(getSupportFragmentManager(), "TypePicker");
                            }
                            isTypeSelecte = false;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            ;
        };
    }


    private void hideLoadingDialog() {
        loadingDialog.dismiss();
        //finish();
    }

    public Calendar showDateTimePickerStart(final TextView h) {
        // TODO Auto-generated method stub
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(StartFromScratch.this);

        final View view = getLayoutInflater().inflate(R.layout.date_and_time_picker_layout, null);
        builder.setView(view);
        final Calendar startDate = Calendar.getInstance();
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
        datePicker.setMinDate(c.getTimeInMillis());

        datePicker.updateDate(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DAY_OF_MONTH));
        final TimePicker timePicker = (TimePicker) view.findViewById(R.id.time_picker);
        timePicker.setCurrentHour(c1.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(c1.get(Calendar.MINUTE));
        final Button ok = (Button) view.findViewById(R.id.datePickerButton);
        final Button remove = (Button) view.findViewById(R.id.datePickerRemove);
        timePicker.setIs24HourView(true);
        // datePicker.setMinDate(System.currentTimeMillis());

        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (datePickerBoolean) {
                    datePicker.setVisibility(View.GONE);
                    timePicker.setVisibility(View.VISIBLE);
                    datePickerBoolean = false;
                } else {
                    datePickerBoolean = true;
                    int eventStartHour = timePicker.getCurrentHour();
                    int eventStartMin = timePicker.getCurrentMinute();
                    int eventStartYear = datePicker.getYear();
                    int eventStartMonth = datePicker.getMonth();
                    int eventStartDay = datePicker.getDayOfMonth();

                    c1.set(eventStartYear, eventStartMonth, eventStartDay, eventStartHour, eventStartMin, 0);
                    String convertedDate = GeneralUtil.convertDate(eventStartYear + "-" + (eventStartMonth + 1) + "-" + eventStartDay + "   " + eventStartHour
                            + ":" + eventStartMin + ":00");
//					h.setText(eventStartYear + "-" + eventStartMonth + "-" + eventStartDay + "   " + eventStartHour
//							+ ":" + eventStartMin);
                    h.setText(convertedDate);
                    datePickerDialog.dismiss();

                }
            }
        });

        builder.setTitle("Set Event date and time");

        datePickerDialog = builder.create();
        datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {


            public void onDismiss(DialogInterface dialog) {
                // TODO Auto-generated method stub
                datePickerBoolean = true;
            }
        });
        datePickerDialog.show();
        return startDate;
    }

    public Calendar showDateTimePickerEnd(final TextView h, final Calendar cL) {
        // TODO Auto-generated method stub
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(StartFromScratch.this);

        final View view = getLayoutInflater().inflate(R.layout.date_and_time_picker_layout, null);
        builder.setView(view);
        final Calendar startDate = Calendar.getInstance();
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
        datePicker.setMinDate(c.getTimeInMillis());
        datePicker.updateDate(c2.get(Calendar.YEAR), (c2.get(Calendar.MONTH)), c2.get(Calendar.DAY_OF_MONTH));
        final TimePicker timePicker = (TimePicker) view.findViewById(R.id.time_picker);
        timePicker.setCurrentHour(c2.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(c2.get(Calendar.MINUTE));
        final Button ok = (Button) view.findViewById(R.id.datePickerButton);
        final Button remove = (Button) view.findViewById(R.id.datePickerRemove);
        timePicker.setIs24HourView(true);
        // datePicker.setMinDate(System.currentTimeMillis());

        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (datePickerBoolean) {
                    datePicker.setVisibility(View.GONE);
                    timePicker.setVisibility(View.VISIBLE);
                    datePickerBoolean = false;
                } else {
                    datePickerBoolean = true;
                    int eventStartHour = timePicker.getCurrentHour();
                    int eventStartMin = timePicker.getCurrentMinute();
                    int eventStartYear = datePicker.getYear();
                    int eventStartMonth = datePicker.getMonth();
                    int eventStartDay = datePicker.getDayOfMonth();

                    c2.set(eventStartYear, eventStartMonth, eventStartDay, eventStartHour, eventStartMin, 0);

                    if (cL.after(c2)) {
                        new android.app.AlertDialog.Builder(StartFromScratch.this).setTitle("Confirmation")
                                .setMessage("Please Enter Valid End Date..").setNeutralButton("Back", null).show();

                    } else {
                        String converDate = GeneralUtil.convertDate(eventStartYear + "-" + (eventStartMonth + 1) + "-" + eventStartDay + "   " + eventStartHour
                                + ":" + eventStartMin + ":00");
//						h.setText(eventStartYear + "-" + eventStartMonth + "-" + eventStartDay + "   " + eventStartHour
//								+ ":" + eventStartMin);
                        h.setText(converDate);
                    }

                    datePickerDialog.dismiss();

                }
            }
        });

        builder.setTitle("Set Event date and time");

        datePickerDialog = builder.create();
        datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                // TODO Auto-generated method stub
                datePickerBoolean = true;
            }
        });
        datePickerDialog.show();
        return startDate;
    }

//    private void postEvent() {
//        AQuery aquery = new AQuery(this);
//        if (GeneralUtil.isNetworkAvailable(this)) {
//            showLoadingDialog();
//            HashMap<String, String> mainJson = new HashMap<String, String>();
//
//            mainJson.put("token", eventInfo.getString(DataHolder.TOKEN, ""));
//            mainJson.put("name", eventName.getText().toString().trim());
//            mainJson.put("description", eventDescription.getText().toString());
//            mainJson.put("location", au_eventLocation.getText().toString());
//            mainJson.put("website", event_Au_email.getText().toString());
//            mainJson.put("eventPhone", eventPhone.getText().toString());
//            mainJson.put("price", eventPrice.getText().toString());
////
//            mainJson.put("start_date",
//                    c1.get(Calendar.YEAR) + "-" + getTime(c1.get(Calendar.MONTH) + 1) + "-"
//                            + getTime(c1.get(Calendar.DAY_OF_MONTH)) + " " + getTime(c1.get(Calendar.HOUR_OF_DAY))
//                            + ":" + getTime(c1.get(Calendar.MINUTE)) + ":00");
//
//
//            mainJson.put("end_date",
//                    c2.get(Calendar.YEAR) + "-" + getTime(c2.get(Calendar.MONTH) + 1) + "-"
//                            + getTime(c2.get(Calendar.DAY_OF_MONTH)) + " " + getTime(c2.get(Calendar.HOUR_OF_DAY))
//                            + ":" + getTime(c2.get(Calendar.MINUTE)) + ":00");
//
//            //      if (isAddEvent) {
////                    mainJson.put("group_id", groupId);
////                }
//            //mainJson.put("category_id", ((CategoryInfo) spinnerCategory.getSelectedItem()).categoryId);
////                    String typeId = "";
////                if (isAddEvent) {
////                    for (int i = 0; i < typeInfoList.size(); i++) {
////                        CategoryInfo mTypeInfo = typeInfoList.get(i);
////                        if (mTypeInfo.typeTitle.equals("Private")) {
////                            typeId = mTypeInfo.typeId;
////                        }
////                    }
////                    mainJson.put("type_id", typeId);
////                } else {
////                    mainJson.put("type_id", ((CategoryInfo) spinnerType.getSelectedItem()).typeId);
////                }
////                for (int i = 0; i < imageList.size(); i++) {
////                    // mainJson.put("image[" + i + "]", CarImageList.get(i));
////                    images.put(imageList.get(i));
////                }
////                mainJson.put("image", images);
//            // mainJson.put("image[1]", images);
//
//            // mainJson.put("event_id", 0);
//            //
//            // mainJson.put("image", images);
//            // mainJson.put("image[1]", images);
//
//            System.out.println(DataHolder.CREATE_EVENT + mainJson);
//            mAqueryTask.sendRequest(DataHolder.CREATE_EVENT, mainJson, JSONObject.class, fetchDataCallback());
//
//        } else {
//            System.out.println(DataHolder.CREATE_EVENT + "no internet");
//            showToast("No internet connection available.");
//        }
//    }
//
//    private AjaxCallback<JSONObject> fetchDataCallback() {
//        return new AjaxCallback<JSONObject>() {
//            public void callback(String url, JSONObject result,
//                                 AjaxStatus status) {
//
//                System.out.println(result);
//                mProgressDialog.dismiss();
//                if (result != null) {
//                    try {
//                        JSONObject object = result.getJSONObject("output");
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//
//                }
//            }
//        };
//    }

    private String getTime(int time) {
        String str = "";
        if (time > 9) {
            str = time + "";
        } else {
            str = "0" + time;
        }
        return str;
    }

    public void setData() {
        eventName.setText(mEventDetail.eventTitle.toUpperCase());
        for (int i = 0; i < mEventDetail.eventImages.size(); i++) {
            if (i == 0) {
                mAqueryTask.setImage(mEventDetail.eventImages.get(0).trim(), circularImage, null);
            } else if (i == 1) {
                mAqueryTask.setImage(mEventDetail.eventImages.get(1).trim(), squareImage1, null);
            } else if (i == 2) {
                mAqueryTask.setImage(mEventDetail.eventImages.get(2).trim(), squareImage2, null);
            } else if (i == 3) {
                mAqueryTask.setImage(mEventDetail.eventImages.get(2).trim(), squareImage3, null);
            } else if (i == 4) {
                mAqueryTask.setImage(mEventDetail.eventImages.get(2).trim(), squareImage4, null);

            }
        }
    }
}