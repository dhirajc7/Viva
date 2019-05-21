package viva.oneplatinum.com.viva.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;

import java.util.ArrayList;
import java.util.Locale;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.utils.GeneralUtil;
import viva.oneplatinum.com.viva.widgets.CircularImageView;


/**
 * This is a simple base adapter implementation class used to load data into the
 * list view object { @see listView_contactsList } defined by
 * google_friends_list layout.
 *
 * @author Rafael Simionato
 */
public class ContactAdapter extends BaseAdapter {

    private static final String TAG = "ContactAdapter";

    private ArrayList<Contact> arraylist;
    // Array list of Contact that hold all the references read from each contact
    // after sign-in the Google+ user's account.
    ArrayList<Contact> arrayListContacts;

    private Bitmap userImageBitmap;
    // Layout inflater reference used to inflate a layout row in the contacts
    // list view object. Please @see R.layout.row
    private final LayoutInflater layoutInflater;
    AQuery aq;
    Context context;
    TextView textView_contactName;
    /**
     * Contact adapter constructor. It stores the initial set of data to be used
     * and gets a reference to a layout inflater that will be used later to
     * inflates each layout row of the contacts list view.
     *
     * @param context
     *            reference for activity where lies the contacts list view
     * @param contacts
     *            initial data to be loaded in this adapter
     */
    public ContactAdapter(Context context, ArrayList<Contact> contacts) {
        layoutInflater = LayoutInflater.from(context);
        arrayListContacts = contacts;
        aq=new AQuery(context);
        this.context = context;

        this.arraylist = new ArrayList<Contact>();
        this.arraylist.addAll(arrayListContacts);
    }

    /**
     * It returns the number of Contact objects in the contacts array list.
     */
    @Override
    public int getCount() {
        return arrayListContacts.size();
    }

    /**
     * It returns a Contact object reference from a specific position in the
     * contacts array list.
     *
     * @param position
     *            index in the contacts array list for the required Contact
     *            object
     *
     * @return Contact object reference
     */
    @Override
    public Object getItem(int position) {
        return arrayListContacts.get(position);
    }

    /**
     * It returns an ID for a specific position in the contacts array list.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * It's used to set a specific Contact to a row layout in the contacts list
     * view object.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        // TODO Auto-generated method stub
        if (convertView == null) {
            //LayoutInflater inflater = LayoutInflater.from(this.context);
            convertView = layoutInflater.inflate(R.layout.row,parent, false);
            viewHolder = new ViewHolder();
            viewHolder.listItemLayout=(LinearLayout) convertView.findViewById(R.id.contactView);
            viewHolder.imageView_contactPicture = (CircularImageView) convertView.findViewById(R.id.imageView_contactPicture);
            viewHolder.textView_contactName = (TextView) convertView.findViewById(R.id.textView_contactName);

            convertView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Contact mFriend = arrayListContacts.get(position);
        if(mFriend.isSelected){
            viewHolder.listItemLayout.setBackgroundResource(R.color.grey);
        }else{
            viewHolder.listItemLayout.setBackgroundResource(R.color.transparent);
        }
        viewHolder.textView_contactName.setText(mFriend.getName());

        AQuery aq1 = aq.recycle(convertView);
        aq1.recycle(convertView);
        final Bitmap defaultImage = BitmapFactory.decodeResource(context
                .getResources(), R.drawable.ic_launcher);
        userImageBitmap = GeneralUtil.getRoundedBitmap(defaultImage);
//         aq.id(i).image(profileInfo.myPhoto)
        aq.id(viewHolder.imageView_contactPicture).image(mFriend.pictureUrl, true, true, 400, 0,
                new BitmapAjaxCallback() {
                    protected void callback(String url, ImageView iv,
                                            Bitmap bm, AjaxStatus status) {
                        if (bm != null) {
                            userImageBitmap = GeneralUtil.getRoundedBitmap(bm);

                        } else {
                            userImageBitmap = GeneralUtil
                                    .getRoundedBitmap(defaultImage);
                        }
                        viewHolder.imageView_contactPicture.setImageBitmap(userImageBitmap);
                    }

                    ;
                });


        viewHolder.listItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mFriend.isSelected) {
                    mFriend.isSelected = true;
                    viewHolder.listItemLayout.setBackgroundResource(R.color.grey);
                } else {
                    mFriend.isSelected = false;
                    viewHolder.listItemLayout.setBackgroundResource(R.color.transparent);
                }
            }
        });
        //viewHolder.setData(position, (Contact) getItem(position));
        return convertView;

    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        arrayListContacts.clear();
        if (charText.length() == 0) {
            arrayListContacts.addAll(arraylist);
        } else {
            for (Contact wp : arraylist) {
                if (wp.name.toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    arrayListContacts.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }


    public  class ViewHolder {
      public TextView textView_contactName;
        public CircularImageView imageView_contactPicture;
        public LinearLayout listItemLayout;
    }
}