package viva.oneplatinum.com.viva.maps;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.activities.EventDetailActivity;
import viva.oneplatinum.com.viva.activities.FilterSearchActivity;
import viva.oneplatinum.com.viva.activities.ParentActivity;
import viva.oneplatinum.com.viva.models.Event;
import viva.oneplatinum.com.viva.parser.ParseUtility;
import viva.oneplatinum.com.viva.utils.DataHolder;
import viva.oneplatinum.com.viva.utils.GeneralUtil;
import viva.oneplatinum.com.viva.widgets.CircularImageView;
import viva.oneplatinum.com.viva.widgets.FontIcon;
import viva.oneplatinum.com.viva.widgets.RobotoBoldTextView;
import viva.oneplatinum.com.viva.widgets.RobotoRegularTextView;

/**
 * Created by Dell on 5/9/2016.
 */
public class MapActivity extends ParentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleMap mMap, dragMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    public static final String TAG = MapActivity.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    Double defaultLat, defaultLng,mapCentreLat=0.00, mapCentreLng=0.00, dragLat, dragLng;
    ArrayList<Event> eventArrayList;
    ArrayList<Event> showingEventList;
    FontIcon nowLocation, location;
    LatLng latvalue;
    LinearLayout eventView;
    CircularImageView eventImage;
    TextView event_name, Date_From,time_From;
    RobotoRegularTextView eventLocation;
    AQuery aq;
    Toolbar mToolBar;
    FontIcon backBtnToolbar, searchToolbar;
    RobotoBoldTextView titleToolbar;
    String eventLocationtitle, latitude, longitude;
    Marker locationMarker;
    Projection projection;
    String data;
    LatLng markerLocation;
    Boolean isFromHome=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        aq=new AQuery(this);
//        latitude = getIntent().getStringExtra("latitude");
//        eventLocationtitle =getIntent().getStringExtra("eventLocationtitle");
//        longitude = getIntent().getStringExtra("longitude");

        eventArrayList=new ArrayList<Event>();
        showingEventList=new ArrayList<Event>();
        setUpMapIfNeeded();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
        data=getIntent().getStringExtra("eventData");
        isFromHome=getIntent().getBooleanExtra("isFromHome", false);
//        getMyEvents(DataHolder.EVENT_LISTING);
        try {
            JSONObject jsonData=new JSONObject(data);
            eventArrayList = ParseUtility.parseAllEventsList(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initView();
        initToolBar();
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

        searchToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapActivity.this, FilterSearchActivity.class);
                startActivity(i);
                finish();
            }
        });



        backBtnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        time_From.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapActivity.this, FilterSearchMapActivity.class);
                startActivity(i);
            }
        });

        return customView;
    }

    private void initView() {


        eventImage= (CircularImageView) findViewById(R.id.eventImage);
        event_name= (RobotoBoldTextView) findViewById(R.id.event_name);
        Date_From= (RobotoBoldTextView) findViewById(R.id.Date_From);
        time_From= (RobotoBoldTextView) findViewById(R.id.time_From);
        eventLocation= (RobotoRegularTextView) findViewById(R.id.eventLocation);

        nowLocation= (FontIcon) findViewById(R.id.nowLocation);
        location= (FontIcon) findViewById(R.id.location);

        eventView= (LinearLayout) findViewById(R.id.eventView);
//        for(int i=0; i<eventArrayList.size();i++){
//            if(setUpMap(Double.parseDouble(eventArrayList.get(i).latitude), Double.parseDouble(eventArrayList.get(i).longititude))){
//                showingEventList.add(eventArrayList.get(i));
//            }
//
//        }

        nowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latvalue = new LatLng(defaultLat, defaultLng);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latvalue, 15));
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);


            }
        });

        location.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (locationMarker == null) {

//
//                    latvalue = new LatLng(mapCentreLat, mapCentreLng);
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latvalue, 15));
//                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
//                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

                   locationMarker= mMap.addMarker(new MarkerOptions().draggable(true).position(new LatLng(mapCentreLat, mapCentreLng)).snippet("draggableMarker").icon(BitmapDescriptorFactory.fromResource(R.drawable.location)));
                    sendLatLng(DataHolder.SETPIN_LOCATION, dragLat, dragLng);
                    mVivaApplicatiion.setUserData(DataHolder.CURRENT_LOCATION_LATITUDE, mapCentreLat + "");
                    mVivaApplicatiion.setUserData(DataHolder.CURRENT_LOCATION_LONGITUDE, mapCentreLng+ "");
                    mVivaApplicatiion.setUserData(DataHolder.ISPINSELECTED,"true");
                    mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                        @Override
                        public void onMarkerDragStart(Marker marker) {

                        }

                        @Override
                        public void onMarkerDrag(Marker marker) {
                            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latvalue, 15));

                        }

                        @Override
                        public void onMarkerDragEnd(Marker marker) {
                            LatLng dragPosition = marker.getPosition();
                            dragLat = dragPosition.latitude;
                            dragLng = dragPosition.longitude;
                            Log.i("info", "on drag end :" + dragLat + " dragLong :" + dragLng);

                            sendLatLng(DataHolder.SETPIN_LOCATION, dragLat, dragLng);

                        }

                    });
