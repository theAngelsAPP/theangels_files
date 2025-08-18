// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.ui.home;

// IMPORTS
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import com.google.firebase.auth.FirebaseAuth;
import co.median.android.a2025_theangels_new.data.services.UserDataManager;
import java.util.Locale;
import android.provider.Settings;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import co.median.android.a2025_theangels_new.data.services.MessageDataManager;
import co.median.android.a2025_theangels_new.data.services.EducationDataManager;
import co.median.android.a2025_theangels_new.data.models.Message;
import co.median.android.a2025_theangels_new.data.models.MessageType;
import co.median.android.a2025_theangels_new.ui.home.MessagesAdapter.OnMessageClickListener;
import co.median.android.a2025_theangels_new.data.models.Education;
import co.median.android.a2025_theangels_new.ui.educations.EducationDetailsActivity;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import co.median.android.a2025_theangels_new.data.services.EventDataManager;
import co.median.android.a2025_theangels_new.data.services.EventTypeDataManager;
import co.median.android.a2025_theangels_new.data.models.EventType;
import com.google.firebase.firestore.ListenerRegistration;

import co.median.android.a2025_theangels_new.R;
import co.median.android.a2025_theangels_new.data.map.HomeMapFragment;
import co.median.android.a2025_theangels_new.data.models.UserSession;
import co.median.android.a2025_theangels_new.data.models.Event;
import co.median.android.a2025_theangels_new.ui.events.list.RecentEventsAdapter;
import co.median.android.a2025_theangels_new.ui.main.BaseActivity;
import co.median.android.a2025_theangels_new.ui.home.OpenEventsAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;

// HomeActivity - Main screen showing map, messages, and volunteer tools
public class HomeActivity extends BaseActivity implements HomeMapFragment.OnAddressChangeListener {

    // VARIABLES
    private LinearLayout locationPermissionContainer;
    private TextView tvLocationMessage, btnEnableLocation;
    private ImageView imgProfile;
    private TextView tvGreeting;
    private LinearLayout volDashboard;
    private TextView tvEventsCount, tvAvgRating;
    private LinearLayout cardAvailability;
    private ImageView imgAvailability;
    private TextView tvAvailabilityStatus, tvAvailabilitySub;
    private FrameLayout mapContainer;
    private TextView tvCurrentAddress;
    private TextView tvNoRecentEvents;
    private LinearLayout recentEventsSection;
    private LinearLayout recentEventsContainer;
    private RecentEventsAdapter recentEventsAdapter;
    private ArrayList<Event> recentEvents = new ArrayList<>();
    private Map<String, String> typeImageMap = new HashMap<>();
    private LinearLayout messagesContainer;
    private MessagesAdapter messagesAdapter;
    private ArrayList<Message> messages = new ArrayList<>();
    private LinearLayout openEventsWidget;
    private TextView tvOpenEventsBanner;
    private LinearLayout openEventsContainer;
    private OpenEventsAdapter openEventsAdapter;
    private ArrayList<Event> openEvents = new ArrayList<>();
    private ArrayList<String> openEventIds = new ArrayList<>();
    private ListenerRegistration openEventsListener;
    private ListenerRegistration recentEventsListener;
    private final com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();

    // Builds the home screen, binds views, and starts data listeners.
    // savedInstanceState - previously saved state bundle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showTopBar(true);
        showBottomBar(true);

