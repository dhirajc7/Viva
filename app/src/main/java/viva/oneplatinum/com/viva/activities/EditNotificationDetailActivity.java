//package viva.oneplatinum.com.viva.activities;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.content.res.Configuration;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Matrix;
//import android.hardware.Camera;
//import android.media.ExifInterface;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.v7.widget.Toolbar;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewTreeObserver;
//import android.view.inputmethod.EditorInfo;
//import android.widget.EditText;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.ScrollView;
//import android.widget.TextView;
//
//import com.androidquery.AQuery;
//import com.androidquery.callback.AjaxCallback;
//import com.androidquery.callback.AjaxStatus;
//import com.androidquery.callback.BitmapAjaxCallback;
//import com.cloudinary.Cloudinary;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//import viva.oneplatinum.com.viva.R;
//import viva.oneplatinum.com.viva.adapters.Hori_ListAdapter;
//import viva.oneplatinum.com.viva.dialogs.CustomGalleryFragment;
//import viva.oneplatinum.com.viva.dialogs.LinkedEventDialog;
//import viva.oneplatinum.com.viva.interfaces.ImageSelectorFromGallery;
//import viva.oneplatinum.com.viva.interfaces.LinkedEventSelectorListener;
//import viva.oneplatinum.com.viva.models.NotificationDetail;
//import viva.oneplatinum.com.viva.models.Notification_Image;
//import viva.oneplatinum.com.viva.models.Notifications;
//import viva.oneplatinum.com.viva.parser.ParseUtility;
//import viva.oneplatinum.com.viva.utils.DataHolder;
//import viva.oneplatinum.com.viva.utils.GeneralUtil;
//import viva.oneplatinum.com.viva.widgets.CircularImageView;
//import viva.oneplatinum.com.viva.widgets.FontIcon;
//import viva.oneplatinum.com.viva.widgets.HorizontalListView;
//import viva.oneplatinum.com.viva.widgets.RobotoBoldTextView;
//import viva.oneplatinum.com.viva.widgets.RobotoLightTextView;
//import viva.oneplatinum.com.viva.widgets.RobotoRegularEditText;
//import viva.oneplatinum.com.viva.widgets.RobotoRegularTextView;
//
///**
// * Created by D-MAX on 4/25/2016.
// */
//public class EditNotificationDetailActivity extends ParentActivity implements SurfaceHolder.Callback, ImageSelectorFromGallery, LinkedEventSelectorListener {
//    RelativeLayout cancel, report, block;
//    RobotoRegularTextView block_permit, reportUs;
//    Hori_ListAdapter hori_ListAdapter;
//    HorizontalListView lv;
//    CircularImageView postUserImage, linkedEventImage;
//    RobotoRegularEditText notificationDescription;
//    RobotoLightTextView linkedEventTitle, linkedEventLocation;
//    NotificationDetail mNotificationDetail;
//    ArrayList<Notification_Image> event;
//    ArrayList<NotificationDetail> list;
//    ArrayList<Notifications> notificationsArrayList;
//    String post_id;
//    Context context;
//    String postDetailResult;
//    JSONArray image = new JSONArray();
//
//    FontIcon key, cameraOpen, gallery, eventOpen, delete;
//    EditText editText;
//    ImageView capture;
//    TextView textView, titleToolbar;
//    String message, lastMessageId = null, userName;
//    String profile_pic_url = "https://lh4.googleusercontent.com/-Ei6KzjrDIT0/AAAAAAAAAAI/AAAAAAAAAGY/PsWxWVS63m0/photo.jpg?sz=50";
//    SimpleDateFormat df;
//    AQuery aq;
//    String now;
//    LinearLayout postContainer;
//    boolean previewing = false;
//    LayoutInflater controlInflater = null;
//    static LinearLayout galleryImageContainer;
//    static Cloudinary cloudinary;
//    static File file;
//    static String images = "";
//    Camera mCamera;
//    static ArrayList<String> imageArray;
//    SurfaceView surfaceView;
//    SurfaceHolder surfaceHolder;
//    Camera.PictureCallback rawCallback;
//    Camera.ShutterCallback shutterCallback;
//    Camera.PictureCallback jpegCallback;
//    Toolbar mToolBar;
//    FontIcon backBtnToolbar, nextBtnToolbar;
//    static FrameLayout imageContainer;
//    TextView usersays;
//    RelativeLayout relLayout, cameraView;
//    CircularImageView userImage;
//    ScrollView mScrollView;
//    static CustomGalleryFragment mCustomGalleryFragment;
//    LinkedEventDialog mLinkedEventDialog;
//    LinearLayout eventLinkView;
//    String linkedEventId = null;
//    Bitmap userImageBitmap;
//
//
//    @Override
//
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.editpostlayout);
//        initViews();
//        post_id = getIntent().getStringExtra("post_id");
//        postDetailResult = getIntent().getStringExtra("postDetailResult");
//        getNotificationDetail(DataHolder.NOTIFICATION_DETAIL);
//        context = this;
//        initToolBar();
//        Log.e("postDetailResult", postDetailResult + "");
//        try {
//            JSONObject result = new JSONObject(postDetailResult);
//            mNotificationDetail = ParseUtility.parseNotificationDetail(result);
//            setData();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        imageArray = new ArrayList<String>();
//        aq = new AQuery(EditNotificationDetailActivity.this);
//        initCloudinary();
//        View imageViewLayout = getLayoutInflater().inflate(
//                R.layout.image_layout, null);
//        lastMessageId = getIntent().getStringExtra("messageId");
//        userName = getIntent().getStringExtra("userName");
//    }
//
//    private void initCloudinary() {
//        Map config = new HashMap();
//        config.put("cloud_name", "dcj2wdnva");
//        config.put("api_key", "197437687815994");
//        config.put("api_secret", "BNWeI5mER5dgcdy_0ewO7m1VF2c");
//        cloudinary = new Cloudinary(config);
//    }
//
//    public void initViews() {
//
//        usersays = (TextView) findViewById(R.id.fr_chattext);
//        userImage = (CircularImageView) findViewById(R.id.fr_profimg);
//        linkedEventImage = (CircularImageView) findViewById(R.id.eventImage);
////        usersays = (RobotoRegularEditText) findViewById(R.id.fr_chattext);
//        linkedEventTitle = (RobotoLightTextView) findViewById(R.id.event_name);
//        linkedEventLocation = (RobotoLightTextView) findViewById(R.id.eventLocation);
//
//        mScrollView = (ScrollView) findViewById(R.id.scrollView);
//        eventLinkView = (LinearLayout) findViewById(R.id.eventLinkView);
//
//
//        mScrollView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mCustomGalleryFragment.isVisible()) {
//                    mCustomGalleryFragment.dismiss();
//                    relLayout.setVisibility(View.GONE);
//                    cameraView.setVisibility(View.GONE);
//                    imageContainer.setVisibility(View.GONE);
//                }
//            }
//        });
//        eventOpen = (FontIcon) findViewById(R.id.eventOpen);
//        cameraView = (RelativeLayout) findViewById(R.id.cameraView);
//        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
//        postContainer = (LinearLayout) findViewById(R.id.postContainer);
//        imageContainer = (FrameLayout) findViewById(R.id.imageContainer);
//        galleryImageContainer = (LinearLayout) findViewById(R.id.galleryImageContainer);
//        gallery = (FontIcon) findViewById(R.id.gallery);
//        imageContainer.removeAllViews();
//
//        surfaceHolder = surfaceView.getHolder();
//        surfaceHolder.addCallback(this);
//        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//
//
//        jpegCallback = new Camera.PictureCallback() {
//            public void onPictureTaken(byte[] data, Camera camera) {
//                FileOutputStream outStream = null;
//                String imagePath;
//                try {
//
//                    Bitmap bitmapPicture = BitmapFactory.decodeByteArray(data, 0, data.length);
//                    Bitmap correctBmp = Bitmap.createBitmap(bitmapPicture, 0, 0, bitmapPicture.getWidth(), bitmapPicture.getHeight(), null, true);
//                    String imgLocation = "/sdcard/viva/", fileName;
//                    OutputStream fOut = null;
//                    Date date = new Date();
//                    File dir = new File(imgLocation);
//                    dir.mkdir();
//                    fileName = "img" + date.getTime() + ".png";
//                    file = new File(dir, fileName);
//
//                    try {
//                        fOut = new FileOutputStream(file);
//                        fOut.write(data);
//                        fOut.close();
//
//                    } catch (FileNotFoundException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                    Bitmap imageBitmap = correctBmp;
//                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
//                    fOut.flush();
//                    fOut.close();
//                    imagePath = imgLocation + fileName;
//                    Log.i("imagepath", imagePath);
//                    final View imageViewLayout = getLayoutInflater().inflate(
//                            R.layout.image_layout, null);
//
//                    imageViewLayout.setLayoutParams(
//                            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
//
//                    ImageView imageView = (ImageView) imageViewLayout.findViewById(R.id.Clickedimage);
//                    final FontIcon deleteIcon = (FontIcon) imageViewLayout.findViewById(R.id.delete);
//                    imageView.setImageBitmap(rotateImage());
//
//                    mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                        @Override
//                        public void onGlobalLayout() {
//                            mScrollView.post(new Runnable() {
//                                public void run() {
//                                    mScrollView.fullScroll(View.FOCUS_DOWN);
//                                }
//                            });
//                        }
//                    });
//                    galleryImageContainer.addView(imageViewLayout);
//                    deleteIcon.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            galleryImageContainer.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    String data = imageArray.get(galleryImageContainer.indexOfChild(imageViewLayout));
//                                    imageArray.remove(data);
//                                    galleryImageContainer.removeView(imageViewLayout);
//                                }
//                            });
//
//                        }
//                    });
//
//                    if (imagePath != null) {
//                        showLoadingDialog();
//                        Upload task = new Upload();
//                        task.execute();
//
//                    }
//                } catch (Exception e) {
//                    Log.i("ImageLoading", "Error in adding image");
//                }
//                showToast("Picture Saved");
//                refreshCamera();
//            }
//        };
//
//
//        relLayout = (RelativeLayout) findViewById(R.id.camerapreview);
//        relLayout.setVisibility(View.GONE);
//        cameraView.setVisibility(View.GONE);
//
//        cameraOpen = (FontIcon) findViewById(R.id.cameraOpen);
//
//        cameraOpen.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                hideSoftKeyboard();
//                imageContainer.setVisibility(View.GONE);
//                cameraView.setVisibility(cameraView.getVisibility() == View.GONE ? View.VISIBLE
//                        : View.GONE);
//                relLayout.setVisibility(cameraView.getVisibility() != View.GONE ? View.VISIBLE
//                        : View.GONE);
//                cameraOpen.setSelected(true);
//                key.setSelected(false);
//                cameraOpen.setSelected(true);
//                gallery.setSelected(false);
//
//
//            }
//        });
//
//        key = (FontIcon) findViewById(R.id.keyboard);
//
//
//        key.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                key.setSelected(true);
//                cameraOpen.setSelected(false);
//                gallery.setSelected(false);
//                relLayout.setVisibility(View.GONE);
//                addDoneinKeyboard();
//            }
//        });
//
//        gallery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                hideSoftKeyboard();
//                gallery.setSelected(true);
//                key.setSelected(false);
//                cameraOpen.setSelected(false);
//                relLayout.setVisibility(View.VISIBLE);
//                imageContainer.setVisibility(View.VISIBLE);
//                cameraView.setVisibility(View.GONE);
//                mCustomGalleryFragment = new CustomGalleryFragment();
//                Bundle b = new Bundle();
//                b.putBoolean("isNewPost", true);
//                mCustomGalleryFragment.setArguments(b);
//                mCustomGalleryFragment.show(getSupportFragmentManager(), "imageGallery");
//
//            }
//        });
//        eventOpen.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                hideSoftKeyboard();
//                eventOpen.setSelected(true);
//                gallery.setSelected(false);
//                key.setSelected(false);
//                cameraOpen.setSelected(false);
//                relLayout.setVisibility(View.GONE);
//                imageContainer.setVisibility(View.GONE);
//                cameraView.setVisibility(View.GONE);
//                mLinkedEventDialog = new LinkedEventDialog();
//                mLinkedEventDialog.show(getSupportFragmentManager(), "events");
//
//            }
//        });
//
//
//        editText = (EditText) findViewById(R.id.newPostEditText);
//
//        editText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                gallery.setSelected(false);
//                key.setSelected(true);
//                cameraOpen.setSelected(false);
//                relLayout.setVisibility(View.GONE);
//            }
//        });
////        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
//        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//
//
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//
//                    message = editText.getText().toString().trim();
//                    usersays.append(message);
//                    Log.d("conversation user image", profile_pic_url);
//
//
//                    editText.setText("");
//                }
//                return false;
//            }
//        });
//    }
//
//    private void initToolBar() {
//        mToolBar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(mToolBar);
//        getSupportActionBar().setHomeButtonEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//
//
//        mToolBar.addView(getCustomView());
//    }
//
//    private View getCustomView() {
//
//        LayoutInflater linflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//        final View customView = linflater.inflate(
//                R.layout.toolbar_item, null);
//        Toolbar.LayoutParams layoutParam = new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
//        customView.setLayoutParams(layoutParam);
//        backBtnToolbar = (FontIcon) customView.findViewById(R.id.backBtnToolbar);
//        titleToolbar = (RobotoBoldTextView) customView.findViewById(R.id.titleToolbar);
//        nextBtnToolbar = (FontIcon) customView.findViewById(R.id.nextBtnToolbar);
//
//        titleToolbar.setText("NOTIFICATIONS");
//        nextBtnToolbar.setText("s");
//        nextBtnToolbar.setTextColor(getResources().getColor(R.color.red));
//        nextBtnToolbar.setVisibility(View.VISIBLE);
//        nextBtnToolbar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                updatePost(DataHolder.EDIT_POST);
//            }
//        });
//
//        titleToolbar.setTextColor(getResources().getColor(R.color.txt_color_default));
//        backBtnToolbar.setText("j");
//        backBtnToolbar.setTextColor(getResources().getColor(R.color.red));
//        backBtnToolbar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//
//
//        return customView;
//    }
//
//    public void getNotificationDetail(String url) {
//        if (isNetworkAvailable()) {
//            Log.e("getCategoriesresponse", "getCategoriesresponse");
//            HashMap<String, String> params = new HashMap<String, String>();
//            params.put("token",  mVivaApplicatiion.getUserData(DataHolder.TOKEN));
//            params.put("post_id", post_id.toString());
//            mAqueryTask.sendRequest(url, params, JSONObject.class, getNotificationDetailCallback());
//
//        }
//    }
//
//    public void updatePost(String url) {
//        if (isNetworkAvailable()) {
//            Log.e("getCategoriesresponse", "getCategoriesresponse");
//            HashMap<String, String> params = new HashMap<String, String>();
//            params.put("token",  mVivaApplicatiion.getUserData(DataHolder.TOKEN));
//            params.put("post_id", post_id.toString());
//            params.put("post", usersays.getText().toString());
//            JSONArray imagesUrl = new JSONArray();
//            if (imageArray.size() > 0) {
//                for (int i = 0; i < imageArray.size(); i++) {
//                    imagesUrl.put(imageArray.get(i));
//                }
//                params.put("image", imagesUrl.toString());
//            }
//            params.put("image", image.toString());
//            Log.d("params", params.toString());
//            mAqueryTask.sendRequest(url, params, JSONObject.class, getUpdatePostCallback());
//
//        }
//    }
//
//    private AjaxCallback<JSONObject> getUpdatePostCallback() {
//        return new AjaxCallback<JSONObject>() {
//            public void callback(String url, JSONObject result,
//                                 AjaxStatus status) {
//
//                System.out.println(result);
//                Log.e("getCategoriesresponse", result.toString());
////                mProgressDialog.dismiss();
//                hideLoadingDialog();
//                if (result != null) {
//                    try {
//                        JSONObject outpuJsonObject = result.getJSONObject("output");
//                        int userStatus = outpuJsonObject.optInt("status");
//                        if (userStatus == 1) {
////                            mNotificationDetail = ParseUtility.parseNotificationDetail(result);
////                            setData();
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            ;
//        };
//    }
//
//    private AjaxCallback<JSONObject> getNotificationDetailCallback() {
//        return new AjaxCallback<JSONObject>() {
//            public void callback(String url, JSONObject result,
//                                 AjaxStatus status) {
//
//                System.out.println(result);
//                Log.e("getCategoriesresponse", result.toString());
////                mProgressDialog.dismiss();
//
//                if (result != null) {
//                    try {
//                        JSONObject outpuJsonObject = result.getJSONObject("output");
//                        int userStatus = outpuJsonObject.optInt("status");
//                        if (userStatus == 1) {
//                            mNotificationDetail = ParseUtility.parseNotificationDetail(result);
//                            setData();
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            ;
//        };
//    }
//
//    public void setData() {
//
//
//        linkedEventTitle.setText(mNotificationDetail.eventName.toUpperCase());
//        usersays.setText(mNotificationDetail.post);
//            linkedEventLocation.setText(mNotificationDetail.location);
////        if (mEventDetail.eventStartdate != null) {
////            if (mEventDetail.eventStartdate.length() > 0) {
////                String dateandtime = GeneralUtil.convertDate(mEventDetail.eventStartdate);
////                String[] date = dateandtime.split(",");
////                eventDate.setText(date[0]);
////                eventTime.setText(date[1].trim());
////            }
////        }
//        mAqueryTask.setImage(mNotificationDetail.userProfile.trim(), userImage, null);
//        mAqueryTask.setImage(mNotificationDetail.eventImage.trim(), linkedEventImage, null);
//        galleryImageContainer.removeAllViews();
//        for (int i = 0; i < mNotificationDetail.image.size(); i++) {
//            Log.d("image", mNotificationDetail.image.toString());
//            final View imageViewLayout = getLayoutInflater().inflate(
//                    R.layout.image_layout, null);
//            imageViewLayout.setLayoutParams(
//                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
//
//            ImageView imageView = (ImageView) imageViewLayout.findViewById(R.id.Clickedimage);
//            mAqueryTask.setImage(mNotificationDetail.image.get(i), imageView, null);
//            galleryImageContainer.addView(imageViewLayout);
//        }
//
//    }
//
//    public void captureImage(View v) throws IOException {
//        mCamera.takePicture(null, null, jpegCallback);
//    }
//
//    public void refreshCamera() {
//        if (surfaceHolder.getSurface() == null) {
//            return;
//        }
//
//        try {
//            mCamera.stopPreview();
//        } catch (Exception e) {
//        }
//
//        try {
//            mCamera.setPreviewDisplay(surfaceHolder);
//            mCamera.startPreview();
//        } catch (Exception e) {
//        }
//    }
//
//
//    //    public void surfaceCreated(SurfaceHolder holder) {
////        try {
////            mCamera = Camera.open();
////        } catch (RuntimeException e) {
////            System.err.println(e);
////            return;
////        }
////        Camera.Parameters param;
////        param = mCamera.getParameters();
////        param.setPreviewSize(352, 288);
////        mCamera.setParameters(param);
////        try {
////            mCamera.setPreviewDisplay(surfaceHolder);
////            mCamera.startPreview();
////        } catch (Exception e) {
////            System.err.println(e);
////            return;
////        }
////    }
//    @Override
//    public void surfaceCreated(SurfaceHolder surfaceHolder) {
//        try {
//            mCamera = Camera.open();
//            Camera.Parameters parameters = mCamera.getParameters();
//            mCamera.setPreviewDisplay(surfaceHolder);
//            parameters.setPreviewSize(surfaceView.getWidth(), surfaceView.getHeight());
//            mCamera.setParameters(parameters);
//            mCamera.startPreview();
//        } catch (RuntimeException e) {
//            System.err.println(e);
//            return;
//        } catch (IOException e) {
//            // left blank for now
//        }
//
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder surfaceHolder, int format,
//                               int width, int height) {
//
//        try {
//            Camera.Parameters parameters = mCamera.getParameters();
//            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
//                parameters.set("orientation", "portrait");
//                mCamera.setDisplayOrientation(90);
//                parameters.setRotation(90);
//                mCamera.setPreviewDisplay(surfaceHolder);
//                mCamera.startPreview();
//            } else {
//                // This is an undocumented although widely known feature
//                parameters.set("orientation", "landscape");
//                // For Android 2.2 and above
//                mCamera.setDisplayOrientation(0);
//                // Uncomment for Android 2.0 and above
//                parameters.setRotation(0);
//            }
//            mCamera.setPreviewDisplay(surfaceHolder);
//            mCamera.startPreview();
//
//        } catch (IOException e) {
//            // left blank for now
//        }
//    }
//
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        mCamera.stopPreview();
//        mCamera.release();
//        mCamera = null;
//    }
//
//    private void upload() {
//        showLoadingDialog();
//        Upload task = new Upload();
//        task.execute();
//    }
//
//    @Override
//    public void image(Bitmap image, String imagePath) {
//        file = new File(imagePath);
//        mCustomGalleryFragment.dismiss();
//        mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                mScrollView.post(new Runnable() {
//                    public void run() {
//                        mScrollView.fullScroll(View.FOCUS_DOWN);
//                    }
//                });
//            }
//        });
//        final View imageViewLayout = getLayoutInflater().inflate(
//                R.layout.image_layout, null);
//        imageViewLayout.setLayoutParams(
//                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
//
//        ImageView imageView = (ImageView) imageViewLayout.findViewById(R.id.Clickedimage);
//        final FontIcon deleteIcon = (FontIcon) imageViewLayout.findViewById(R.id.delete);
//        imageView.setImageBitmap(rotateImage());
//        imageContainer.setVisibility(View.GONE);
//        relLayout.setVisibility(View.GONE);
//        galleryImageContainer.addView(imageViewLayout);
//        deleteIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                galleryImageContainer.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        String data = imageArray.get(galleryImageContainer.indexOfChild(imageViewLayout));
//                        galleryImageContainer.removeView(imageViewLayout);
//                        imageArray.remove(data);
//                    }
//                });
//            }
//        });
//        if (imagePath != null) {
//            upload();
//
//        }
//    }
//
//    @Override
//    public void event(String eventName, String eventLocation, String eventImage, String eventId, String Date) {
//
//    }
//
//    @Override
//    public void event(String eventName, String eventLocation, String eventImage, String eventId) {
//
//        linkedEventTitle.setText(eventName);
//        linkedEventLocation.setText(eventLocation);
//        final Bitmap defaultImage = BitmapFactory.decodeResource(getApplicationContext()
//                .getResources(), R.drawable.ic_launcher);
//        userImageBitmap = GeneralUtil.getRoundedBitmap(defaultImage);
//        // aq.id(userImage).image(profileInfo.myPhoto);
//        if (eventImage.length() > 0) {
//            aq.id(linkedEventImage).image(eventImage, true, true, 400, 0,
//                    new BitmapAjaxCallback() {
//                        protected void callback(String url, ImageView iv,
//                                                Bitmap bm, AjaxStatus status) {
//                            if (bm != null) {
//                                userImageBitmap = GeneralUtil.getRoundedBitmap(bm);
//
//                            } else {
//                                userImageBitmap = GeneralUtil
//                                        .getRoundedBitmap(defaultImage);
//                            }
//                            linkedEventImage.setImageBitmap(userImageBitmap);
//                        }
//
//                        ;
//                    });
//        }
//        linkedEventId = eventId;
//        mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                mScrollView.post(new Runnable() {
//                    public void run() {
//                        mScrollView.fullScroll(View.FOCUS_DOWN);
//                    }
//                });
//            }
//        });
//        eventLinkView.setVisibility(View.VISIBLE);
//    }
//
//    private static class Upload extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... urls) {
//            String response = "";
//
//            try {
//
//                JSONObject result = cloudinary.uploader().upload(file,
//                        Cloudinary.emptyMap());
//                Log.v("data", result.toString());
//                try {
//                    response = result.getString("url");
//                } catch (JSONException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            return response;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            // TODO Auto-generated method stub
//            super.onPostExecute(result);
//            hideLoadingDialog();
//            images = result;
//            imageArray.add(result);
//        }
//
//    }
//
//    public Bitmap rotateImage() {
//        Bitmap scaled = null;
//        try {
//            FileInputStream fis = new FileInputStream(file);
//            BitmapFactory.Options bounds = new BitmapFactory.Options();
//            bounds.inJustDecodeBounds = true;
//            Bitmap bm = BitmapFactory.decodeStream(fis);
//
//            ExifInterface exif = new ExifInterface(file.getAbsolutePath());
//
//            String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
////            Log.d("TAG_orientation EXIFDATA", orientString);
//            int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
//            int rotationAngle = 0;
//            if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
//                rotationAngle = 90;
//            if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
//                rotationAngle = 180;
//            if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
//                rotationAngle = 270;
//            // Rotate Bitmap
//            Matrix matrix = new Matrix();
//            matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
//            Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
//            int height = rotatedBitmap.getHeight(), scaledheight, scaledwidth;
//            int width = rotatedBitmap.getWidth();
//            float aspect;
//
//            if (width < height) {// portrait
//                aspect = ((float) height / (float) width);
//                scaledwidth = 400;
//                scaledheight = (int) (400 * aspect);
//            } else {// landscape
//                aspect = ((float) width / (float) height);
//                scaledheight = 400;
//                scaledwidth = (int) (400 * aspect);
//            }
//            scaled = Bitmap.createScaledBitmap(rotatedBitmap, scaledwidth, scaledheight, false);
//            // impic.setImageURI(Uri.fromFile(photoFile));
//
//            // create a file to write bitmap data
//            File f = new File(getCacheDir(), "scaledBitmap");
//            f.createNewFile();
//
//            // Convert bitmap to byte array
//            Bitmap bitmap = scaled;
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /* ignored for PNG */, bos);
//            byte[] bitmapdata = bos.toByteArray();
//
//            // write the bytes in file
//            FileOutputStream fos = new FileOutputStream(f);
//            fos.write(bitmapdata);
//            fos.flush();
//            fos.close();
//
//            // Equate the file to photofile
//            file = f;
//
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            Log.e("photofile io error", "EXif not done");
//        }
//        return scaled;
//    }
//
//    static Dialog loadingDialog;
//
//    public void showLoadingDialog() {
//        loadingDialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
//        View view = LayoutInflater.from(this).inflate(R.layout.layout_loading, null);
//        loadingDialog.addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        loadingDialog.setCanceledOnTouchOutside(false);
//        loadingDialog.setCancelable(false);
//        loadingDialog.show();
//
//    }
//
//    private static void hideLoadingDialog() {
//        loadingDialog.dismiss();
//    }
//}
