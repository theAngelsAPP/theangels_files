// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.map.utils;
// IMPORTS
import android.content.Context;
import android.graphics.Color;
import android.animation.ValueAnimator;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import co.median.android.a2025_theangels_new.R;
import co.median.android.a2025_theangels_new.data.map.MapStyleHelper;
// MapUtils - Utility methods for map calculations
public class MapUtils {
// Performs apply dark style.
    public static void applyDarkStyle(GoogleMap map, Context ctx) {
        MapStyleHelper.applyStyle(map, ctx, R.raw.map_style);
    }
// Performs move3 d camera.
    public static void move3DCamera(GoogleMap map, LatLng target, float bearing) {
        CameraPosition pos = new CameraPosition.Builder()
                .target(target)
                .zoom(MapConstants.CAMERA_ZOOM)
                .tilt(MapConstants.CAMERA_TILT)
                .bearing(bearing)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(pos));
    }
// Performs fade in polyline.
    public static void fadeInPolyline(Polyline line) {
        if (line == null) return;
        final int color = line.getColor();
        ValueAnimator anim = ValueAnimator.ofInt(0, 255);
        anim.setDuration(MapConstants.POLYLINE_FADE_DURATION_MS);
        anim.addUpdateListener(v -> {
            int alpha = (int) v.getAnimatedValue();
            line.setColor(Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color)));
        });
        anim.start();
    }
// Performs fit markers.
    public static void fitMarkers(GoogleMap map, LatLng p1, LatLng p2) {
        com.google.android.gms.maps.model.LatLngBounds bounds =
                new com.google.android.gms.maps.model.LatLngBounds.Builder()
                        .include(p1).include(p2).build();
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120));
    }
}
