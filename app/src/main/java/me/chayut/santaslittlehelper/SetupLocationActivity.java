package me.chayut.santaslittlehelper;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import me.chayut.SantaHelperLogic.SantaLogic;
import me.chayut.SantaHelperLogic.SantaLocation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class SetupLocationActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapClickListener,GoogleMap.OnMapLongClickListener {


    static final int REQUEST_ACTION =1;
    private final static String TAG = "SetupLocationActivity";
    Button btnOK, btnCancel,btnGetCurrentLoc;
    private GoogleMap mMap;
    private Marker hereMarker;
    private SantaLocation mLocation;
    private EditText etLocName;

    private LocationManager locationManager;

    private boolean mLocationSelected = false;
    final LocationListener locationListener = new LocationListener()
    {
        public void onLocationChanged(Location location) {

            Log.d(TAG,String.format("Location: %s",location.toString()));

            mMap.clear();
            LatLng mLatLag = new LatLng(location.getLatitude(),location.getLongitude());
            hereMarker =  mMap.addMarker(new MarkerOptions().position(mLatLag).title("This Location"));
            mLocation.setLatitude(location.getLatitude());
            mLocation.setLongitude(location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLag, 10));

            mLocationSelected = true;

            Toast.makeText(getBaseContext(),"Current Location Acquired",
                    Toast.LENGTH_SHORT).show();

            locationManager.removeUpdates(locationListener);

        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_location);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_set);
        mapFragment.getMapAsync(this);


        mLocation = new SantaLocation();

        etLocName = (EditText) findViewById(R.id.etLocName);


        btnOK = (Button) findViewById(R.id.btnOK);
        btnOK.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {

                        if(mLocationSelected) {
                            Intent intent = new Intent();
                            mLocation.setName(etLocName.getText().toString());
                            intent.putExtra(SantaLogic.EXTRA_SANTA_LOCATION, mLocation);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(getBaseContext(),"You have not select location",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        setResult(RESULT_CANCELED, intent);
                        finish();
                    }
                }
        );

        btnGetCurrentLoc = (Button) findViewById(R.id.btnCurrentLoc);
        btnGetCurrentLoc.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        locationManager = (LocationManager) SetupLocationActivity.this.getSystemService(Context.LOCATION_SERVICE);
                        try
                        {
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                        } catch (SecurityException e){}
                    }
                }
        );

        //prompt user
        Toast.makeText(getBaseContext(),"Click on map to Select location",
                Toast.LENGTH_LONG).show();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-33.86997, 151.2089);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10));

        mMap.setOnMapClickListener(this);
        //mMap.setOnMapLongClickListener(this);

        if(getIntent().hasExtra(SantaLogic.EXTRA_SANTA_LOCATION))
        {
            mLocation =getIntent().getParcelableExtra(SantaLogic.EXTRA_SANTA_LOCATION);
            etLocName.setText(mLocation.getName());

            LatLng mLatLag = new LatLng(mLocation.getLatitude(),mLocation.getLongitude());
            hereMarker =  mMap.addMarker(new MarkerOptions().position(mLatLag).title("This Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLag, 10));

            mLocationSelected = true;
        }

    }

    @Override
    public void onMapClick(LatLng point)
    {
        mMap.clear();
        hereMarker =  mMap.addMarker(new MarkerOptions().position(point).title("This Location"));
        mLocation.setLatitude(point.latitude);
        mLocation.setLongitude(point.longitude);
        mLocationSelected = true;
    }

    @Override
    public void onMapLongClick(LatLng point)
    {
        mMap.addMarker(new MarkerOptions().position(point).title("Reference Location"));
    }


}
