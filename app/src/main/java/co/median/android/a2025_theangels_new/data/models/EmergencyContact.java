// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.models;
// IMPORTS
import com.google.firebase.firestore.DocumentId;
// EmergencyContact - Represents a contact designated for emergencies
public class EmergencyContact {
// VARIABLES
    @DocumentId
    private String id;
    private String contactName;
    private String contactPhone;
    private String contactRelationship;
    private String contactUserUID;
// Constructs a new EmergencyContact.
    public EmergencyContact() {}
// Returns the id.
    public String getId() { return id; }
// Updates the id.
    public void setId(String id) { this.id = id; }
// Returns the contact name.
    public String getContactName() { return contactName; }
// Updates the contact name.
    public void setContactName(String contactName) { this.contactName = contactName; }
// Returns the contact phone.
    public String getContactPhone() { return contactPhone; }
// Updates the contact phone.
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
// Returns the contact relationship.
    public String getContactRelationship() { return contactRelationship; }
// Updates the contact relationship.
    public void setContactRelationship(String contactRelationship) { this.contactRelationship = contactRelationship; }
// Returns the contact user u id.
    public String getContactUserUID() { return contactUserUID; }
// Updates the contact user u id.
    public void setContactUserUID(String contactUserUID) { this.contactUserUID = contactUserUID; }
}
