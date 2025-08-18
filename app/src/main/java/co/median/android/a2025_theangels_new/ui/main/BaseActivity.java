// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)

package co.median.android.a2025_theangels_new.ui.main;

// IMPORTS
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import co.median.android.a2025_theangels_new.R;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import co.median.android.a2025_theangels_new.data.models.UserSession;
import co.median.android.a2025_theangels_new.ui.educations.EducationActivity;
import co.median.android.a2025_theangels_new.ui.events.active.EventUserActivity;
import co.median.android.a2025_theangels_new.ui.events.create.NewEventActivity;
import co.median.android.a2025_theangels_new.ui.events.list.EventsActivity;
import co.median.android.a2025_theangels_new.ui.home.HomeActivity;
import co.median.android.a2025_theangels_new.ui.profile.ProfileActivity;
import co.median.android.a2025_theangels_new.data.services.ActiveEventManager;

// BaseActivity - Provides shared UI and navigation behavior for app screens

public abstract class BaseActivity extends AppCompatActivity {

    // VARIABLES
    private View topBar;
    private BottomAppBar bottomAppBar;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fabEmergency;
    private ImageView imgProfile;
    private TextView tvGreeting;
    private ActiveEventManager.ActiveEventListener activeEventListener;

    // onCreate - Sets up the base layout, session data, and navigation; savedInstanceState holds previous state, returns nothing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setTransparentStatusBar();
        hideSystemUI();

        // Force RTL layout on all devices
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        // Inflate child layout into the base container
        ViewGroup contentFrame = findViewById(R.id.activity_content);
        getLayoutInflater().inflate(getLayoutResourceId(), contentFrame, true);

        // Initialize top bar and user info
        topBar = findViewById(R.id.top_bar);
        if (topBar != null) {
            topBar.setAlpha(0f);
            topBar.animate().alpha(1f).setDuration(600).start();
            imgProfile = topBar.findViewById(R.id.img_profile);
            tvGreeting = topBar.findViewById(R.id.tv_greeting);
            refreshUserSessionIfNeeded();
            updateUserInfo();
        }

        // Initialize bottom navigation and emergency button
        bottomAppBar = findViewById(R.id.bottomAppBar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fabEmergency = findViewById(R.id.fab_emergency);
        if (fabEmergency != null) {
            fabEmergency.setOnClickListener(v -> handleEmergencyFabClick());
        }

        // Listen for active event changes
        ActiveEventManager.startListening();
        activeEventListener = this::applyActiveEventState;
        ActiveEventManager.registerListener(activeEventListener);
        applyActiveEventState(ActiveEventManager.getActiveEventId());

        // Configure bottom navigation actions
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    startActivityWithAnimation(HomeActivity.class);
                } else if (itemId == R.id.nav_education) {
                    startActivityWithAnimation(EducationActivity.class);
                } else if (itemId == R.id.nav_events) {
                    startActivityWithAnimation(EventsActivity.class);
                } else if (itemId == R.id.nav_profile) {
                    startActivityWithAnimation(ProfileActivity.class);
                }

