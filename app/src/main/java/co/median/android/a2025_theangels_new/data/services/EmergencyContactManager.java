// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.services;
// IMPORTS
import android.util.Log;
import androidx.annotation.Nullable;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import co.median.android.a2025_theangels_new.data.models.EmergencyContact;
// EmergencyContactManager - Manages loading and updating emergency contacts
public class EmergencyContactManager {
// VARIABLES
    private static final String TAG = "EmergencyContactMgr";
    public interface ContactCallback {
        void onContactLoaded(@Nullable EmergencyContact contact);
        void onError(Exception e);
    }
// Returns the contact by user id.
    public static void getContactByUserId(String uid, ContactCallback callback) {
        FirebaseFirestore.getInstance().collection("contacts")
                .whereEqualTo("contactUserUID", uid)
                .limit(1)
                .get()
                .addOnSuccessListener(query -> {
                    EmergencyContact contact = null;
                    for (QueryDocumentSnapshot doc : query) {
                        contact = doc.toObject(EmergencyContact.class);
                        break;
                    }
                    callback.onContactLoaded(contact);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching emergency contact", e);
                    callback.onError(e);
                });
    }
    public interface OperationCallback {
        void onSuccess();
        void onError(Exception e);
    }
// Performs add contact.
    public static void addContact(EmergencyContact contact, OperationCallback callback) {
        FirebaseFirestore.getInstance().collection("contacts")
                .add(contact)
                .addOnSuccessListener(r -> { if (callback != null) callback.onSuccess(); })
                .addOnFailureListener(e -> { Log.e(TAG, "Error adding contact", e); if (callback != null) callback.onError(e); });
    }
// Performs update contact.
    public static void updateContact(EmergencyContact contact, OperationCallback callback) {
        if (contact.getId() == null) { if (callback != null) callback.onError(new IllegalArgumentException("No ID")); return; }
        FirebaseFirestore.getInstance().collection("contacts")
                .document(contact.getId())
                .set(contact)
                .addOnSuccessListener(unused -> { if (callback != null) callback.onSuccess(); })
                .addOnFailureListener(e -> { Log.e(TAG, "Error updating contact", e); if (callback != null) callback.onError(e); });
    }
// Performs delete contact.
    public static void deleteContact(String contactId, OperationCallback callback) {
        FirebaseFirestore.getInstance().collection("contacts")
                .document(contactId)
                .delete()
                .addOnSuccessListener(unused -> { if (callback != null) callback.onSuccess(); })
                .addOnFailureListener(e -> { Log.e(TAG, "Error deleting contact", e); if (callback != null) callback.onError(e); });
    }
}
