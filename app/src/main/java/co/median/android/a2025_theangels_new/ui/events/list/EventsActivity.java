// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)

package co.median.android.a2025_theangels_new.ui.events.list;

// IMPORTS
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import co.median.android.a2025_theangels_new.R;
import co.median.android.a2025_theangels_new.data.models.Event;
import co.median.android.a2025_theangels_new.data.models.EventType;
import co.median.android.a2025_theangels_new.data.services.EventDataManager;
import co.median.android.a2025_theangels_new.data.services.EventTypeDataManager;
import co.median.android.a2025_theangels_new.ui.main.BaseActivity;

// EventsActivity - Lists all events created by the logged-in user
public class EventsActivity extends BaseActivity {

    // VARIABLES
    private static final String TAG = "EventsActivity";
    private ListView eventsListView;
    private EventsAdapter adapter;
    private ArrayList<Event> events;
    private Map<String, String> typeImageMap = new HashMap<>();

    // Sets up the list and loads event data
    // Receives the saved state bundle and returns nothing
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showTopBar(true);
        showBottomBar(true);

        // Prepare list and adapter
        eventsListView = findViewById(R.id.events_lv);
        events = new ArrayList<>();
        adapter = new EventsAdapter(this, R.layout.event, events);
        eventsListView.setAdapter(adapter);

        loadEventTypes();
    }

    // Fetches event type images before loading events
    // No parameters and no return value
    private void loadEventTypes() {
        EventTypeDataManager.getAllEventTypes(new EventTypeDataManager.EventTypeCallback() {
            @Override
            public void onEventTypesLoaded(ArrayList<EventType> types) {
                for (EventType type : types) {
                    typeImageMap.put(type.getTypeName(), type.getTypeImageURL());
                }
                adapter.setEventTypeImages(typeImageMap);
                loadEventsFromFirestore();
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error loading event types", e);
                loadEventsFromFirestore();
            }
        });
    }

    // Retrieves the user's events from Firestore
    // No parameters and no return value
    private void loadEventsFromFirestore() {
        Log.d(TAG, "Fetching user's events from Firestore...");
        String uid = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
        EventDataManager.getEventsCreatedByUser(uid, new EventDataManager.EventCallback() {
            @Override
            public void onEventsLoaded(ArrayList<Event> loadedEvents) {
                Log.d(TAG, "evets loaded successfully. Count: " + loadedEvents.size());
                events.clear();
                events.addAll(loadedEvents);
                adapter.notifyDataSetChanged();
                Log.d(TAG, "Adapter updated with new trainings");
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error loading events from Firestore", e);
                Toast.makeText(EventsActivity.this, R.string.error_loading_events, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Provides the layout resource used by this activity
    // Returns the layout identifier
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_events;
    }
}
