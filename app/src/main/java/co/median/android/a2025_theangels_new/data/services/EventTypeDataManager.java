// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.services;
// IMPORTS
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import co.median.android.a2025_theangels_new.data.models.EventType;
// EventTypeDataManager - Handles event type retrieval and storage
public class EventTypeDataManager {
// VARIABLES
    private static final String TAG = "EventTypeDataManager";
    public interface EventTypeCallback {
        void onEventTypesLoaded(ArrayList<EventType> types);
        void onError(Exception e);
    }
// Returns the all event types.
    public static void getAllEventTypes(EventTypeCallback callback) {
        Log.d(TAG, "getAllEventTypes called - starting Firestore fetch");
        FirebaseFirestore.getInstance().collection("eventsType")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<EventType> types = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        try {
                            EventType type = doc.toObject(EventType.class);
                            if (type != null) {
                                Log.d(TAG, "EventType loaded: " + type.getTypeName());
                                types.add(type);
                            } else {
                                Log.w(TAG, "EventType is null for document: " + doc.getId());
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to convert document to EventType: " + doc.getId(), e);
                        }
                    }
                    Log.d(TAG, "Total event types fetched: " + types.size());
                    callback.onEventTypesLoaded(types);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching event types from Firestore", e);
                    callback.onError(e);
                });
    }
    public interface SingleEventTypeCallback {
        void onEventTypeLoaded(EventType type);
        void onError(Exception e);
    }
// Returns the event type by name.
    public static void getEventTypeByName(@NonNull String typeName, SingleEventTypeCallback callback) {
        FirebaseFirestore.getInstance().collection("eventsType")
                .whereEqualTo("typeName", typeName)
                .limit(1)
                .get()
                .addOnSuccessListener(q -> {
                    EventType type = null;
                    for (DocumentSnapshot doc : q.getDocuments()) {
                        type = doc.toObject(EventType.class);
                        break;
                    }
                    callback.onEventTypeLoaded(type);
                })
                .addOnFailureListener(callback::onError);
    }
}
