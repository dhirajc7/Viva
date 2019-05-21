package viva.oneplatinum.com.viva.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.cloudinary.Cloudinary;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.dialogs.CustomGalleryFragment;
import viva.oneplatinum.com.viva.dialogs.LinkedEventDialog;
import viva.oneplatinum.com.viva.interfaces.ImageSelectorFromGallery;
import viva.oneplatinum.com.viva.interfaces.LinkedEventSelectorListener;
import viva.oneplatinum.com.viva.models.NotificationDetail;
import viva.oneplatinum.com.viva.models.UserDetail;
import viva.oneplatinum.com.viva.parser.ParseUtility;
import viva.oneplatinum.com.viva.utils.DataHolder;
import viva.oneplatinum.com.viva.utils.GeneralUtil;
import viva.oneplatinum.com.viva.widgets.CircularImageView;
import viva.oneplatinum.com.viva.widgets.FontIcon;
import viva.oneplatinum.com.viva.widgets.RobotoBoldTextView;
import viva.oneplatinum.com.viva.widgets.RobotoRegularEditText;

/**
 * Created by Dell on 3/27/2016.
 */
public class NewPostActivity extends ParentActivity implements SurfaceHolder.Callback, ImageSelectorFromGallery, LinkedEventSelectorListener {

    FontIcon key, cameraOpen, gallery, eventOpen, delete;
    EditText editText;
    ImageView capture;
    TextView textView, titleToolbar;
    String message, lastMessageId = null, userName;
    String profile_pic_url = "";
    SimpleDateFormat df;
    AQuery aq;
    String now;
    LinearLayout postContainer;
    boolean previewing = false;
    LayoutInflater controlInflater = null;
    static LinearLayout galleryImageContainer;
    static Cloudinary cloudinary;
    static File file;
    static String images = "";
    Camera mCamera;
    static ArrayList<String> imageArray;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Camera.PictureCallback rawCallback;
    Camera.ShutterCallback shutterCallback;
    Camera.PictureCallback jpegCallback;
    Toolbar mToolBar;
    FontIcon backBtnToolbar, nextBtnToolbar;
    static FrameLayout imageContainer;
    RobotoRegularEditText usersays;
    RelativeLayout relLayout, cameraView;
    CircularImageView userImage;
    ScrollView mScrollView;
    static CustomGalleryFragment mCustomGalleryFragment;
    LinkedEventDialog mLinkedEventDialog;
    LinearLayout eventLinkView;
    TextView linkedEventTitle, linkedEventLocation;
    ImageView linkedEventImage;
    String linkedEventId = null;
    Bitmap userImageBitmap;
    String post_id, postDetailResult;
    Boolean isFromNotification;
    UserDetail mUserDetail;
    NotificationDetail mNotificationDetail;
    Boolean hasEvent=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_post);
        post_id = getIntent().getStringExtra("post_id");
        postDetailResult= getIntent().getStringExtra("postDetailResult");
        isFromNotification = getIntent().getBooleanExtra("isFromNotification",false);

        imageArray = new ArrayList<String>();
        aq = new AQuery(NewPostActivity.this);
        profile_pic_url=mVivaApplicatiion.getUserData(DataHolder.USERIMAGE);
        initCloudinary();
        initView();
        View imageViewLayout = getLayoutInflater().inflate(
                R.layout.image_layout, null);
        lastMessageId = getIntent().getStringExtra("messageId");
        userName = getIntent().getStringExtra("userName");
        initToolbar();

        if(isFromNotification) {
            try {
                JSONObject result = new JSONObject(postDetailResult);
                mNotificationDetail = ParseUtility.parseNotificationDetail(result);
                setData();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public static void setEventImage(Bitmap bitmap, String path) {
//        file = new File(path);
//        mCustomGalleryFragment.dismiss();
//
////
////                    imageViewLayout.setLayoutParams(
////                            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT));
//
//        ImageView imageView = (ImageView) imageViewLayout.findViewById(R.id.Clickedimage);
//        final FontIcon deleteIcon = (FontIcon) imageViewLayout.findViewById(R.id.delete);
//        imageView.setImageBitmap(bitmap);
//        galleryImageContainer.addView(imageViewLayout);
//        deleteIcon.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    imageContainer.removeView(imageViewLayout);
//                }
//            });
//            if (path != null) {
//                upload();
//
//            }

    }

    private void initCloudinary() {
        Map config = new HashMap();
        config.put("cloud_name", "dcj2wdnva");
        config.put("api_key", "197437687815994");
        config.put("api_secret", "BNWeI5mER5dgcdy_0ewO7m1VF2c");
        cloudinary = new Cloudinary(config);
    }


    private void initView() {
        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        eventLinkView = (LinearLayout) findViewById(R.id.eventLinkView);
        linkedEventTitle = (TextView) findViewById(R.id.event_name);
        linkedEventLocation = (TextView) findViewById(R.id.eventLocation);
        linkedEventImage = (ImageView) findViewById(R.id.eventImage);


        mScrollView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCustomGalleryFragment.isVisible()) {
                    mCustomGalleryFragment.dismiss();
                    relLayout.setVisibility(View.GONE);
                    cameraView.setVisibility(View.GONE);
                    imageContainer.setVisibility(View.GONE);
                }
            }
        });
        eventOpen = (FontIcon) findViewById(R.id.eventOpen);
        cameraView = (RelativeLayout) findViewById(R.id.cameraView);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        postContainer = (LinearLayout) findViewById(R.id.postContainer);
        imageContainer = (FrameLayout) findViewById(R.id.imageContainer);
        galleryImageContainer = (LinearLayout) findViewById(R.id.galleryImageContainer);
        gallery = (FontIcon) findViewById(R.id.gallery);
        imageContainer.removeAllViews();
        usersays = (RobotoRegularEditText) findViewById(R.id.fr_chattext);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(usersays, InputMethodManager.SHOW_IMPLICIT);

        userImage = (CircularImageView) findViewById(R.id.fr_profimg);
        aq.id(userImage).image(profile_pic_url);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        jpegCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {

                FileOutputStream outStream = null;
                String imagePath;
                Bitmap bm = null;



//                if (android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.JELLY_BEAN_MR1){
//                    camera.enableShutterSound(false);
//
//                }
//                else{
//                    AudioManager audio= (AudioManager)this.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
//                    currentVolume=audio.getStreamVolume(AudioManager.STREAM_SYSTEM);
//                    audio.setStreamVolume(AudioManager.STREAM_SYSTEM, 0,   AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
//                    MediaPlayer media= MediaPlayer.create(SecondCamera.this, R.raw.camera_click);
//                    media.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
//                    isVolumeChanged=true;
//                }
                try {
                    if (data != null) {
                        int screenWidth = surfaceView.getWidth();
                        int screenHeight = surfaceView.getHeight();
                        bm = BitmapFactory.decodeByteArray(data, 0, (data != null) ? data.length : 0);
                        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                            // Notice that width and height are reversed
                            Bitmap scaled = Bitmap.createScaledBitmap(bm, screenHeight, screenWidth, true);
                            int w = scaled.getWidth();
                            int h = scaled.getHeight();
                            // Setting post rotate to 90
                            Matrix mtx = new Matrix();
                            mtx.postRotate(90);
                            // Rotating Bitmap
                            bm = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, true);
                            int height = bm.getHeight(), scaledheight, scaledwidth;
                            int width = bm.getWidth();
                            float aspect;
                            if (width < height) {// portrait
                                aspect = ((float) height / (float) width);
                                scaledwidth = 400;
                                scaledheight = (int) (400 * aspect);
                            } else {// landscape
                                aspect = ((float) width / (float) height);
                                scaledheight = 400;
                                scaledwidth = (int) (400 * aspect);
                            }
                            scaled = Bitmap.createScaledBitmap(bm, scaledwidth, scaledheight, false);
                            bm=scaled;
                        } else {// LANDSCAPE MODE
                            //No need to reverse width and height
                            int height = bm.getHeight(), scaledheight, scaledwidth;
                            int width = bm.getWidth();
                            float aspect;
                            if (width < height) {// portrait
                                aspect = ((float) height / (float) width);
                                scaledwidth = 400;
                                scaledheight = (int) (400 * aspect);
                            } else {// landscape
                                aspect = ((float) width / (float) height);
                                scaledheight = 400;
                                scaledwidth = (int) (400 * aspect);
                            }
                            Bitmap scaled = Bitmap.createScaledBitmap(bm, scaledwidth, scaledheight, true);
                            bm = scaled;
                        }
                    }
