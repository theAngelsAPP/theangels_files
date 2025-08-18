// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.ui.profile.support;

// IMPORTS
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import co.median.android.a2025_theangels_new.R;
import co.median.android.a2025_theangels_new.ui.main.ImmersiveActivity;

// SupportActivity - Hosts FAQ and contact tabs for user assistance
public class SupportActivity extends ImmersiveActivity {

    // VARIABLES
    private Button btnFAQ, btnContact;

    // onCreate - Sets up tab buttons and default fragment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        // Force RTL layout
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        btnFAQ = findViewById(R.id.btn_faq);
        btnContact = findViewById(R.id.btn_contact);

        // Load FAQ by default
        loadFragment(new FAQFragment());

        // Switch fragments when tabs are pressed
        btnFAQ.setOnClickListener(v -> loadFragment(new FAQFragment()));
        btnContact.setOnClickListener(v -> loadFragment(new ContactFragment()));
    }

    // loadFragment - Replaces content container with given fragment
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_container, fragment);
        transaction.commit();
    }

    // sendEmail - Shortcut for email contact (used in XML)
    public void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + getString(R.string.support_email)));
        startActivity(intent);
    }

    // openWebsite - Opens project website
    public void openWebsite() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.support_website_url)));
        startActivity(intent);
    }

    // sendWhatsApp - Opens WhatsApp conversation with support
    public void sendWhatsApp() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.support_whatsapp_url)));
        startActivity(intent);
    }
}
