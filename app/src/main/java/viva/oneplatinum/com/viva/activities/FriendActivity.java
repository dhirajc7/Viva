package viva.oneplatinum.com.viva.activities;

/**
 * Created by D-MAX on 4/19/2016.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import viva.oneplatinum.com.viva.R;

public class FriendActivity extends AppCompatActivity {
//    CustomeFriendListAdapter mCustomeFriendListAdapter;
    List<String> list = new ArrayList<String>();
    List<String> img_list = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.viva_friends);
        Intent intent = getIntent();
        list = intent.getStringArrayListExtra("friendsName");
        img_list = intent.getStringArrayListExtra("friendsPic");
//        setListView();
    }

//    private void setListView() {
//
//        mCustomeFriendListAdapter = new CustomeFriendListAdapter(this, list, img_list);
//        ListView list = (ListView) findViewById(R.id.event_list);
//        list.setAdapter(mCustomeFriendListAdapter);
//
//
//    }
}
