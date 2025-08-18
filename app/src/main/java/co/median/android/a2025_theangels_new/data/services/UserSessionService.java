// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.services;
// IMPORTS
import com.google.firebase.auth.FirebaseAuth;
import co.median.android.a2025_theangels_new.data.models.UserSession;
// UserSessionService - Connects session info with persistent storage
public class UserSessionService {
// Returns the user first name.
    public static String getUserFirstName(String userUID) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null &&
                FirebaseAuth.getInstance().getCurrentUser().getUid().equals(userUID)) {
            return UserSession.getInstance().getFirstName();
        }
        return "";
    }
// Returns the user role.
    public static String getUserRole(String userUID) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null &&
                FirebaseAuth.getInstance().getCurrentUser().getUid().equals(userUID)) {
            return UserSession.getInstance().getRole();
        }
        return "";
    }
// Returns the user city.
    public static String getUserCity(String userUID) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null &&
                FirebaseAuth.getInstance().getCurrentUser().getUid().equals(userUID)) {
            return UserSession.getInstance().getCity();
        }
        return "";
    }
// Returns the user full name.
    public static String getUserFullName(String userUID) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null &&
                FirebaseAuth.getInstance().getCurrentUser().getUid().equals(userUID)) {
            String first = UserSession.getInstance().getFirstName();
            String last = UserSession.getInstance().getLastName();
            if (first == null) first = "";
            if (last == null) last = "";
            return (first + " " + last).trim();
        }
        return "";
    }
}
