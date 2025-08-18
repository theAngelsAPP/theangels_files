// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.models;
// IMPORTS
import java.util.Date;
import java.util.List;
import com.onesignal.Continue;
import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;
// UserSession - Stores information about the currently logged-in user
public class UserSession {
// VARIABLES
    private static UserSession instance;
    private String email;
    private String phone;
    private Date birthDate;
    private String city;
    private String firstName;
    private boolean haveGunLicense;
    private String idNumber;
    private String imageURL;
    private String lastName;
    private List<String> medicalDetails;
    private String role;
    private List<String> volAvailable;
    private List<String> volCities;
    private Boolean volHaveDriverLicense;
    private String volVerification;
    private List<String> volSpecialty;
// Constructs a new UserSession.
    private UserSession() {}
    public UserSession(String email, String phone, Date birthDate, String city,
                       String firstName, boolean haveGunLicense, String idNumber,
                       String imageURL, String lastName, List<String> medicalDetails,
                       String role, List<String> volAvailable, List<String> volCities,
                       Boolean volHaveDriverLicense, String volVerification,
                       List<String> volSpecialty) {
        initialize(email, phone, birthDate, city, firstName, haveGunLicense,
                idNumber, imageURL, lastName, medicalDetails, role,
                volAvailable, volCities, volHaveDriverLicense, volVerification,
                volSpecialty);
    }
// Constructs a new UserSession.
    public UserSession(String firstName, String lastName, String imageURL) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.imageURL = imageURL;
    }
// Returns the instance.
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }
    public void initialize(String email, String phone, Date birthDate, String city,
                           String firstName, boolean haveGunLicense, String idNumber,
                           String imageURL, String lastName, List<String> medicalDetails, String role,
                           List<String> volAvailable, List<String> volCities,
                           Boolean volHaveDriverLicense, String volVerification,
                           List<String> volSpecialty) {
        this.email = email;
        this.phone = phone;
        this.birthDate = birthDate;
        this.city = city;
        this.firstName = firstName;
        this.haveGunLicense = haveGunLicense;
        this.idNumber = idNumber;
        this.imageURL = imageURL;
        this.lastName = lastName;
        this.medicalDetails = medicalDetails;
        this.role = role;
        this.volAvailable = volAvailable;
        this.volCities = volCities;
        this.volHaveDriverLicense = volHaveDriverLicense;
        this.volVerification = volVerification;
        this.volSpecialty = volSpecialty;
    }
// Returns the email.
    public String getEmail() { return email; }
// Returns the phone.
    public String getPhone() { return phone; }
// Returns the birth date.
    public Date getBirthDate() { return birthDate; }
// Returns the city.
    public String getCity() { return city; }
// Returns the first name.
    public String getFirstName() { return firstName; }
// Performs has gun license.
    public boolean hasGunLicense() { return haveGunLicense; }
// Returns the id number.
    public String getIdNumber() { return idNumber; }
// Returns the image url.
    public String getImageURL() { return imageURL; }
// Returns the last name.
    public String getLastName() { return lastName; }
// Returns the medical details.
    public List<String> getMedicalDetails() { return medicalDetails; }
// Returns the role.
    public String getRole() { return role; }
// Returns the vol available.
    public List<String> getVolAvailable() { return volAvailable; }
// Returns the vol cities.
    public List<String> getVolCities() { return volCities; }
// Returns the vol have driver license.
    public Boolean getVolHaveDriverLicense() { return volHaveDriverLicense; }
// Returns the vol verification.
    public String getVolVerification() { return volVerification; }
// Returns the vol specialty.
    public List<String> getVolSpecialty() { return volSpecialty; }
// Returns the medical details as string.
    public String getMedicalDetailsAsString() {
        if (medicalDetails == null || medicalDetails.isEmpty()) return "";
        return String.join(", ", medicalDetails);
    }
// Returns the vol available as string.
    public String getVolAvailableAsString() {
        if (volAvailable == null || volAvailable.isEmpty()) return "";
        return String.join(", ", volAvailable);
    }
// Returns the vol cities as string.
    public String getVolCitiesAsString() {
        if (volCities == null || volCities.isEmpty()) return "";
        return String.join(", ", volCities);
    }
// Returns the vol specialty as string.
    public String getVolSpecialtyAsString() {
        if (volSpecialty == null || volSpecialty.isEmpty()) return "";
        return String.join(", ", volSpecialty);
    }
// Performs clear.
    public void clear() {
        try {
            com.onesignal.OneSignal.logout();
            com.onesignal.OneSignal.getUser().removeTag("role");
            com.onesignal.OneSignal.getUser().removeTag("city");
        } catch (Exception e) {
            android.util.Log.e("UserSession", "Failed clearing OneSignal info", e);
        }
        instance = null;
    }
}
