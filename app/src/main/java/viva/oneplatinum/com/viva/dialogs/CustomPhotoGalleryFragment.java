package viva.oneplatinum.com.viva.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.app.VivaApplication;
import viva.oneplatinum.com.viva.interfaces.ImageSelectorFromGallery;
import viva.oneplatinum.com.viva.models.ImageItem;
import viva.oneplatinum.com.viva.utils.DataHolder;

public class CustomPhotoGalleryFragment extends DialogFragment {

    private GridView grdImages;
    ImageView btnSelect, selectAll;
    TextView Imagecount;
    Boolean selectedAll = false;
    ArrayList<String> mSelectionCount;
    private ImageAdapter imageAdapter;
    private String[] arrPath;
    private boolean[] thumbnailsselection;
    private int ids[];
    private int count, height, width, density;
    Boolean GridViewReady = false;
    String albumId;
    DataHolder.Dataholder dataholder;
    ArrayList<ImageItem> imageItemList;
    Picasso mPicasso;
    Boolean isFromGridActivity = false,isFromStartFromScratch=false,isFromEditEvent=false;
    VivaApplication app;
    CustomPhotoGalleryFragment cpga;
    Boolean BuildVersioner = true;
    private CustomGalleryFragment.OnDismissListner listner;
    VivaApplication mVivaApplication;
    Dialog customDailog;
    HashMap<Integer, Bitmap> imageCache = new HashMap<Integer, Bitmap>();
    Context mContext;
    ImageSelectorFromGallery mImageSelectorFromGallery;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageSelectorFromGallery=(ImageSelectorFromGallery)getActivity();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        mVivaApplication = (VivaApplication) mContext.getApplicationContext();
    }

    public void setDismissListner(CustomGalleryFragment.OnDismissListner listner) {
        this.listner = listner;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        customDailog = new Dialog(mContext);
        View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.custom_gallery, null);
        mPicasso = Picasso.with(getActivity());

        // get application
        app = (VivaApplication) getActivity().getApplication();

        // get this activity
        cpga = CustomPhotoGalleryFragment.this;

        dataholder = DataHolder.Dataholder.getInstance();
        WindowManager wm = getActivity().getWindowManager();
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        height = dm.heightPixels;
        width = dm.widthPixels;
        density = dm.densityDpi;
        Bundle bundle = getArguments();
        isFromGridActivity = bundle.getBoolean("groupDetail", false);
        isFromStartFromScratch = bundle.getBoolean("isFromStartFromScratch",false);
        isFromEditEvent =  bundle.getBoolean("isFromEditEvent",false);
        albumId = bundle.getString("album");
        imageItemList = new ArrayList<ImageItem>();

        Log.d("albumId", albumId);

        grdImages = (GridView) view.findViewById(R.id.grdImages);

        mSelectionCount = new ArrayList<String>();

        // get the build version of the OS
        BuildVersioner = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

        final String[] columns = {MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID};
        final String orderBy = MediaStore.Images.Media.DATE_ADDED;
        Cursor imagecursor = getActivity().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                "bucket_display_name = \"" + albumId + "\"", null, orderBy);
        // Cursor imagecursor = getContentResolver().query(
        // MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
        // null, orderBy);
        int image_column_index = imagecursor
                .getColumnIndex(MediaStore.Images.Media._ID);
        this.count = imagecursor.getCount();
        this.arrPath = new String[this.count];
        ids = new int[this.count];
        this.thumbnailsselection = new boolean[this.count];
        final int imCount = imagecursor.getCount();
        for (int i = this.count - 1; i >= 0; i--) {
            imagecursor.moveToPosition(Math.abs(this.count - i - 1));
            ids[i] = imagecursor.getInt(image_column_index);
            int dataColumnIndex = imagecursor
                    .getColumnIndex(MediaStore.Images.Media.DATA);
            arrPath[i] = imagecursor.getString(dataColumnIndex);
        }

        imageAdapter = new ImageAdapter(getActivity(),
                grdImages);
        grdImages.setAdapter(imageAdapter);
        grdImages.setNumColumns(3);
        imagecursor.close();
        grdImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "fdsfdfdfdsf", Toast.LENGTH_SHORT).show();
            }
        });
        customDailog.requestWindowFeature((int) Window.FEATURE_NO_TITLE);
        customDailog.setContentView(view);
        customDailog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        customDailog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(256));
        customDailog.show();
        Window window = customDailog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        customDailog.show();
        return customDailog;
    }

    private void setBitmap(final ImageView iv, final int id, final int position) {

        if (iv.getId() == position) {
            new AsyncTask<Void, Void, Bitmap>() {

                @Override
                protected Bitmap doInBackground(Void... params) {
                    return MediaStore.Images.Thumbnails.getThumbnail(
                            getActivity().getContentResolver(), id,
                            MediaStore.Images.Thumbnails.MINI_KIND, null);
                }

                @Override
                protected void onPostExecute(Bitmap result) {
                    super.onPostExecute(result);
                    // mPicasso.load(arrPath[position]).into(
                    // holder.imgThumb);
                    if (iv.getId() == position) {
                        iv.setImageBitmap(result);
                    }
                }
            }.execute();
        }
    }

    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context mContext;
        private GridView gView;
        int sState;
        int f, total;

        public ImageAdapter(Context context, GridView mGridView) {
            mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.mContext = context;
            this.gView = mGridView;
        }

        public int getCount() {
            Log.d("imagesCount", count + "");
            return count;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("NewApi")
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            Log.d("atGetView", "position " + position);
            final ViewHolder holder;

            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.custom_gallery_item,
                    parent, false);
            holder.imgThumb = (ImageView) convertView
                    .findViewById(R.id.imgThumb);
            holder.enlarge = (ImageView) convertView
                    .findViewById(R.id.imgEnlarge);
            convertView.setLayoutParams(new GridView.LayoutParams(width / 3,
                    width / 3));
            convertView.setPadding(0, 0, 0, 0);
            convertView.setTag(holder);


            holder.imgThumb.setId(position);

            setBitmaptask(holder.imgThumb, ids[position], position,
                    arrPath[position]);
            // }

            //holder.imgThumb.setImageResource(R.drawable.square);

            holder.imgThumb.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
