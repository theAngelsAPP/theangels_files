// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)

package co.median.android.a2025_theangels_new.ui.events.list;

// IMPORTS
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import co.median.android.a2025_theangels_new.R;
import co.median.android.a2025_theangels_new.data.map.StaticMapFragment;
import co.median.android.a2025_theangels_new.data.models.Event;
import co.median.android.a2025_theangels_new.data.services.EventDataManager;
import co.median.android.a2025_theangels_new.data.services.UserDataManager;
import co.median.android.a2025_theangels_new.ui.main.BaseActivity;

// EventDetailsActivity - Shows detailed information for a selected event
public class EventDetailsActivity extends BaseActivity {

    // Builds the screen, reads intent extras, and loads the static map
    // Receives the saved state bundle and returns nothing
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        showTopBar(false);
        showBottomBar(false);

        // Read values passed from the previous screen
        String eventType = getIntent().getStringExtra("eventType");
        String handleBy = getIntent().getStringExtra("eventHandleBy");
        int rating = getIntent().getIntExtra("eventRating", 0);
        double eventLat = getIntent().getDoubleExtra("lat", 0);
        double eventLng = getIntent().getDoubleExtra("lng", 0);
        String imageUrl = getIntent().getStringExtra("typeImageURL");

        // Bind views used in the layout
        TextView tvType = findViewById(R.id.event_type_text);
        Button btnNavigate = findViewById(R.id.btnNavigate);
        TextView tvVolunteer = findViewById(R.id.volunteer_name);
        ImageView volunteerImage = findViewById(R.id.volunteer_image);
        RatingBar ratingBar = findViewById(R.id.ratingBarDetails);
        ImageView ivType = findViewById(R.id.event_type_image);
        LinearLayout findingsContainer = findViewById(R.id.findings_container);
        TextView durationText = findViewById(R.id.duration_text);
        TextView eventTypeSummary = findViewById(R.id.summary_event_type);
        TextView eventSubTypeSummary = findViewById(R.id.summary_event_subtype);
        TextView eventAnalysisText = findViewById(R.id.summary_event_analysis);
        TextView personAnalysisText = findViewById(R.id.summary_person_analysis);
        TextView guidanceAnalysisText = findViewById(R.id.summary_guidance_analysis);
        TextView closeReasonText = findViewById(R.id.close_reason_text);

        // Populate header fields if available
        if (tvType != null && eventType != null) {
            tvType.setText(eventType);
        }
        if (tvVolunteer != null && handleBy != null) {
            tvVolunteer.setText(handleBy);
        }
        if (imageUrl != null && ivType != null) {
            Glide.with(this).load(imageUrl).placeholder(R.drawable.event_medical).into(ivType);
        }
        if (ratingBar != null) {
            ratingBar.setRating(rating);
        }

        // Retrieve full event details from the database
        EventDataManager.getEventByType(eventType, new EventDataManager.SingleEventCallback() {
            @Override
            public void onEventLoaded(Event event) {
                if (event == null) {
                    return;
                }

                // Display findings section
                Map<String, Object> form = event.getEventForm();
                if (form != null && findingsContainer != null) {
                    findingsContainer.removeAllViews();
                    for (Map.Entry<String, Object> entry : form.entrySet()) {
                        boolean value = entry.getValue() instanceof Boolean && (Boolean) entry.getValue();
                        TextView tv = new TextView(EventDetailsActivity.this);
                        tv.setText((value ? "\u2714 " : "\u2716 ") + entry.getKey());
                        int color = getResources().getColor(value ? android.R.color.holo_green_dark : android.R.color.holo_red_dark);
                        tv.setTextColor(color);
                        findingsContainer.addView(tv);
                    }
                }

                // Show how long the event lasted
                if (event.getEventTimeStarted() != null && event.getEventTimeEnded() != null && durationText != null) {
                    long diff = event.getEventTimeEnded().toDate().getTime() - event.getEventTimeStarted().toDate().getTime();
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
                    durationText.setText("האירוע התרחש במשך " + minutes + " דקות");
                }

                // Populate summary text fields
                if (eventTypeSummary != null) eventTypeSummary.setText(event.getEventType());
                if (eventSubTypeSummary != null) eventSubTypeSummary.setText(event.getEventQuestionChoice());
                if (eventAnalysisText != null) eventAnalysisText.setText(event.getEventAnalysis());
                if (personAnalysisText != null) personAnalysisText.setText(event.getPersonAnalysis());
                if (guidanceAnalysisText != null) guidanceAnalysisText.setText(event.getGuidanceAnalysis());

                // Show reason the event was closed if provided
                if (closeReasonText != null && event.getEventCloseReason() != null) {
                    closeReasonText.setText(event.getEventCloseReason());
                }

                ratingBar.setRating(event.getEventRating());

                // Load volunteer information and photo
                String volunteerUid = event.getEventHandleBy();
                if (volunteerUid != null && !volunteerUid.isEmpty()) {
                    UserDataManager.loadBasicUserInfo(volunteerUid, info -> {
                        if (info != null) {
                            String name = info.getFirstName() + " " + info.getLastName();
                            if (tvVolunteer != null) {
                                tvVolunteer.setText(name);
                            }
                            if (!isFinishing() && !isDestroyed() && volunteerImage != null &&
                                    info.getImageURL() != null && !info.getImageURL().isEmpty()) {
                                Glide.with(EventDetailsActivity.this)
                                        .load(info.getImageURL())
                                        .placeholder(R.drawable.newuserpic)
                                        .circleCrop()
                                        .into(volunteerImage);
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                // No specific error handling required here
            }
        });

        // Translate coordinates into a readable address
        if (eventLat != 0 && eventLng != 0) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(eventLat, eventLng, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    String fullAddress = addresses.get(0).getAddressLine(0);
                    if (btnNavigate != null) {
                        btnNavigate.setText(fullAddress);
                    }
                }
            } catch (IOException ignored) {
            }
        }

        // Embed a static map with the event location
        StaticMapFragment mapFragment = StaticMapFragment.newInstance(eventLat, eventLng);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.map_container, mapFragment);
        transaction.commit();

        // Close the screen when the back button is pressed
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    // Supplies the layout resource used by this activity
    // Returns the layout identifier
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_event_details;
    }
}
