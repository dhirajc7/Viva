package viva.oneplatinum.com.viva.image;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.data.ContactViewHolder;


/**
 * This class handles how to set bitmaps for an entry ImageView or
 * ContactViewHolder object. It can be retrieved from the BitmapCache or
 * download based on a entry URL.
 *
 * When handling concurrency is required, it also handles canceling no longer
 * needed tasks before starting new download tasks. Nice ideas on handling
 * concurrency came from the following post on the Android developers blog :
 *
 * @link http://android-developers.blogspot.com.br/2010/07/multithreading-for-
 *       performance.html
 */
public class BitmapHandler {

    private static final String TAG = "BitmapHandler";

    /**
     * This is to set a bitmap not specifically related to a view holder.
     *
     * @param url
     *            key for the bitmap to be set
     * @param imageView
     *            reference to the object which the bitmap will be set
     */
    public void setBitmap(String url, ImageView imageView) {

        if (url == null) {
            Log.d(TAG, "Setting default bitmap as provided url is null.");
            setDefaultBitmap(imageView);
            return;
        }

        // First check if the required bitmap was already cached
        Bitmap bitmap = BitmapCache.getInstance().getBitmap(url);

        if (bitmap == null) {
            // If its not in the cache, start a new download task
            Log.d(TAG, "Starting download bitmap : " + url);
            BitmapHandlerDownloadTask task = new BitmapHandlerDownloadTask(url, imageView);
            task.execute();
        } else {
            Log.d(TAG, "Getting bitmap for URL " + url + " from cache");
            imageView.setImageBitmap(bitmap);
        }

    }


    public void setBitmap(int position, String url, ContactViewHolder vHolder) {

        ImageView imageView = vHolder.getPicture();

        if (url == null) {
            Log.d(TAG, "Setting default bitmap @ position " + position
                    + " as provided url is null.");
            cancelDownloadTask(vHolder);
            setDefaultBitmap(imageView);
            imageView.setTag(null);
            return;
        }

        cancelLowPriorityDownload(position, url, vHolder);

        // First check if the required bitmap was already cached
        Bitmap bitmap = BitmapCache.getInstance().getBitmap(url);

        if (bitmap == null) {
            // If its not in the cache, start a new download task
            Log.d(TAG, "Starting download bitmap @ position " + position + " : " + url);
            setDefaultBitmap(imageView);
            BitmapHandlerDownloadTask task = new BitmapHandlerDownloadTask(position, url, vHolder);
            vHolder.setBitmapDownloadTaskRef(task);
            task.execute();
        } else {
            Log.d(TAG, "Getting bitmap from cache @ position " + position);
            vHolder.setBitmapDownloadTaskRef(null);
            imageView.setImageBitmap(bitmap);
            imageView.setTag(null);
        }

    }

    /**
     * Sets the default bitmap to the entry image view.
     */
    private void setDefaultBitmap(ImageView imageView) {
        imageView.setImageDrawable(imageView.getContext().getResources()
                .getDrawable(R.drawable.list_image_0));
    }

    /**
     * It cancels any previous task thrown for the entry view holder considering
     * the current URL image to be set. If there is already a task in place to
     * download an image from the same URL address, we let it goes.
     */
    private void cancelLowPriorityDownload(int position, String url, ContactViewHolder vHolder) {
        BitmapHandlerDownloadTask taskRef = vHolder.getBitmapDownloadTaskRef();
        if (taskRef != null) {
            String bitmapUrl = taskRef.getPictureUrl();
            if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
                Log.d(TAG, "Cancelling low priority BitmapHandlerDownloadTask @ position "
                        + taskRef.getPosition() + ". Current position on focus is " + position);
                vHolder.setBitmapDownloadTaskRef(null);
                taskRef.cancel(true);
            }
        }
    }

    /**
     * It cancels any previous task thrown for the entry view holder.
     */
    private void cancelDownloadTask(ContactViewHolder vHolder) {
        BitmapHandlerDownloadTask taskRef = vHolder.getBitmapDownloadTaskRef();
        if (taskRef != null) {
            Log.d(TAG, "Cancelling BitmapHandlerDownloadTask @ position " + taskRef.getPosition());
            vHolder.setBitmapDownloadTaskRef(null);
            taskRef.cancel(true);
        }
    }

}