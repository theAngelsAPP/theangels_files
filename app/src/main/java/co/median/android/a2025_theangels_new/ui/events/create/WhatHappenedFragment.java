// =======================================
// IMPORTS
// =======================================
package co.median.android.a2025_theangels_new.ui.events.create;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;

import co.median.android.a2025_theangels_new.R;
import co.median.android.a2025_theangels_new.data.models.EventType;
import co.median.android.a2025_theangels_new.data.services.EventTypeDataManager;

// =======================================
// WhatHappenedFragment - Handles incident type selection with optional free text
// =======================================
public class WhatHappenedFragment extends Fragment {

    // =======================================
    // VARIABLES
    // =======================================
    private MaterialButton selectedButton = null;
    private NewEventViewModel viewModel;

    // =======================================
    // onCreateView - Inflates layout for the fragment
    // =======================================
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_what_happened, container, false);
    }

    // =======================================
    // onViewCreated - Sets up buttons and free text visibility logic
    // =======================================
    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(NewEventViewModel.class);

        MaterialButton btnUnconscious = view.findViewById(R.id.btnUnconscious);
        MaterialButton btnAllergy = view.findViewById(R.id.btnAllergy);
        MaterialButton btnChoking = view.findViewById(R.id.btnChoking);
        MaterialButton btnOther = view.findViewById(R.id.btnOther);
        EditText etFreeText = view.findViewById(R.id.etFreeText);

        // Default: hide free text field
        etFreeText.setVisibility(View.GONE);

        // Handle button selection
        View.OnClickListener clickListener = v -> {
            MaterialButton clickedButton = (MaterialButton) v;

            // Reset previously selected button
            if (selectedButton != null) {
                selectedButton.setBackgroundColor(
                        ContextCompat.getColor(requireContext(), R.color.incident_option_default)
                );
            }

            // Highlight the newly selected button
            clickedButton.setBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.incident_option_selected)
            );
            selectedButton = clickedButton;

            // Show free text input only when "Other" is selected
            if (clickedButton.getId() == R.id.btnOther) {
                etFreeText.setVisibility(View.VISIBLE);
                viewModel.setEventQuestionChoice(null);
            } else {
                etFreeText.setVisibility(View.GONE);
                viewModel.setEventQuestionChoice(clickedButton.getText().toString());
            }
        };

        btnUnconscious.setOnClickListener(clickListener);
        btnAllergy.setOnClickListener(clickListener);
        btnChoking.setOnClickListener(clickListener);
        btnOther.setOnClickListener(clickListener);

        // Load questions dynamically based on selected event type
        String type = viewModel.getEventType();
        if (type != null) {
            EventTypeDataManager.getEventTypeByName(type, new EventTypeDataManager.SingleEventTypeCallback() {
                @Override
                public void onEventTypeLoaded(EventType eventType) {
                    if (eventType != null && eventType.getQuestions() != null && eventType.getQuestions().size() >= 3) {
                        btnUnconscious.setText(eventType.getQuestions().get(0));
                        btnAllergy.setText(eventType.getQuestions().get(1));
                        btnChoking.setText(eventType.getQuestions().get(2));
                    }
                    applySavedChoice(btnUnconscious, btnAllergy, btnChoking, btnOther, etFreeText);
                }

                @Override
                public void onError(Exception e) {
                    // ignore
                }
            });
        }

        applySavedChoice(btnUnconscious, btnAllergy, btnChoking, btnOther, etFreeText);

        etFreeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // no-op
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Save any text the user enters. Validation is done when advancing to the next step.
                viewModel.setEventQuestionChoice(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // no-op
            }
        });
    }

    private void applySavedChoice(MaterialButton btnUnconscious,
                                  MaterialButton btnAllergy,
                                  MaterialButton btnChoking,
                                  MaterialButton btnOther,
                                  EditText etFreeText) {
        String saved = viewModel.getEventQuestionChoice();
        if (saved == null) return;

        MaterialButton[] buttons = new MaterialButton[]{btnUnconscious, btnAllergy, btnChoking};
        for (MaterialButton b : buttons) {
            if (saved.equals(b.getText().toString())) {
                b.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.incident_option_selected));
                selectedButton = b;
                etFreeText.setVisibility(View.GONE);
                return;
            }
        }

        btnOther.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.incident_option_selected));
        selectedButton = btnOther;
        etFreeText.setVisibility(View.VISIBLE);
        etFreeText.setText(saved);
    }
}
