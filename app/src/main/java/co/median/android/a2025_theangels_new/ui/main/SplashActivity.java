// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)

package co.median.android.a2025_theangels_new.ui.main;

// IMPORTS
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;

import androidx.annotation.Nullable;

import co.median.android.a2025_theangels_new.R;

// SplashActivity - Shows the wings animation once before opening the main screen
public class SplashActivity extends ImmersiveActivity {

    // onCreate - Plays the splash animation and navigates to MainActivity; savedInstanceState holds previous state, returns nothing
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Force RTL layout for the splash
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        LottieAnimationView animationView = findViewById(R.id.splashAnimation);
        animationView.setRepeatCount(0); // play animation once
        animationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}
