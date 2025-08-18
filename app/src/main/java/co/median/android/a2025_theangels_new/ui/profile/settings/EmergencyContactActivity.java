// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.ui.profile.settings;

// IMPORTS
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.Nullable;

import co.median.android.a2025_theangels_new.ui.main.ImmersiveActivity;

import co.median.android.a2025_theangels_new.R;
import co.median.android.a2025_theangels_new.data.models.EmergencyContact;
import co.median.android.a2025_theangels_new.data.services.EmergencyContactManager;

// EmergencyContactActivity - Manages a single emergency contact for the user
public class EmergencyContactActivity extends ImmersiveActivity {

    // VARIABLES
    private TextInputEditText etName, etPhone, etRelationship;
    private MaterialCheckBox chkConfirm;
    private Button btnAddContact, btnEditContact, btnRemoveContact;
    private View formContainer, cardContainer, noContactMessage;
    private TextView tvCardName, tvCardPhone, tvCardRelationship;
    private ProgressBar progressBar;

    private EmergencyContact currentContact;
    private boolean isEditing = false;

    // onCreate - Initializes views and loads existing contact
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact);

        // Force RTL layout
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        etName = findViewById(R.id.et_contact_name);
        etPhone = findViewById(R.id.et_contact_phone);
        etRelationship = findViewById(R.id.et_contact_relationship);
        chkConfirm = findViewById(R.id.checkbox_confirm);
        btnAddContact = findViewById(R.id.btn_add_contact);
        formContainer = findViewById(R.id.form_container);
        cardContainer = findViewById(R.id.contact_card);
        noContactMessage = findViewById(R.id.no_contact_message);
        tvCardName = findViewById(R.id.tv_card_name);
        tvCardPhone = findViewById(R.id.tv_card_phone);
        tvCardRelationship = findViewById(R.id.tv_card_relationship);
        btnEditContact = findViewById(R.id.btn_edit_contact);
        btnRemoveContact = findViewById(R.id.btn_remove_contact);
        progressBar = findViewById(R.id.progress_bar);

        chkConfirm.setOnCheckedChangeListener((b, checked) -> btnAddContact.setEnabled(checked));
        btnAddContact.setOnClickListener(v -> saveContact());
        btnEditContact.setOnClickListener(v -> showEditForm());
        btnRemoveContact.setOnClickListener(v -> confirmRemove());

        loadContact();
    }

    // loadContact - Retrieves contact from database and updates UI
    private void loadContact() {
        progressBar.setVisibility(View.VISIBLE);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        EmergencyContactManager.getContactByUserId(uid, new EmergencyContactManager.ContactCallback() {
            @Override
            public void onContactLoaded(@Nullable EmergencyContact contact) {
                progressBar.setVisibility(View.GONE);
                if (contact == null) {
                    showAddForm();
                } else {
                    currentContact = contact;
                    showContactCard(contact);
                }
            }

            @Override
            public void onError(Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EmergencyContactActivity.this, getString(R.string.error_loading_contact), Toast.LENGTH_SHORT).show();
                showAddForm();
            }
        });
    }

    // saveContact - Validates input and writes contact to Firestore
    private void saveContact() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String relationship = etRelationship.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || relationship.isEmpty()) {
            Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
            Toast.makeText(this, getString(R.string.invalid_phone), Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        EmergencyContact contact = new EmergencyContact();
        contact.setContactName(name);
        contact.setContactPhone(phone);
        contact.setContactRelationship(relationship);
        contact.setContactUserUID(uid);

        if (isEditing && currentContact != null) {
            contact.setId(currentContact.getId());
            EmergencyContactManager.updateContact(contact, new EmergencyContactManager.OperationCallback() {
                @Override public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(EmergencyContactActivity.this, getString(R.string.contact_saved), Toast.LENGTH_SHORT).show();
                    currentContact = contact;
                    showContactCard(contact);
                }
                @Override public void onError(Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(EmergencyContactActivity.this, getString(R.string.error_saving_details), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            EmergencyContactManager.addContact(contact, new EmergencyContactManager.OperationCallback() {
                @Override public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(EmergencyContactActivity.this, getString(R.string.contact_saved), Toast.LENGTH_SHORT).show();
                    currentContact = contact;
                    showContactCard(contact);
                }
                @Override public void onError(Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(EmergencyContactActivity.this, getString(R.string.error_saving_details), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // removeContact - Deletes the existing contact
    private void removeContact() {
        if (currentContact == null) return;
        progressBar.setVisibility(View.VISIBLE);
        EmergencyContactManager.deleteContact(currentContact.getId(), new EmergencyContactManager.OperationCallback() {
            @Override public void onSuccess() {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EmergencyContactActivity.this, getString(R.string.contact_removed), Toast.LENGTH_SHORT).show();
                currentContact = null;
                showAddForm();
            }
            @Override public void onError(Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EmergencyContactActivity.this, getString(R.string.error_saving_details), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // showAddForm - Displays empty form for adding a new contact
    private void showAddForm() {
        isEditing = false;
        btnAddContact.setText(getString(R.string.add_contact_button));
        formContainer.setVisibility(View.VISIBLE);
        cardContainer.setVisibility(View.GONE);
        noContactMessage.setVisibility(View.VISIBLE);
        etName.setText("");
        etPhone.setText("");
        etRelationship.setText("");
        chkConfirm.setChecked(false);
        btnAddContact.setEnabled(false);
    }

    // confirmRemove - Asks user to confirm contact deletion
    private void confirmRemove() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage(getString(R.string.confirm_remove_contact))
                .setPositiveButton(R.string.remove_contact_button, (d, w) -> removeContact())
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    // showEditForm - Prefills form with existing contact for editing
    private void showEditForm() {
        if (currentContact == null) return;
        isEditing = true;
        btnAddContact.setText(getString(R.string.save_contact_button));
        formContainer.setVisibility(View.VISIBLE);
        cardContainer.setVisibility(View.GONE);
        noContactMessage.setVisibility(View.GONE);
        etName.setText(currentContact.getContactName());
        etPhone.setText(currentContact.getContactPhone());
        etRelationship.setText(currentContact.getContactRelationship());
        chkConfirm.setChecked(false);
        btnAddContact.setEnabled(false);
    }

    // showContactCard - Displays contact details card
    private void showContactCard(EmergencyContact contact) {
        isEditing = false;
        formContainer.setVisibility(View.GONE);
        cardContainer.setVisibility(View.VISIBLE);
        noContactMessage.setVisibility(View.GONE);
        tvCardName.setText(contact.getContactName());
        tvCardPhone.setText(contact.getContactPhone());
        tvCardRelationship.setText(contact.getContactRelationship());
    }
}
