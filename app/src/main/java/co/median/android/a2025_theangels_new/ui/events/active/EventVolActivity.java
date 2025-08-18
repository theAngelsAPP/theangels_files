// =======================================
// IMPORTS
// =======================================
package co.median.android.a2025_theangels_new.ui.events.active;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import co.median.android.a2025_theangels_new.databinding.ActivityEventVolBinding;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.shuhart.stepview.StepView;
import java.util.Arrays;
import java.util.List;
import android.os.Handler;


import co.median.android.a2025_theangels_new.R;
import co.median.android.a2025_theangels_new.data.map.StaticMapFragment;
import co.median.android.a2025_theangels_new.data.map.AddressHelper;
import co.median.android.a2025_theangels_new.data.map.MapRouteManager;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;
import co.median.android.a2025_theangels_new.data.models.Event;
import co.median.android.a2025_theangels_new.data.services.EventDataManager;
import co.median.android.a2025_theangels_new.data.services.UserDataManager;
import co.median.android.a2025_theangels_new.util.TimerUtils;
import com.google.firebase.firestore.ListenerRegistration;
import co.median.android.a2025_theangels_new.ui.events.active.VolClaimFragment;
import co.median.android.a2025_theangels_new.ui.events.active.VolStatusFragment;
import co.median.android.a2025_theangels_new.ui.events.active.VolCloseFragment;
import co.median.android.a2025_theangels_new.ui.main.BaseActivity;
// =======================================
// EventVolActivity - Handles the volunteer flow during an active event
// =======================================
public class EventVolActivity extends BaseActivity {

    // =======================================
    // VARIABLES
    // =======================================
    private StepView stepView;
    private TextView timerTextView;
    private boolean isRunning = true;
    private int seconds = 0;
    private long eventStartMillis = -1L;
    private Handler handler = new Handler();
    private FrameLayout mapContainer;
    private SupportMapFragment mapFragment;
    private MapRouteManager routeManager;
    private int currentStep = 0;
    private ActivityEventVolBinding binding;
    private TextView etaTextView;
    private int lastEta = -1;

    private String eventId;
    private GeoPoint eventLocation;

    private List<Fragment> stepFragments;

    private void initFragments() {
        stepFragments = Arrays.asList(
                VolClaimFragment.newInstance(eventId),
                VolStatusFragment.newInstance(eventId),
                VolCloseFragment.newInstance(eventId)
        );
    }
    private ListenerRegistration eventListener;

    // =======================================
    // onCreate - Initializes volunteer event screen and step flow
    // =======================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showTopBar(false);
        showBottomBar(true);

        android.view.ViewGroup content = findViewById(co.median.android.a2025_theangels_new.R.id.activity_content);
        binding = ActivityEventVolBinding.bind(content.getChildAt(0));

        stepView = binding.stepView;
        timerTextView = binding.timerTextView;
        mapContainer = binding.mapContainer;
        etaTextView = binding.etaTextView;

        eventId = getIntent().getStringExtra("eventId");

        if (eventId != null) {
            android.content.Intent svc = new android.content.Intent(this, co.median.android.a2025_theangels_new.data.map.LocationUpdateService.class);
            svc.putExtra("eventId", eventId);
            androidx.core.content.ContextCompat.startForegroundService(this, svc);

            Intent statusSvc = new Intent(this, co.median.android.a2025_theangels_new.data.services.EmergencyStatusService.class);
            statusSvc.putExtra("eventId", eventId);
            statusSvc.putExtra("role", "volunteer");
            androidx.core.content.ContextCompat.startForegroundService(this, statusSvc);
        }

        startTimer();
        setupStepView();
        // map will be updated once event data arrives

        initFragments();
        loadStepFragment(0);

