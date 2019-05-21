//package viva.oneplatinum.com.viva.adapters;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//
//import com.androidquery.AQuery;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import viva.oneplatinum.com.viva.R;
//import viva.oneplatinum.com.viva.models.Friend;
//import viva.oneplatinum.com.viva.widgets.CircularImageView;
//import viva.oneplatinum.com.viva.widgets.RobotoLightTextView;
//
///**
// * Created by D-MAX on 4/19/2016.
// */
//public class CustomeFriendListAdapter extends ArrayAdapter<List> {
//    private Context context;
//    ArrayList<Friend> friend;
//    private Bitmap userImageBitmap;
//    AQuery aq;
//    View view;
//    List<String> list = new ArrayList<String>();
//    List<String> img_list = new ArrayList<String>();
//
//    public CustomeFriendListAdapter(Context context, List<String> textViewResourceId, List<String> list) {
//        // TODO Auto-generated constructor stub
//        this.context = context;
//        aq = new AQuery(context);
//    }
//
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        final ViewHolder viewHolder;
//
//        // TODO Auto-generated method stub
//        view = convertView;
//        if (view == null) {
//            LayoutInflater inflater = LayoutInflater.from(this.context);
//            view = inflater.inflate(R.layout.friendlistview, null);
//            viewHolder = new ViewHolder();
//
//            viewHolder.friendImage = (CircularImageView) view.findViewById(R.id.icon);
//            viewHolder.firstName = (RobotoLightTextView) view.findViewById(R.id.item);
//            view.setTag(viewHolder);
//        } else {
//
//            viewHolder = (ViewHolder) view.getTag();
//        }
//
//        return view;
//
//    }
//
//    public class ViewHolder {
//        public RobotoLightTextView firstName;
//        public RobotoLightTextView lastName;
//        public CircularImageView friendImage;
//
//    }
//}
