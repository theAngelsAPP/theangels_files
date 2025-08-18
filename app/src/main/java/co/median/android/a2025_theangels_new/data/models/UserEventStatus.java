// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.models;
// UserEventStatus - Tracks the relationship between a user and an event
public enum UserEventStatus {
// VARIABLES
    LOOKING_FOR_VOLUNTEER("חיפוש מתנדב"),
    VOLUNTEER_ON_THE_WAY("מתנדב בדרך"),
    VOLUNTEER_AT_EVENT("מתנדב באירוע"),
    EVENT_FINISHED("האירוע הסתיים");
    private final String dbValue;
    UserEventStatus(String dbValue) {
        this.dbValue = dbValue;
    }
// Returns the db value.
    public String getDbValue() {
        return dbValue;
    }
// Performs from db value.
    public static UserEventStatus fromDbValue(String value) {
        for (UserEventStatus s : values()) {
            if (s.dbValue.equals(value)) {
                return s;
            }
        }
        if ("אירוע הסתיים".equals(value)) {
            return EVENT_FINISHED;
        }
        return null;
    }
}
