// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)

package co.median.android.a2025_theangels_new.ui.onboarding;

// IMPORTS
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.util.Arrays;
import java.util.List;

import co.median.android.a2025_theangels_new.R;
import co.median.android.a2025_theangels_new.ui.main.MainActivity;

// OnboardingActivity - Guides users through introductory slides and launches the app
public class OnboardingActivity extends AppCompatActivity {

    // VARIABLES
    private ViewPager2 viewPager;
    private Button startButton;
    private List<Integer> images;

    // onCreate - Sets up the onboarding flow and handles completion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        // Use right-to-left layout on every device
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        // Enable immersive full screen
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        // Initialize views
        viewPager = findViewById(R.id.viewPager);
        viewPager.setLayoutDirection(View.LAYOUT_DIRECTION_LTR); // Force left-to-right pager
        startButton = findViewById(R.id.startButton);

        // Prepare onboarding images
        images = Arrays.asList(
                R.drawable.onboarding_1,
                R.drawable.onboarding_2,
                R.drawable.onboarding_3
        );

        // Attach adapter
        OnboardingAdapter adapter = new OnboardingAdapter(this, images);
        viewPager.setAdapter(adapter);

        // Fade and slide between pages
        viewPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float alpha = 0f;
                if (position >= -1 && position <= 1) {
                    alpha = 1 - Math.abs(position);
                }
                page.setAlpha(alpha);
                page.setTranslationX(-position * page.getWidth());
                page.setTranslationZ(-Math.abs(position));
            }
        });

        // Toggle start button and confetti when pages change
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                boolean shouldShowConfetti = (position == 0);

                Fragment fragment = getSupportFragmentManager().getFragments().get(position);
                if (fragment instanceof OnboardingFragment) {
                    ((OnboardingFragment) fragment).setShowConfetti(shouldShowConfetti);
                }

                if (position == images.size() - 1) {
                    startButton.setVisibility(View.VISIBLE);
                } else {
                    startButton.setVisibility(View.GONE);
                }
            }
        });

        // Finish onboarding when user taps the button
        startButton.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
            prefs.edit().putBoolean("onboarding_complete", true).apply();

            startActivity(new Intent(OnboardingActivity.this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        // Hide button until final page
        startButton.setVisibility(View.GONE);
    }
}