//                    Bitmap bitmapPicture = BitmapFactory.decodeByteArray(data, 0, data.length);
//                    Bitmap correctBmp = Bitmap.createBitmap(bitmapPicture, 0, 0, bitmapPicture.getWidth(), bitmapPicture.getHeight(), null, true);
                    String imgLocation = "/sdcard/viva/", fileName;
                    OutputStream fOut = null;
                    Date date = new Date();
                    File dir = new File(imgLocation);
                    dir.mkdir();
                    fileName = "img" + date.getTime() + ".png";
                    file = new File(dir, fileName);
                    try {
                        fOut = new FileOutputStream(file);
                        ExifInterface exif = new ExifInterface(file.toString());
                        Log.d("EXIF value", exif.getAttribute(ExifInterface.TAG_ORIENTATION));
//                        if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")){
//                            bm= rotate(realImage, 90);
//                        } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")){
//                            realImage= rotate(realImage, 270);
//                        } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")){
//                            realImage= rotate(realImage, 180);
//                        } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("0")){
//                            realImage= rotate(realImage, 90);
//                        }
                        Bitmap imageBitmap = bm;
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        imageBitmap.compress(Bitmap.CompressFormat.PNG, 85, stream);
                        byte[] byteArray = stream.toByteArray();
                        fOut.write(byteArray);
                        fOut.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    fOut.flush();
                    fOut.close();
                    imagePath = imgLocation + fileName;
                    Log.i("imagepath", imagePath);
                    final View imageViewLayout = getLayoutInflater().inflate(
                            R.layout.image_layout, null);
                    imageViewLayout.setLayoutParams(
                            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    ImageView imageView = (ImageView) imageViewLayout.findViewById(R.id.Clickedimage);
                    final FontIcon deleteIcon = (FontIcon) imageViewLayout.findViewById(R.id.delete);
                    imageView.setImageBitmap(bm);
                    mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            mScrollView.post(new Runnable() {
                                public void run() {
                                    mScrollView.fullScroll(View.FOCUS_DOWN);
                                }
                            });
                        }
                    });
                    galleryImageContainer.addView(imageViewLayout);
                    deleteIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            galleryImageContainer.post(new Runnable() {
                                @Override
                                public void run() {
                                    String data = imageArray.get(galleryImageContainer.indexOfChild(imageViewLayout));
                                    imageArray.remove(data);
                                    galleryImageContainer.removeView(imageViewLayout);
                                }
                            });
                        }
                    });
                    if (imagePath != null) {
                        showLoadingDialog();
                        Upload task = new Upload();
                        task.execute();
                    }
                } catch (Exception e) {
                    Log.i("ImageLoading", "Error in adding image");
                }

                showToast("Picture Saved");

                refreshCamera();


                hideSoftKeyboardAuto();
                hideSoftKeyboardA();
                // key.setSelected(false);
            }
        };


        relLayout = (RelativeLayout) findViewById(R.id.camerapreview);
        relLayout.setVisibility(View.GONE);
        cameraView.setVisibility(View.GONE);

        cameraOpen = (FontIcon) findViewById(R.id.cameraOpen);

        cameraOpen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                imageContainer.setVisibility(View.GONE);
                cameraView.setVisibility(cameraView.getVisibility() == View.GONE ? View.VISIBLE
                        : View.GONE);
                relLayout.setVisibility(cameraView.getVisibility() != View.GONE ? View.VISIBLE
                        : View.GONE);
                cameraOpen.setSelected(true);
                key.setSelected(false);
                cameraOpen.setSelected(true);
                gallery.setSelected(false);


            }
        });

        key = (FontIcon) findViewById(R.id.keyboard);


        key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                key.setSelected(true);
                cameraOpen.setSelected(false);
                gallery.setSelected(false);
                relLayout.setVisibility(View.GONE);
                addDoneinKeyboard();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                imageContainer.setVisibility(imageContainer.getVisibility() == View.GONE ? View.VISIBLE
//                        : View.GONE);
                relLayout.setVisibility(galleryImageContainer.getVisibility() != View.GONE ? View.VISIBLE
                        : View.GONE);
                gallery.setSelected(true);
                //key.setSelected(false);
                cameraOpen.setSelected(false);
                relLayout.setVisibility(View.VISIBLE);
                imageContainer.setVisibility(View.VISIBLE);
                cameraView.setVisibility(View.GONE);
                mCustomGalleryFragment = new CustomGalleryFragment();
                Bundle b = new Bundle();
                b.putBoolean("isNewPost", true);
                mCustomGalleryFragment.setArguments(b);
                mCustomGalleryFragment.show(getSupportFragmentManager(), "imageGallery");

            }
        });
        eventOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                eventOpen.setSelected(true);
                gallery.setSelected(false);
                key.setSelected(false);
                cameraOpen.setSelected(false);
                relLayout.setVisibility(View.GONE);
                imageContainer.setVisibility(View.GONE);
                cameraView.setVisibility(View.GONE);
                mLinkedEventDialog = new LinkedEventDialog();
                hasEvent=true;
                mLinkedEventDialog.show(getSupportFragmentManager(), "events");

            }
        });


        editText = (EditText) findViewById(R.id.newPostEditText);

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gallery.setSelected(false);
                key.setSelected(true);
                cameraOpen.setSelected(false);
                relLayout.setVisibility(View.GONE);
            }
        });
