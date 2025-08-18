// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.ui.profile;

// IMPORTS
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import co.median.android.a2025_theangels_new.R;
import co.median.android.a2025_theangels_new.ui.main.ImmersiveActivity;

// CreditsActivity - Shows application credits and a back button
public class CreditsActivity extends ImmersiveActivity {

    // onCreate - Loads credits layout and handles back navigation
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        // Always render in RTL
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());
    }
}
