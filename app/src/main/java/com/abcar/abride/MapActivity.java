package com.abcar.abride;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private MapView mapView;
    private TextView addressText, jenisView, infoView, titikView;
    private String jenis, alamat;
    private Boolean isDestination;
    private Button buttonSet;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    LatLng pickPoint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        addressText = (TextView)findViewById(R.id.addressText);
        jenisView = (TextView)findViewById(R.id.jenisView);
        infoView = (TextView)findViewById(R.id.infoView);
        titikView = (TextView)findViewById(R.id.titikView);
        buttonSet = (Button)findViewById(R.id.buttonSet);
        jenis = getIntent().getStringExtra("jenis");
        jenisView.setText(jenis);
        isDestination = getIntent().getBooleanExtra("is_destination",false);
        buttonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isDestination){
                    editor.putString("jenis", jenis);
                    editor.putString("alamat_tujuan", alamat);
                    editor.putString("latitude_tujuan", String.valueOf(pickPoint.latitude));
                    editor.putString("longitude_tujuan", String.valueOf(pickPoint.longitude));
                    editor.commit();
                    if(jenis.equalsIgnoreCase("deliver")) {
                        Intent i = new Intent(getApplicationContext(), DeliverActivity.class);
                        startActivity(i);
                    } else {
                        Intent i = new Intent(getApplicationContext(), SummaryActivity.class);
                        startActivity(i);
                    }
                }else {
                    editor.putString("alamat_jemput", alamat);
                    editor.putString("latitude_jemput", String.valueOf(pickPoint.latitude));
                    editor.putString("longitude_jemput", String.valueOf(pickPoint.longitude));
                    editor.commit();
                    Intent i = new Intent(getApplicationContext(), MapActivity.class);
                    i.putExtra("jenis",jenis);
                    i.putExtra("is_destination",true);
                    startActivity(i);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final Marker mMarker;
        final LatLng[] jak = {new LatLng(-6.1634976, 106.8119999)};
        if(isDestination){
            infoView.setText("Pilih Titik Tujuan Anda");
            titikView.setText("Titik Tujuan");
            buttonSet.setText("Set Titik Tujuan");
            mMarker = mMap.addMarker(new MarkerOptions().position(jak[0]).title("Titik Tujuan"));
        }else{
            infoView.setText("Pilih Titik Jemput");
            titikView.setText("Titik Jemput");
            buttonSet.setText("Set Titik Jemput");
            mMarker = mMap.addMarker(new MarkerOptions().position(jak[0]).title("Titik Jemput"));
        }
        mMarker.setDraggable(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                jak[0] = latLng;
                mMarker.setPosition(jak[0]);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(jak[0]));
                mMap.setMinZoomPreference(15);
                pickPoint = latLng;
                try {
                    Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    alamat = addresses.get(0).getAddressLine(0) + ", " +
                            addresses.get(0).getAddressLine(1) + ", " + addresses.get(0).getAddressLine(2);
                    addressText.setText(alamat);
                } catch (Exception e) {

                }
            }
        });
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                jak[0] = marker.getPosition();
                mMarker.setPosition(jak[0]);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(jak[0]));
                mMap.setMinZoomPreference(15);
                pickPoint = marker.getPosition();
                try {
                    Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(jak[0].latitude, jak[0].longitude, 1);
                    alamat = addresses.get(0).getAddressLine(0) + ", " +
                            addresses.get(0).getAddressLine(1) + ", " + addresses.get(0).getAddressLine(2);
                    addressText.setText(alamat);
                } catch (Exception e) {

                }
            }
        });
        // Add a marker in Sydney and move the camera

        try {
            Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(jak[0].latitude, jak[0].longitude, 1);
            alamat = addresses.get(0).getAddressLine(0) + ", " +
                    addresses.get(0).getAddressLine(1) + ", " + addresses.get(0).getAddressLine(2);
            addressText.setText(alamat);
        } catch (Exception e) {

        }
        pickPoint = jak[0];
        mMap.moveCamera(CameraUpdateFactory.newLatLng(jak[0]));
        mMap.setMinZoomPreference(15);
    }
    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}
