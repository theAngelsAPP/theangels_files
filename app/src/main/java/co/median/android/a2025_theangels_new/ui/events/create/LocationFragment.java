// =======================================
// IMPORTS
// =======================================
package co.median.android.a2025_theangels_new.ui.events.create;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import co.median.android.a2025_theangels_new.data.map.AddressHelper;
import co.median.android.a2025_theangels_new.data.map.AutocompleteHelper;
import co.median.android.a2025_theangels_new.data.map.LocationService;
import co.median.android.a2025_theangels_new.data.map.MapHelper;
import co.median.android.a2025_theangels_new.data.map.MapStyleHelper;

import com.google.android.libraries.places.api.Places;
import co.median.android.a2025_theangels_new.ui.events.create.ManualLocationDialogFragment;

import java.util.Locale;

import co.median.android.a2025_theangels_new.R;
import co.median.android.a2025_theangels_new.ui.events.create.NewEventViewModel;
import androidx.lifecycle.ViewModelProvider;

// =======================================
// LocationFragment - Displays a map with optional manual address input
// =======================================
public class LocationFragment extends Fragment implements OnMapReadyCallback, ManualLocationDialogFragment.OnLocationSelected {

    // =======================================
    // VARIABLES
    // =======================================
    private GoogleMap mMap;
    private LocationService locationService;
    private Marker locationMarker;
    private LinearLayout locationBox;
    private TextView tvAddress;
    private Button btnManualLocation;
    private TextView tvManualMode;
    private boolean manualMode = false;
    private LatLng manualLatLng;

    private NewEventViewModel viewModel;

    private LatLng pendingLocation;
    private String pendingAddress;

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    showMap();
                } else {
                    showPermissionBox();
                }
            });

    // =======================================
    // onCreateView - Inflates the layout for the fragment
    // =======================================
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    // =======================================
    // onViewCreated - Initializes map and manual location logic
    // =======================================
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(NewEventViewModel.class);

        if (viewModel.getEventLocation() != null) {
            double lat = viewModel.getEventLocation().getLatitude();
            double lng = viewModel.getEventLocation().getLongitude();
            pendingLocation = new LatLng(lat, lng);
            pendingAddress = AddressHelper.getAddressFromLatLng(requireContext(), lat, lng);
            manualMode = true;
        }

        btnManualLocation = view.findViewById(R.id.btnManualLocation);
        tvManualMode = view.findViewById(R.id.tv_manual_mode);
        locationBox = view.findViewById(R.id.location_box);
        tvAddress = view.findViewById(R.id.tv_current_address);
        locationService = new LocationService(requireContext());

        AutocompleteHelper.initPlaces(requireContext());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnManualLocation.setOnClickListener(v ->
                new ManualLocationDialogFragment().show(getChildFragmentManager(), "manualLocation"));



    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        applyCustomStyle();
        mMap.setOnMapClickListener(this::handleMapSelection);
        mMap.setOnMapLongClickListener(this::handleMapSelection);
        checkPermission();
        if (pendingLocation != null) {
            String addr = pendingAddress != null ? pendingAddress : getString(R.string.address_not_found);
            updateManualLocation(pendingLocation, addr);
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            showMap();
        } else {
            showPermissionBox();
        }
    }

    private void showMap() {
        View mapView = requireView().findViewById(R.id.map);
        mapView.setVisibility(View.VISIBLE);
        locationBox.setVisibility(View.GONE);
        if (!manualMode) {
            tvManualMode.setVisibility(View.GONE);
            startLocationUpdates();
        }
    }

    private void showPermissionBox() {
        View mapView = requireView().findViewById(R.id.map);
        mapView.setVisibility(View.GONE);
        locationBox.setVisibility(View.VISIBLE);
        tvAddress.setText(getString(R.string.address_not_found));
    }

    private void startLocationUpdates() {
        locationService.getCurrentLocation(new LocationService.SimpleLocationListener() {
            @Override
            public void onLocation(android.location.Location location) {
                if (!manualMode) {
                    updateLiveLocation(location);
                }
            }

            @Override
            public void onError(Exception e) {
            }
        });

        locationService.startLocationUpdates(new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                android.location.Location location = result.getLastLocation();
                if (location != null && !manualMode) {
                    updateLiveLocation(location);
                }
            }
        });
    }

    private void stopLocationUpdates() {
        locationService.stopLocationUpdates();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    private void updateLiveLocation(android.location.Location location) {
        LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
        if (locationMarker == null) {
            locationMarker = MapHelper.addMarker(mMap, pos, getString(R.string.your_location));
        } else {
            locationMarker.setPosition(pos);
        }
        MapHelper.moveCamera(mMap, pos, 15f);

        viewModel.setEventLocation(new com.google.firebase.firestore.GeoPoint(pos.latitude, pos.longitude));

        String address = AddressHelper.getAddressFromLatLng(requireContext(), pos.latitude, pos.longitude);
        if (address == null) address = getString(R.string.address_not_found);
        tvAddress.setText(address);
    }

    private void updateManualLocation(LatLng pos, String address) {
        manualLatLng = pos;
        manualMode = true;
        stopLocationUpdates();
        pendingLocation = pos;
        pendingAddress = address;

        if (mMap != null) {
            if (locationMarker == null) {
                locationMarker = MapHelper.addMarker(mMap, pos, getString(R.string.your_location));
            } else {
                locationMarker.setPosition(pos);
            }
            MapHelper.moveCamera(mMap, pos, 15f);
        }

        tvAddress.setText(address);
        tvManualMode.setVisibility(View.VISIBLE);
        viewModel.setEventLocation(new com.google.firebase.firestore.GeoPoint(pos.latitude, pos.longitude));
    }

    @Override
    public void onLocationSelected(LatLng latLng, String address) {
        updateManualLocation(latLng, address);
    }

    @Override
    public void onUseCurrentLocation() {
        manualMode = false;
        manualLatLng = null;
        tvManualMode.setVisibility(View.GONE);
        startLocationUpdates();
    }


    private void handleMapSelection(LatLng pos) {
        String address = AddressHelper.getAddressFromLatLng(requireContext(), pos.latitude, pos.longitude);
        if (address == null) address = getString(R.string.address_not_found);
        updateManualLocation(pos, address);
    }

    private void applyCustomStyle() {
        MapStyleHelper.applyStyle(mMap, requireContext(), R.raw.map_style);
    }
}
