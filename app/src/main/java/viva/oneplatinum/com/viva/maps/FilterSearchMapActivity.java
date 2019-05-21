package viva.oneplatinum.com.viva.maps;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import viva.oneplatinum.com.viva.R;

public class FilterSearchMapActivity extends FragmentActivity {
	
	GoogleMap googleMap;	
	SharedPreferences sharedPreferences;	
	int locationCount = 0;
 	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_google);
		
		
		// Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // Showing status
        if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        }else { // Google Play Services are available        	

            // Getting reference to the SupportMapFragment of activity_main.xml
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            // Getting GoogleMap object from the fragment
            googleMap = fm.getMap();

            // Enabling MyLocation Layer of Google Map
            googleMap.setMyLocationEnabled(true);           
            
            // Opening the sharedPreferences object
    		sharedPreferences = getSharedPreferences("location", 0);
    		
    		// Getting number of locations already stored
    		locationCount = sharedPreferences.getInt("locationCount", 0);
    		
    		    		
    		// Getting stored zoom level if exists else return 0
    		String zoom = sharedPreferences.getString("zoom", "0");
    		
    		// If locations are already saved
    		if(locationCount!=0){  			
    			
    			String lat = "";
    			String lng = "";
    			
    			// Iterating through all the locations stored
    			for(int i=0;i<locationCount;i++){
    				
    				// Getting the latitude of the i-th location
    				lat = sharedPreferences.getString("lat"+i,"0");
    				
    				// Getting the longitude of the i-th location
    				lng = sharedPreferences.getString("lng"+i,"0");
    				
    				// Drawing marker on the map
        			drawMarker(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));    				
    			}
    			
    			// Moving CameraPosition to last clicked position
    			googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))));
    			
    			// Setting the zoom level in the map on last position  is clicked
    			googleMap.animateCamera(CameraUpdateFactory.zoomTo(Float.parseFloat(zoom)));   							
    		}
    		
        }        
        
        googleMap.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng point) {		
				locationCount++;
									
				// Drawing marker on the map
				drawMarker(point);
		        
		        /** Opening the editor object to write data to sharedPreferences */
		        SharedPreferences.Editor editor = sharedPreferences.edit();	        
		        
		        // Storing the latitude for the i-th location
		        editor.putString("lat"+ Integer.toString((locationCount-1)), Double.toString(point.latitude));
		        
		        // Storing the longitude for the i-th location
		        editor.putString("lng"+ Integer.toString((locationCount-1)), Double.toString(point.longitude));
		        
		        // Storing the count of locations or marker count
		        editor.putInt("locationCount", locationCount);		        
		        
		        /** Storing the zoom level to the shared preferences */
		        editor.putString("zoom", Float.toString(googleMap.getCameraPosition().zoom));		        

		        /** Saving the values stored in the shared preferences */
		        editor.commit();	        
		        
		        Toast.makeText(getBaseContext(), "Marker is added to the Map", Toast.LENGTH_SHORT).show();			        
		        
			}
		});	
        
        
        googleMap.setOnMapLongClickListener(new OnMapLongClickListener() {				
			@Override
			public void onMapLongClick(LatLng point) {
				
				// Removing the marker and circle from the Google Map
				googleMap.clear();
				
				// Opening the editor object to delete data from sharedPreferences
		        SharedPreferences.Editor editor = sharedPreferences.edit();
		        
		        // Clearing the editor
		        editor.clear();
				
		        // Committing the changes
				editor.commit();
				
				// Setting locationCount to zero
				locationCount=0;
				
			}
		});           
        
        
	}
     
    private void drawMarker(LatLng point){
    	// Creating an instance of MarkerOptions
    	MarkerOptions markerOptions = new MarkerOptions();					
    		
    	// Setting latitude and longitude for the marker
    	markerOptions.position(point);
    		
    	// Adding marker on the Google Map
    	googleMap.addMarker(markerOptions);    		
    }
        
        



}