                return true;
            });

            highlightCurrentTab();
        }
    }

    // hideSystemUI - Hides status and navigation bars; no parameters, returns nothing
    private void hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Use WindowInsetsController on newer versions
            getWindow().setDecorFitsSystemWindows(false);
            getWindow().getInsetsController().hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            getWindow().getInsetsController().setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        } else {
            // Legacy flags for older versions
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );
        }
    }

    // showSystemUI - Restores status and navigation bars; no parameters, returns nothing
    protected void showSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Use WindowInsetsController on newer versions
            getWindow().setDecorFitsSystemWindows(true);
            getWindow().getInsetsController().show(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        } else {
            // Legacy approach for older versions
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    // setTransparentStatusBar - Makes the status bar transparent on Lollipop and above; no parameters, returns nothing
    private void setTransparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(android.graphics.Color.TRANSPARENT);
        }
    }

    // startActivityWithAnimation - Launches targetActivity without transitions and closes this screen; targetActivity is the destination class, returns nothing
    private void startActivityWithAnimation(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
        overridePendingTransition(0, 0); // remove default animations
        finish();
    }

    // highlightCurrentTab - Marks the active navigation item based on this activity; no parameters, returns nothing
    private void highlightCurrentTab() {
        if (bottomNavigationView != null) {
            int currentItemId = getCurrentMenuItemId();
            bottomNavigationView.getMenu().findItem(currentItemId).setChecked(true);
        }
    }

    // getCurrentMenuItemId - Returns the navigation item ID that matches the current activity; no parameters, returns menu item ID
    private int getCurrentMenuItemId() {
        if (this instanceof HomeActivity) {
            return R.id.nav_home;
        } else if (this instanceof EducationActivity) {
            return R.id.nav_education;
        } else if (this instanceof EventsActivity) {
            return R.id.nav_events;
        } else if (this instanceof ProfileActivity) {
            return R.id.nav_profile;
        }
        return R.id.nav_home;
    }

    // onResume - Reapplies fullscreen mode and refreshes user details; no parameters, returns nothing
    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
        highlightCurrentTab();
        refreshUserSessionIfNeeded();
        updateUserInfo();
    }

    // showTopBar - Toggles the top bar visibility; show indicates whether to display it, returns nothing
    protected void showTopBar(boolean show) {
        if (topBar != null) {
            topBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    // showBottomBar - Toggles bottom app bar, navigation, and emergency button; show controls visibility, returns nothing
    protected void showBottomBar(boolean show) {
        if (bottomAppBar != null) {
            bottomAppBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (fabEmergency != null) {
            fabEmergency.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    // updateUserInfo - Refreshes greeting text and profile picture from the session; no parameters, returns nothing
    protected void updateUserInfo() {
        if (imgProfile == null || tvGreeting == null) return;

        UserSession session = UserSession.getInstance();
        String first = session.getFirstName();
        String last = session.getLastName();
        if (first != null) {
            String name = first + (last != null ? " " + last : "");
            tvGreeting.setText("שלום, " + name);
        }

        String url = session.getImageURL();
        if (url != null && !url.isEmpty()) {
            Glide.with(this).load(url).placeholder(R.drawable.newuserpic).into(imgProfile);
        } else {
            imgProfile.setImageResource(R.drawable.newuserpic);
        }
    }

    // refreshUserSessionIfNeeded - Retrieves user details if session data is missing; no parameters, returns nothing
    private void refreshUserSessionIfNeeded() {
        com.google.firebase.auth.FirebaseUser user =
                com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        UserSession session = UserSession.getInstance();
        if (session.getFirstName() == null || session.getLastName() == null) {
            co.median.android.a2025_theangels_new.data.services.UserDataManager
                    .loadUserDetails(user.getUid(), s -> updateUserInfo());
        }
    }

    // handleEmergencyFabClick - Triggers feedback and opens the active event or creates a new one; no parameters, returns nothing
    private void handleEmergencyFabClick() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(fabEmergency, "scaleX", 1.2f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(fabEmergency, "scaleY", 1.2f, 1.0f);
        scaleX.setInterpolator(new OvershootInterpolator());
        scaleY.setInterpolator(new OvershootInterpolator());
        scaleX.setDuration(150);
        scaleY.setDuration(150);
        scaleX.start();
        scaleY.start();

        // Provide haptic feedback on supported devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        }

        // Open active event if exists, otherwise start a new one
        String activeId = ActiveEventManager.getActiveEventId();
        if (activeId != null) {
            Intent intent = new Intent(this, EventUserActivity.class);
            intent.putExtra("eventId", activeId);
            startActivity(intent);
            overridePendingTransition(0, 0);
        } else {
            startActivityWithAnimation(NewEventActivity.class);
        }
    }

    // applyActiveEventState - Updates emergency button styling based on active event; eventId is the current event identifier, returns nothing
    private void applyActiveEventState(String eventId) {
        if (fabEmergency == null) return;
        if (eventId != null) {
            fabEmergency.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.red_alert)));
            fabEmergency.setImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE));
            Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
            fabEmergency.startAnimation(pulse);
        } else {
            fabEmergency.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.blue_gradient_start)));
            fabEmergency.setImageTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.gray_dark)));
            fabEmergency.clearAnimation();
        }
    }

    // onDestroy - Unregisters the active event listener; no parameters, returns nothing
    @Override
    protected void onDestroy() {
        if (activeEventListener != null) {
            ActiveEventManager.unregisterListener(activeEventListener);
        }
        super.onDestroy();
    }

    // getLayoutResourceId - Supplies the layout resource ID for subclasses to inflate; no parameters, returns layout resource ID
    protected abstract int getLayoutResourceId();
}