//        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {


            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    message = editText.getText().toString().trim();
                    usersays.append(message);
                    Log.d("conversation user image", profile_pic_url);


                    editText.setText("");
                }
                return false;
            }
        });

    }


    public void captureImage(View v) throws IOException {
        mCamera.takePicture( new Camera.ShutterCallback() { @Override public void onShutter() { } },
                new Camera.PictureCallback() { @Override public void onPictureTaken(byte[] data, Camera camera) { } },
                jpegCallback);
        hideSoftKeyboardA();
        hideSoftKeyboardAuto();
        hideSoftKeyboard();
    }

    public void refreshCamera() {
        if (surfaceHolder.getSurface() == null) {
            return;
        }

        try {
            mCamera.stopPreview();
        } catch (Exception e) {
        }

        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        } catch (Exception e) {
        }
    }


    //    public void surfaceCreated(SurfaceHolder holder) {
//        try {
//            mCamera = Camera.open();
//        } catch (RuntimeException e) {
//            System.err.println(e);
//            return;
//        }
//        Camera.Parameters param;
//        param = mCamera.getParameters();
//        param.setPreviewSize(352, 288);
//        mCamera.setParameters(param);
//        try {
//            mCamera.setPreviewDisplay(surfaceHolder);
//            mCamera.startPreview();
//        } catch (Exception e) {
//            System.err.println(e);
//            return;
//        }
//    }
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            mCamera = Camera.open();
            Camera.Parameters parameters = mCamera.getParameters();
            mCamera.setPreviewDisplay(surfaceHolder);
            parameters.setPreviewSize(dpToPx(256), dpToPx(256));
            mCamera.setParameters(parameters);
            setCameraDisplayOrientation(NewPostActivity.this, Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);
            mCamera.startPreview();
        } catch (RuntimeException e) {
            System.err.println(e);
            return;
        } catch (IOException e) {
            // left blank for now
        }



    }
    public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format,
                               int width, int height) {

        try {
            Camera.Parameters parameters = mCamera.getParameters();
            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                parameters.set("orientation", "portrait");
                mCamera.setDisplayOrientation(90);
                parameters.setRotation(90);
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.startPreview();
            } else {
                // This is an undocumented although widely known feature
                parameters.set("orientation", "landscape");
                // For Android 2.2 and above
                mCamera.setDisplayOrientation(0);
                // Uncomment for Android 2.0 and above
                parameters.setRotation(0);
            }
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();

        } catch (IOException e) {
            // left blank for now
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    private void upload() {
        showLoadingDialog();
        Upload task = new Upload();
        task.execute();
    }

    @Override
    public void image(Bitmap image, String imagePath) {
        file = new File(imagePath);
        mCustomGalleryFragment.dismiss();
        mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mScrollView.post(new Runnable() {
                    public void run() {
                        mScrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        });
        final View imageViewLayout = getLayoutInflater().inflate(
                R.layout.image_layout, null);
        imageViewLayout.setLayoutParams(
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

        ImageView imageView = (ImageView) imageViewLayout.findViewById(R.id.Clickedimage);
        final FontIcon deleteIcon = (FontIcon) imageViewLayout.findViewById(R.id.delete);
        imageView.setImageBitmap(rotateImage());
        imageContainer.setVisibility(View.GONE);
        relLayout.setVisibility(View.GONE);
        galleryImageContainer.addView(imageViewLayout);
        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryImageContainer.post(new Runnable() {
                    @Override
                    public void run() {
                        String data = imageArray.get(galleryImageContainer.indexOfChild(imageViewLayout));
                        galleryImageContainer.removeView(imageViewLayout);
                        imageArray.remove(data);
                    }
                });
            }
        });
        if (imagePath != null) {
            upload();
            hideSoftKeyboard();
        }
    }

    @Override
    public void event(String eventName, String eventLocation, String eventImage, String eventId) {

        linkedEventTitle.setText(eventName);
        linkedEventLocation.setText(eventLocation);
        final Bitmap defaultImage = BitmapFactory.decodeResource(getApplicationContext()
                .getResources(), R.drawable.ic_launcher);
        userImageBitmap = GeneralUtil.getRoundedBitmap(defaultImage);
        // aq.id(userImage).image(profileInfo.myPhoto);
        if (eventImage.length() > 0) {
            aq.id(linkedEventImage).image(eventImage, true, true, 400, 0,
                    new BitmapAjaxCallback() {
                        protected void callback(String url, ImageView iv,
                                                Bitmap bm, AjaxStatus status) {
                            if (bm != null) {
                                userImageBitmap = GeneralUtil.getRoundedBitmap(bm);

                            } else {
                                userImageBitmap = GeneralUtil
                                        .getRoundedBitmap(defaultImage);
                            }
                            linkedEventImage.setImageBitmap(userImageBitmap);
                        }

                        ;
                    });
        }
        linkedEventId = eventId;
        mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mScrollView.post(new Runnable() {
                    public void run() {
                        mScrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        });
        eventLinkView.setVisibility(View.VISIBLE);
    }

    private static class Upload extends AsyncTask<String, Void, String> {
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
            imageArray.add(result);
        }

    }

    public Bitmap rotateImage() {
        Bitmap scaled = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            Bitmap bm = BitmapFactory.decodeStream(fis);

            ExifInterface exif = new ExifInterface(file.getAbsolutePath());

            String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
//            Log.d("TAG_orientation EXIFDATA", orientString);
            int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
            int rotationAngle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
                rotationAngle = 90;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
                rotationAngle = 180;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
                rotationAngle = 270;
            // Rotate Bitmap
            Matrix matrix = new Matrix();
            matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            int height = rotatedBitmap.getHeight(), scaledheight, scaledwidth;
            int width = rotatedBitmap.getWidth();
            float aspect;

            if (width < height) {// portrait
                aspect = ((float) height / (float) width);
                scaledwidth = 400;
                scaledheight = (int) (400 * aspect);
            } else {// landscape
                aspect = ((float) width / (float) height);
                scaledheight = 400;
                scaledwidth = (int) (400 * aspect);
            }
            scaled = Bitmap.createScaledBitmap(rotatedBitmap, scaledwidth, scaledheight, false);
            // impic.setImageURI(Uri.fromFile(photoFile));

            // create a file to write bitmap data
            File f = new File(getCacheDir(), "scaledBitmap");
            f.createNewFile();

            // Convert bitmap to byte array
            Bitmap bitmap = scaled;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /* ignored for PNG */, bos);
            byte[] bitmapdata = bos.toByteArray();

            // write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

            // Equate the file to photofile
            file = f;

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("photofile io error", "EXif not done");
        }
        return scaled;
    }

    static Dialog loadingDialog;

    private void showLoadingDialog() {
        loadingDialog = new Dialog(NewPostActivity.this,
                android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        View view = LayoutInflater.from(this).inflate(R.layout.loading_layout,
                null);
        loadingDialog.addContentView(view, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(true);
        loadingDialog.show();

    }

    private static void hideLoadingDialog() {
        loadingDialog.dismiss();
    }

    public void getNewPost() {
        if (isNetworkAvailable()) {
            Log.d("checkNewPost", "check edit event");
            mProgressDialog.setMessage("Posting data..");
            mProgressDialog.show();
            Map<String, Object> params = null;
//            showLoadingDialog();
            // displayInfo(DataHolder.DISPLAY_INFO_LOADING, null);
            try {
                JSONObject mainJson = new JSONObject();
                mainJson.put("token",  mVivaApplicatiion.getUserData(DataHolder.TOKEN));
                mainJson.put("event_id", linkedEventId);
                mainJson.put("post", usersays.getText().toString());
                JSONArray imagesUrl = new JSONArray();
                if (imageArray.size() > 0) {
                    for (int i = 0; i < imageArray.size(); i++) {
                        imagesUrl.put(imageArray.get(i));
                    }
                    mainJson.put("image", imagesUrl);
                }

                StringEntity s = new StringEntity(mainJson.toString(),
                        HTTP.UTF_8);
                params = new HashMap<String, Object>();
                params.put(AQuery.POST_ENTITY, s);
                Log.e("mainjson", mainJson.toString());
                aq.ajax(DataHolder.ADD_POST, params, JSONObject.class, getNewPostCallback());
            } catch (Exception e) {
            }
        }
    }

    private AjaxCallback<JSONObject> getNewPostCallback() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);
                Log.d("getNewPostResonse", result.toString());
                mProgressDialog.dismiss();
                if (result != null) {
                    try {
                        JSONObject outputJsonObject = result.getJSONObject("output");
                        int userStatus = outputJsonObject.optInt("status");
                        String message = outputJsonObject.optString("message");
                        if (userStatus == 1) {
                            showToast(message);
                            finish();
                        } else {
                            showToast(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            private void setData() {

            }

        }.header("Content-Type", "application/json");
    }
    public void updatePost() {
        if (isNetworkAvailable()) {
            Log.d("checkNewPost", "check");
            mProgressDialog.setMessage("Updating post..");
            mProgressDialog.show();
            Map<String, Object> params = null;
//            showLoadingDialog();
            // displayInfo(DataHolder.DISPLAY_INFO_LOADING, null);
            try {
                JSONObject mainJson = new JSONObject();
                mainJson.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));
                mainJson.put("post_id", post_id.toString());
                mainJson.put("post", usersays.getText().toString());
                JSONArray imagesUrl = new JSONArray();
                if (imageArray.size() > 0) {
                    for (int i = 0; i < imageArray.size(); i++) {
                        imagesUrl.put(imageArray.get(i));
                    }
                    mainJson.put("image", imagesUrl);
                }

                StringEntity s = new StringEntity(mainJson.toString(),
                        HTTP.UTF_8);
                params = new HashMap<String, Object>();
                params.put(AQuery.POST_ENTITY, s);
                Log.e("mainjson", mainJson.toString());
                aq.ajax(DataHolder.EDIT_POST, params, JSONObject.class, getupdatePostCallback());
            } catch (Exception e) {
            }
        }
    }

    private AjaxCallback<JSONObject> getupdatePostCallback() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);
                Log.d("getNewPostResonse", result.toString());
                mProgressDialog.dismiss();
                if (result != null) {
                    try {
                        JSONObject outputJsonObject = result.getJSONObject("output");
                        int userStatus = outputJsonObject.optInt("status");
                        String message = outputJsonObject.optString("message");
                        if (userStatus == 1) {
                            showToast(message);
                            Intent i = new Intent(NewPostActivity.this,NotificationDetailActivity.class);
                            i.putExtra("post_id",mNotificationDetail.postId);
                            startActivity(i);
                            finish();
                        } else {
                            showToast(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            private void setData() {

            }

        }.header("Content-Type", "application/json");
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

        if(isFromNotification)
        {
            titleToolbar.setText("EDIT POST");
        }
        else{
            titleToolbar.setText("NEW POST");
        }

        titleToolbar.setTextColor(getResources().getColor(R.color.txt_color_default));
        nextBtnToolbar.setText("s");
        nextBtnToolbar.setTextColor(getResources().getColor(R.color.Blue));
        backBtnToolbar.setText("j");
        backBtnToolbar.setTextColor(getResources().getColor(R.color.red));

        backBtnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                hideSoftKeyboard();
            }
        });
        nextBtnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()){
                    if (isFromNotification) {
                        updatePost();
                        hideSoftKeyboard();
                    } else {
                        if (validation()) {
                            getNewPost();
                            hideSoftKeyboard();
                        }
                    }
                }
            }
        });
        return customView;
    }
    public Boolean validation() {
        Boolean val = true;
        if (hasEvent.equals(false)) {
            val = false;
            showToast("please select an event to post...");
        }

//        if ((galleryImageContainer.equals(null)) && (usersays.equals(null))){
//            val=false;
//            showToast("Empty post. Please check..");
//        }


        return val;
    }
    public void setData() {
        if(mNotificationDetail.eventId.length()>0)
        {

            linkedEventTitle.setText(mNotificationDetail.eventName.toUpperCase());
            linkedEventLocation.setText(mNotificationDetail.location);
            mAqueryTask.setImage(mNotificationDetail.eventImage.trim(), linkedEventImage, null);
            eventLinkView.setVisibility(View.VISIBLE);
        }
        usersays.setText(mNotificationDetail.post);
//        usersays.setText(mNotificationDetail.post);

//        if (mEventDetail.eventStartdate != null) {
//            if (mEventDetail.eventStartdate.length() > 0) {
//                String dateandtime = GeneralUtil.convertDate(mEventDetail.eventStartdate);
//                String[] date = dateandtime.split(",");
//                eventDate.setText(date[0]);
//                eventTime.setText(date[1].trim());
//            }
//        }
        mAqueryTask.setImage(mNotificationDetail.userProfile.trim(), userImage, null);
        galleryImageContainer.removeAllViews();
        for (int i = 0; i < mNotificationDetail.image.size(); i++) {
            Log.d("image", mNotificationDetail.image.toString());
            final View imageViewLayout = getLayoutInflater().inflate(
                    R.layout.image_layout, null);
            imageViewLayout.setLayoutParams(
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

            ImageView imageView = (ImageView) imageViewLayout.findViewById(R.id.Clickedimage);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailIntent = new Intent(NewPostActivity.this,
                            ImageGalleryActivity.class);
                    detailIntent.putExtra("position", 0);
                    detailIntent.putStringArrayListExtra("IMAGES", mNotificationDetail.image);
                    startActivity(detailIntent);
                }
            });
            final FontIcon deleteIcon = (FontIcon) imageViewLayout.findViewById(R.id.delete);
            mAqueryTask.setImage(mNotificationDetail.image.get(i), imageView, null);
            imageArray.add(mNotificationDetail.image.get(i));
            galleryImageContainer.addView(imageViewLayout);
            hideSoftKeyboard();
            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    galleryImageContainer.post(new Runnable() {
                        @Override
                        public void run() {
                            String data = imageArray.get(galleryImageContainer.indexOfChild(imageViewLayout));
                            imageArray.remove(data);
                            galleryImageContainer.removeView(imageViewLayout);

                        }
                    });

                }
            });

        }

    }

    public void hideSoftKeyboardAuto() {
        InputMethodManager imm = (InputMethodManager) getApplicationContext()
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(usersays.getApplicationWindowToken(), 0);

    }

    public void hideSoftKeyboardA() {
        InputMethodManager imm = (InputMethodManager) getApplicationContext()
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getApplicationWindowToken(), 0);

    }
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}

