// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.services;
// IMPORTS
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import co.median.android.a2025_theangels_new.data.models.Education;
// EducationDataManager - Manages education records for users
public class EducationDataManager {
// VARIABLES
    private static final String TAG = "EducationDataManager";
    public interface EducationCallback {
        void onEducationsLoaded(ArrayList<Education> educations);
        void onError(Exception e);
    }
// Returns the all educations.
    public static void getAllEducations(EducationCallback callback) {
        Log.d(TAG, "getAllEducations called - starting Firestore fetch");
        FirebaseFirestore.getInstance().collection("education")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Education> educations = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        try {
                            Education training = doc.toObject(Education.class);
                            if (training != null) {
                                Log.d(TAG, "Education loaded: " + training.getEduTitle());
                                educations.add(training);
                            } else {
                                Log.w(TAG, "Education is null for document: " + doc.getId());
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to convert document to Education: " + doc.getId(), e);
                        }
                    }
                    Log.d(TAG, "Total educations fetched: " + educations.size());
                    callback.onEducationsLoaded(educations);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching educations from Firestore", e);
                    callback.onError(e);
                });
    }
    public interface SingleEducationCallback {
        void onEducationLoaded(Education education);
        void onError(Exception e);
    }
// Returns the education by id.
    public static void getEducationById(@NonNull String id, SingleEducationCallback callback) {
        FirebaseFirestore.getInstance().collection("education").document(id)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc != null && doc.exists()) {
                        callback.onEducationLoaded(doc.toObject(Education.class));
                    } else {
                        callback.onEducationLoaded(null);
                    }
                })
                .addOnFailureListener(callback::onError);
    }
}
