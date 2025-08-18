// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.models;
// IMPORTS
import java.util.List;
import com.google.firebase.firestore.DocumentId;
// EventType - Defines an event category and its default questions
public class EventType {
// VARIABLES
    @DocumentId
    private String id;
    private String typeName;
    private String typeImageURL;
    private List<String> questions;
// Constructs a new EventType.
    public EventType() {
    }
// Returns the type name.
    public String getTypeName() {
        return typeName;
    }
// Updates the type name.
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
// Returns the type image url.
    public String getTypeImageURL() {
        return typeImageURL;
    }
// Updates the type image url.
    public void setTypeImageURL(String typeImageURL) {
        this.typeImageURL = typeImageURL;
    }
// Returns the questions.
    public List<String> getQuestions() {
        return questions;
    }
// Updates the questions.
    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }
// Returns the id.
    public String getId() { return id; }
// Updates the id.
    public void setId(String id) { this.id = id; }
}
