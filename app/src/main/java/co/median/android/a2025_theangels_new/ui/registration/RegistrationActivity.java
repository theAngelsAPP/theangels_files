// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.ui.registration;

// IMPORTS
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import okhttp3.*;
import co.median.android.a2025_theangels_new.R;
import co.median.android.a2025_theangels_new.data.map.AutocompleteHelper;
import co.median.android.a2025_theangels_new.data.services.UserDataManager;
import co.median.android.a2025_theangels_new.databinding.ActivityRegistrationBinding;
import co.median.android.a2025_theangels_new.ui.main.MainActivity;

// RegistrationActivity - Handles user registration and account creation
public class RegistrationActivity extends AppCompatActivity {

    // VARIABLES
    private ActivityRegistrationBinding binding;
    private EditText firstName, lastName, idNumber, email, phone, password, confirmPassword;
    private Button selectBirthDateButton, selectImageButton, registerButton;
    private CheckBox weaponLicenseCheckBox;
    private ImageView profileImageView;
    private ChipGroup medicalOptionsGroup;
    private EditText cityEditText;
    private String selectedCity = "";

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CITY_AUTOCOMPLETE_REQUEST = 2;
    private Bitmap selectedImageBitmap = null;
    private String selectedBirthDate = "";
    private FirebaseAuth auth;

    private final String IMGUR_CLIENT_ID = "47eaf978d864043";

    // onCreate - Sets up views, listeners, and screen preferences
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Force right-to-left layout on all devices
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        // Hide system UI for an immersive experience
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        auth = FirebaseAuth.getInstance();

        // Bind view references
        firstName = binding.firstNameEditText;
        lastName = binding.lastNameEditText;
        idNumber = binding.idNumberEditText;
        email = binding.emailEditText;
        phone = binding.phoneEditText;
        password = binding.passwordEditText;
        confirmPassword = binding.confirmPasswordEditText;

