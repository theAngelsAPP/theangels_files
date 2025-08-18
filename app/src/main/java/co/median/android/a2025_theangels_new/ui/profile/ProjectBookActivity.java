// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.ui.profile;

// IMPORTS
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import co.median.android.a2025_theangels_new.ui.main.ImmersiveActivity;
import co.median.android.a2025_theangels_new.R;

// ProjectBookActivity - Displays the project's book inside a WebView
public class ProjectBookActivity extends ImmersiveActivity {

    // onCreate - Loads the project book page and handles back navigation
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_book);

        // Force RTL layout
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        WebView webView = findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(getString(R.string.project_book_url));
    }
}
