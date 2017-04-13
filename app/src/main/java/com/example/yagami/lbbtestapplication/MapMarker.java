package com.example.yagami.lbbtestapplication;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapMarker extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String PlaceID;
    private TextView mname,maddress,mphone,mrating,murl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mname=(TextView)findViewById(R.id.name1);
        maddress=(TextView)findViewById(R.id.address1);
        mphone=(TextView)findViewById(R.id.phonenumber1);
        mrating=(TextView)findViewById(R.id.rating);
        murl=(TextView)findViewById(R.id.website);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent intent = getIntent();
        PlaceID = intent.getStringExtra(PlaceSearcher.PlaceID);
        Log.d("Message",PlaceID);
        Places.GeoDataApi.getPlaceById(PlaceSearcher.googleApiClient,PlaceID)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess()) {
                            final Place myPlace = places.get(0);
                            LatLng queriedLocation = myPlace.getLatLng();
                            mMap.addMarker(new MarkerOptions().position(queriedLocation));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(queriedLocation));
                            mMap.animateCamera(CameraUpdateFactory.zoomIn());
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(15),2000,null);
                            murl.setText(getString(R.string.URL,String.valueOf(myPlace.getWebsiteUri())));
                            mrating.setText(getString(R.string.Rating,String.valueOf(myPlace.getRating())));
                            mphone.setText(getString(R.string.PhoneNumber,String.valueOf(myPlace.getPhoneNumber())));
                            maddress.setText(getString(R.string.Address,String.valueOf(myPlace.getAddress())));
                            mname.setText(getString(R.string.Name,String.valueOf(myPlace.getName())));
                        }
                        places.release();
                    }
                });
    }
}
