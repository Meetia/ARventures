package jumbois.arventures;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.nfc.Tag;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener{

    private GoogleMap mMap;
    LatLng curr_loc = new LatLng(42.407266, -71.120172);
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final int REQUEST_LOCATION_PERMISSION = 300;
    Marker marker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Fabric.with(this, new Crashlytics());
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // COORDS FOR TUFTS UNIVERSITY: 42.407266, -71.120172
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);



        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        System.out.println(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }

        Location location = locationManager.getLastKnownLocation(locationManager
                .getBestProvider(criteria, false));
        if (location != null)
            curr_loc = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions mkr = new MarkerOptions().position(curr_loc).title("Your location");
        marker = mMap.addMarker(mkr);
        float zoomLevel = 16.0f;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curr_loc, zoomLevel));
        mMap.setOnMapClickListener(this);


        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    @Override
    public void onMapClick(LatLng pos) {
        // TODO Auto-generated method stub
        Log.d("arg0", pos.latitude + "-" + pos.longitude);
        marker.setPosition(pos);
    }

    public void onStartClick(View view) {
        Intent getMainScreenIntent = new Intent(this, MainActivity.class);
        LatLng addrPos = marker.getPosition();

        EditText et = (EditText) findViewById(R.id.addressInput);
        String addr = et.getText().toString();
        String addrFound = "";
        if (addr != "") {
            Geocoder geocoder = new Geocoder(this);
            List<Address> addresses;
            try {
                addresses = geocoder.getFromLocationName(addr, 1);
                if(addresses != null && addresses.size() > 0) {
                    double latitude= addresses.get(0).getLatitude();
                    double longitude= addresses.get(0).getLongitude();
                    addrFound = addresses.get(0).getAddressLine(0);
                    addrPos = new LatLng(latitude, longitude);
                }
            } catch (IOException ioException) {
                Log.e("Exception", "Service not available");
            }
        }
        
        Bundle args = new Bundle();
        args.putParcelable("position", addrPos);
        getMainScreenIntent.putExtra("addrLine", addrFound);
        getMainScreenIntent.putExtra("bundle", args);
        startActivity(getMainScreenIntent);
    }

}
