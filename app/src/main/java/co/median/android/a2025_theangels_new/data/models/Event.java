// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.models;
// IMPORTS
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.DocumentId;
import java.util.Map;
// Event - Represents an event and its related data
public class Event {
// VARIABLES
    @DocumentId
    private String id;
    private String eventCloseReason;
    private String eventCreatedBy;
    private Map<String, Object> eventForm;
    private String eventHandleBy;
    private GeoPoint eventLocation;
    private GeoPoint volunteerLocation;
    private int volunteerETA;
    private String eventQuestionChoice;
    private int eventRating;
    private String eventRatingText;
    private String eventStatus;
    private Timestamp eventTimeStarted;
    private Timestamp eventTimeEnded;
    private String eventType;
    private String eventAnalysis;
    private String personAnalysis;
    private String guidanceAnalysis;
// Constructs a new Event.
    public Event() {
    }
    public Event(String eventCloseReason,
                 String eventCreatedBy,
                 Map<String, Object> eventForm,
                 String eventHandleBy,
                 GeoPoint eventLocation,
                 GeoPoint volunteerLocation,
                 String eventQuestionChoice,
                 int eventRating,
                 String eventRatingText,
                 String eventStatus,
                 Timestamp eventTimeStarted,
                 Timestamp eventTimeEnded,
                 String eventType) {
        this.eventCloseReason = eventCloseReason
        ;this.eventCreatedBy = eventCreatedBy;
        this.eventForm = eventForm;
        this.eventHandleBy = eventHandleBy;
        this.eventLocation = eventLocation;
        this.volunteerLocation = volunteerLocation;
        this.eventQuestionChoice = eventQuestionChoice;
        this.eventRating = eventRating;
        this.eventRatingText = eventRatingText;
        this.eventStatus = eventStatus;
        this.eventTimeStarted = eventTimeStarted;
        this.eventTimeEnded = eventTimeEnded;
        this.eventType = eventType;
    }
// Returns the event close reason.
    public String getEventCloseReason() {
        return eventCloseReason;
    }
// Updates the event close reason.
    public void setEventCloseReason(String eventCloseReason) {
        this.eventCloseReason = eventCloseReason;
    }
// Returns the event created by.
    public String getEventCreatedBy() {
        return eventCreatedBy;
    }
// Updates the event created by.
    public void setEventCreatedBy(String eventCreatedBy) {
        this.eventCreatedBy = eventCreatedBy;
    }
// Returns the event form.
    public Map<String, Object> getEventForm() {
        return eventForm;
    }
// Updates the event form.
    public void setEventForm(Map<String, Object> eventForm) {
        this.eventForm = eventForm;
    }
// Returns the event handle by.
    public String getEventHandleBy() {
        return eventHandleBy;
    }
// Updates the event handle by.
    public void setEventHandleBy(String eventHandleBy) {
        this.eventHandleBy = eventHandleBy;
    }
// Returns the event location.
    public GeoPoint getEventLocation() {
        return eventLocation;
    }
// Updates the event location.
    public void setEventLocation(GeoPoint eventLocation) {
        this.eventLocation = eventLocation;
    }
// Returns the volunteer location.
    public GeoPoint getVolunteerLocation() {
        return volunteerLocation;
    }
// Updates the volunteer location.
    public void setVolunteerLocation(GeoPoint volunteerLocation) {
        this.volunteerLocation = volunteerLocation;
    }
// Returns the event question choice.
    public String getEventQuestionChoice() {
        return eventQuestionChoice;
    }
// Updates the event question choice.
    public void setEventQuestionChoice(String eventQuestionChoice) {
        this.eventQuestionChoice = eventQuestionChoice;
    }
// Returns the event rating.
    public int getEventRating() {
        return eventRating;
    }
// Updates the event rating.
    public void setEventRating(int eventRating) {
        this.eventRating = eventRating;
    }
// Returns the event rating text.
    public String getEventRatingText() {
        return eventRatingText;
    }
// Updates the event rating text.
    public void setEventRatingText(String eventRatingText) {
        this.eventRatingText = eventRatingText;
    }
// Returns the event status.
    public String getEventStatus() {
        return eventStatus;
    }
// Updates the event status.
    public void setEventStatus(String eventStatus) {
        this.eventStatus = eventStatus;
    }
// Returns the event time started.
    public Timestamp getEventTimeStarted() {
        return eventTimeStarted;
    }
// Updates the event time started.
    public void setEventTimeStarted(Timestamp eventTimeStarted) {
        this.eventTimeStarted = eventTimeStarted;
    }
// Returns the event time ended.
    public Timestamp getEventTimeEnded() {
        return eventTimeEnded;
    }
// Updates the event time ended.
    public void setEventTimeEnded(Timestamp eventTimeEnded) {
        this.eventTimeEnded = eventTimeEnded;
    }
// Returns the event type.
    public String getEventType() {
        return eventType;
    }
// Updates the event type.
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
// Returns the event analysis.
    public String getEventAnalysis() { return eventAnalysis; }
// Updates the event analysis.
    public void setEventAnalysis(String eventAnalysis) { this.eventAnalysis = eventAnalysis; }
// Returns the person analysis.
    public String getPersonAnalysis() { return personAnalysis; }
// Updates the person analysis.
    public void setPersonAnalysis(String personAnalysis) { this.personAnalysis = personAnalysis; }
// Returns the guidance analysis.
    public String getGuidanceAnalysis() { return guidanceAnalysis; }
// Updates the guidance analysis.
    public void setGuidanceAnalysis(String guidanceAnalysis) { this.guidanceAnalysis = guidanceAnalysis; }
// Returns the volunteer e t a.
    public int getVolunteerETA() { return volunteerETA; }
// Updates the volunteer e t a.
    public void setVolunteerETA(int volunteerETA) { this.volunteerETA = volunteerETA; }
// Returns the id.
    public String getId() { return id; }
// Updates the id.
    public void setId(String id) { this.id = id; }
}
