// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.map;
// IMPORTS
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
// LocationService - Provides location data to the app
public class LocationService {
// VARIABLES
    private final FusedLocationProviderClient fusedClient;
    private LocationRequest locationRequest;
    private LocationCallback internalCallback;
    public interface SimpleLocationListener {
        void onLocation(Location location);
        void onError(Exception e);
    }
// Constructs a new LocationService.
    public LocationService(Context context) {
        fusedClient = LocationServices.getFusedLocationProviderClient(context);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    @SuppressLint("MissingPermission")
// Returns the current location.
    public void getCurrentLocation(SimpleLocationListener listener) {
        fusedClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        listener.onLocation(location);
                    } else {
                        startSingleUpdate(listener);
                    }
                })
                .addOnFailureListener(listener::onError);
    }
    @SuppressLint("MissingPermission")
// Performs start single update.
    private void startSingleUpdate(SimpleLocationListener listener) {
        LocationCallback callback = new LocationCallback() {
            @Override
// Performs on location result.
            public void onLocationResult(LocationResult result) {
                fusedClient.removeLocationUpdates(this);
                if (result != null && !result.getLocations().isEmpty()) {
                    listener.onLocation(result.getLastLocation());
                } else {
                    listener.onError(new Exception("Location unavailable"));
                }
            }
        };
        fusedClient.requestLocationUpdates(locationRequest, callback, null);
    }
    @SuppressLint("MissingPermission")
// Performs start location updates.
    public void startLocationUpdates(LocationCallback callback) {
        internalCallback = callback;
        fusedClient.requestLocationUpdates(locationRequest, callback, null);
    }
// Performs stop location updates.
    public void stopLocationUpdates() {
        if (internalCallback != null) {
            fusedClient.removeLocationUpdates(internalCallback);
        }
    }
}
