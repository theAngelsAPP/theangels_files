// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.services;
// IMPORTS
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Collections;
import co.median.android.a2025_theangels_new.data.models.Event;
// EventDataManager - Coordinates event data operations
public class EventDataManager {
// VARIABLES
    private static final String TAG = "EventDataManager";
    public interface EventCallback {
        void onEventsLoaded(ArrayList<Event> events);
        void onError(Exception e);
    }
// Returns the all events.
    public static void getAllEvents(EventCallback callback) {
        Log.d(TAG, "getAllEvents called - starting Firestore fetch");
        FirebaseFirestore.getInstance().collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Event> events = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        try {
                            Event event = doc.toObject(Event.class);
                            if (event != null) {
                                Log.d(TAG, "Event loaded: " + event.getEventType());
                                events.add(event);
                            } else {
                                Log.w(TAG, "Event is null for document: " + doc.getId());
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to convert document to Event: " + doc.getId(), e);
                        }
                    }
                    Log.d(TAG, "Total events fetched: " + events.size());
                    callback.onEventsLoaded(events);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching events from Firestore", e);
                    callback.onError(e);
                });
    }
// Returns the last events created by user.
    public static void getLastEventsCreatedByUser(String uid, int limit, EventCallback callback) {
        FirebaseFirestore.getInstance().collection("events")
                .whereEqualTo("eventCreatedBy", uid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Event> events = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Event event = doc.toObject(Event.class);
                        if (event != null) {
                            events.add(event);
                        }
                    }
                    Collections.sort(events, (e1, e2) -> {
                        if (e1.getEventTimeStarted() == null && e2.getEventTimeStarted() == null) {
                            return 0;
                        } else if (e1.getEventTimeStarted() == null) {
                            return 1;
                        } else if (e2.getEventTimeStarted() == null) {
                            return -1;
                        }
                        return e2.getEventTimeStarted().compareTo(e1.getEventTimeStarted());
                    });
                    if (events.size() > limit) {
                        events = new ArrayList<>(events.subList(0, limit));
                    }
                    callback.onEventsLoaded(events);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching recent events", e);
                    callback.onError(e);
                });
    }
    public static com.google.firebase.firestore.ListenerRegistration listenToLastEventsCreatedByUser(
            String uid, int limit, EventCallback callback) {
        return FirebaseFirestore.getInstance().collection("events")
                .whereEqualTo("eventCreatedBy", uid)
                .addSnapshotListener((snap, e) -> {
                    if (e != null) {
                        if (callback != null) callback.onError(e);
                        return;
                    }
                    if (snap == null) return;
                    java.util.ArrayList<Event> events = new java.util.ArrayList<>();
                    for (DocumentSnapshot doc : snap.getDocuments()) {
                        Event event = doc.toObject(Event.class);
                        if (event != null) {
                            events.add(event);
                        }
                    }
                    java.util.Collections.sort(events, (e1, e2) -> {
                        if (e1.getEventTimeStarted() == null && e2.getEventTimeStarted() == null) {
                            return 0;
                        } else if (e1.getEventTimeStarted() == null) {
                            return 1;
                        } else if (e2.getEventTimeStarted() == null) {
                            return -1;
                        }
                        return e2.getEventTimeStarted().compareTo(e1.getEventTimeStarted());
                    });
                    if (events.size() > limit) {
                        events = new java.util.ArrayList<>(events.subList(0, limit));
                    }
                    if (callback != null) callback.onEventsLoaded(events);
                });
    }
// Returns the events created by user.
    public static void getEventsCreatedByUser(String uid, EventCallback callback) {
        FirebaseFirestore.getInstance().collection("events")
                .whereEqualTo("eventCreatedBy", uid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Event> events = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Event event = doc.toObject(Event.class);
                        if (event != null) {
                            events.add(event);
                        }
                    }
                    Collections.sort(events, (e1, e2) -> {
                        if (e1.getEventTimeStarted() == null && e2.getEventTimeStarted() == null) {
                            return 0;
                        } else if (e1.getEventTimeStarted() == null) {
                            return 1;
                        } else if (e2.getEventTimeStarted() == null) {
                            return -1;
                        }
                        return e2.getEventTimeStarted().compareTo(e1.getEventTimeStarted());
                    });
                    callback.onEventsLoaded(events);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user's events", e);
                    callback.onError(e);
                });
    }
    public interface SingleEventCallback {
        void onEventLoaded(Event event);
        void onError(Exception e);
    }
// Returns the event by type.
    public static void getEventByType(@NonNull String eventType, SingleEventCallback callback) {
        FirebaseFirestore.getInstance().collection("events")
                .whereEqualTo("eventType", eventType)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Event event = null;
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        event = doc.toObject(Event.class);
                        if (event != null) {
                            break;
                        }
                    }
                    callback.onEventLoaded(event);
                })
                .addOnFailureListener(callback::onError);
    }
    public interface StringCallback {
        void onSuccess(String value);
    }
    public interface ErrorCallback {
        void onError(Exception e);
    }
