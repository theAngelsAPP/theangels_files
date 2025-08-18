// =======================================
// IMPORTS
// =======================================
package co.median.android.a2025_theangels_new.ui.events.create;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import co.median.android.a2025_theangels_new.R;
import co.median.android.a2025_theangels_new.data.map.AddressHelper;
import co.median.android.a2025_theangels_new.data.map.StaticMapFragment;
import co.median.android.a2025_theangels_new.data.models.EventType;
import co.median.android.a2025_theangels_new.data.services.EventTypeDataManager;

// =======================================
// SummaryFragment - Final summary step in event creation
// =======================================
public class SummaryFragment extends Fragment {

    // =======================================
    // onCreateView - Inflates the summary fragment layout
    // =======================================
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_summary, container, false);
    }

    // =======================================
    // onViewCreated - Called after the view is created
    // Use this to populate the summary from ViewModel or arguments if needed
    // =======================================
    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NewEventViewModel viewModel = new ViewModelProvider(requireActivity()).get(NewEventViewModel.class);

        android.widget.ImageView ivType = view.findViewById(R.id.ivSummaryTypeImage);
        android.widget.TextView tvType = view.findViewById(R.id.tvSummaryType);
        android.widget.TextView tvWhat = view.findViewById(R.id.tvSummaryWhat);
        android.widget.TextView tvAddress = view.findViewById(R.id.tvSummaryAddress);
        androidx.fragment.app.FragmentContainerView mapContainer = view.findViewById(R.id.summary_map);
        android.widget.LinearLayout llForm = view.findViewById(R.id.llSummaryForm);

        if (tvType != null && viewModel.getEventType() != null) {
            tvType.setText(viewModel.getEventType());
            EventTypeDataManager.getEventTypeByName(viewModel.getEventType(), new EventTypeDataManager.SingleEventTypeCallback() {
                @Override
                public void onEventTypeLoaded(EventType type) {
                    if (type != null && type.getTypeImageURL() != null && ivType != null) {
                        Glide.with(requireContext()).load(type.getTypeImageURL())
                                .placeholder(R.drawable.ic_event)
                                .into(ivType);
                    }
                }

                @Override
                public void onError(Exception e) { }
            });
        }

        if (tvWhat != null && viewModel.getEventQuestionChoice() != null) {
            tvWhat.setText(viewModel.getEventQuestionChoice());
        }

        if (viewModel.getEventLocation() != null) {
            double lat = viewModel.getEventLocation().getLatitude();
            double lng = viewModel.getEventLocation().getLongitude();
            String address = AddressHelper.getAddressFromLatLng(requireContext(), lat, lng);
            if (tvAddress != null) {
                tvAddress.setText(address != null ? address : getString(R.string.address_not_found));
            }
            if (mapContainer != null) {
                StaticMapFragment mapFragment = StaticMapFragment.newInstance(lat, lng);
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.summary_map, mapFragment).commit();
            }
        }

        if (llForm != null) {
            llForm.removeAllViews();
            for (java.util.Map.Entry<String, Boolean> entry : viewModel.getEventForm().entrySet()) {
                android.widget.LinearLayout row = new android.widget.LinearLayout(requireContext());
                row.setOrientation(android.widget.LinearLayout.HORIZONTAL);
                row.setPadding(0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()), 0,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));

                android.widget.TextView q = new android.widget.TextView(requireContext());
                q.setLayoutParams(new android.widget.LinearLayout.LayoutParams(0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                q.setText(entry.getKey());
                q.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                q.setTypeface(q.getTypeface(), android.graphics.Typeface.BOLD);

                android.widget.TextView a = new android.widget.TextView(requireContext());
                a.setText(entry.getValue() ? getString(R.string.yes) : getString(R.string.no));
                a.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                a.setTypeface(a.getTypeface(), android.graphics.Typeface.BOLD);
                int color = entry.getValue() ? ContextCompat.getColor(requireContext(), R.color.questionnaire_green)
                        : ContextCompat.getColor(requireContext(), R.color.questionnaire_red);
                a.setTextColor(color);

                row.addView(q);
                row.addView(a);
                llForm.addView(row);
            }
        }
    }
}
