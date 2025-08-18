// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.services;
// IMPORTS
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.onesignal.OneSignal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.Map;
import com.onesignal.Continue;
import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;
import co.median.android.a2025_theangels_new.data.models.UserSession;
// UserDataManager - Handles user data interaction with backend
public class UserDataManager {
// VARIABLES
    private static final String TAG = "UserDataManager";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
// Returns the events created by user.
    public static void getEventsCreatedByUser(String uid, Consumer<List<Map<String, Object>>> callback) {
        db.collection("events")
                .whereEqualTo("eventCreatedBy", uid)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Map<String, Object>> events = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        events.add(doc.getData());
                    }
                    callback.accept(events);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "שגיאה בשליפת אירועים שנוצרו על ידי המשתמש", e);
                    callback.accept(new ArrayList<>());
                });
    }
// Returns the events handled by user.
    public static void getEventsHandledByUser(String uid, Consumer<List<Map<String, Object>>> callback) {
        db.collection("events")
                .whereEqualTo("eventHandleBy", uid)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Map<String, Object>> events = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        events.add(doc.getData());
                    }
                    callback.accept(events);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "שגיאה בשליפת אירועים שטופלו על ידי המשתמש", e);
                    callback.accept(new ArrayList<>());
                });
    }
// Performs load user details.
    public static void loadUserDetails(String uid, Consumer<UserSession> callback) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(document -> {
                    if (document != null && document.exists()) {
                        String email = document.getString("Email");
                        String phone = document.getString("Phone");
                        java.util.Date birthDate = document.getDate("birthDate");
                        String city = document.getString("city");
                        String firstName = document.getString("firstName");
                        Boolean gun = document.getBoolean("haveGunLicense");
                        String idNumber = document.getString("idNumber");
                        String imageURL = document.getString("imageURL");
                        String lastName = document.getString("lastName");
                        List<String> medicalDetails = (List<String>) document.get("medicalDetails");
                        String role = document.getString("role");
                        java.util.List<String> volAvailable = (java.util.List<String>) document.get("volAvailable");
                        java.util.List<String> volCities = (java.util.List<String>) document.get("volCities");
                        Boolean volDriver = document.getBoolean("volHaveDriverLicense");
                        String volVerification = document.getString("volVerification");
                        java.util.List<String> volSpecialty = (java.util.List<String>) document.get("volSpecialty");
                        UserSession.getInstance().initialize(
                                email, phone, birthDate, city,
                                firstName, gun != null && gun,
                                idNumber, imageURL, lastName,
                                medicalDetails, role,
                                volAvailable, volCities,
                                volDriver, volVerification,
                                volSpecialty);
                        String firebaseUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Log.d(TAG, "Setting OneSignal external ID: " + firebaseUid);
                        OneSignal.login(firebaseUid);
                        String userCity = UserSession.getInstance().getCity();
                        if (userCity != null && !userCity.isEmpty()) {
                            Log.d(TAG, "Adding OneSignal city tag: " + userCity);
                            OneSignal.getUser().addTag("city", userCity);
                        }
                        String userRole = UserSession.getInstance().getRole();
                        if ("מתנדב".equals(userRole)) {
                            Log.d(TAG, "Adding OneSignal volunteer tag");
                            OneSignal.getUser().addTag("role", "volunteer");
                        } else {
                            Log.d(TAG, "Adding OneSignal user tag");
                            OneSignal.getUser().addTag("role", "user");
                        }
                        callback.accept(UserSession.getInstance());
                    } else {
                        callback.accept(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "שגיאה בשליפת נתוני משתמש", e);
                    callback.accept(null);
                });
    }
