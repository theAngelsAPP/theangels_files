// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)

package co.median.android.a2025_theangels_new.ui.events.list;

// IMPORTS
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import co.median.android.a2025_theangels_new.R;
import co.median.android.a2025_theangels_new.data.models.Event;
import co.median.android.a2025_theangels_new.data.models.UserSession;
import co.median.android.a2025_theangels_new.data.services.UserDataManager;

// EventsAdapter - Binds event data to list rows
public class EventsAdapter extends ArrayAdapter<Event> {

    // VARIABLES
    private Context context;
    private ArrayList<Event> events;
    private int resource;
    private Map<String, String> eventTypeImages;
    private Map<String, String> eventStatusColors;
    private Map<String, UserSession> volunteerCache = new HashMap<>();

    // Builds the adapter with the context and event list
    // Accepts the host context, layout resource, and data set
    public EventsAdapter(Context context, int resource, ArrayList<Event> events) {
        super(context, resource, events);
        this.context = context;
        this.events = events;
        this.resource = resource;
    }

    // Supplies a map of event type names to their icons
    // Receives a mapping and returns nothing
    public void setEventTypeImages(Map<String, String> eventTypeImages) {
        this.eventTypeImages = eventTypeImages;
    }

    // Supplies status labels and their associated colors
    // Receives a mapping and returns nothing
    public void setEventStatusColors(Map<String, String> eventStatusColors) {
        this.eventStatusColors = eventStatusColors;
    }

    // Reports the number of events held by the adapter
    // Returns the size of the list
    @Override
    public int getCount() {
        return events.size();
    }

    // Retrieves the event at the requested position
    // Accepts the list position and returns the event
    @Nullable
    @Override
    public Event getItem(int position) {
        return events.get(position);
    }

    // Creates or reuses a view for an event row and populates its fields
    // Receives the item position, an optional recycled view, and the parent view group
    @NonNull
    @Override
    public View getView(int position, @Nullable View rootView, @NonNull ViewGroup parent) {
        if (rootView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            rootView = inflater.inflate(resource, parent, false);
        }

        // Force right-to-left layout direction
        rootView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        rootView.setTextDirection(View.TEXT_DIRECTION_RTL);
        View container = rootView.findViewById(R.id.event_card_container);
        if (container != null) {
            container.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            container.setTextDirection(View.TEXT_DIRECTION_RTL);
        }

        Event event = getItem(position);

        ImageView typeIcon = rootView.findViewById(R.id.event_category_icon);
        TextView whatHappened = rootView.findViewById(R.id.event_title);
        TextView date = rootView.findViewById(R.id.event_date);
        TextView statusLabel = rootView.findViewById(R.id.event_status);
        ImageView volunteerImage = rootView.findViewById(R.id.creator_image);
        TextView volunteerName = rootView.findViewById(R.id.creator_name);

        if (event != null) {
            // Show basic event info
            whatHappened.setText("אירוע " + event.getEventType());
            if (eventTypeImages != null && eventTypeImages.containsKey(event.getEventType())) {
                Glide.with(context.getApplicationContext())
                        .load(eventTypeImages.get(event.getEventType()))
                        .placeholder(R.drawable.event_vehicle)
                        .into(typeIcon);
            }

            // Display relative time
            if (event.getEventTimeStarted() != null) {
                Date d = event.getEventTimeStarted().toDate();
                date.setText(getTimeAgo(d));
            } else {
                date.setText("תאריך לא ידוע");
            }

            // Set status text and background color
            String statusText = event.getEventStatus() != null ? event.getEventStatus() : "לא ידוע";
            statusLabel.setText(statusText);
            if (eventStatusColors != null && eventStatusColors.containsKey(statusText)) {
                try {
                    int color = android.graphics.Color.parseColor(eventStatusColors.get(statusText));
                    statusLabel.setBackgroundColor(color);
                } catch (Exception ignored) {
                }
            }

            // Load volunteer information, using cache when available
            String uid = event.getEventHandleBy();
            if (uid != null && !uid.isEmpty()) {
                if (volunteerCache.containsKey(uid)) {
                    UserSession info = volunteerCache.get(uid);
                    if (info != null) {
                        volunteerName.setText(info.getFirstName() + " " + info.getLastName());
                        if (info.getImageURL() != null && !info.getImageURL().isEmpty()) {
                            Glide.with(context.getApplicationContext())
                                    .load(info.getImageURL())
                                    .placeholder(R.drawable.newuserpic)
                                    .circleCrop()
                                    .into(volunteerImage);
                        }
                    }
                } else {
                    UserDataManager.loadBasicUserInfo(uid, info -> {
                        if (info != null) {
                            volunteerCache.put(uid, info);
                            volunteerName.setText(info.getFirstName() + " " + info.getLastName());
                            if (info.getImageURL() != null && !info.getImageURL().isEmpty()) {
                                Glide.with(context.getApplicationContext())
                                        .load(info.getImageURL())
                                        .placeholder(R.drawable.newuserpic)
                                        .circleCrop()
                                        .into(volunteerImage);
                            }
                        }
                    });
                }
            }
        }

        // Navigate to the event details screen when tapped
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

        return rootView;
    }

    // Formats a date into a user-friendly relative time string
    // Accepts a Date and returns a localized description
    private String getTimeAgo(Date date) {
        long now = System.currentTimeMillis();
        if (date == null) {
            return "";
        }
        CharSequence ago = DateUtils.getRelativeTimeSpanString(
                date.getTime(),
                now,
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.FORMAT_SHOW_DATE);
        return ago != null ? ago.toString() : "";
    }
}
