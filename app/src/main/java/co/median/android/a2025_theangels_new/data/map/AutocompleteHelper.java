// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.map;
// IMPORTS
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.gms.common.api.Status;
import android.util.Log;
import android.widget.Toast;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import co.median.android.a2025_theangels_new.R;
// AutocompleteHelper - Manages place autocomplete interactions
public class AutocompleteHelper {
// Performs init places.
    public static void initPlaces(Context context) {
        if (!Places.isInitialized()) {
            String apiKey = context.getString(R.string.google_places_key);
            if (apiKey == null || apiKey.isEmpty()) {
                Toast.makeText(context, R.string.places_init_error, Toast.LENGTH_SHORT).show();
                Log.e("AutocompleteHelper", "Google Places API key missing");
                return;
            }
            Places.initialize(context.getApplicationContext(), apiKey, new Locale("he"));
        }
    }
// Performs open city autocomplete.
    public static void openCityAutocomplete(Activity activity, int requestCode) {
        initPlaces(activity);
        if (!Places.isInitialized()) {
            return;
        }
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .setTypeFilter(TypeFilter.CITIES)
                .setCountries(Arrays.asList("IL"))
                .build(activity);
        activity.startActivityForResult(intent, requestCode);
    }
// Performs open address autocomplete.
    public static void openAddressAutocomplete(Activity activity, int requestCode) {
        initPlaces(activity);
        if (!Places.isInitialized()) {
            return;
        }
        List<Place.Field> fields = Arrays.asList(
                Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .setCountries(Arrays.asList("IL"))
                .build(activity);
        activity.startActivityForResult(intent, requestCode);
    }
// Returns the place from result.
    public static Place getPlaceFromResult(Intent data) {
        return Autocomplete.getPlaceFromIntent(data);
    }
// Returns the error status.
    public static Status getErrorStatus(Intent data) {
        return Autocomplete.getStatusFromIntent(data);
    }
}