// Performs fetch user details.
    public static void fetchUserDetails(String uid, Consumer<UserSession> callback) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(document -> {
                    if (document != null && document.exists()) {
                        String email = document.getString("Email");
                        String phone = document.getString("Phone");
                        java.util.Date birthDate = document.getDate("birthDate");
                        String city = document.getString("city");
                        String firstName = document.getString("firstName");
                        Boolean gun = document.getBoolean("haveGunLicense");
                        String idNumber = document.getString("idNumber");
                        String imageURL = document.getString("imageURL");
                        String lastName = document.getString("lastName");
                        java.util.List<String> medicalDetails = (java.util.List<String>) document.get("medicalDetails");
                        String role = document.getString("role");
                        java.util.List<String> volAvailable = (java.util.List<String>) document.get("volAvailable");
                        java.util.List<String> volCities = (java.util.List<String>) document.get("volCities");
                        Boolean volDriver = document.getBoolean("volHaveDriverLicense");
                        String volVerification = document.getString("volVerification");
                        java.util.List<String> volSpecialty = (java.util.List<String>) document.get("volSpecialty");
                        UserSession session = new UserSession(
                                email, phone, birthDate, city,
                                firstName, gun != null && gun,
                                idNumber, imageURL, lastName,
                                medicalDetails, role,
                                volAvailable, volCities,
                                volDriver, volVerification,
                                volSpecialty);
                        callback.accept(session);
                    } else {
                        callback.accept(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "שגיאה בשליפת נתוני משתמש", e);
                    callback.accept(null);
                });
    }
// Returns the handled events count.
    public static void getHandledEventsCount(String uid, Consumer<Integer> callback) {
        db.collection("events")
                .whereEqualTo("eventHandleBy", uid)
                .get()
                .addOnSuccessListener(querySnapshot -> callback.accept(querySnapshot.size()))
                .addOnFailureListener(e -> {
                    callback.accept(0);
                });
    }
// Returns the handled events average rating.
    public static void getHandledEventsAverageRating(String uid, Consumer<Double> callback) {
        db.collection("events")
                .whereEqualTo("eventHandleBy", uid)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    double sum = 0;
                    int count = 0;
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Long rating = doc.getLong("eventRating");
                        if (rating != null) {
                            sum += rating;
                            count++;
                        }
                    }
                    callback.accept(count > 0 ? sum / count : 0.0);
                })
                .addOnFailureListener(e -> {
                    callback.accept(0.0);
                });
    }
// Performs update user details.
    public static void updateUserDetails(String uid, Map<String, Object> updates, Consumer<Boolean> callback) {
        db.collection("users").document(uid).update(updates)
                .addOnSuccessListener(unused ->
                        loadUserDetails(uid, session -> callback.accept(true)))
                .addOnFailureListener(e -> {
                    callback.accept(false);
                });
    }
// Performs load basic user info.
    public static void loadBasicUserInfo(String uid, Consumer<co.median.android.a2025_theangels_new.data.models.UserSession> callback) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(document -> {
                    if (document != null && document.exists()) {
                        String firstName = document.getString("firstName");
                        String lastName = document.getString("lastName");
                        String imageURL = document.getString("imageURL");
                        callback.accept(new co.median.android.a2025_theangels_new.data.models.UserSession(firstName, lastName, imageURL));
                    } else {
                        callback.accept(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "שגיאה בשליפת נתוני משתמש בסיסיים", e);
                    callback.accept(null);
                });
    }
// Performs create user.
    public static void createUser(String uid, Map<String, Object> data, Runnable onSuccess, Consumer<Exception> onError) {
        db.collection("users").document(uid).set(data)
                .addOnSuccessListener(unused -> { if (onSuccess != null) onSuccess.run(); })
                .addOnFailureListener(e -> { if (onError != null) onError.accept(e); });
    }
// Performs load medical details.
    public static void loadMedicalDetails(Consumer<java.util.List<String>> callback) {
        db.collection("medicalDetails").get()
                .addOnSuccessListener(querySnapshot -> {
                    java.util.List<String> list = new java.util.ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String name = doc.getString("name");
                        if (name != null) list.add(name);
                    }
                    callback.accept(list);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "שגיאה בטעינת פרטי רפואה", e);
                    callback.accept(new java.util.ArrayList<>());
                });
    }
}
