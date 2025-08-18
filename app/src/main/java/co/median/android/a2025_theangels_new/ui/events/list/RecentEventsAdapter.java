// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)

package co.median.android.a2025_theangels_new.ui.events.list;

// IMPORTS
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Map;

import co.median.android.a2025_theangels_new.R;
import co.median.android.a2025_theangels_new.data.models.Event;

// RecentEventsAdapter - Displays a compact list of the user's latest events
public class RecentEventsAdapter extends ArrayAdapter<Event> {

    // VARIABLES
    private final Context context;
    private final ArrayList<Event> events;
    private final int resource;
    private Map<String, String> eventTypeImages;
    private Map<String, String> eventStatusColors;

    // Builds the adapter for recent events
    // Accepts the host context, layout resource, and the event list
    public RecentEventsAdapter(Context context, int resource, ArrayList<Event> events) {
        super(context, resource, events);
        this.context = context;
        this.events = events;
        this.resource = resource;
    }

    // Sets the mapping of event types to image URLs
    // Receives a map and returns nothing
    public void setEventTypeImages(Map<String, String> eventTypeImages) {
        this.eventTypeImages = eventTypeImages;
    }

    // Sets the mapping of event statuses to colors
    // Receives a map and returns nothing
    public void setEventStatusColors(Map<String, String> eventStatusColors) {
        this.eventStatusColors = eventStatusColors;
    }

    // Reports how many events are available
    // Returns the size of the list
    @Override
    public int getCount() {
        return events.size();
    }

    // Retrieves an event at a given position
    // Accepts the position and returns the event
    @Nullable
    @Override
    public Event getItem(int position) {
        return events.get(position);
    }

    // Creates or recycles a row view and fills it with event data
    // Receives the item position, an optional recycled view, and the parent view group
    @NonNull
    @Override
    public View getView(int position, @Nullable View rootView, @NonNull ViewGroup parent) {
        if (rootView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            rootView = inflater.inflate(resource, parent, false);
        }

        Event event = getItem(position);

        ImageView icon = rootView.findViewById(R.id.event_icon);
        View statusDot = rootView.findViewById(R.id.status_dot);
        TextView typeName = rootView.findViewById(R.id.event_type_name);
        TextView statusLabel = rootView.findViewById(R.id.event_status);

        if (event != null) {
            // Populate basic labels
            typeName.setText(event.getEventType());
            statusLabel.setText(event.getEventStatus());

            // Load the type icon if available
            if (eventTypeImages != null && eventTypeImages.containsKey(event.getEventType())) {
                Glide.with(context.getApplicationContext())
                        .load(eventTypeImages.get(event.getEventType()))
                        .placeholder(R.drawable.medicevent)
                        .into(icon);
            }

            // Determine the status color
            int color = co.median.android.a2025_theangels_new.data.services.EventDisplayService
                    .getStatusColor(context, event.getEventStatus());
            if (co.median.android.a2025_theangels_new.data.models.UserEventStatus
                    .EVENT_FINISHED.getDbValue().equals(event.getEventStatus())) {
                color = Color.parseColor("#388E3C");
            }
            statusDot.setBackgroundColor(color);
        }

        // Different handling for active versus finished events
        String finished = context.getString(R.string.status_event_finished);
        if (!finished.equals(event.getEventStatus())) {
            rootView.setBackgroundResource(R.drawable.active_event_background);
            rootView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.blink));
            rootView.setOnClickListener(v -> {
                Intent intent = new Intent(context, co.median.android.a2025_theangels_new.ui.events.active.EventUserActivity.class);
                intent.putExtra("eventId", event.getId());
                context.startActivity(intent);
            });
        } else {
            rootView.setBackgroundResource(R.drawable.event_row_background);
            rootView.clearAnimation();
            rootView.setOnClickListener(v -> {
                Intent intent = new Intent(context, EventDetailsActivity.class);
                intent.putExtra("eventType", event.getEventType());
                intent.putExtra("eventStatus", event.getEventStatus());
                intent.putExtra("eventHandleBy", event.getEventHandleBy());
                if (event.getEventTimeStarted() != null) {
                    intent.putExtra("eventTimeStarted", event.getEventTimeStarted().getSeconds());
                }
                intent.putExtra("eventRating", event.getEventRating());
                if (event.getEventLocation() != null) {
                    intent.putExtra("lat", event.getEventLocation().getLatitude());
                    intent.putExtra("lng", event.getEventLocation().getLongitude());
                }
                if (eventTypeImages != null && eventTypeImages.containsKey(event.getEventType())) {
                    intent.putExtra("typeImageURL", eventTypeImages.get(event.getEventType()));
                }
                context.startActivity(intent);
            });
        }

        return rootView;
    }
}
