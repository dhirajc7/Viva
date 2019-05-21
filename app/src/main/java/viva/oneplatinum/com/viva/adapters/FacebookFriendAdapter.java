package viva.oneplatinum.com.viva.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.util.List;
import java.util.Locale;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.models.FbFriends;
import viva.oneplatinum.com.viva.utils.GeneralUtil;
import viva.oneplatinum.com.viva.widgets.CircularImageView;
import viva.oneplatinum.com.viva.widgets.RobotoRegularTextView;

/**
 * Created by D-MAX on 5/12/2016.
 */
public class FacebookFriendAdapter extends ArrayAdapter<FbFriends> {
    static FriendAdapter mGrid_Adapter;
    private Context context;
    ArrayList<FbFriends> friend;
    private Bitmap userImageBitmap;
    public List<FbFriends> _data;
    private ArrayList<FbFriends> arraylist;
    AQuery aq;
    View view;

    public FacebookFriendAdapter(Context context, int textViewResourceId, ArrayList<FbFriends> list) {
        super(context, textViewResourceId, list);
        friend = list;
        this.friend = friend;
        this.context = context;
        aq=new AQuery(context);
        this.arraylist = new ArrayList<FbFriends>();
        this.arraylist.addAll(friend);
    }
    //    @Override
//    public int getCount() {
//        return 10;
//    }
//
//    @Override
//    public Friend getItem(int position) {
//        return friend.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return 0;
//    }
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

            view.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) view.getTag();
        }
        final FbFriends mFriend = friend.get(position);
        if(mFriend.isSelected){
            viewHolder.listItemLayout.setBackgroundResource(R.color.grey);
        }else{
            viewHolder.listItemLayout.setBackgroundResource(R.color.transparent);
        }
        viewHolder.firstName.setText(mFriend.name);

        AQuery aq1 = aq.recycle(view);
        aq1.recycle(view);
        final Bitmap defaultImage = BitmapFactory.decodeResource(context
                .getResources(), R.drawable.ic_launcher);
        userImageBitmap = GeneralUtil.getRoundedBitmap(defaultImage);
        // aq.id(userImage).image(profileInfo.myPhoto);
        aq.id(viewHolder.friendImage).image("https://graph.facebook.com/"
        + mFriend.id + "/picture?type=large", true, true, 400, 0,
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
    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        friend.clear();
        if (charText.length() == 0) {
            friend.addAll(arraylist);
        } else {
            for (FbFriends wp : arraylist) {
                if (wp.name.toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    friend.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    public  class ViewHolder {
        public RobotoRegularTextView firstName;
        public RobotoRegularTextView lastName;
        public CircularImageView friendImage;
        public LinearLayout listItemLayout;
    }
}