// Returns the event by id.
    public static void getEventById(@NonNull String eventId, SingleEventCallback callback) {
        FirebaseFirestore.getInstance().collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(doc -> {
                    Event ev = null;
                    if (doc != null && doc.exists()) {
                        ev = doc.toObject(Event.class);
                    }
                    callback.onEventLoaded(ev);
                })
                .addOnFailureListener(callback::onError);
    }
    public static void createNewEvent(@NonNull java.util.Map<String, Object> data,
                                      StringCallback onSuccess,
                                      ErrorCallback onError) {
        FirebaseFirestore.getInstance().collection("events")
                .add(data)
                .addOnSuccessListener(docRef -> {
                    if (onSuccess != null) onSuccess.onSuccess(docRef.getId());
                })
                .addOnFailureListener(e -> {
                    if (onError != null) onError.onError(e);
                });
    }
    public static com.google.firebase.firestore.ListenerRegistration listenToEvent(
            @NonNull String eventId,
            com.google.firebase.firestore.EventListener<DocumentSnapshot> listener) {
        return FirebaseFirestore.getInstance()
                .collection("events")
                .document(eventId)
                .addSnapshotListener(listener);
    }
    public static void updateEvent(@NonNull String eventId,
                                   @NonNull java.util.Map<String, Object> updates,
                                   Runnable onSuccess,
                                   ErrorCallback onError) {
        FirebaseFirestore.getInstance().collection("events")
                .document(eventId)
                .update(updates)
                .addOnSuccessListener(unused -> { if (onSuccess != null) onSuccess.run(); })
                .addOnFailureListener(e -> { if (onError != null) onError.onError(e); });
    }
    public static void claimEvent(@NonNull String eventId,
                                  @NonNull String volunteerUid,
                                  Runnable onSuccess,
                                  ErrorCallback onError) {
        java.util.Map<String, Object> updates = new java.util.HashMap<>();
        updates.put("eventHandleBy", volunteerUid);
        updates.put("eventStatus", co.median.android.a2025_theangels_new.data.models.UserEventStatus.VOLUNTEER_ON_THE_WAY.getDbValue());
        updateEvent(eventId, updates, onSuccess, onError);
    }
    public static void updateEventStatus(@NonNull String eventId,
                                         @NonNull String status,
                                         Runnable onSuccess,
                                         ErrorCallback onError) {
        java.util.Map<String, Object> updates = new java.util.HashMap<>();
        updates.put("eventStatus", status);
        updateEvent(eventId, updates, onSuccess, onError);
    }
    public static void updateVolunteerLocation(@NonNull String eventId,
                                               @NonNull com.google.firebase.firestore.GeoPoint loc,
                                               Runnable onSuccess,
                                               ErrorCallback onError) {
        java.util.Map<String, Object> updates = new java.util.HashMap<>();
        updates.put("volunteerLocation", loc);
        updateEvent(eventId, updates, onSuccess, onError);
    }
    public static void updateVolunteerETA(@NonNull String eventId,
                                          int etaMinutes,
                                          Runnable onSuccess,
                                          ErrorCallback onError) {
        java.util.Map<String, Object> updates = new java.util.HashMap<>();
        updates.put("volunteerETA", etaMinutes);
        updateEvent(eventId, updates, onSuccess, onError);
    }
    public interface OpenEventsListener {
        void onEventsUpdate(java.util.ArrayList<String> ids, java.util.ArrayList<Event> events);
    }
// Performs listen to active events.
    public static com.google.firebase.firestore.ListenerRegistration listenToActiveEvents(OpenEventsListener listener) {
        java.util.List<String> activeStatuses = java.util.Arrays.asList(
                co.median.android.a2025_theangels_new.data.models.UserEventStatus.LOOKING_FOR_VOLUNTEER.getDbValue(),
                co.median.android.a2025_theangels_new.data.models.UserEventStatus.VOLUNTEER_ON_THE_WAY.getDbValue(),
                co.median.android.a2025_theangels_new.data.models.UserEventStatus.VOLUNTEER_AT_EVENT.getDbValue()
        );
        return FirebaseFirestore.getInstance().collection("events")
                .whereIn("eventStatus", activeStatuses)
                .addSnapshotListener((snap, e) -> {
                    if (e == null && snap != null) {
                        java.util.ArrayList<Event> list = new java.util.ArrayList<>();
                        java.util.ArrayList<String> ids = new java.util.ArrayList<>();
                        for (DocumentSnapshot doc : snap.getDocuments()) {
                            Event ev = doc.toObject(Event.class);
                            if (ev != null) {
                                list.add(ev);
                                ids.add(doc.getId());
                            }
                        }
                        if (listener != null) listener.onEventsUpdate(ids, list);
                    }
                });
    }
}
