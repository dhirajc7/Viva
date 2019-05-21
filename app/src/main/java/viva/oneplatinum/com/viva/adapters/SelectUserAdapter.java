package viva.oneplatinum.com.viva.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.models.SelectUser;

/**
 * Created by Trinity Tuts on 10-01-2015.
 */
public class SelectUserAdapter extends BaseAdapter {

    public List<SelectUser> _data;
    private ArrayList<SelectUser> arraylist;
    Context _c;
    ViewHolder holder;
    String MobilePattern = "[0-9]{10}";

    public SelectUserAdapter(List<SelectUser> selectUsers, Context context) {
        _data = selectUsers;
        _c = context;
        this.arraylist = new ArrayList<SelectUser>();
        this.arraylist.addAll(_data);
    }

    @Override
    public int getCount() {
        return _data.size();
    }

    @Override
    public Object getItem(int i) {
        return _data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;

        if (view == null) {
            LayoutInflater li = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.addressbook_friendlist_item, null);
            holder = new ViewHolder();
            holder.itemView=(LinearLayout)view.findViewById(R.id.itemView);
            holder.title = (TextView) view.findViewById(R.id.name);
            holder.phone = (TextView) view.findViewById(R.id.no);
            holder.email = (TextView) view.findViewById(R.id.email);
            holder.imageView = (ImageView) view.findViewById(R.id.pic);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }




        final SelectUser data = (SelectUser) _data.get(i);
        if(data.getPhone().matches(MobilePattern)) {
            holder.title.setText(data.getName());
            holder.phone.setText(data.getPhone());
            holder.email.setText(data.getEmail());
            notifyDataSetChanged();
        }
        else{
            notifyDataSetChanged();
        }

        if(data.isSelected){
            holder.itemView.setBackgroundResource(R.color.grey);
        }else{
            holder.itemView.setBackgroundResource(R.color.transparent);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!data.isSelected) {
                    data.isSelected = true;
                    holder.itemView.setBackgroundResource(R.color.grey);
                } else {
                    data.isSelected = false;
                    holder.itemView.setBackgroundResource(R.color.transparent);
                }
                notifyDataSetChanged();
            }
        });
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                        if (!data.getCheckedBox()) {
//                            data.setCheckedBox(true);
//                            holder.itemView.setBackgroundResource(R.color.grey);
//                        } else {
//                            data.setCheckedBox(false);
//                            holder.itemView.setBackgroundResource(R.color.transparent);
//                        }
//                notifyDataSetChanged();
//                    }
//        });
        try {

            if (data.getThumb() != null) {
                holder.imageView.setImageBitmap(data.getThumb());
            } else {
                holder.imageView.setImageResource(R.drawable.ic_launcher);
            }
            // Seting round image
            //Bitmap bm = BitmapFactory.decodeResource(view.getResources(),R.mipmap.ic_launcher); // Load default image
//            roundedImage = new RoundImage(bm);
//            holder.imageView.setImageDrawable(roundedImage);
        } catch (OutOfMemoryError e) {
            // Add default picture
            holder.imageView.setImageDrawable(this._c.getDrawable(R.mipmap.ic_launcher));
            e.printStackTrace();
        }

        Log.e("Image Thumb", "--------------" + data.getThumb());

        /*// Set check box listener android
        holder.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) view;
                if (checkBox.isChecked()) {
                    data.setCheckedBox(true);
                  } else {
                    data.setCheckedBox(false);
                }
            }
        });*/

        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        _data.clear();
        if (charText.length() == 0) {
            _data.addAll(arraylist);
        } else {
            for (SelectUser wp : arraylist) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    _data.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
    static class ViewHolder {
        ImageView imageView;
        TextView title, phone,email;
        CheckBox check;
        LinearLayout itemView;
    }
    private static boolean isValidPhoneNumber(String mobile) {
        String regEx = "^(?:0091|\\\\+91|0)[7-9][0-9]{9}$";
        return mobile.matches(regEx);
    }
}