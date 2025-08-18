// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.ui.home;

// IMPORTS
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import java.util.Map;

import co.median.android.a2025_theangels_new.R;
import co.median.android.a2025_theangels_new.data.models.Event;
import co.median.android.a2025_theangels_new.ui.events.active.EventVolActivity;
import co.median.android.a2025_theangels_new.data.map.AddressHelper;
import co.median.android.a2025_theangels_new.data.services.EventDisplayService;

// OpenEventsAdapter - Displays active events and keeps timers updated
public class OpenEventsAdapter extends ArrayAdapter<Event> {

    // VARIABLES
    private final Context context;
    private final ArrayList<Event> events;
    private final ArrayList<String> ids;
    private final int resource;
    private Map<String, String> eventTypeImages;
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            notifyDataSetChanged();
            timerHandler.postDelayed(this, 1000);
        }
    };

    // Starts a repeating task that refreshes relative times.
    // No params; returns nothing.
    public void startTimers() {
        timerHandler.post(timerRunnable);
    }

    // Stops the timer refresh task.
    // No params; returns nothing.
    public void stopTimers() {
        timerHandler.removeCallbacks(timerRunnable);
    }

    // Builds the adapter with context, layout, events, and IDs.
    // context - host activity; resource - layout ID; events - list of events; ids - event IDs
    public OpenEventsAdapter(Context context, int resource, ArrayList<Event> events, ArrayList<String> ids) {
        super(context, resource, events);
        this.context = context;
        this.events = events;
        this.ids = ids;
        this.resource = resource;
    }

    // Supplies images for event types.
    // map - mapping between event type and image URL; returns nothing.
    public void setEventTypeImages(Map<String, String> map) {
        this.eventTypeImages = map;
    }

    // Returns the number of events in the list.
    // No params; returns size of list.
    @Override
    public int getCount() {
        return events.size();
    }

    // Fetches an event by its position.
    // position - list index; returns the event.
    @Nullable
    @Override
    public Event getItem(int position) {
        return events.get(position);
    }

    // Inflates and fills an event row with data.
    // position - index in list; convertView - recycled view; parent - parent view group
    // Returns the prepared row view.
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(resource, parent, false);
        }

        Event event = getItem(position);
        ImageView icon = convertView.findViewById(R.id.open_event_icon);
        TextView title = convertView.findViewById(R.id.open_event_title);
        TextView since = convertView.findViewById(R.id.open_event_since);
        TextView location = convertView.findViewById(R.id.open_event_location);
        TextView status = convertView.findViewById(R.id.open_event_status);

        if (event != null) {
            // Build title from type and question choice
            title.setText("אירוע מסוג " + event.getEventType() +
                    " - דיווח על " + event.getEventQuestionChoice());

            // Load icon for this event type if available
            if (eventTypeImages != null && eventTypeImages.containsKey(event.getEventType())) {
                Glide.with(context.getApplicationContext())
                        .load(eventTypeImages.get(event.getEventType()))
                        .placeholder(R.drawable.medicevent)
                        .into(icon);
            }

            // Show how long the event has been open
            if (event.getEventTimeStarted() != null) {
                since.setText("האירוע נפתח " +
                        EventDisplayService.getRelativeTimeString(event.getEventTimeStarted()));
            }

            // Resolve the event address
            if (event.getEventLocation() != null) {
                String addr = AddressHelper.getAddressFromLatLng(context,
                        event.getEventLocation().getLatitude(),
                        event.getEventLocation().getLongitude());
                if (addr != null) {
                    String[] parts = EventDisplayService.splitAddress(addr);
                    String display = parts[1];
                    if (!parts[0].isEmpty()) {
                        display += ", " + parts[0];
                    }
                    location.setText("כתובת: " + display);
                }
            }

            // Display current status with appropriate color
            status.setText("סטטוס: " + event.getEventStatus());
            status.setTextColor(EventDisplayService.getStatusColor(context, event.getEventStatus()));
        }

        // Open event details when clicked
        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventVolActivity.class);
            intent.putExtra("eventId", ids.get(position));
            context.startActivity(intent);
        });
        return convertView;
    }
}