        // Locate all view elements
        locationPermissionContainer = findViewById(R.id.location_permission_container);
        tvLocationMessage = findViewById(R.id.tv_location_message);
        btnEnableLocation = findViewById(R.id.btn_enable_location);
        imgProfile = findViewById(R.id.img_profile);
        tvGreeting = findViewById(R.id.tv_greeting);
        volDashboard = findViewById(R.id.volDashboard);
        tvEventsCount = findViewById(R.id.tv_events_count);
        tvAvgRating = findViewById(R.id.tv_avg_rating);
        cardAvailability = findViewById(R.id.card_availability);
        imgAvailability = findViewById(R.id.img_availability);
        tvAvailabilityStatus = findViewById(R.id.tv_availability_status);
        tvAvailabilitySub = findViewById(R.id.tv_availability_sub);
        mapContainer = findViewById(R.id.map_container);
        tvCurrentAddress = findViewById(R.id.tv_current_address);
        tvNoRecentEvents = findViewById(R.id.tv_no_recent_events);
        recentEventsSection = findViewById(R.id.recent_events_section);
        recentEventsContainer = findViewById(R.id.recent_events_container);
        recentEventsAdapter = new RecentEventsAdapter(this, R.layout.item_recent_event, recentEvents);
        messagesContainer = findViewById(R.id.messages_container);
        messagesAdapter = new MessagesAdapter(this, R.layout.message_item, messages);
        openEventsWidget = findViewById(R.id.vol_active_events_widget);
        tvOpenEventsBanner = findViewById(R.id.tv_open_events_banner);
        openEventsAdapter = new OpenEventsAdapter(this, R.layout.item_open_event, openEvents, openEventIds);

        // Load user details and greeting
        UserSession session = UserSession.getInstance();
        String fullName = session.getFirstName() + " " + session.getLastName();
        tvGreeting.setText("שלום, " + fullName);
        String url = session.getImageURL();
        if (url != null && !url.isEmpty()) {
            Glide.with(this).load(url).placeholder(R.drawable.newuserpic).into(imgProfile);
        }

        // Availability card setup
        updateAvailabilityCard();
        cardAvailability.setOnClickListener(v -> showAvailabilityEditor());

        // Volunteer-only dashboard and events
        if ("מתנדב".equals(session.getRole())) {
            volDashboard.setVisibility(View.VISIBLE);
            openEventsWidget.setVisibility(View.VISIBLE);
            loadVolunteerDashboardData();
            startOpenEventsListener();
            openEventsWidget.setOnClickListener(v -> showOpenEventsPopup());
        } else {
            volDashboard.setVisibility(View.GONE);
            openEventsWidget.setVisibility(View.GONE);
        }

        // Load event types and messages from server
        loadEventTypes();
        loadMessages();

