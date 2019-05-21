package viva.oneplatinum.com.viva.data;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.image.BitmapHandler;
import viva.oneplatinum.com.viva.image.BitmapHandlerDownloadTask;
import viva.oneplatinum.com.viva.widgets.CircularImageView;


/**
 * This class implements a view holder for the elements defined by row layout.
 *
 * This is used by the contact adapter class { @see ContactAdapter } to avoid
 * inflating a new layout and calling 3 times findViewById for each line in the
 * list view object { @see listView_contactsList } defined by
 * google_friends_list layout.
 *
 * For further information, please visit Android developers web site using the
 * following link.
 *
 * @link
 *       http://developer.android.com/training/improving-layouts/smooth-scrolling
 *       .html
 *
 * @author Rafael Simionato
 */
public class ContactViewHolder {

    // These are the view holder references for the row layout elements. Their
    // data may change high frequently according the scrolling of the list view
    // object every time getView method of ContactAdapter is called
    private final CircularImageView imageView_contactPicture;
    private final TextView textView_contactName;

    // It handles how to set bitmaps for the ImageView object referred by the
    // view holder
    private final BitmapHandler mBitmapHandler = new BitmapHandler();

    // It holds a weak reference to the last task thrown to download a bitmap
    // for the ImageView object referred by the view holder
    private WeakReference<BitmapHandlerDownloadTask> mBitmapHandlerDownloadTaskReference = new WeakReference<BitmapHandlerDownloadTask>(
            null);

    /**
     * It finds the references for each element in the entry row layout.
     *
     * @param layoutRow
     *            row layout object that refers one line of the list view object
     *            { @see listView_contactsList } defined by
     *            google_friends_list layout.
     */
    public ContactViewHolder(View layoutRow) {
        imageView_contactPicture = (CircularImageView) layoutRow.findViewById(R.id.imageView_contactPicture);
        textView_contactName = (TextView) layoutRow.findViewById(R.id.textView_contactName);
    }

    /**
     * It maps the data in the entry contact to the row layout elements referred
     * by the view holder. It also starts an asynchronous request to set the
     * ImageView object with a bitmap specified by the image URL in the entry
     * contact.
     *
     * As soon as the bitmap is given available, the bitmap handler object sets
     * it to the ImageView object if the view holder still refers the row layout
     * elements in the list view @param position.
     *
     * @param position
     *            refers the contact index in the list view object defined by
     *            google_friends_list layout
     * @param contact
     *            entry data to be mapped to the row layout elements
     */
    public void setData(int position, Contact contact) {
        textView_contactName.setText(contact.getName());
        mBitmapHandler.setBitmap(position, contact.getPictureUrl(),this);
    }

    /**
     * It returns the ImageView object referred by the view holder
     */
    public CircularImageView getPicture() {
        return imageView_contactPicture;
    }

    /**
     * It returns a reference to the last task thrown to download a bitmap for
     * the ImageView object referred by the view holder.
     */
    public BitmapHandlerDownloadTask getBitmapDownloadTaskRef() {
        return mBitmapHandlerDownloadTaskReference.get();
    }

    /**
     * It sets a weak reference to the task thrown to download a bitmap for the
     * ImageView object referred by the view holder based in the @param contact
     * of the this.setData method.
     */
    public void setBitmapDownloadTaskRef(BitmapHandlerDownloadTask taskRef) {
        mBitmapHandlerDownloadTaskReference = new WeakReference<BitmapHandlerDownloadTask>(taskRef);
    }

}