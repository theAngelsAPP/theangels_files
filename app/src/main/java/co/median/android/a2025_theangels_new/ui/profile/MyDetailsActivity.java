// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.ui.profile;

// IMPORTS
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import co.median.android.a2025_theangels_new.ui.main.ImmersiveActivity;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import co.median.android.a2025_theangels_new.R;
import co.median.android.a2025_theangels_new.data.models.UserSession;
import co.median.android.a2025_theangels_new.data.services.UserDataManager;
import co.median.android.a2025_theangels_new.data.map.AutocompleteHelper;
import co.median.android.a2025_theangels_new.util.ImageUploadUtils;

// MyDetailsActivity - Allows editing of personal details and profile image
public class MyDetailsActivity extends ImmersiveActivity {

    // VARIABLES
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CITY_AUTOCOMPLETE_REQUEST = 2;

    private TextInputEditText etFirstName, etLastName, etBirthDate, etIdNumber, etCity, etEmail, etPhone;
    private ImageView ivProfile;
    private ProgressBar progressBar;
    private Bitmap selectedImageBitmap = null;

    // onCreate - Builds the form, loads existing data and handles save logic
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_details);

        // Force RTL layout
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etBirthDate = findViewById(R.id.et_birth_date);
        etIdNumber = findViewById(R.id.et_id_number);
        etCity = findViewById(R.id.et_city);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        ivProfile = findViewById(R.id.iv_profile);
        progressBar = findViewById(R.id.progressBar);
        View btnChangeImage = findViewById(R.id.btn_change_image);

        // Open gallery when picking a profile image
        View.OnClickListener imagePicker = v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), PICK_IMAGE_REQUEST);
        };
        btnChangeImage.setOnClickListener(imagePicker);
        ivProfile.setOnClickListener(imagePicker);

        // Prefill fields from session
        UserSession session = UserSession.getInstance();
        if (session.getFirstName() != null) etFirstName.setText(session.getFirstName());
        if (session.getLastName() != null) etLastName.setText(session.getLastName());
        if (session.getBirthDate() != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
            etBirthDate.setText(sdf.format(session.getBirthDate()));
        }
        if (session.getIdNumber() != null) etIdNumber.setText(session.getIdNumber());
        if (session.getCity() != null) etCity.setText(session.getCity());
        if (session.getEmail() != null) etEmail.setText(session.getEmail());
        if (session.getPhone() != null) etPhone.setText(session.getPhone());
        if (session.getImageURL() != null && !session.getImageURL().isEmpty()) {
            Glide.with(this).load(session.getImageURL()).placeholder(R.drawable.newuserpic).into(ivProfile);
        }

        // Open Google Places autocomplete for city field
        etCity.setOnClickListener(v -> AutocompleteHelper.openCityAutocomplete(this, CITY_AUTOCOMPLETE_REQUEST));

        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        // Save changes when user taps the button
        findViewById(R.id.btn_save_changes).setOnClickListener(v -> {
            // Close keyboard if open
            View focusedView = getCurrentFocus();
            if (focusedView != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
            }

            if (!validateInputs()) return;

            progressBar.setVisibility(View.VISIBLE);

            Map<String, Object> updates = new HashMap<>();
            updates.put("firstName", etFirstName.getText().toString().trim());
            updates.put("lastName", etLastName.getText().toString().trim());
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
                java.util.Date d = sdf.parse(etBirthDate.getText().toString().trim());
                updates.put("birthDate", d);
            } catch (Exception e) {
                updates.put("birthDate", null);
            }
            updates.put("city", etCity.getText().toString().trim());
            updates.put("Email", etEmail.getText().toString().trim());
            updates.put("Phone", etPhone.getText().toString().trim());

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            Runnable doUpdate = () -> UserDataManager.updateUserDetails(uid, updates, success -> {
                progressBar.setVisibility(View.GONE);
                if (success) {
                    Toast.makeText(this, getString(R.string.details_saved), Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(R.animator.slide_in_left, R.animator.slide_out_right);
                } else {
                    Toast.makeText(this, getString(R.string.error_saving_details), Toast.LENGTH_SHORT).show();
                }
            });

            if (selectedImageBitmap != null) {
                ImageUploadUtils.uploadBitmapToImgur(selectedImageBitmap, url -> {
                    if (!url.isEmpty()) {
                        updates.put("imageURL", url);
                    }
                    doUpdate.run();
                });
            } else {
                doUpdate.run();
            }
        });

        // Display date picker
        etBirthDate.setOnClickListener(v -> showDatePicker());

        // Notify that ID field cannot be edited
        etIdNumber.setOnClickListener(v ->
                Toast.makeText(this, getString(R.string.id_field_locked), Toast.LENGTH_SHORT).show()
        );

        // Hide keyboard when touching outside inputs
        findViewById(R.id.root_layout).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                View focusedView2 = getCurrentFocus();
                if (focusedView2 != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(focusedView2.getWindowToken(), 0);
                }
            }
            return false;
        });
    }

    // showDatePicker - Opens calendar dialog and writes chosen date
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    etBirthDate.setText(selectedDate);
                },
                year,
                month,
                day);

        datePickerDialog.show();
    }

    // validateInputs - Ensures all required fields are filled and email is valid
    private boolean validateInputs() {
        if (etFirstName.getText().toString().trim().isEmpty() ||
                etLastName.getText().toString().trim().isEmpty() ||
                etBirthDate.getText().toString().trim().isEmpty() ||
                etCity.getText().toString().trim().isEmpty() ||
                etEmail.getText().toString().trim().isEmpty() ||
                etPhone.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString().trim()).matches()) {
            Toast.makeText(this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // onActivityResult - Handles image selection and city autocomplete results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                Uri imageUri = data.getData();
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ivProfile.setImageBitmap(selectedImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == CITY_AUTOCOMPLETE_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {
                Place place = AutocompleteHelper.getPlaceFromResult(data);
                if (place != null) etCity.setText(place.getName());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR && data != null) {
                Status status = AutocompleteHelper.getErrorStatus(data);
                Toast.makeText(this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