        // Show map or request location permission
        checkLocationPermission();
        btnEnableLocation.setOnClickListener(v -> requestLocationPermission());
    }

    // Checks if location permission is granted and shows the map or a request banner.
    // No params; returns nothing.
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            hideLocationRequestBanner();
            mapContainer.setVisibility(View.VISIBLE);
            if (getSupportFragmentManager().findFragmentById(R.id.map_container) == null) {
                loadMapFragment();
            }
        } else {
            mapContainer.setVisibility(View.GONE);
            showLocationRequestBanner();
        }
    }

    // Loads the map fragment into the map container.
    // No params; returns nothing.
    private void loadMapFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        HomeMapFragment fragment = new HomeMapFragment();
        fragment.setAddressChangeListener(this);
        transaction.replace(R.id.map_container, fragment);
        transaction.commit();
    }

    // Asks the user for location permission when needed.
    // No params; returns nothing.
    private void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1002);
        }
    }

    // Displays the banner asking the user to grant location access.
    // No params; returns nothing.
    private void showLocationRequestBanner() {
        locationPermissionContainer.setVisibility(View.VISIBLE);
        tvLocationMessage.setText(getString(R.string.location_permission_message));
        btnEnableLocation.setText(getString(R.string.enable_permission_button));
    }

    // Fades out and hides the location permission banner.
    // No params; returns nothing.
    private void hideLocationRequestBanner() {
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(500);
        fadeOut.setFillAfter(true);
        locationPermissionContainer.startAnimation(fadeOut);
        locationPermissionContainer.setVisibility(View.GONE);
    }

    // Opens the application's settings page in Android system settings.
    // No params; returns nothing.
    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    // Shows the map and hides the permission banner after permission is granted.
    // No params; returns nothing.
    private void showMap() {
        hideLocationRequestBanner();
        mapContainer.setVisibility(View.VISIBLE);
        loadMapFragment();
    }

    // Pulls volunteer statistics and updates the dashboard widgets.
    // No params; returns nothing.
    private void loadVolunteerDashboardData() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UserDataManager.getHandledEventsCount(uid, count ->
                tvEventsCount.setText(String.valueOf(count)));
        UserDataManager.getHandledEventsAverageRating(uid, avg ->
                tvAvgRating.setText(String.format(Locale.getDefault(), "%.1f", avg)));
    }

    // Retrieves event type data and sets up listeners once loaded.
    // No params; returns nothing.
    private void loadEventTypes() {
        EventTypeDataManager.getAllEventTypes(new EventTypeDataManager.EventTypeCallback() {
            @Override
            public void onEventTypesLoaded(ArrayList<EventType> types) {
                for (EventType type : types) {
                    typeImageMap.put(type.getTypeName(), type.getTypeImageURL());
                }
                recentEventsAdapter.setEventTypeImages(typeImageMap);
                openEventsAdapter.setEventTypeImages(typeImageMap);
                startRecentEventsListener();
            }

            @Override
            public void onError(Exception e) {
                startRecentEventsListener();
            }
        });
    }


    // Starts a listener for the user's recent events and updates the list when data changes.
    // No params; returns nothing.
    private void startRecentEventsListener() {
        if (recentEventsListener != null) recentEventsListener.remove();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        recentEventsListener = EventDataManager.listenToLastEventsCreatedByUser(uid, 3, new EventDataManager.EventCallback() {
            @Override
            public void onEventsLoaded(ArrayList<Event> events) {
                recentEvents.clear();
                recentEvents.addAll(events);
                displayRecentEvents();
            }

            @Override
            public void onError(Exception e) {
            }
        });
    }

    // Renders the recent events list or hides the section if none exist.
    // No params; returns nothing.
    private void displayRecentEvents() {
        if (recentEventsSection == null) return;
        recentEventsContainer.removeAllViews();
        if (recentEvents.isEmpty()) {
            recentEventsSection.setVisibility(View.GONE);
            tvNoRecentEvents.setVisibility(View.GONE);
            return;
        }
        recentEventsSection.setVisibility(View.VISIBLE);
        tvNoRecentEvents.setVisibility(View.GONE);
        for (int i = 0; i < recentEvents.size(); i++) {
            View item = recentEventsAdapter.getView(i, null, recentEventsContainer);
            recentEventsContainer.addView(item);
        }
    }

    // Downloads messages and message types, then displays them.
    // No params; returns nothing.
    private void loadMessages() {
        messagesAdapter.setOnMessageClickListener(this::handleMessageClick);
        MessageDataManager.getMessagesWithTypes(new MessageDataManager.MessagesCallback() {
            @Override
            public void onMessagesLoaded(ArrayList<Message> msgs, Map<String, MessageType> types) {
                messages.clear();
                messages.addAll(msgs);
                messagesAdapter.setTypeMap(types);
                displayMessages();
            }

            @Override
            public void onError(Exception e) {
            }
        });
    }

    // Populates the messages section or hides it if no messages are available.
    // No params; returns nothing.
    private void displayMessages() {
        if (messagesContainer == null) return;
        messagesContainer.removeAllViews();
        if (messages.isEmpty()) {
            messagesContainer.setVisibility(View.GONE);
            return;
        }
        messagesContainer.setVisibility(View.VISIBLE);
        for (int i = 0; i < messages.size(); i++) {
            View item = messagesAdapter.getView(i, null, messagesContainer);
            messagesContainer.addView(item);
        }
    }

    // Listens for open events nearby and keeps the widget updated.
    // No params; returns nothing.
    private void startOpenEventsListener() {
        if (openEventsListener != null) openEventsListener.remove();
        openEventsListener = EventDataManager.listenToActiveEvents((ids, events) -> {
            openEventIds.clear();
            openEvents.clear();
            openEventIds.addAll(ids);
            openEvents.addAll(events);
            displayOpenEvents();
        });
    }

    // Shows the number of open events and renders them if present.
    // No params; returns nothing.
    private void displayOpenEvents() {
        if (openEventsWidget == null || tvOpenEventsBanner == null) return;
        if (openEvents.isEmpty()) {
            openEventsWidget.setVisibility(View.GONE);
            return;
        }
        openEventsWidget.setVisibility(View.VISIBLE);
        tvOpenEventsBanner.setText(openEvents.size() + " אירועים פתוחים באזורך");
        if (openEventsContainer != null) {
            openEventsContainer.removeAllViews();
            for (int i = 0; i < openEvents.size(); i++) {
                View item = openEventsAdapter.getView(i, null, openEventsContainer);
                openEventsContainer.addView(item);
            }
        }
    }

    // Opens a bottom sheet displaying all open events with live timers.
    // No params; returns nothing.
    private void showOpenEventsPopup() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_open_events, null);
        openEventsContainer = view.findViewById(R.id.open_events_container);
        openEventsContainer.removeAllViews();
        for (int i = 0; i < openEvents.size(); i++) {
            View item = openEventsAdapter.getView(i, null, openEventsContainer);
            openEventsContainer.addView(item);
        }
        openEventsAdapter.startTimers();
        dialog.setOnDismissListener(d -> {
            openEventsAdapter.stopTimers();
            openEventsContainer = null;
        });
        dialog.setContentView(view);
        dialog.show();
    }

    // Handles taps on a message; opens related education content if available.
    // message - the tapped message object
    // Returns nothing.
    private void handleMessageClick(Message message) {
        String ref = message.getMessageRef();
        if (ref == null || ref.isEmpty()) {
            return;
        }

        EducationDataManager.getEducationById(ref, new EducationDataManager.SingleEducationCallback() {
            @Override
            public void onEducationLoaded(Education education) {
                if (education != null) {
                    Intent intent = new Intent(HomeActivity.this, EducationDetailsActivity.class);
                    intent.putExtra("id", ref);
                    intent.putExtra("eduTitle", education.getEduTitle());
                    intent.putExtra("eduData", education.getEduData());
                    intent.putExtra("eduImageURL", education.getEduImageURL());
                    intent.putExtra("eduType", education.getEduType());
                    startActivity(intent);
                } else {
                    Toast.makeText(HomeActivity.this, R.string.education_not_found, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(HomeActivity.this, R.string.education_not_found, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Updates the availability card according to whether the volunteer is on call today.
    // No params; returns nothing.
    private void updateAvailabilityCard() {
        java.util.List<String> list = UserSession.getInstance().getVolAvailable();
        String today = getTodayHebrew();
        boolean available = list != null && list.contains(today);
        if (available) {
            tvAvailabilityStatus.setText("זמין");
            tvAvailabilitySub.setText("בכוננות");
            cardAvailability.setBackgroundResource(R.drawable.bg_available);
            Glide.with(this)
                    .load("https://cdn-icons-png.flaticon.com/128/18019/18019692.png")
                    .placeholder(R.drawable.ic_event)
                    .into(imgAvailability);
        } else {
            tvAvailabilityStatus.setText("לא זמין");
            tvAvailabilitySub.setText("לא בכוננות");
            cardAvailability.setBackgroundResource(R.drawable.bg_unavailable);
            Glide.with(this)
                    .load("https://cdn-icons-png.flaticon.com/128/16756/16756574.png")
                    .placeholder(R.drawable.ic_event)
                    .into(imgAvailability);
        }
    }

    // Returns today's weekday name in Hebrew for availability checks.
    // No params; returns the name of the day.
    private String getTodayHebrew() {
        String[] days = {"ראשון", "שני", "שלישי", "רביעי", "חמישי", "שישי", "שבת"};
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int index = cal.get(java.util.Calendar.DAY_OF_WEEK) - 1;
        return days[index];
    }

    // Opens a bottom sheet for selecting the volunteer's available days.
    // No params; returns nothing.
    private void showAvailabilityEditor() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        android.view.View view = getLayoutInflater().inflate(R.layout.bottom_sheet_availability, null);
        android.widget.LinearLayout container = view.findViewById(R.id.container_days);

        java.util.List<String> current = UserSession.getInstance().getVolAvailable();
        if (current == null) current = new java.util.ArrayList<>();

        String[] days = {"ראשון", "שני", "שלישי", "רביעי", "חמישי", "שישי", "שבת"};
        for (String day : days) {
            android.view.View row = getLayoutInflater().inflate(R.layout.item_day_toggle, container, false);
            android.widget.TextView tv = row.findViewById(R.id.tv_day_name);
            com.google.android.material.switchmaterial.SwitchMaterial sw = row.findViewById(R.id.day_switch);
            tv.setText(day);
            sw.setChecked(current.contains(day));
            sw.setOnCheckedChangeListener((buttonView, isChecked) -> updateDayAvailability(day, isChecked));
            container.addView(row);
        }

        dialog.setContentView(view);
        dialog.show();
    }

    // Updates a specific day in Firestore and refreshes the local session.
    // day - weekday name; add - true to add availability, false to remove
    // Returns nothing.
    private void updateDayAvailability(String day, boolean add) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (add) {
            db.collection("users").document(uid)
                    .update("volAvailable", com.google.firebase.firestore.FieldValue.arrayUnion(day));
        } else {
            db.collection("users").document(uid)
                    .update("volAvailable", com.google.firebase.firestore.FieldValue.arrayRemove(day));
        }

        java.util.List<String> updated = new java.util.ArrayList<>();
        java.util.List<String> current = UserSession.getInstance().getVolAvailable();
        if (current != null) updated.addAll(current);
        if (add && !updated.contains(day)) {
            updated.add(day);
        } else if (!add) {
            updated.remove(day);
        }

        UserSession session = UserSession.getInstance();
        session.initialize(
                session.getEmail(), session.getPhone(), session.getBirthDate(), session.getCity(),
                session.getFirstName(), session.hasGunLicense(), session.getIdNumber(),
                session.getImageURL(), session.getLastName(), session.getMedicalDetails(),
                session.getRole(), updated, session.getVolCities(),
                session.getVolHaveDriverLicense(), session.getVolVerification(), session.getVolSpecialty());
        updateAvailabilityCard();
    }

    // Handles the result from permission requests, directing the user accordingly.
    // requestCode - identifier for the permission request
    // permissions/grantResults - arrays provided by Android
    // Returns nothing.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1002) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showMap();
            } else {
                tvLocationMessage.setText(getString(R.string.location_permission_settings_message));
                btnEnableLocation.setText(getString(R.string.open_settings_button));
                btnEnableLocation.setOnClickListener(v -> openAppSettings());
            }
        }
    }

    // Supplies the layout resource used by this activity.
    // No params; returns the layout ID.
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home;
    }

    // Receives address updates from the map fragment and shows them on screen.
    // address - formatted address string; returns nothing.
    @Override
    public void onAddressChanged(String address) {
        if (tvCurrentAddress != null) {
            tvCurrentAddress.setText(address);
        }
    }

    // Cleans up listeners and timers when the activity is destroyed.
    // No params; returns nothing.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (openEventsListener != null) {
            openEventsListener.remove();
        }
        if (recentEventsListener != null) {
            recentEventsListener.remove();
        }
        openEventsAdapter.stopTimers();
    }
}