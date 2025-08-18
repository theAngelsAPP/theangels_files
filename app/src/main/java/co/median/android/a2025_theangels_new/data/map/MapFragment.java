// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.map;
// IMPORTS
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import co.median.android.a2025_theangels_new.R;
import co.median.android.a2025_theangels_new.data.map.MapStyleHelper;
// MapFragment - Base fragment handling map interactions
public class MapFragment extends Fragment implements OnMapReadyCallback {
// VARIABLES
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LinearLayout mapPlaceholder;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private OnAddressChangeListener addressChangeListener;
    private final ActivityResultLauncher<String> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    enableUserLocation();
                } else {
                    showPlaceholder();
                }
            });
// Constructs a new MapFragment.
    public MapFragment() {
        super(R.layout.fragment_map);
    }
    @Override
// Performs on detach.
    public void onDetach() {
        super.onDetach();
        addressChangeListener = null;
    }
// Updates the address change listener.
    public void setAddressChangeListener(OnAddressChangeListener listener) {
        this.addressChangeListener = listener;
    }
    public interface OnAddressChangeListener {
        void onAddressChanged(String address);
    }
    @Override
// Performs on view created.
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        mapPlaceholder = view.findViewById(R.id.map_placeholder);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationCallback = new LocationCallback() {
            @Override
// Performs on location result.
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    updateMapLocation(location);
                }
            }
        };
        mapPlaceholder.setOnClickListener(v ->
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION));
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }
    @Override
// Performs on map ready.
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        applyCustomMapStyle();
        checkLocationPermission();
    }
// Performs apply custom map style.
    private void applyCustomMapStyle() {
        MapStyleHelper.applyStyle(mMap, requireContext(), R.raw.map_style);
    }
// Performs check location permission.
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation();
        } else {
            showPlaceholder();
        }
    }
// Performs enable user location.
    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            View mapView = requireView().findViewById(R.id.map);
            mapView.setVisibility(View.VISIBLE);
            mapPlaceholder.setVisibility(View.GONE);
            mMap.setMyLocationEnabled(true);
            startLocationUpdates();
        }
    }
// Performs show placeholder.
    private void showPlaceholder() {
        View mapView = requireView().findViewById(R.id.map);
        mapView.setVisibility(View.GONE);
        mapPlaceholder.setVisibility(View.VISIBLE);
        if (addressChangeListener != null) {
            addressChangeListener.onAddressChanged(getString(R.string.address_not_found));
        }
    }
// Returns the user location.
    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        updateMapLocation(location);
                    }
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }
// Performs start location updates.
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        getUserLocation();
    }
// Performs stop location updates.
    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
    @Override
// Performs on stop.
    public void onStop() {
        super.onStop();
        stopLocationUpdates();
    }
// Performs update map location.
    private void updateMapLocation(Location location) {
        LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(userLatLng)
                .zoom(15)
                .tilt(30)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(userLatLng)
                .title(getString(R.string.your_location))
                .icon(resizeMarker(R.drawable.custom_marker, 130, 130)));
        if (addressChangeListener != null) {
            String address = getAddressFromLocation(location);
            addressChangeListener.onAddressChanged(address);
        }
    }
// Performs resize marker.
    private BitmapDescriptor resizeMarker(int drawableRes, int width, int height) {
        Drawable drawable = ContextCompat.getDrawable(requireContext(), drawableRes);
        if (drawable == null) return null;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
// Returns the address from location.
    private String getAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }
        } catch (IOException ignored) {}
        return getString(R.string.address_not_found);
    }
}