//                LatLngBounds curScreen=mMap.getProjection().getVisibleRegion().latLngBounds;
//                Log.i("info", "" + curScreen);
                }else{
                    unSendLatLng(DataHolder.UNSETPIN_LOCATION);
                    locationMarker.remove();
                    locationMarker=null;
                    mVivaApplicatiion.setUserData(DataHolder.ISPINSELECTED,"false");
                }
            }
        });
    }

    public void sendLatLng(String url,Double lat,Double lng) {
        if (isNetworkAvailable()) {
            mProgressDialog.setMessage("Loading Events..");
            mProgressDialog.show();
            Map<String, Object> params;
            try {
                JSONObject mainJson = new JSONObject();
                mainJson.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));
                mainJson.put("lat", lat);
                mainJson.put("lng", lng);
                StringEntity s;

                s = new StringEntity(mainJson.toString(), HTTP.UTF_8);

                params = new HashMap<String, Object>();
                params.put(AQuery.POST_ENTITY, s);

                Log.i("checkParams", "" + mainJson);
                //params.put("friends",);

                mAqueryTask.sendPostObjectRequest(url, params, JSONObject.class, getDataCallback());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private AjaxCallback<JSONObject> getDataCallback() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {
                Log.i("checkResult", "" + result);
//                mProgressDialog.dismiss();
                if (result != null) {
                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        showToast(outpuJsonObject.optString("message"));
                        getMyEvents(DataHolder.EVENT_LISTING);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    mProgressDialog.dismiss();
                }

            }

        }.header("Content-Type", "application/json");
    }
    public void unSendLatLng(String url) {
        if (isNetworkAvailable()) {
//            mProgressDialog.setMessage("Sending request..");
//            mProgressDialog.show();
            Map<String, Object> params;
            try {
                JSONObject mainJson = new JSONObject();
                mainJson.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));
                StringEntity s;

                s = new StringEntity(mainJson.toString(), HTTP.UTF_8);

                params = new HashMap<String, Object>();
                params.put(AQuery.POST_ENTITY, s);

                Log.i("checkParams", "" + mainJson);
                //params.put("friends",);

                mAqueryTask.sendPostObjectRequest(url, params, JSONObject.class, getDataCallback1());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private AjaxCallback<JSONObject> getDataCallback1() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {
                Log.i("checkResult", "" + result);
//                mProgressDialog.dismiss();
                if (result != null) {
                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        showToast(outpuJsonObject.optString("message"));
                        getMyEvents(DataHolder.EVENT_LISTING);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

        }.header("Content-Type", "application/json");
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {

            }
        }



    }

    private Boolean setUpMap(Double latitude, Double longitude) {
        LatLng position=new LatLng(latitude,longitude);
//        locationMarker=mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).snippet(id).icon(BitmapDescriptorFactory.fromResource(R.drawable.eventmap)));
        projection= mMap.getProjection();
        Point screenPosition = projection.toScreenLocation(position);
        if(screenPosition.x>0 && screenPosition.y>0){
            return true;
        }else {
            return false;
        }
    }


    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        defaultLat=currentLatitude;
        defaultLng=currentLongitude;

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

//        mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Current Location"));
//        MarkerOptions options = new MarkerOptions()
//                .position(latLng)
//                .title("I am here!");
//        mMap.addMarker(options);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

        Log.i("checklatlng", "" + currentLatitude + currentLongitude);

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                LatLng latvalue = cameraPosition.target;

                mapCentreLat=latvalue.latitude;
                mapCentreLng =latvalue.longitude;
                for(int i=0;i<eventArrayList.size();i++){
                    if(setUpMap(Double.parseDouble(eventArrayList.get(i).latitude), Double.parseDouble(eventArrayList.get(i).longitude))){
                        showingEventList.add(eventArrayList.get(i));
                    }
                }

                mMap.clear();
                if (locationMarker != null) {

                    locationMarker = mMap.addMarker(new MarkerOptions().draggable(true).position(new LatLng(mapCentreLat, mapCentreLng)).snippet("draggableMarker").icon(BitmapDescriptorFactory.fromResource(R.drawable.location)));
                }
                if(showingEventList.size()>15){
                    for (int i=0;i<15;i++){
                        Log.v("chek",""+showingEventList.get(i).latitude+","+showingEventList.get(i).longitude);
                        //locationMarker=mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(showingEventList.get(i).latitude), Double.parseDouble(showingEventList.get(i).longitude))).snippet(showingEventList.get(i).eventId).icon(BitmapDescriptorFactory.fromResource(R.drawable.eventmap)));
                        if(showingEventList.get(i).latitude!=null && showingEventList.get(i).longitude!=null) {
                            mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(showingEventList.get(i).latitude), Double.parseDouble(showingEventList.get(i).longitude))).snippet(showingEventList.get(i).eventId).icon(BitmapDescriptorFactory.fromResource(R.drawable.eventmap)));
                        }
                    }
                }else if(showingEventList.size()==0){

                }else{
                    for (int i=0;i<showingEventList.size();i++){
                        mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(showingEventList.get(i).latitude.trim()), Double.parseDouble(showingEventList.get(i).longitude.trim()))).snippet(showingEventList.get(i).eventId).icon(BitmapDescriptorFactory.fromResource(R.drawable.eventmap)));
                    }
                }

