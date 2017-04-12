package com.example.yagami.lbbtestapplication;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

public class PlaceSearcher extends AppCompatActivity{

    public static GoogleApiClient googleApiClient;
    private AutoCompleteTextView autoCompleteTextView;
    private PlaceSearcherAdapter placeSearcherAdapter;
    private String placeId;
    public static final String PlaceID = "YAGAMI";
    private static final LatLngBounds BOUNDS_GREATER_DELHI = new LatLngBounds(new LatLng(28.7041,77.1025),new LatLng(28.9011,77.3150));
    private AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setCountry("IN").setTypeFilter(AutocompleteFilter.TYPE_FILTER_REGIONS).setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS).setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_GEOCODE).build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this.getApplicationContext(),"HardCoded City for this Application is Delhi",Toast.LENGTH_LONG).show();
        googleApiClient = new GoogleApiClient.Builder(this).addApi(Places.GEO_DATA_API).addApi(Places.PLACE_DETECTION_API).build();
        autoCompleteTextView = (AutoCompleteTextView)
                findViewById(R.id.autocomplete_places);
        autoCompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        placeSearcherAdapter = new PlaceSearcherAdapter(this,googleApiClient, BOUNDS_GREATER_DELHI,typeFilter);
        autoCompleteTextView.setAdapter(placeSearcherAdapter);
        Button clearButton = (Button) findViewById(R.id.button_clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteTextView.setText("");
            }
        });
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final AutocompletePrediction item = placeSearcherAdapter.getItem(position);
            placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(googleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(),MapMarker.class);
            intent.putExtra(PlaceID,placeId);
            startActivity(intent);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                places.release();
                return;
            }
        }
    };

}
