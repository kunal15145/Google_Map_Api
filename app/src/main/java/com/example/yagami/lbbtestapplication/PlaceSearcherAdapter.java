package com.example.yagami.lbbtestapplication;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;
import android.content.Context;
import android.graphics.Typeface;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlaceSearcherAdapter extends ArrayAdapter<AutocompletePrediction> implements Filterable {

    private static final String TAG = "Adapter Debugging";
    private ArrayList<AutocompletePrediction> mResultList;
    private GoogleApiClient googleApiClient;
    private LatLngBounds mBounds;
    private boolean flag;
    private AutocompleteFilter mPlaceFilter;
    public PlaceSearcherAdapter(Context context, GoogleApiClient googleApiClient, LatLngBounds bounds, AutocompleteFilter filter) {
        super(context, android.R.layout.simple_expandable_list_item_2, android.R.id.text1);
        this.googleApiClient = googleApiClient;
        mBounds = bounds;
        mPlaceFilter = filter;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence datafilter) {
                FilterResults filterResults = new FilterResults();
                ArrayList<AutocompletePrediction> filterData = new ArrayList<>();
                if (datafilter != null) filterData = getAutocomplete(datafilter);
                filterResults.values = filterData;
                if (filterData != null) filterResults.count = filterData.size();
                else filterResults.count = 0;
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence datafilter, FilterResults filterResults) {
                if (filterResults != null && filterResults.count > 0) {
                    mResultList = (ArrayList<AutocompletePrediction>)filterResults.values;
                    notifyDataSetChanged();
                } else notifyDataSetInvalidated();
            }
            @Override
            public CharSequence convertResultToString(Object resultValue) {
                if (resultValue instanceof AutocompletePrediction) return ((AutocompletePrediction)resultValue).getFullText(null);
                else return super.convertResultToString(resultValue);
            }
        };
    }

    private ArrayList<AutocompletePrediction> getAutocomplete(CharSequence PlacetoSearch) {
        googleApiClient.connect();
        if (googleApiClient.isConnected()) {
            PendingResult<AutocompletePredictionBuffer> results=Places.GeoDataApi.getAutocompletePredictions(googleApiClient,PlacetoSearch.toString(),mBounds,mPlaceFilter);
            AutocompletePredictionBuffer autocompletePredictions = results.await(60,TimeUnit.SECONDS);
            final Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess()) {
                Toast.makeText(getContext(), "Error contacting API: " + status.toString(), Toast.LENGTH_SHORT).show();
                autocompletePredictions.release();
                return null;
            }
            return DataBufferUtils.freezeAndClose(autocompletePredictions);
        }
        Log.e(TAG, "Google API client is not connected for autocomplete query.");
        return null;
    }

    @Override
    public int getCount() {return mResultList.size();}

    @Override
    public AutocompletePrediction getItem(int position) {return mResultList.get(position);}
        
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View element = super.getView(position, convertView, parent);
        AutocompletePrediction item = getItem(position);
        TextView textView1 = (TextView)element.findViewById(android.R.id.text1);
        textView1.setText(item.getPrimaryText(new StyleSpan(Typeface.BOLD)));
        TextView textView = (TextView)element.findViewById(android.R.id.text2);
        textView.setText(item.getSecondaryText(new StyleSpan(Typeface.BOLD_ITALIC)));
        return element;
    }
        
    public int check() {
         if(flag) return 1;
         else return 2;
    }        
}
