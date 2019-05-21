package viva.oneplatinum.com.viva.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import java.io.InputStream;
import java.lang.ref.WeakReference;

import viva.oneplatinum.com.viva.data.ContactViewHolder;

/**
 * This class implements an asynchronous task to download required bitmaps.
 * After downloading the bitmap, it will be cached and set to the entry image
 * view. If handling concurrency is required, it checks if the entry view holder
 * stills refer the same data when requesting the download.
 */
public class BitmapHandlerDownloadTask extends AsyncTask<Void, Void, Bitmap> {

    private static final String TAG = "BitmapHandlerDownloadTask";

    // Address for the bitmap being download by this task
    private final String pictureUrl;

    // This is to check when the task gets finished if the entry ImageView
    // reference stills valid for the task purposes
    private final int position;
    private final WeakReference<ImageView> imageViewReference;

    private final ContactViewHolder vHolder;

    /**
     * This is to download a bitmap not specifically related to a view holder.
     * In this case handling concurrency is not required.
     *
     * @param url
     *            address for the bitmap to be download
     * @param imageView
     *            reference to the object which the new bitmap will be set
     */
    public BitmapHandlerDownloadTask(String url, ImageView imageView) {
        position = -1;
        vHolder = null;
        pictureUrl = url;
        imageViewReference = new WeakReference<ImageView>(imageView);
    }

    /**
     * This is to download a bitmap specifically related to the entry view
     * holder. As said in the ContactViewHolder class, view holder data may
     * change high frequently according the scrolling of the list view object
     * every time getView method of ContactAdapter is called. So in this case
     * for purposes of handling concurrency, its a good practice to store some
     * data now to identify if the entry view holder stills refer the same data
     * as soon as this task gets finished.
     *
     * @param pos
     *            refers the contact index in the list view object defined by
     *            google_friends_list layout
     * @param url
     *            address for the bitmap to be download
     * @param holder
     *            reference to view holder that requires download a new bitmap
     *
     * @TODO for the future, try using only the ContactViewHolder as an entry
     *       parameter for this method
     */
    public BitmapHandlerDownloadTask(int pos, String url, ContactViewHolder holder) {

        pictureUrl = url;
        vHolder = holder;
        imageViewReference = new WeakReference<ImageView>(vHolder.getPicture());

        // Storing the entry position in the class attribute to check again when
        // this task gets finished
        position = pos;

        // Storing the entry position in the ImageView tag. If it gets
        // overwritten until this task gets finished, that means entry view
        // holder data has changed.
        vHolder.getPicture().setTag(pos);

    }


    /**
     * It returns the address for the bitmap being download by this task
     */
    public String getPictureUrl() {
        return pictureUrl;
    }

    /**
     * It returns the contact index in the list view object defined by
     * google_friends_list layout that this task refers to.
     */
    public final int getPosition() {
        return position;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        return downloadBitmap();
    }

    /**
     * After downloading the bitmap it will be cached and set to the entry image
     * view. If handling concurrency is required, this method will check if the
     * entry view holder stills refer the same data when creating this task.
     */
    @Override
    protected void onPostExecute(Bitmap bitmap) {

        if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                if (bitmap != null) {

                    // At this point the new bitmap is ready to be cached
                    BitmapCache.getInstance().addBitmapToCache(pictureUrl, bitmap);

                    // Treating the specific case when downloading a bitmap not
                    // related to a view holder. That means handling concurrency
                    // is not required.
                    if (position == -1 && vHolder == null) {
                        imageView.setImageBitmap(bitmap);
                        return;
                    }

                    if ((Integer) imageView.getTag() != null) {
                        // Checking if current position in the ImageView tag has
                        // been changed since it was set when creating this task
                        final int currentPosition = (Integer) imageView.getTag();
                        if (currentPosition == position) {
                            imageView.setImageBitmap(bitmap);
                            imageView.setTag(null);
                        } else {
                        }
                    } else {
                    }
                }
            }
        }

        if (vHolder != null && this == vHolder.getBitmapDownloadTaskRef()) {
            vHolder.setBitmapDownloadTaskRef(null);
        }

    }

    /**
     * It instantiate a http client to download the required bitmap. It also
     * append a resizing string to the original request.
     */
    private Bitmap downloadBitmap() {
        final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        final HttpGet getRequest = new HttpGet(pictureUrl + "?sz=144");
        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("TAG", "Error " + statusCode + " while retrieving bitmap from " + pictureUrl);
                return null;
            }
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            getRequest.abort();
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return null;
    }

}