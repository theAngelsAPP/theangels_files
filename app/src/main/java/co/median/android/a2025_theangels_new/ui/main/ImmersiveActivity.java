// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)

package co.median.android.a2025_theangels_new.ui.main;

// IMPORTS
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

// ImmersiveActivity - Base activity that provides a fullscreen experience without navigation bars

public abstract class ImmersiveActivity extends AppCompatActivity {

    // onCreate - Prepares the window for immersive mode; savedInstanceState holds previous state, returns nothing
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransparentStatusBar();
    }

    // onResume - Reapplies fullscreen flags after a pause; no parameters, returns nothing
    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }

    // hideSystemUI - Hides status and navigation bars; no parameters, returns nothing
    private void hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            View decorView = getWindow().getDecorView();
            if (decorView != null) {
                getWindow().setDecorFitsSystemWindows(false);
                WindowInsetsController controller = decorView.getWindowInsetsController();
                if (controller != null) {
                    controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                    controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                }
            }
        } else {
            // Legacy flags for older Android versions
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );
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
}
