// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.services;
// IMPORTS
import android.content.Context;
import android.graphics.Color;
import com.google.firebase.Timestamp;
import java.util.Locale;
// EventDisplayService - Formats event information for presentation
public class EventDisplayService {
// Returns the relative time string.
    public static String getRelativeTimeString(Timestamp start) {
        if (start == null) return "";
        long diffSec = (System.currentTimeMillis() - start.toDate().getTime()) / 1000;
        if (diffSec < 60) {
            return "לפני פחות מדקה";
        }
        long minutes = diffSec / 60;
        if (minutes < 60) {
            if (minutes == 1) return "לפני דקה";
            return String.format(Locale.getDefault(), "לפני %d דקות", minutes);
        }
        long hours = minutes / 60;
        if (hours < 24) {
            if (hours == 1) return "לפני שעה";
            return String.format(Locale.getDefault(), "לפני %d שעות", hours);
        }
        long days = hours / 24;
        if (days == 1) return "לפני יום";
        return String.format(Locale.getDefault(), "לפני %d ימים", days);
    }
// Performs split address.
    public static String[] splitAddress(String fullAddress) {
        if (fullAddress == null) return new String[]{"", ""};
        String[] parts = fullAddress.split(",");
        String street = parts.length > 0 ? parts[0].trim() : fullAddress;
        String city = parts.length > 1 ? parts[1].trim() : "";
        return new String[]{city, street};
    }
// Returns the status color.
    public static int getStatusColor(Context ctx, String status) {
        if (status == null) return Color.GRAY;
        if (co.median.android.a2025_theangels_new.data.models.UserEventStatus.LOOKING_FOR_VOLUNTEER.getDbValue().equals(status)) {
            return Color.RED;
        } else if (co.median.android.a2025_theangels_new.data.models.UserEventStatus.VOLUNTEER_ON_THE_WAY.getDbValue().equals(status)) {
            return Color.BLUE;
        } else if (co.median.android.a2025_theangels_new.data.models.UserEventStatus.VOLUNTEER_AT_EVENT.getDbValue().equals(status)) {
            return Color.parseColor("#388E3C");
        }
        return Color.DKGRAY;
    }
}