        if (eventId != null) {
            eventListener = EventDataManager.listenToEvent(eventId, (snapshot, e) -> {
                if (e == null && snapshot != null && snapshot.exists()) {
                    Event event = snapshot.toObject(Event.class);
                    if (event != null) {
                        eventLocation = event.getEventLocation();
                        if (eventLocation != null) {
                            updateMap(eventLocation.getLatitude(), eventLocation.getLongitude());
                            String addr = AddressHelper.getAddressFromLatLng(this, eventLocation.getLatitude(), eventLocation.getLongitude());
                            if (addr != null) {
                                TextView tv = findViewById(R.id.eventAddressText);
                                if (tv != null) tv.setText(addr);
                            }
                        }
                        if (routeManager != null) {
                            if (event.getEventCreatedBy() != null) {
                                UserDataManager.loadBasicUserInfo(event.getEventCreatedBy(), info -> {
                                    if (info != null) {
                                        routeManager.setUserProfileImage(EventVolActivity.this, info.getImageURL());
                                    }
                                });
                            }
                            if (event.getEventHandleBy() != null) {
                                UserDataManager.loadBasicUserInfo(event.getEventHandleBy(), info -> {
                                    if (info != null) {
                                        routeManager.setVolunteerProfileImage(EventVolActivity.this, info.getImageURL());
                                    }
                                });
                            }
                        }
                        if (routeManager != null && event.getVolunteerLocation() != null) {
                            LatLng pos = new LatLng(event.getVolunteerLocation().getLatitude(),
                                    event.getVolunteerLocation().getLongitude());
                            routeManager.updateRouteIfNeeded(pos);
                        }
                        if (event.getEventTimeStarted() != null && eventStartMillis == -1L) {
                            eventStartMillis = event.getEventTimeStarted().toDate().getTime();
                        }
                        if (event.getEventStatus() != null) {
                            java.util.List<String> statuses = java.util.Arrays.asList(
                                    getString(R.string.step_looking),
                                    getString(R.string.step_on_the_way),
                                    getString(R.string.step_arrived),
                                    getString(R.string.step_finished)
                            );
                            int idx = statuses.indexOf(event.getEventStatus());
                            if (idx >= 0 && idx < 3) updateStep(idx);
                            if (co.median.android.a2025_theangels_new.data.models.UserEventStatus.EVENT_FINISHED.getDbValue()
                                    .equals(event.getEventStatus())) {
                                runOnUiThread(this::navigateToHome);
                            }
                        }
                    }
                }
            });
        }

    }

    // =======================================
    // getLayoutResourceId - Returns layout resource
    // =======================================
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_event_vol;
    }

    // =======================================
    // setupMap - Initializes static map fragment with event location
    // =======================================
    private void updateMap(double lat, double lng) {
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            tx.replace(R.id.map_fragment_container, mapFragment).commit();
            mapFragment.getMapAsync(gMap -> {
                routeManager = new MapRouteManager(getString(R.string.google_maps_key));
                Event ev = new Event();
                ev.setId(eventId);
                ev.setEventLocation(new GeoPoint(lat, lng));
                routeManager.startTracking(ev, gMap, this);
                routeManager.setEtaListener(min -> runOnUiThread(() -> updateEta(min)));
            });
        }
    }

    // =======================================
    // setupStepView - Sets up step titles in StepView
    // =======================================
    private void setupStepView() {
        stepView.setSteps(Arrays.asList(
                getString(R.string.step_vol_claim),
                getString(R.string.step_vol_status),
                getString(R.string.step_vol_close)
        ));
        stepView.go(0, true);
    }

    // =======================================
    // updateStep - Updates StepView and replaces fragment according to current step
    // =======================================
    public void updateStep(int step) {
        if (stepView != null) {
            stepView.go(step, true);
            loadStepFragment(step);
        }
    }

    public void advanceToStep(int step) {
        currentStep = step;
        updateStep(step);
    }

    // =======================================
    // loadStepFragment - Loads fragment that corresponds to the current step
    // =======================================
    private void loadStepFragment(int step) {
        Fragment fragment = stepFragments.get(step);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    // =======================================
    // startTimer - Starts real-time timer for event duration
    // =======================================
    private void startTimer() {
        java.util.concurrent.atomic.AtomicLong counter = new java.util.concurrent.atomic.AtomicLong(seconds);
        TimerUtils.startTimer(timerTextView, handler,
                () -> eventStartMillis,
                () -> isRunning,
                counter);
    }

    private void updateEta(int minutes) {
        if (etaTextView == null) return;
        String text = getString(R.string.eta_format, minutes);
        if (minutes != lastEta) {
            lastEta = minutes;
            etaTextView.animate().alpha(0f).setDuration(200).withEndAction(() -> {
                etaTextView.setText(text);
                etaTextView.setVisibility(View.VISIBLE);
                etaTextView.animate().alpha(1f).setDuration(200).start();
            }).start();
        }
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, co.median.android.a2025_theangels_new.ui.home.HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eventListener != null) {
            eventListener.remove();
        }
        if (eventId != null) {
            android.content.Intent svc = new android.content.Intent(this, co.median.android.a2025_theangels_new.data.map.LocationUpdateService.class);
            stopService(svc);

            // Do not stop EmergencyStatusService so notification persists
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
    }}