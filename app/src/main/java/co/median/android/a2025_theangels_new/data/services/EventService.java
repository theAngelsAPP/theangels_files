// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.services;
// IMPORTS
import android.content.Context;
import java.text.SimpleDateFormat;
import java.util.Locale;
import co.median.android.a2025_theangels_new.data.map.AddressHelper;
import co.median.android.a2025_theangels_new.data.models.Event;
// EventService - Provides operations for event-related actions
public class EventService {
// Returns the address from event.
    public static String getAddressFromEvent(Context context, Event event) {
        if (event == null || event.getEventLocation() == null) return "";
        return AddressHelper.getAddressFromLatLng(
                context,
                event.getEventLocation().getLatitude(),
                event.getEventLocation().getLongitude());
    }
// Returns the city from event.
    public static String getCityFromEvent(Context context, Event event) {
        String address = getAddressFromEvent(context, event);
        if (address == null || address.isEmpty()) return "";
        String[] parts = EventDisplayService.splitAddress(address);
        return parts.length > 0 ? parts[0] : "";
    }
// Returns the readable event title.
    public static String getReadableEventTitle(Event event) {
        if (event == null) return "";
        String type = event.getEventType();
        return type != null ? "אירוע " + type : "אירוע";
    }
// Returns the formatted event time.
    public static String getFormattedEventTime(Event event) {
        if (event == null || event.getEventTimeStarted() == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(event.getEventTimeStarted().toDate());
    }
// Returns the street from event.
    public static String getStreetFromEvent(Context context, Event event) {
        String address = getAddressFromEvent(context, event);
        if (address == null || address.isEmpty()) return "";
        String[] parts = EventDisplayService.splitAddress(address);
        return parts.length > 1 ? parts[1] : "";
    }
}
