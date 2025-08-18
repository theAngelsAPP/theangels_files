// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.map;
// IMPORTS
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.graphics.Color;
import android.widget.LinearLayout;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import co.median.android.a2025_theangels_new.R;
import co.median.android.a2025_theangels_new.data.map.MapStyleHelper;
import co.median.android.a2025_theangels_new.data.map.utils.CustomMarkerHelper;
import co.median.android.a2025_theangels_new.data.models.UserSession;
// HomeMapFragment - Displays the main map on the home screen
public class HomeMapFragment extends Fragment implements OnMapReadyCallback {
// VARIABLES
    private GoogleMap mMap;
    private LocationService locationService;
    private Marker userMarker;
    private LinearLayout locationBox;
    private String profileImageUrl;
    private OnAddressChangeListener addressChangeListener;
    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    showMap();
                } else {
                    showPermissionBox();
                }
            });
// Constructs a new HomeMapFragment.
    public HomeMapFragment() {
        super(R.layout.fragment_home_map);
    }
    @Override
// Performs on detach.
    public void onDetach() {
        super.onDetach();
        addressChangeListener = null;
    }
    public interface OnAddressChangeListener {
        void onAddressChanged(String address);
    }
// Updates the address change listener.
    public void setAddressChangeListener(OnAddressChangeListener listener) {
        this.addressChangeListener = listener;
    }
    @Override
// Performs on view created.
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        locationService = new LocationService(requireContext());
        profileImageUrl = UserSession.getInstance().getImageURL();
        locationBox = view.findViewById(R.id.location_box);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        locationBox.setOnClickListener(v ->
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION));
    }
    @Override
// Performs on map ready.
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        applyCustomStyle();
        checkPermission();
    }
// Performs check permission.
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            showMap();
        } else {
            showPermissionBox();
        }
    }
// Performs show map.
    private void showMap() {
        View mapView = requireView().findViewById(R.id.map);
        mapView.setVisibility(View.VISIBLE);
        locationBox.setVisibility(View.GONE);
        startLocationUpdates();
    }
// Performs show permission box.
    private void showPermissionBox() {
        View mapView = requireView().findViewById(R.id.map);
        mapView.setVisibility(View.GONE);
        locationBox.setVisibility(View.VISIBLE);
        if (addressChangeListener != null) {
            addressChangeListener.onAddressChanged(getString(R.string.address_not_found));
        }
    }
// Performs start location updates.
    private void startLocationUpdates() {
        locationService.getCurrentLocation(new LocationService.SimpleLocationListener() {
            @Override
// Performs on location.
            public void onLocation(Location location) {
                updateUserLocation(location);
            }
            @Override
// Performs on error.
            public void onError(Exception e) {
            }
        });
        locationService.startLocationUpdates(new LocationCallback() {
            @Override
// Performs on location result.
            public void onLocationResult(@NonNull LocationResult result) {
                Location location = result.getLastLocation();
                if (location != null) {
                    updateUserLocation(location);
                }
            }
        });
    }
// Performs stop location updates.
    private void stopLocationUpdates() {
        locationService.stopLocationUpdates();
    }
    @Override
// Performs on stop.
    public void onStop() {
        super.onStop();
        stopLocationUpdates();
    }
// Performs update user location.
    private void updateUserLocation(Location location) {
        LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
        if (userMarker == null) {
            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                CustomMarkerHelper.loadMarker(requireContext(), profileImageUrl, 120,
                        Color.parseColor("#2196F3"), descriptor -> {
                            userMarker = mMap.addMarker(new MarkerOptions()
                                    .position(pos)
                                    .icon(descriptor)
                                    .anchor(0.5f, 0.5f)
                                    .title(getString(R.string.your_location)));
                        });
            } else {
                userMarker = MapHelper.addMarker(mMap, pos, getString(R.string.your_location));
            }
        } else {
            userMarker.setPosition(pos);
        }
        MapHelper.moveCamera(mMap, pos, 15f);
        if (addressChangeListener != null) {
            String address = AddressHelper.getAddressFromLatLng(requireContext(), pos.latitude, pos.longitude);
            if (address == null) address = getString(R.string.address_not_found);
            addressChangeListener.onAddressChanged(address);
        }
    }
// Performs apply custom style.
    private void applyCustomStyle() {
        MapStyleHelper.applyStyle(mMap, requireContext(), R.raw.map_style);
    }
}
