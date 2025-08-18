// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.services;
// IMPORTS
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import co.median.android.a2025_theangels_new.data.models.UserEventStatus;
// ActiveEventManager - Keeps track of the user’s current event
public class ActiveEventManager {
// VARIABLES
    private static String activeEventId = null;
    private static ListenerRegistration registration = null;
    private static final List<ActiveEventListener> listeners = new CopyOnWriteArrayList<>();
    public interface ActiveEventListener {
        void onActiveEventChanged(String eventId);
    }
// Returns the active event id.
    public static String getActiveEventId() {
        return activeEventId;
    }
// Performs start listening.
    public static void startListening() {
        if (registration != null) return;
        String uid = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (uid == null) return;
        List<String> activeStatuses = Arrays.asList(
                UserEventStatus.LOOKING_FOR_VOLUNTEER.getDbValue(),
                UserEventStatus.VOLUNTEER_ON_THE_WAY.getDbValue(),
                UserEventStatus.VOLUNTEER_AT_EVENT.getDbValue()
        );
        registration = FirebaseFirestore.getInstance().collection("events")
                .whereEqualTo("eventCreatedBy", uid)
                .whereIn("eventStatus", activeStatuses)
                .addSnapshotListener((snap, e) -> {
                    String newId = null;
                    if (e == null && snap != null && !snap.getDocuments().isEmpty()) {
                        DocumentSnapshot doc = snap.getDocuments().get(0);
                        newId = doc.getId();
                    }
                    if ((newId == null && activeEventId != null) ||
                            (newId != null && !newId.equals(activeEventId))) {
                        activeEventId = newId;
                        notifyListeners();
                    }
                });
    }
// Performs stop listening.
    public static void stopListening() {
        if (registration != null) {
            registration.remove();
            registration = null;
        }
    }
// Performs register listener.
    public static void registerListener(ActiveEventListener l) {
        if (l == null) return;
        listeners.add(l);
        l.onActiveEventChanged(activeEventId);
    }
// Performs unregister listener.
    public static void unregisterListener(ActiveEventListener l) {
        listeners.remove(l);
    }
// Performs notify listeners.
    private static void notifyListeners() {
        for (ActiveEventListener l : new ArrayList<>(listeners)) {
            l.onActiveEventChanged(activeEventId);
        }
    }
}
