// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.models;
// IMPORTS
import com.google.firebase.firestore.DocumentId;
// Education - Contains a user education record
public class Education {
// VARIABLES
    @DocumentId
    private String id;
    private String eduTitle;
    private String eduData;
    private String eduImageURL;
    private String eduType;
// Constructs a new Education.
    public Education() {
    }
// Constructs a new Education.
    public Education(String eduTitle, String eduData, String eduImageURL, String eduType) {
        this.eduTitle = eduTitle;
        this.eduData = eduData;
        this.eduImageURL = eduImageURL;
        this.eduType = eduType;
    }
// Returns the edu title.
    public String getEduTitle() {
        return eduTitle;
    }
// Updates the edu title.
    public void setEduTitle(String eduTitle) {
        this.eduTitle = eduTitle;
    }
// Returns the edu data.
    public String getEduData() {
        return eduData;
    }
// Updates the edu data.
    public void setEduData(String eduData) {
        this.eduData = eduData;
    }
// Returns the edu image url.
    public String getEduImageURL() {
        return eduImageURL;
    }
// Updates the edu image url.
    public void setEduImageURL(String eduImageURL) {
        this.eduImageURL = eduImageURL;
    }
// Returns the edu type.
    public String getEduType() {
        return eduType;
    }
// Updates the edu type.
    public void setEduType(String eduType) {
        this.eduType = eduType;
    }
// Returns the id.
    public String getId() { return id; }
// Updates the id.
    public void setId(String id) { this.id = id; }
}
