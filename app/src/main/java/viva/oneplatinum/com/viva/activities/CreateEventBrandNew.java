package viva.oneplatinum.com.viva.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.widgets.FontIcon;
import viva.oneplatinum.com.viva.widgets.RobotoBoldTextView;

/**
 * Created by D-MAX on 3/18/2016.
 */
public class CreateEventBrandNew extends  ParentActivity {
    Toolbar mToolBar;
    LinearLayout useaPreviousEvent,startFromScratch;
    FontIcon backBtnToolbar;
    RobotoBoldTextView titleToolbar;
    ImageView nextBtnToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event_brandnew);
        initViews();
        onClick();
        initToolBar();
    }

    private void onClick() {
        startFromScratch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreateEventBrandNew.this, StartFromScratch.class);
                startActivity(i);
                finish();
            }

        });
        useaPreviousEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreateEventBrandNew.this, UsePreviousTemplateActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
    private void initToolBar() {
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        mToolBar.addView(getCustomView());
    }
    private View getCustomView() {

        LayoutInflater linflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View customView = linflater.inflate(
                R.layout.toolbar_item_addfriend, null);
        Toolbar.LayoutParams layoutParam = new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        customView.setLayoutParams(layoutParam);
        backBtnToolbar = (FontIcon) customView.findViewById(R.id.backBtnToolbar);
        titleToolbar = (RobotoBoldTextView) customView.findViewById(R.id.titleToolbar);
        nextBtnToolbar = (ImageView) customView.findViewById(R.id.nextBtnToolbar);

        titleToolbar.setText("CREATE EVENT");
//        nextBtnToolbar.setText("s");
//        nextBtnToolbar.setVisibility(View.GONE);
//        nextBtnToolbar.setTextColor(getResources().getColor(R.color.Blue));

        titleToolbar.setTextColor(getResources().getColor(R.color.txt_color_default));
        backBtnToolbar.setText("j");
        backBtnToolbar.setTextColor(getResources().getColor(R.color.red));
        backBtnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        nextBtnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return customView;
    }
    private void initViews() {
        startFromScratch = (LinearLayout) findViewById(R.id.startFromScratch);
        useaPreviousEvent = (LinearLayout) findViewById(R.id.useaPreviousEvent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