//                Location selected_location = new Location("A");
//                selected_location.setLatitude(lat);
//                selected_location.setLongitude(lng);
//
//                Location prevLocation = new Location("B");
//                prevLocation.setLatitude(prevLat);
//                prevLocation.setLongitude(prevLong);
//
//                Float distance = selected_location.distanceTo(prevLocation);
//
//                LatLngBounds curScreen=mMap.getProjection().getVisibleRegion().latLngBounds;
//                Log.i("info", "" + curScreen);
//
//               /* prevLat=lat;
//                prevLong=lng;*/
//                int zoomLevel = (int) mMap.getCameraPosition().zoom;
//                Log.i("checkLat", distance + "," + zoomLevel + "," + latvalue);

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        String id = marker.getSnippet();
                        if(id.equals("draggableMarker")){

                        }else {

//                        eventView.setVisibility(View.VISIBLE);
                            populateEvents(id);
                            marker.hideInfoWindow();
                            //String id=marker.getSnippet();
                        }
                        return false;

                    }
                });
            }
        });
    }
    public void getMyEvents(String url) {
        if (isNetworkAvailable()) {
           Map<String, Object> params;
            try {
                JSONObject mainJson = new JSONObject();
                mainJson.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));
                StringEntity s;

                s = new StringEntity(mainJson.toString(), HTTP.UTF_8);

                params = new HashMap<String, Object>();
                params.put(AQuery.POST_ENTITY, s);
                mAqueryTask.sendPostObjectRequest(url, params,JSONObject.class, getMyEventsCallback());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private AjaxCallback<JSONObject> getMyEventsCallback() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);
                Log.e("checkMapResp", result.toString());
                mProgressDialog.dismiss();

                if (result != null) {
                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        int userStatus = outpuJsonObject.optInt("status");
                        if (userStatus == 1) {
                            eventArrayList = ParseUtility.parseAllEventsList(result);
                            Location mLocation=new Location("pinLocation");
                            mLocation.setLatitude(mapCentreLat);
                            mLocation.setLongitude(mapCentreLng);
                            handleNewLocation(mLocation);
                        } else {
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            ;
        };
    }



    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            //Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }


    @Override
    public void onLocationChanged(Location location) {
//        handleNewLocation(location);
    }


//    @Override
//    public boolean onMarkerClick(Marker marker) {
//        String id = marker.getSnippet();
////        marker.getId();
////        Toast.makeText(this, marker.getSize() + " (including " + title + ")", Toast.LENGTH_SHORT).show();
//
//
//        Log.i("check1",""+id);
//        return true;
//    }

    private void populateEvents(final String id) {
        Log.i("check2","check"+id);
        for(int i=0; i<eventArrayList.size();i++) {
            Event mEvent = eventArrayList.get(i);
            if (id.equals(mEvent.eventId)) {
                Log.i("check3", "check");
                event_name.setText(mEvent.eventName);
                if(mEvent.location!=null){
                    eventLocation.setText(mEvent.location);
                }else{
                    eventLocation.setText("");
                }
//
                Date_From.setText(GeneralUtil.convertDate(mEvent.start_date));
                time_From.setText("");
                aq.id(eventImage).image(mEvent.mainImage);
                eventView.setVisibility(View.VISIBLE);
                Log.i("checkDetails", "" + mEvent.eventName + "," + mEvent.eventLocation);
                eventView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MapActivity.this, EventDetailActivity.class);
                        intent.putExtra("eventId", id);
                        startActivity(intent);
                    }
                });

            }
            mMap.setOnMapClickListener(new OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if (eventView.getVisibility()==View.VISIBLE){
                        eventView.setVisibility(View.GONE);
                    }
                }
            });
        }
    }



}
