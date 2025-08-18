// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.models;
// IMPORTS
import com.google.firebase.firestore.DocumentId;
// Message - Represents a message sent between users
public class Message {
// VARIABLES
    @DocumentId
    private String id;
    private String messageTitle;
    private String messageData;
    private String messageType;
    private String messageRef;
// Constructs a new Message.
    public Message() {
    }
// Returns the message title.
    public String getMessageTitle() {
        return messageTitle;
    }
// Updates the message title.
    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }
// Returns the message data.
    public String getMessageData() {
        return messageData;
    }
// Updates the message data.
    public void setMessageData(String messageData) {
        this.messageData = messageData;
    }
// Returns the message type.
    public String getMessageType() {
        return messageType;
    }
// Updates the message type.
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
// Returns the message ref.
    public String getMessageRef() {
        return messageRef;
    }
// Updates the message ref.
    public void setMessageRef(String messageRef) {
        this.messageRef = messageRef;
    }
// Returns the id.
    public String getId() { return id; }
// Updates the id.
    public void setId(String id) { this.id = id; }
}
