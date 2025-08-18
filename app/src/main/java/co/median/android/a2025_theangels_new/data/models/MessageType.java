// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.models;
// IMPORTS
import com.google.firebase.firestore.DocumentId;
// MessageType - Lists possible message types in the app
public class MessageType {
// VARIABLES
    @DocumentId
    private String id;
    private String typeName;
    private String color;
    private String iconURL;
// Constructs a new MessageType.
    public MessageType() {
    }
// Returns the type name.
    public String getTypeName() {
        return typeName;
    }
// Updates the type name.
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
// Returns the color.
    public String getColor() {
        return color;
    }
// Updates the color.
    public void setColor(String color) {
        this.color = color;
    }
// Returns the icon url.
    public String getIconURL() {
        return iconURL;
    }
// Updates the icon url.
    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }
// Returns the id.
    public String getId() { return id; }
// Updates the id.
    public void setId(String id) { this.id = id; }
}
