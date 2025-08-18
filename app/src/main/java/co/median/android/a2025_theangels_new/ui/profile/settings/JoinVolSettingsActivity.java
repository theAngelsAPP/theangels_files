// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.ui.profile.settings;

// IMPORTS
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.common.api.Status;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.median.android.a2025_theangels_new.R;
import co.median.android.a2025_theangels_new.data.map.AutocompleteHelper;
import co.median.android.a2025_theangels_new.ui.main.BaseActivity;
import co.median.android.a2025_theangels_new.ui.home.HomeActivity;
import co.median.android.a2025_theangels_new.data.services.UserDataManager;
import co.median.android.a2025_theangels_new.util.ImageUploadUtils;

// JoinVolSettingsActivity - Collects information for users who want to become volunteers
public class JoinVolSettingsActivity extends BaseActivity {

    // VARIABLES
    private static final int CITY_AUTOCOMPLETE_REQUEST = 1001;
    private static final int PICK_IMAGE_REQUEST = 1002;

    private EditText inputCity;
    private ChipGroup chipGroupCities;
    private ChipGroup chipGroupSpecialties;
    private ChipGroup chipGroupDays;
    private MaterialButton btnSelectFile;
    private android.widget.TextView tvFileUploaded;
    private MaterialCheckBox chkDriverLicense;
    private MaterialCheckBox checkboxAgree;
    private MaterialButton btnSubmit;
    private Bitmap selectedImageBitmap = null;
    private final List<String> selectedCities = new ArrayList<>();

    // onCreate - Initializes the registration form and listeners
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showTopBar(false);
        showBottomBar(false);

        // Initialize city and chip views
        inputCity = findViewById(R.id.input_city);
        chipGroupCities = findViewById(R.id.chip_group_cities);
        chipGroupSpecialties = findViewById(R.id.chip_group_specialties);
        chipGroupDays = findViewById(R.id.chip_group_days);
        btnSelectFile = findViewById(R.id.btn_select_file);
        tvFileUploaded = findViewById(R.id.tv_file_uploaded);
        chkDriverLicense = findViewById(R.id.chk_driver_license);
        checkboxAgree = findViewById(R.id.checkbox_agree);
        btnSubmit = findViewById(R.id.btn_submit);

        // Image picker for verification document
        if (btnSelectFile != null) {
            btnSelectFile.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), PICK_IMAGE_REQUEST);
            });
        }

        // Submit form
        if (btnSubmit != null) {
            btnSubmit.setOnClickListener(v -> handleSubmit());
        }

        // Open Google Places autocomplete for cities
        if (inputCity != null) {
            inputCity.setOnClickListener(v ->
                    AutocompleteHelper.openCityAutocomplete(this, CITY_AUTOCOMPLETE_REQUEST));
        }

        // Back button
        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());
    }

    // getLayoutResourceId - Layout used by BaseActivity
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_joinvol_settings;
    }

    // onActivityResult - Handles city selection and image picking results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CITY_AUTOCOMPLETE_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {
                Place place = AutocompleteHelper.getPlaceFromResult(data);
                if (place != null) {
                    String city = place.getName();
                    if (!selectedCities.contains(city)) {
                        selectedCities.add(city);
                        addCityChip(city);
                    } else {
                        Toast.makeText(this, R.string.city_already_selected, Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR && data != null) {
                Status status = AutocompleteHelper.getErrorStatus(data);
                Toast.makeText(this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                Uri imageUri = data.getData();
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                if (tvFileUploaded != null) {
                    tvFileUploaded.setVisibility(View.VISIBLE);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // addCityChip - Adds a removable chip for the chosen city
    private void addCityChip(String city) {
        Chip chip = new Chip(this);
        chip.setText(city);
        chip.setChipBackgroundColorResource(R.color.volunteer_chip_unselected);
        chip.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            chipGroupCities.removeView(chip);
            selectedCities.remove(city);
        });
        chipGroupCities.addView(chip);
    }

    // handleSubmit - Validates input, uploads image, and updates user record
    private void handleSubmit() {
        if (checkboxAgree != null && !checkboxAgree.isChecked()) {
            Toast.makeText(this, R.string.must_agree_terms, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> specialties = new ArrayList<>();
        if (chipGroupSpecialties != null) {
            for (int i = 0; i < chipGroupSpecialties.getChildCount(); i++) {
                Chip c = (Chip) chipGroupSpecialties.getChildAt(i);
                if (c.isChecked()) specialties.add(c.getText().toString());
            }
        }

        List<String> days = new ArrayList<>();
        if (chipGroupDays != null) {
            for (int i = 0; i < chipGroupDays.getChildCount(); i++) {
                Chip c = (Chip) chipGroupDays.getChildAt(i);
                if (c.isChecked()) days.add(c.getText().toString());
            }
        }

        boolean hasLicense = chkDriverLicense != null && chkDriverLicense.isChecked();

        ImageUploadUtils.uploadBitmapToImgur(selectedImageBitmap, url -> {
            Map<String, Object> updates = new HashMap<>();
            updates.put("role", "מתנדב");
            updates.put("volAvailable", days);
            updates.put("volCities", selectedCities);
            updates.put("volHaveDriverLicense", hasLicense);
            updates.put("volSpecialty", specialties);
            updates.put("volVerification", url);

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            UserDataManager.updateUserDetails(uid, updates, success -> {
                if (success) {
                    new AlertDialog.Builder(this)
                            .setMessage(R.string.volunteer_join_success)
                            .setPositiveButton(R.string.ok_button, (d, w) -> {
                                startActivity(new Intent(this, HomeActivity.class));
                                finish();
                            })
                            .show();
                } else {
                    Toast.makeText(this, R.string.error_saving_details, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
