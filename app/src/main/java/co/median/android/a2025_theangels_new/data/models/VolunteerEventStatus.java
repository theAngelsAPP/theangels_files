// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.models;
// VolunteerEventStatus - Tracks volunteer status for specific events
public enum VolunteerEventStatus {
// VARIABLES
    CLAIM("שיוך אירוע", UserEventStatus.LOOKING_FOR_VOLUNTEER),
    ARRIVAL_UPDATE("עדכון הגעה", UserEventStatus.VOLUNTEER_ON_THE_WAY),
    CLOSE("סגירת אירוע", UserEventStatus.VOLUNTEER_AT_EVENT);
    private final String dbValue;
    private final UserEventStatus userStatus;
    VolunteerEventStatus(String dbValue, UserEventStatus userStatus) {
        this.dbValue = dbValue;
        this.userStatus = userStatus;
    }
// Returns the db value.
    public String getDbValue() {
        return dbValue;
    }
// Returns the user status.
    public UserEventStatus getUserStatus() {
        return userStatus;
    }
// Performs from db value.
    public static VolunteerEventStatus fromDbValue(String value) {
        for (VolunteerEventStatus s : values()) {
            if (s.dbValue.equals(value)) {
                return s;
            }
        }
        return null;
    }
}
