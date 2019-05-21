package viva.oneplatinum.com.viva.adapters;

/**
 * Created by D-MAX on 4/12/2016.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;

import java.util.ArrayList;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.models.Friend;
import viva.oneplatinum.com.viva.utils.GeneralUtil;
import viva.oneplatinum.com.viva.widgets.CircularImageView;
import viva.oneplatinum.com.viva.widgets.RobotoRegularTextView;

public class AddFriendAdapter extends ArrayAdapter<Friend> {
    private Context context;
    ArrayList<Friend> friend;
    private Bitmap userImageBitmap;
    AQuery aq;
    View view;
    private LayoutInflater inflater;
    private SparseBooleanArray mSelectedItemsIds;

    public AddFriendAdapter(Context context, int textViewResourceId, ArrayList<Friend> list) {
        super(context, textViewResourceId, list);
        // TODO Auto-generated constructor stub
        mSelectedItemsIds = new SparseBooleanArray();
        friend = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        aq = new AQuery(context);
    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        // TODO Auto-generated method stub
        view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(this.context);
            view = inflater.inflate(R.layout.add_friend_items, null);
            viewHolder = new ViewHolder();
            viewHolder.listItemLayout=(LinearLayout) view.findViewById(R.id.listItemView);
            viewHolder.friendImage = (CircularImageView) view.findViewById(R.id.friendImage);
            viewHolder.firstName = (RobotoRegularTextView) view.findViewById(R.id.firstName);
            viewHolder.lastName = (RobotoRegularTextView) view.findViewById(R.id.lastName);
            view.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) view.getTag();
        }
        final Friend mFriend = friend.get(position);
        if(mFriend.isSelected){
            viewHolder.listItemLayout.setBackgroundResource(R.color.grey);
        }else{
            viewHolder.listItemLayout.setBackgroundResource(R.color.transparent);
        }
        viewHolder.firstName.setText(mFriend.first_name);
        viewHolder.lastName.setText(mFriend.last_name);
        viewHolder.mFriend = mFriend;
        AQuery aq1 = aq.recycle(view);
        aq1.recycle(view);
        final Bitmap defaultImage = BitmapFactory.decodeResource(context
                .getResources(), R.drawable.ic_launcher);
        userImageBitmap = GeneralUtil.getRoundedBitmap(defaultImage);
        // aq.id(userImage).image(profileInfo.myPhoto);
        aq.id(viewHolder.friendImage).image(mFriend.profile_picture, true, true, 400, 0,
                new BitmapAjaxCallback() {
                    protected void callback(String url, ImageView iv,
                                            Bitmap bm, AjaxStatus status) {
                        if (bm != null) {
                            userImageBitmap = GeneralUtil.getRoundedBitmap(bm);

                        } else {
                            userImageBitmap = GeneralUtil
                                    .getRoundedBitmap(defaultImage);
                        }
                        viewHolder.friendImage.setImageBitmap(userImageBitmap);
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

        return view;

    }
    public void remove(String string) {
        friend.remove(string);
        notifyDataSetChanged();
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
    public  class ViewHolder {
        public RobotoRegularTextView firstName;
        public RobotoRegularTextView lastName;
        public CircularImageView friendImage;
        public Friend mFriend;
        public LinearLayout listItemLayout;
    }
}
