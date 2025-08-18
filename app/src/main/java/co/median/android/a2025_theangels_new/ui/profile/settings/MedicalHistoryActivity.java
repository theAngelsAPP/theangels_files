// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.ui.profile.settings;

// IMPORTS
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import co.median.android.a2025_theangels_new.R;
import co.median.android.a2025_theangels_new.ui.main.ImmersiveActivity;
import co.median.android.a2025_theangels_new.data.models.UserSession;
import co.median.android.a2025_theangels_new.data.services.UserDataManager;

// MedicalHistoryActivity - Lets users manage their list of medical conditions
public class MedicalHistoryActivity extends ImmersiveActivity {

    // VARIABLES
    private LinearLayout container;
    private ProgressBar progressBar;
    private List<String> userSelections = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // onCreate - Builds the list of conditions and loads current selections
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history);

        // Force RTL layout
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        container = findViewById(R.id.container_conditions);
        progressBar = findViewById(R.id.progressBar);
        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        // Copy existing selections from session
        List<String> current = UserSession.getInstance().getMedicalDetails();
        if (current != null) userSelections.addAll(current);

        loadConditions();
    }

    // loadConditions - Fetches condition names and creates rows
    private void loadConditions() {
        progressBar.setVisibility(View.VISIBLE);
        UserDataManager.loadMedicalDetails(list -> {
            progressBar.setVisibility(View.GONE);
            for (String name : list) {
                addConditionRow(name);
            }
        });
    }

    // addConditionRow - Adds a single condition row with yes/no buttons
    private void addConditionRow(String name) {
        View row = getLayoutInflater().inflate(R.layout.item_medical_condition, container, false);
        TextView tvName = row.findViewById(R.id.tv_condition_name);
        Button btnYes = row.findViewById(R.id.btn_yes);
        Button btnNo = row.findViewById(R.id.btn_no);
        tvName.setText(name);

        // Set initial state based on user selections
        if (userSelections.contains(name)) {
            highlightYes(btnYes, btnNo);
        } else {
            highlightNo(btnYes, btnNo);
        }

        btnYes.setOnClickListener(v -> onSelectionChanged(name, true, btnYes, btnNo));
        btnNo.setOnClickListener(v -> onSelectionChanged(name, false, btnYes, btnNo));
        container.addView(row);
    }

    // onSelectionChanged - Updates selection and visual state
    private void onSelectionChanged(String name, boolean yesSelected, Button btnYes, Button btnNo) {
        if (yesSelected && !userSelections.contains(name)) {
            updateSelection(name, true);
            highlightYes(btnYes, btnNo);
        } else if (!yesSelected && userSelections.contains(name)) {
            updateSelection(name, false);
            highlightNo(btnYes, btnNo);
        }
    }

    // updateSelection - Writes change to Firestore and session
    private void updateSelection(String name, boolean add) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (add) {
            db.collection("users").document(uid)
                    .update("medicalDetails", FieldValue.arrayUnion(name));
            userSelections.add(name);
        } else {
            db.collection("users").document(uid)
                    .update("medicalDetails", FieldValue.arrayRemove(name));
            userSelections.remove(name);
        }

        // Refresh session with new medical details
        UserSession.getInstance().initialize(
                UserSession.getInstance().getEmail(),
                UserSession.getInstance().getPhone(),
                UserSession.getInstance().getBirthDate(),
                UserSession.getInstance().getCity(),
                UserSession.getInstance().getFirstName(),
                UserSession.getInstance().hasGunLicense(),
                UserSession.getInstance().getIdNumber(),
                UserSession.getInstance().getImageURL(),
                UserSession.getInstance().getLastName(),
                userSelections,
                UserSession.getInstance().getRole(),
                UserSession.getInstance().getVolAvailable(),
                UserSession.getInstance().getVolCities(),
                UserSession.getInstance().getVolHaveDriverLicense(),
                UserSession.getInstance().getVolVerification(),
                UserSession.getInstance().getVolSpecialty()
        );
    }

    // highlightYes - Marks the Yes button as selected
    private void highlightYes(Button yes, Button no) {
        yes.setBackgroundResource(R.drawable.rounded_button_green);
        yes.setBackgroundTintList(null);
        yes.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        no.setBackgroundResource(R.drawable.rounded_button_gray);
        no.setBackgroundTintList(null);
        no.setTextColor(ContextCompat.getColor(this, android.R.color.black));
    }

    // highlightNo - Marks the No button as selected
    private void highlightNo(Button yes, Button no) {
        yes.setBackgroundResource(R.drawable.rounded_button_gray);
        yes.setBackgroundTintList(null);
        yes.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        no.setBackgroundResource(R.drawable.rounded_button_red);
        no.setBackgroundTintList(null);
        no.setTextColor(ContextCompat.getColor(this, android.R.color.white));
    }
}
