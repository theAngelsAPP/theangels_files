// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.map;
// IMPORTS
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
// MapHelper - Helper functions for common map operations
public class MapHelper {
// Performs add marker.
    public static Marker addMarker(GoogleMap map, LatLng pos, String title) {
        MarkerOptions options = new MarkerOptions().position(pos).title(title);
        return map.addMarker(options);
    }
// Performs move camera.
    public static void moveCamera(GoogleMap map, LatLng pos, float zoom) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, zoom));
    }
    public static Marker[] updateLiveMarkers(GoogleMap map,
                                             Marker userMarker,
                                             Marker volunteerMarker,
                                             LatLng userPos,
                                             LatLng volunteerPos) {
        if (userMarker == null) {
            userMarker = addMarker(map, userPos, "User");
        } else {
            userMarker.setPosition(userPos);
        }
        if (volunteerMarker == null) {
            volunteerMarker = addMarker(map, volunteerPos, "Volunteer");
        } else {
            volunteerMarker.setPosition(volunteerPos);
        }
        return new Marker[]{userMarker, volunteerMarker};
    }
// Performs open navigation.
    public static void openNavigation(Context context, double lat, double lng) {
        Uri uri = Uri.parse("google.navigation:q=" + lat + "," + lng + "&mode=w");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        context.startActivity(intent);
    }
// Performs open street view.
    public static void openStreetView(Context context, double lat, double lng) {
        Uri uri = Uri.parse("google.streetview:cbll=" + lat + "," + lng);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        context.startActivity(intent);
    }
}
