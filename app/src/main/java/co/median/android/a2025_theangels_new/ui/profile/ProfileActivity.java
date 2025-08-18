// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.ui.profile;

// IMPORTS
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.onesignal.OneSignal;

import co.median.android.a2025_theangels_new.R;
import co.median.android.a2025_theangels_new.data.models.UserSession;
import co.median.android.a2025_theangels_new.ui.main.BaseActivity;
import co.median.android.a2025_theangels_new.ui.main.MainActivity;
import co.median.android.a2025_theangels_new.ui.profile.settings.EmergencyContactActivity;
import co.median.android.a2025_theangels_new.ui.profile.settings.JoinVolSettingsActivity;
import co.median.android.a2025_theangels_new.ui.profile.settings.MedicalHistoryActivity;
import co.median.android.a2025_theangels_new.ui.profile.settings.PrivacySettingsActivity;
import co.median.android.a2025_theangels_new.ui.profile.support.SupportActivity;

// ProfileActivity - Displays user profile and provides links to settings and resources
public class ProfileActivity extends BaseActivity {

    // VARIABLES
    private ImageView imgProfile;
    private TextView tvUsername;
    private TextView tvUserRole;
    private View btnJoinVolunteers;

    // onCreate - Sets up UI components for the profile screen
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showTopBar(false);
        showBottomBar(true);
        imgProfile = findViewById(R.id.img_profile_large);
        tvUsername = findViewById(R.id.tv_username);
        tvUserRole = findViewById(R.id.tv_user_role);
        btnJoinVolunteers = findViewById(R.id.btn_join_volunteers);

        populateUserDetails();
    }

    // getLayoutResourceId - Provides layout for BaseActivity
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_profile;
    }

    // onResume - Refreshes user details when returning to this screen
    @Override
    protected void onResume() {
        super.onResume();
        populateUserDetails();
    }

    // onMyDetailsClicked - Opens the personal details editor
    public void onMyDetailsClicked(View view) {
        startActivity(new Intent(this, MyDetailsActivity.class));
    }

    // onPrivacySettingsClicked - Opens privacy permissions settings
    public void onPrivacySettingsClicked(View view) {
        startActivity(new Intent(this, PrivacySettingsActivity.class));
    }

    // onEmergencyContactClicked - Opens emergency contact editor
    public void onEmergencyContactClicked(View view) {
        startActivity(new Intent(this, EmergencyContactActivity.class));
    }

    // onMedicalHistoryClicked - Opens medical history form
    public void onMedicalHistoryClicked(View view) {
        startActivity(new Intent(this, MedicalHistoryActivity.class));
    }

    // onSupportSettingsClicked - Opens support page with FAQ and contact options
    public void onSupportSettingsClicked(View view) {
        startActivity(new Intent(this, SupportActivity.class));
    }

    // onJoinVolSettingsClicked - Opens registration screen for new volunteers
    public void onJoinVolSettingsClicked(View view) {
        startActivity(new Intent(this, JoinVolSettingsActivity.class));
    }

    // onProjectBookClicked - Opens the project book webpage
    public void onProjectBookClicked(View view) {
        startActivity(new Intent(this, ProjectBookActivity.class));
    }

    // onPodcastClicked - Opens the podcast webpage
    public void onPodcastClicked(View view) {
        startActivity(new Intent(this, PodcastActivity.class));
    }

    // onCreditsClicked - Shows credits screen
    public void onCreditsClicked(View view) {
        startActivity(new Intent(this, CreditsActivity.class));
    }

    // onShareAppClicked - Shares the application link using a chooser
    public void onShareAppClicked(View view) {
        String shareText = getString(R.string.share_app_text);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)));
    }

    // populateUserDetails - Fills profile view with session information
    private void populateUserDetails() {
        UserSession session = UserSession.getInstance();

        // Full name
        String fullName = session.getFirstName() + " " + session.getLastName();
        tvUsername.setText(fullName);

        // Role display
        String role = session.getRole();
        if (role != null && !role.isEmpty()) {
            tvUserRole.setText(role);
        }

        // Volunteer button visibility
        if (role != null && role.contains("מתנדב")) {
            btnJoinVolunteers.setVisibility(View.GONE);
        } else {
            btnJoinVolunteers.setVisibility(View.VISIBLE);
        }

        // Load profile picture
        String url = session.getImageURL();
        if (url != null && !url.isEmpty()) {
            Glide.with(this).load(url).placeholder(R.drawable.newuserpic).into(imgProfile);
        }
    }

    // onLogoutClicked - Shows confirmation dialog and performs logout
    public void onLogoutClicked(View view) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.logout_title)
                .setMessage(R.string.logout_message)
                .setPositiveButton(R.string.logout_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();

                        // Disconnect from OneSignal notifications
                        OneSignal.logout();

                        // Clear local session
                        UserSession.getInstance().clear();

                        // Return to main screen
                        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.logout_no, null)
                .show();
    }
}
