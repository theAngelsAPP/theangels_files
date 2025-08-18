// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.map;
// IMPORTS
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import com.google.android.gms.maps.model.LatLng;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
// AddressHelper - Provides address lookup and formatting utilities
public class AddressHelper {
// Returns the address from lat lng.
    public static String getAddressFromLatLng(Context context, double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, new Locale("he"));
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }
        } catch (IOException ignored) {
        }
        return null;
    }
// Returns the lat lng from address.
    public static LatLng getLatLngFromAddress(Context context, String address) {
        Geocoder geocoder = new Geocoder(context, new Locale("he"));
        try {
            List<Address> list = geocoder.getFromLocationName(address, 1);
            if (list != null && !list.isEmpty()) {
                Address addr = list.get(0);
                return new LatLng(addr.getLatitude(), addr.getLongitude());
            }
        } catch (IOException ignored) {
        }
        return null;
    }
}
