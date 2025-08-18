package co.median.android.a2025_theangels_new.ui.events.create;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import co.median.android.a2025_theangels_new.R;
import co.median.android.a2025_theangels_new.data.map.AutocompleteHelper;

public class ManualLocationDialogFragment extends DialogFragment {

    public interface OnLocationSelected {
        void onLocationSelected(LatLng latLng, String address);
        void onUseCurrentLocation();
    }

    private OnLocationSelected callback;
    private EditText etAddress;
    private ListView lvSuggestions;
    private Button btnUseCurrent;

    private ArrayAdapter<String> suggestionsAdapter;
    private final ArrayList<String> suggestionList = new ArrayList<>();
    private final HashMap<String, String> suggestionIdMap = new HashMap<>();
    private PlacesClient placesClient;
    private AutocompleteSessionToken sessionToken;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnLocationSelected) {
            callback = (OnLocationSelected) getParentFragment();
        } else if (context instanceof OnLocationSelected) {
            callback = (OnLocationSelected) context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_manual_location, null);
        etAddress = view.findViewById(R.id.etDialogAddress);
        lvSuggestions = view.findViewById(R.id.lvDialogSuggestions);
        btnUseCurrent = view.findViewById(R.id.btnDialogUseCurrent);
        Button btnCancel = view.findViewById(R.id.btnDialogCancel);

        suggestionsAdapter = new ArrayAdapter<>(requireContext(), R.layout.address_suggestion_item, suggestionList);
        lvSuggestions.setAdapter(suggestionsAdapter);
        lvSuggestions.setOnItemClickListener((parent, v, position, id) -> {
            String address = suggestionList.get(position);
            String placeId = suggestionIdMap.get(address);
            handleSuggestionSelection(address, placeId);
        });

        AutocompleteHelper.initPlaces(requireContext());
        if (Places.isInitialized()) {
            placesClient = Places.createClient(requireContext());
            sessionToken = AutocompleteSessionToken.newInstance();
        }

        etAddress.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fetchAddressSuggestions(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) { }
        });

        etAddress.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                handleManualAddress();
                return true;
            }
            return false;
        });

        btnUseCurrent.setOnClickListener(v -> {
            if (callback != null) {
                callback.onUseCurrentLocation();
            }
            dismiss();
        });

        btnCancel.setOnClickListener(v -> dismiss());

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setView(view);
        return builder.create();
    }

    private void fetchAddressSuggestions(String query) {
        if (placesClient == null || query.length() < 3) {
            suggestionList.clear();
            suggestionIdMap.clear();
            suggestionsAdapter.notifyDataSetChanged();
            lvSuggestions.setVisibility(View.GONE);
            return;
        }

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(sessionToken)
                .setTypeFilter(TypeFilter.ADDRESS)
                .setCountries(java.util.Collections.singletonList("IL"))
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener(response -> {
                    suggestionList.clear();
                    suggestionIdMap.clear();
                    for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                        String text = prediction.getFullText(null).toString();
                        suggestionList.add(text);
                        suggestionIdMap.put(text, prediction.getPlaceId());
                    }
                    suggestionsAdapter.notifyDataSetChanged();
                    lvSuggestions.setVisibility(suggestionList.isEmpty() ? View.GONE : View.VISIBLE);
                })
                .addOnFailureListener(e -> lvSuggestions.setVisibility(View.GONE));
    }

    private void handleSuggestionSelection(String address, String placeId) {
        etAddress.setText(address);
        lvSuggestions.setVisibility(View.GONE);
        if (placesClient == null || placeId == null) {
            handleManualAddress();
            return;
        }

        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, java.util.Arrays.asList(Place.Field.LAT_LNG, Place.Field.ADDRESS))
                .setSessionToken(sessionToken)
                .build();
        placesClient.fetchPlace(request)
                .addOnSuccessListener(response -> {
                    Place place = response.getPlace();
                    LatLng pos = place.getLatLng();
                    if (pos != null) {
                        if (callback != null) {
                            callback.onLocationSelected(pos, place.getAddress());
                        }
                        dismiss();
                    }
                })
                .addOnFailureListener(e -> handleManualAddress());
    }

    private void handleManualAddress() {
        String input = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";
        if (input.isEmpty()) return;

        android.location.Geocoder geocoder = new android.location.Geocoder(requireContext(), new Locale("he"));
        try {
            java.util.List<android.location.Address> list = geocoder.getFromLocationName(input, 1);
            if (list != null && !list.isEmpty()) {
                android.location.Address addr = list.get(0);
                if ("IL".equalsIgnoreCase(addr.getCountryCode())) {
                    LatLng pos = new LatLng(addr.getLatitude(), addr.getLongitude());
                    if (callback != null) {
                        callback.onLocationSelected(pos, addr.getAddressLine(0));
                    }
                    Toast.makeText(requireContext(), R.string.location_updated, Toast.LENGTH_SHORT).show();
                    dismiss();
                    return;
                } else {
                    Toast.makeText(requireContext(), R.string.invalid_israel_address, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Toast.makeText(requireContext(), R.string.address_not_found, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(requireContext(), R.string.address_lookup_error, Toast.LENGTH_SHORT).show();
        }
    }
}
