package viva.oneplatinum.com.viva.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.app.VivaApplication;
import viva.oneplatinum.com.viva.models.Album;
import viva.oneplatinum.com.viva.models.ImageData;
import viva.oneplatinum.com.viva.utils.DataHolder;

public class CustomGalleryFragment extends DialogFragment {
    private GridView grdImages;
    Picasso mPicasso;
    ImageView btnSelect, selectAll, logo;
    LinearLayout selectLayout, galleryContainer, doneLayout, emptyView;
    TextView Imagecount;
    Boolean selectedAll = false;
    ArrayList<String> mSelectionCount;
    private ImageAdapter imageAdapter;
    private String[] arrPath;
    private boolean[] thumbnailsselection;
    private int ids[];
    private int count, exitcount = 0, height, width, density;
    Boolean GridViewReady = false;
    DataHolder.Dataholder dataholder;
    Boolean isFromGridActivity = false, BuildVersioner = true,isFromStartFromScratch=false,isFromEditEvent=false;
    Context mContext;
    Album mAlbum;
    HashMap<String, Album> mAlbumList;
    HashMap<String, ArrayList<String>> albumContents;
    ArrayList<Album> albums;
    private OnDismissListner listner;
    VivaApplication mVivaApplication;
    Dialog customDailog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        mVivaApplication = (VivaApplication) mContext.getApplicationContext();
    }

    public void setDismissListner(OnDismissListner listner) {
        this.listner = listner;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        customDailog = new Dialog(mContext);
        View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.custom_gallery, null);
        grdImages = (GridView) view.findViewById(R.id.grdImages);
        galleryContainer = (LinearLayout) view.findViewById(R.id.gallery_container);
        dataholder = DataHolder.Dataholder.getInstance();
        mPicasso = Picasso.with(getActivity());

        mAlbumList = new HashMap<String, Album>();
        albums = new ArrayList<Album>();

        //	isFromGridActivity = getIntent().getBooleanExtra("GridActivity", false);

        WindowManager wm = getActivity().getWindowManager();
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        height = dm.heightPixels;
        width = dm.widthPixels;
        density = dm.densityDpi;


        // get the build version for the OS
        BuildVersioner = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

        final String[] columns = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA};

        final String orderBy = MediaStore.Images.Media.DATE_MODIFIED;

        Cursor imagecursor = getActivity().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy);
        int image_column_index = imagecursor
                .getColumnIndex(MediaStore.Images.Media._ID);
        this.count = imagecursor.getCount();
        this.arrPath = new String[this.count];
        ids = new int[this.count];
        this.thumbnailsselection = new boolean[this.count];
        final int imCount = imagecursor.getCount();
        for (int i = this.count - 1; i >= 0; i--) {
            mAlbum = new Album();
            ImageData imageData = new ImageData();
            imagecursor.moveToPosition(i);
            ids[i] = imagecursor.getInt(image_column_index);
            int dataColumnIndex = imagecursor
                    .getColumnIndex(MediaStore.Images.Media.DATA);

            mAlbum.id = imagecursor.getString(imagecursor
                    .getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
            mAlbum.name = imagecursor
                    .getString(imagecursor
                            .getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
            mAlbum.coverId = imagecursor.getLong(imagecursor
                    .getColumnIndex(MediaStore.Images.Media._ID));

            imageData.imagePath = imagecursor.getString(dataColumnIndex);
            Log.d("albumsItem", imageData.imagePath + "");
            imageData.imageId = imagecursor.getInt(image_column_index);
            mAlbum.imagesList.add(imageData);
            if (!mAlbumList.containsKey(mAlbum.name)) {
                mAlbumList.put(mAlbum.name, mAlbum);
                albums.add(mAlbum);
            }
        }
        Log.d("albumlist", mAlbumList.size() + "");
        Log.d("albumlist", mAlbumList.toString());
        imagecursor.close();

        imageAdapter = new ImageAdapter(getActivity(), grdImages);
        grdImages.setAdapter(imageAdapter);
        grdImages.setNumColumns(3);
        TextView tv = new TextView(getActivity());
        tv.setText("There are no galleries on this device");
        grdImages.setEmptyView(tv);
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
            gView.setOnScrollListener(new OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view,
                                                 int scrollState) {
                    // TODO Auto-generated method stub
                    sState = scrollState;
                    Log.d("atGridScrollstate", "ScrollState " + scrollState
                            + " " + sState);
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {
                    // TODO Auto-generated method stub
                    Log.d("atGridScroll", "firstVi " + firstVisibleItem
                            + " total vis" + visibleItemCount);
                    Log.d("atGridScroll",
                            "GridViewChildren" + gView.getChildCount());
                    f = firstVisibleItem;
                    total = visibleItemCount;
                    if (GridViewReady && sState != 2) {
                        for (int i = 0; i < visibleItemCount; i++) {
                            gView.getChildAt(i).invalidate();
                        }
                    }
                }
            });
        }

        public int getCount() {
            Log.d("albumlist sze", albums.size() + "");
            if (mAlbumList.size() == 0) {
                if (emptyView != null) {
                    emptyView.setVisibility(View.VISIBLE);
                }
            } else {
                if (emptyView != null) {
                    emptyView.setVisibility(View.GONE);
                }
            }
            return mAlbumList.size();
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

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.custom_gallery_item,
                        parent, false);
                holder.imgThumb = (ImageView) convertView
                        .findViewById(R.id.imgThumb);
                holder.chkImage = (ImageView) convertView
                        .findViewById(R.id.chkImage);
                holder.chkImage.setVisibility(View.GONE);
                holder.enlarge = (ImageView) convertView
                        .findViewById(R.id.imgEnlarge);
                holder.title = (TextView) convertView
                        .findViewById(R.id.gallery_image_title);
                holder.enlarge.setVisibility(View.GONE);
                convertView.setLayoutParams(new GridView.LayoutParams(
                        width / 3, width / 3));
                convertView.setPadding(0, 0, 0, 0);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.chkImage.setId(position);

            holder.imgThumb.setId(position);
            setBitmaptask(holder.imgThumb,
                    albums.get(position).imagesList.get(0).imageId, position,
                    albums.get(position).imagesList.get(0));
            holder.title.setText(albums.get(position).name);
            holder.title.setVisibility(View.VISIBLE);

