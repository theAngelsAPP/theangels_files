// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.ui.educations;

// IMPORTS
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import co.median.android.a2025_theangels_new.R;
import co.median.android.a2025_theangels_new.data.models.Education;
import co.median.android.a2025_theangels_new.data.models.EventType;
import co.median.android.a2025_theangels_new.data.services.EducationDataManager;
import co.median.android.a2025_theangels_new.data.services.EventTypeDataManager;
import co.median.android.a2025_theangels_new.ui.main.BaseActivity;

// EducationActivity - Manages the education list with filtering and pagination
public class EducationActivity extends BaseActivity {

    // VARIABLES
    private static final String TAG = "EducationActivity";

    private ListView educationsListView;
    private ArrayList<Education> educations;
    private ArrayList<Education> allEducations = new ArrayList<>();
    private ArrayList<Education> filteredEducations = new ArrayList<>();
    private EducationAdapter adapter;
    private Map<String, String> typeImages = new HashMap<>();

    private static final int ITEMS_PER_PAGE = 5;
    private int currentPage = 0;
    private LinearLayout paginationLayout;

    private Chip chipMedical, chipSecurity, chipCar, chipAnimals;
    private Chip currentFilterChip;

    // onCreate - sets up UI elements and loads data. Receives saved state and returns nothing
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showTopBar(true);
        showBottomBar(true);

        // Prepare list and adapter
        educationsListView = findViewById(R.id.educations_list_view);
        educations = new ArrayList<>();
        adapter = new EducationAdapter(this, R.layout.education_item, educations);
        educationsListView.setAdapter(adapter);
        paginationLayout = findViewById(R.id.pagination_layout);

        // Configure filter chips
        chipMedical = findViewById(R.id.chip_medical);
        chipSecurity = findViewById(R.id.chip_security);
        chipCar = findViewById(R.id.chip_car);
        chipAnimals = findViewById(R.id.chip_animals);

        setupChip(chipMedical, "רפואי", R.color.medical_event_color);
        setupChip(chipSecurity, "ביטחוני", R.color.education_security_color);
        setupChip(chipCar, "רכב", R.color.car_event_color);
        setupChip(chipAnimals, "בעלי חיים", R.color.animal_event_color);

        loadEventTypes();
    }

    // loadEventTypes - fetches event types and their images; takes no parameters and returns void
    private void loadEventTypes() {
        EventTypeDataManager.getAllEventTypes(new EventTypeDataManager.EventTypeCallback() {
            @Override
            public void onEventTypesLoaded(ArrayList<EventType> types) {
                for (EventType type : types) {
                    typeImages.put(type.getTypeName(), type.getTypeImageURL());
                }
                adapter.setTypeImages(typeImages);
                loadEducationsFromFirestore();
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error loading event types", e);
                loadEducationsFromFirestore();
            }
        });
    }

    // loadEducationsFromFirestore - pulls all educations and refreshes the list; no params, returns void
    private void loadEducationsFromFirestore() {
        EducationDataManager.getAllEducations(new EducationDataManager.EducationCallback() {
            @Override
            public void onEducationsLoaded(ArrayList<Education> loadedEducations) {
                allEducations.clear();
                allEducations.addAll(loadedEducations);
                filteredEducations.clear();
                filteredEducations.addAll(allEducations);
                currentPage = 0;
                updatePagination();
                showCurrentPage();
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error loading educations from Firestore", e);
                Toast.makeText(EducationActivity.this, R.string.error_loading_educations, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // getLayoutResourceId - provides layout for this activity. No params, returns layout id
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_education;
    }

    // setupChip - prepares a chip for filtering; accepts chip view, type name and color resource. Returns void
    private void setupChip(Chip chip, String type, int colorRes) {
        if (chip == null) {
            return;
        }
        chip.setCheckable(false);
        chip.setTextSize(14f);
        chip.setOnClickListener(v -> {
            if (currentFilterChip == chip) {
                chip.setChipBackgroundColorResource(R.color.light_gray);
                currentFilterChip = null;
                clearFilter();
            } else {
                if (currentFilterChip != null) {
                    currentFilterChip.setChipBackgroundColorResource(R.color.light_gray);
                }
                chip.setChipBackgroundColorResource(colorRes);
                currentFilterChip = chip;
                applyFilter(type);
            }
        });
        chip.setChipBackgroundColorResource(R.color.light_gray);
    }

    // applyFilter - filters the list by type. Takes a type string and returns void
    private void applyFilter(String type) {
        filteredEducations.clear();
        for (Education e : allEducations) {
            if (e.getEduType() != null && e.getEduType().equalsIgnoreCase(type)) {
                filteredEducations.add(e);
            }
        }
        currentPage = 0;
        updatePagination();
        showCurrentPage();
    }

    // clearFilter - restores full list with no filter. No params, returns void
    private void clearFilter() {
        filteredEducations.clear();
        filteredEducations.addAll(allEducations);
        currentPage = 0;
        updatePagination();
        showCurrentPage();
    }

    // showCurrentPage - displays the current page of items; no params, returns void
    private void showCurrentPage() {
        educations.clear();
        int start = currentPage * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, filteredEducations.size());
        for (int i = start; i < end; i++) {
            educations.add(filteredEducations.get(i));
        }
        adapter.notifyDataSetChanged();
    }

    // updatePagination - rebuilds page buttons based on current filter. No params, returns void
    private void updatePagination() {
        if (paginationLayout == null) {
            return;
        }
        paginationLayout.removeAllViews();
        int totalPages = (int) Math.ceil((double) filteredEducations.size() / ITEMS_PER_PAGE);
        for (int i = 0; i < totalPages; i++) {
            Button btn = new Button(this);
            btn.setText(String.valueOf(i + 1));
            btn.setTextSize(14f);

            // Make the button square with margins
            int size = (int) getResources().getDimension(R.dimen.pagination_button_size);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            int margin = (int) getResources().getDimension(R.dimen.pagination_button_margin);
            params.setMargins(margin, margin, margin, margin);
            btn.setLayoutParams(params);

            // Highlight the selected page
            btn.setBackgroundResource(R.drawable.pagination_button);
            btn.setSelected(i == currentPage);

            final int pageIndex = i;
            btn.setOnClickListener(v -> {
                currentPage = pageIndex;
                showCurrentPage();
                updatePagination();
            });

            paginationLayout.addView(btn);
        }
    }
}
