package viva.oneplatinum.com.viva.maps;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.activities.ParentActivity;
import viva.oneplatinum.com.viva.widgets.FontIcon;

public class ShowEventLocationActivity extends ParentActivity {

	private GoogleMap googleMap;

    Toolbar mToolBar;
    FontIcon backBtnToolbar, searchToolbar;
    double latitude;
    double longitude;
    String eventLocationtitle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        latitude = Double.parseDouble(getIntent().getStringExtra("latitude"));
        eventLocationtitle =getIntent().getStringExtra("eventLocationtitle");
        longitude = Double.parseDouble(getIntent().getStringExtra("longitude"));
        initToolBar();
        if (googleMap == null)
        {
            googleMap = ((MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
//            googleMap.setMyLocationEnabled(true);//NEED permission for GPS Location: You will see current location in map fragment

        }
        gotoLocation(latitude, longitude);
       Marker marker1 = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude)).title(eventLocationtitle)
                        .draggable(false).snippet(""));
    }



	void gotoLocation(double Lat,double Long){
		LatLng ll = new LatLng(Lat,Long);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(ll).zoom(15).build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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
                R.layout.toolbar_item_maps, null);
        Toolbar.LayoutParams layoutParam = new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        customView.setLayoutParams(layoutParam);
        backBtnToolbar = (FontIcon) customView.findViewById(R.id.backBtnToolbar);
        searchToolbar = (FontIcon) customView.findViewById(R.id.searchToolbar);

        backBtnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        searchToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });




        return customView;
    }

}