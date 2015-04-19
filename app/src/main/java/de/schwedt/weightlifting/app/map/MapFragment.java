package de.schwedt.weightlifting.app.map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.schwedt.weightlifting.app.R;
import de.schwedt.weightlifting.app.WeightliftingApp;

/**
 * Created by Benni on 31.01.14.
 */

public class MapFragment extends SupportMapFragment {
    GoogleMap map;

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Test for Google Play Services
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()) != ConnectionResult.SUCCESS) {
            Log.d(WeightliftingApp.TAG, "Google Play Services not availabe");
            return;
        }

        map = getMap();
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker marker) {

                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater(savedInstanceState).inflate(R.layout.map_info_window_layout, null);

                // Getting reference to the TextView to set title
                TextView note = (TextView) v.findViewById(R.id.marker_note);
                note.setText(marker.getTitle());

                // Getting reference to the TextView to set snippet
                TextView snippet = (TextView) v.findViewById(R.id.marker_snippet);
                snippet.setText(marker.getSnippet());

                // Getting reference to the ImageView to set MarkerImage
                ImageView markerImage = (ImageView) v.findViewById(R.id.marker_image);
//                markerImage.setImageDrawable(R.drawable.hpi_panorama);


                // Returning the view containing InfoWindow contents
                return v;

            }

        });

        setRetainInstance(true);

        LatLng pos = new LatLng(52.39387, 13.13166);
        CameraUpdate center = CameraUpdateFactory.newLatLng(pos);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

        map.moveCamera(center);
        map.animateCamera(zoom);

        //Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.map_marker);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        Bitmap halfIcon = Bitmap.createScaledBitmap(icon, icon.getWidth() / 4, icon.getHeight() / 4, false);


        Marker newMarker = map.addMarker(new MarkerOptions()
                .position(new LatLng(52.39387, 13.13166))
                .title("Hasso-Plattner-Institut")
                .icon(BitmapDescriptorFactory.fromBitmap(halfIcon))
                .snippet("Potsdam .... Adress to be added"));
        Marker hoersaalMarker = map.addMarker(new MarkerOptions()
                .position(new LatLng(52.39407, 13.13232))
                .title("Hörsäle 1-3")
                .icon(BitmapDescriptorFactory.fromBitmap(halfIcon))
                .snippet("Inklusive Ulfs Café"));

        Marker hautgebaeudeMarker = map.addMarker(new MarkerOptions()
                .position(new LatLng(52.39396, 13.13348))
                .title("Hauptgebäude")
                .icon(BitmapDescriptorFactory.fromBitmap(halfIcon))
                .snippet("Empfang, Seminar- und Poolräume"));

        Marker abcMarker = map.addMarker(new MarkerOptions()
                .position(new LatLng(52.39341, 13.13152))
                .title("ABC Gebäude")
                .icon(BitmapDescriptorFactory.fromBitmap(halfIcon))
                .snippet("Ulfs Café, Sitz verschiedener Lehrstühle, Poolräume"));
    }
}