        // Restrict characters and length for name, ID, and phone fields
        InputFilter nameFilter = (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                char c = source.charAt(i);
                if (!Character.isLetter(c) && c != ' ' && c != '-') {
                    return "";
                }
            }
            return null;
        };
        firstName.setFilters(new InputFilter[]{nameFilter, new InputFilter.LengthFilter(20)});
        lastName.setFilters(new InputFilter[]{nameFilter, new InputFilter.LengthFilter(20)});
        idNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});
        phone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

        selectBirthDateButton = binding.selectBirthDateButton;
        selectImageButton = binding.selectImageButton;
        registerButton = binding.registerButton;
        weaponLicenseCheckBox = binding.weaponLicenseCheckBox;
        profileImageView = binding.profileImageView;
        medicalOptionsGroup = binding.medicalOptions;
        cityEditText = binding.cityEditText;

        // Open city autocomplete when the user taps the city field
        cityEditText.setOnClickListener(v ->
                AutocompleteHelper.openCityAutocomplete(this, CITY_AUTOCOMPLETE_REQUEST));

        // Load selectable medical details
        loadMedicalDetails();

        // Set listeners for picking birth date and image
        selectBirthDateButton.setOnClickListener(v -> showDatePicker());

        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "בחר תמונה"), PICK_IMAGE_REQUEST);
        });

        // Back button returns to previous screen
        binding.btnBack.setOnClickListener(v -> onBackPressed());

        // Begin registration when user submits the form
        registerButton.setOnClickListener(v -> handleRegister());
    }

    // showDatePicker - Displays a calendar and records the chosen date
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedBirthDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                    selectBirthDateButton.setText("נבחר: " + selectedBirthDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    // handleRegister - Validates input, creates account, and saves user data
    private void handleRegister() {
        Log.d("Registration", "handleRegister called");

        // Stop early if validation fails
        if (!validateInputs()) {
            Toast.makeText(this, getString(R.string.fill_details_correctly), Toast.LENGTH_SHORT).show();
            Log.e("Registration", "Input validation failed");
            return;
        }

        String emailStr = email.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();

        Log.d("Registration", "Starting Firebase Auth registration");

        // Attempt to create account with Firebase
        auth.createUserWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
                        Log.d("Registration", "User created with UID: " + uid);
                        // Upload image before saving user data
                        uploadImageToImgur(imageUrl -> saveUserData(uid, imageUrl));
                    } else {
                        Log.e("Registration", "Firebase registration failed: " + task.getException());
                        Toast.makeText(this, getString(R.string.error_generic, task.getException().getMessage()), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // uploadImageToImgur - Sends the bitmap to Imgur and returns its link via callback
    private void uploadImageToImgur(OnImageUploadListener listener) {
        if (selectedImageBitmap == null) {
            listener.onUploaded("");
            return;
        }

        // Convert bitmap to Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        selectedImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageBase64 = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            imageBase64 = Base64.getEncoder().encodeToString(baos.toByteArray());
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder().add("image", imageBase64).build();

        Request request = new Request.Builder()
                .url("https://api.imgur.com/3/image")
                .header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                .post(requestBody)
                .build();

        // Execute upload asynchronously
        client.newCall(request).enqueue(new okhttp3.Callback() {
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> listener.onUploaded(""));
            }

            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String json = response.body().string();
                try {
                    JSONObject data = new JSONObject(json).getJSONObject("data");
                    String link = data.getString("link");
                    runOnUiThread(() -> listener.onUploaded(link));
                } catch (Exception e) {
                    runOnUiThread(() -> listener.onUploaded(""));
                }
            }
        });
    }

    // saveUserData - Stores user information in Firestore and navigates to the main screen
    private void saveUserData(String uid, String imageUrl) {
        Log.d("Registration", "Saving user data to Firestore");

        // Collect selected medical options
        List<String> medicalSelections = new ArrayList<>();
        for (int i = 0; i < medicalOptionsGroup.getChildCount(); i++) {
            Chip chip = (Chip) medicalOptionsGroup.getChildAt(i);
            if (chip.isChecked()) medicalSelections.add(chip.getText().toString());
        }

        // Build user data map
        Map<String, Object> userData = new HashMap<>();
        userData.put("firstName", firstName.getText().toString().trim());
        userData.put("lastName", lastName.getText().toString().trim());
        userData.put("idNumber", idNumber.getText().toString().trim());
        userData.put("Email", email.getText().toString().trim());
        userData.put("Phone", phone.getText().toString().trim());
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
            java.util.Date d = sdf.parse(selectedBirthDate);
            userData.put("birthDate", d);
        } catch (Exception e) {
            userData.put("birthDate", null);
        }
        userData.put("haveGunLicense", weaponLicenseCheckBox.isChecked());
        userData.put("imageURL", imageUrl);
        userData.put("medicalDetails", medicalSelections);
        userData.put("city", selectedCity);
        userData.put("role", "משתמש");

        UserDataManager.createUser(uid, userData,
                () -> {
                    Log.d("Registration", "User data saved successfully");
                    Toast.makeText(this, getString(R.string.registration_success), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                },
                e -> {
                    Log.e("Registration", "Failed to save user data: " + e.getMessage());
                    Toast.makeText(this, getString(R.string.error_saving_details), Toast.LENGTH_SHORT).show();
                });
    }


    // validateInputs - Checks every form field and reports errors to the user
    private boolean validateInputs() {
        boolean valid = true;

        if (!isValidName(firstName.getText().toString())) {
            firstName.setError(getString(R.string.invalid_name));
            valid = false;
        }

        if (!isValidName(lastName.getText().toString())) {
            lastName.setError(getString(R.string.invalid_name));
            valid = false;
        }

        if (!isValidId(idNumber.getText().toString())) {
            idNumber.setError(getString(R.string.invalid_id));
            valid = false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            email.setError(getString(R.string.invalid_email));
            valid = false;
        }

        if (!isValidPhone(phone.getText().toString())) {
            phone.setError(getString(R.string.invalid_phone));
            valid = false;
        }

        if (password.getText().toString().isEmpty() || !password.getText().toString().equals(confirmPassword.getText().toString())) {
            confirmPassword.setError(getString(R.string.passwords_do_not_match));
            valid = false;
        }

        if (selectedBirthDate.isEmpty()) {
            Toast.makeText(this, R.string.select_birth_date_error, Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (selectedCity.isEmpty()) {
            Toast.makeText(this, R.string.select_city_error, Toast.LENGTH_SHORT).show();
            valid = false;
        }

        Log.d("Validation", "Inputs valid: " + valid);
        return valid;
    }

    // onActivityResult - Handles results from image picker and city autocomplete
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                Uri imageUri = data.getData();
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImageView.setImageBitmap(selectedImageBitmap);
                profileImageView.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == CITY_AUTOCOMPLETE_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {
                Place place = AutocompleteHelper.getPlaceFromResult(data);
                selectedCity = place.getName();
                cityEditText.setText(selectedCity);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR && data != null) {
                Status status = AutocompleteHelper.getErrorStatus(data);
                Toast.makeText(this, getString(R.string.error_select_city, status.getStatusMessage()), Toast.LENGTH_SHORT).show();
            }
        }
    }


    // loadMedicalDetails - Retrieves medical options and adds them as chips
    private void loadMedicalDetails() {
        UserDataManager.loadMedicalDetails(list -> {
            for (String name : list) {
                Chip chip = new Chip(this);
                chip.setText(name);
                chip.setCheckable(true);
                chip.setChipBackgroundColorResource(R.color.chip_background);
                medicalOptionsGroup.addView(chip);
            }
        });
    }

    // isValidName - Verifies that a name contains only letters, spaces, or hyphens
    private boolean isValidName(String name) {
        return name.matches("[a-zA-Zא-ת\s-]+");
    }

    // isValidId - Confirms the ID has exactly nine digits
    private boolean isValidId(String id) {
        return id.matches("\\d{9}");
    }

    // isValidPhone - Ensures the phone number has nine or ten digits
    private boolean isValidPhone(String phoneStr) {
        return phoneStr.matches("\\d{9,10}");
    }

    // OnImageUploadListener - Callback invoked after image upload completes
    interface OnImageUploadListener {
        void onUploaded(String url);
    }
}