//			if (thumbnailsselection[position]) {
//				holder.chkImage.setImageResource(R.drawable.ic_green_checked);
//			} else {
//				holder.chkImage.setImageResource(R.drawable.ic_checkbox);
//			}
            holder.id = position;
            GridViewReady = true;
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    CustomPhotoGalleryFragment mCustomGalleryFragment = new CustomPhotoGalleryFragment();
                    Bundle b = new Bundle();
                    b.putString("album", albums.get(position).name);
                    b.putBoolean("GridActivity", isFromGridActivity);
                    b.putBoolean("isFromStartFromScratch", isFromStartFromScratch);
                    b.putBoolean("isFromEditEvent", isFromEditEvent);
                    mCustomGalleryFragment.setArguments(b);
                    mCustomGalleryFragment.show(getChildFragmentManager(), "imageGallery");
//                    Fragment gallery = new CustomPhotoGalleryFragment();
//                    Bundle b = new Bundle();
//                    b.putString("album", albums.get(position).name);
//                    b.putBoolean("GridActivity", isFromGridActivity);
//                    gallery.setArguments(b);
//                    getActivity().getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.imageContainer, gallery).commit();
                }
            });
            return convertView;
        }
    }

    private void setBitmaptask(final ImageView iv, final int id,
                               final int position, final ImageData idata) {

        if (iv.getId() == position) {
            AsyncTask<Void, Void, Bitmap> my_task = new AsyncTask<Void, Void, Bitmap>() {

                @Override
                protected Bitmap doInBackground(Void... params) {
                    Bitmap bb = dataholder.imageCache.get(id + "");
                    if (bb != null) {
                        Log.d("ImageFromCache", id + "  hit");
                        return bb;
                    } else {
                        Bitmap b = MediaStore.Images.Thumbnails.getThumbnail(
                                getActivity().getContentResolver(),
                                id, MediaStore.Images.Thumbnails.MINI_KIND,
                                null);

                        Log.e("ImageFromCache", id + " miss");
                        Matrix matrix = new Matrix();
                        int rotationAngle = 0;
                        try {
                            ExifInterface exif = new ExifInterface(
                                    idata.imagePath);
                            String orientString = exif
                                    .getAttribute(ExifInterface.TAG_ORIENTATION);
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
                                    + " imagePath " + idata.imagePath);

                            matrix.setRotate(rotationAngle,
                                    (float) b.getWidth() / 2,
                                    (float) b.getHeight() / 2);
                            Bitmap s = Bitmap.createBitmap(b, 0, 0,
                                    b.getWidth(), b.getHeight(), matrix, true);
                            b = s;
                            dataholder.imageCache.put(id + "", b);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (NullPointerException npe) {
                            npe.printStackTrace();
                        }
                        return b;
                    }
                }

                @Override
                protected void onPostExecute(Bitmap result) {
                    super.onPostExecute(result);
                    if (iv.getId() == position && result != null) {
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

    class ViewHolder {
        ImageView imgThumb, chkImage, enlarge;
        TextView title;
        int id;
    }

//    public void onBackPressed() {
//        // TODO Auto-generated method stub
//        if (isFromGridActivity) {
//            Intent intent = new Intent(getActivity(),
//                    GridActivity.class);
//            startActivity(intent);
//        } else {
//            Timer timer = new Timer();
//            TimerTask tt = new TimerTask() {
//
//                @Override
//                public void run() {
//                    // TODO Auto-generated method stub
//                    exitcount = 0;
//                }
//            };
//            if (exitcount == 0) {
//                exitcount = 1;
//                Toast.makeText(getActivity(),
//                        "Press Back Once Again to Exit Application",
//                        Toast.LENGTH_SHORT).show();
//
//                timer.schedule(tt, 3000);
//            } else {
//                tt.cancel();
//            }
//        }
//
//    }

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
