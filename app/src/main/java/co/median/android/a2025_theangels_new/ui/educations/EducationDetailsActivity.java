// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.ui.educations;

// IMPORTS
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import co.median.android.a2025_theangels_new.R;
import co.median.android.a2025_theangels_new.ui.main.BaseActivity;

// EducationDetailsActivity - Presents full details of a selected education item
public class EducationDetailsActivity extends BaseActivity {

    // onCreate - populates the screen with provided data and handles back navigation. Receives saved state, returns nothing
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showTopBar(false);
        showBottomBar(false);

        // Extract data from the incoming intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("eduTitle");
        String data = intent.getStringExtra("eduData");
        String imageUrl = intent.getStringExtra("eduImageURL");

        // Bind views
        TextView tvTitle = findViewById(R.id.title);
        TextView tvContent = findViewById(R.id.education_content);
        ImageView ivHeader = findViewById(R.id.header_image);

        if (tvTitle != null) {
            tvTitle.setText(title);
        }
        if (tvContent != null) {
            tvContent.setText(data);
        }
        if (ivHeader != null) {
            Glide.with(this).load(imageUrl).placeholder(R.drawable.training1).into(ivHeader);
        }

        // Close the screen when back button is pressed
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    // getLayoutResourceId - returns layout resource for this activity
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_education_details;
    }

    // onResume - keeps lifecycle hooks and relies on BaseActivity for system UI handling
    @Override
    protected void onResume() {
        super.onResume();
    }
}