//					holder.chkImage.performClick();
                 Bitmap bitmap=   BitmapFactory.decodeFile(arrPath[position]);
//                    if(isFromStartFromScratch)
//                    {
                    mImageSelectorFromGallery.image(bitmap,arrPath[position]);
//                        ((StartFromScratch) mContext).setEventImage(bitmap,arrPath[position]);
//                    }
//                    if(isFromEditEvent)
//                    {
//                        ((EditEventActivity) mContext).setEventImage(bitmap,arrPath[position]);
//                    }

                    dismiss();
                }
            });
            holder.enlarge.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                }
            });

//            if (thumbnailsselection[position]) {
//                holder.chkImage.setImageResource(R.drawable.ic_green_checked);
//            } else {
//            }
            holder.id = position;
            GridViewReady = true;
            return convertView;
        }

    }

    public void onBackPressed() {
        // TODO Auto-generated method stub

        Intent in = new Intent(getActivity(),
                CustomGalleryFragment.class);
        in.putExtra("GridActivity", isFromGridActivity);
        in.putExtra("isFromStartFromScratch",isFromStartFromScratch);
        in.putExtra("isFromEditEvent",isFromEditEvent);
        startActivity(in);
        dataholder.imageCache.evictAll();
    }

    private void setBitmaptask(final ImageView iv, final int id,
                               final int position, final String path) {

        if (cpga != null) {
            // only fetch the images when this activity is running
            if (iv.getId() == position) {
                AsyncTask<Void, Void, Bitmap> my_task = new AsyncTask<Void, Void, Bitmap>() {

                    @Override
                    protected Bitmap doInBackground(Void... params) {
                        Bitmap bb = dataholder.imageCache.get(id + "");
                        if (bb != null) {
                            Log.d("ImageFromCache", id + "  hit");
                            return bb;
                        } else {
                            Bitmap b = MediaStore.Images.Thumbnails
                                    .getThumbnail(
                                            getActivity()
                                                    .getContentResolver(),
                                            id,
                                            MediaStore.Images.Thumbnails.MINI_KIND,
                                            null);
                            dataholder.imageCache.put(id + "", b);
                            Log.e("ImageFromCache", id + " miss");
                            Matrix matrix = new Matrix();
                            int rotationAngle = 0;
                            try {
                                ExifInterface exif = new ExifInterface(path);
                                String orientString = exif
                                        .getAttribute(ExifInterface.TAG_ORIENTATION);
//                                Log.d("TAG_orientation EXIFDATA", orientString);
                                int orientation = orientString != null ? Integer
                                        .parseInt(orientString)
                                        : ExifInterface.ORIENTATION_NORMAL;

                                if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
                                    rotationAngle = 90;
                                if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
                                    rotationAngle = 180;
                                if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
                                    rotationAngle = 270;
                                // Rotate Bitmap
                                Log.d("thumbnailRotationA", "id " + id
                                        + " rotationAngle " + rotationAngle
                                        + " imagePath " + path);

                                matrix.setRotate(rotationAngle,
                                        (float) b.getWidth() / 2,
                                        (float) b.getHeight() / 2);
                                Bitmap s = Bitmap.createBitmap(b, 0, 0,
                                        b.getWidth(), b.getHeight(), matrix,
                                        true);
                                b = s;
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            return b;
                        }
                    }

                    @Override
                    protected void onPostExecute(Bitmap result) {
                        super.onPostExecute(result);
                        if (iv.getId() == position) {
                            iv.setImageBitmap(result);
                        }
                    }
                };
                if (BuildVersioner) {
                    my_task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                            (Void[]) null);
                } else {
                    my_task.execute((Void[]) null);
                }
            }
        }
    }

    class ViewHolder {
        ImageView imgThumb, chkImage, enlarge;
        int id;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public interface OnDismissListner {
        public void onDismissDialog();
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

}