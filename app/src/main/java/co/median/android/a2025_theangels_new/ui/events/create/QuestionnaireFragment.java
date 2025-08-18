// =======================================
// IMPORTS
// =======================================
package co.median.android.a2025_theangels_new.ui.events.create;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import co.median.android.a2025_theangels_new.R;

// =======================================
// QuestionnaireFragment - Handles questionnaire logic for incident state
// =======================================
public class QuestionnaireFragment extends Fragment {

    private NewEventViewModel viewModel;

    // =======================================
    // onCreateView - Inflates the questionnaire layout
    // =======================================
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_questionnaire, container, false);
    }

    // =======================================
    // onViewCreated - Initializes radio groups and listeners
    // =======================================
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(NewEventViewModel.class);

        setupButtonGroup(view, R.id.btnSafetyYes, R.id.btnSafetyNo, getString(R.string.q_safety), true);
        setupButtonGroup(view, R.id.btnPulseYes, R.id.btnPulseNo, getString(R.string.q_pulse), false);
        setupButtonGroup(view, R.id.btnBreathingYes, R.id.btnBreathingNo, getString(R.string.q_breathing), false);
        setupButtonGroup(view, R.id.btnBleedingYes, R.id.btnBleedingNo, getString(R.string.q_bleeding), false);
    }

    // =======================================
    // setupButtonGroup - Configures Yes/No buttons with color feedback
    // =======================================
    private void setupButtonGroup(View view, int yesId, int noId, String question, boolean isSafetyQuestion) {
        Button btnYes = view.findViewById(yesId);
        Button btnNo = view.findViewById(noId);

        btnYes.setOnClickListener(v -> {
            highlightYes(btnYes, btnNo);
            viewModel.setFormAnswer(question, true);
        });

        btnNo.setOnClickListener(v -> {
            highlightNo(btnYes, btnNo);
            if (isSafetyQuestion) {
                new AlertDialog.Builder(requireContext())
                        .setTitle(R.string.safety_popup_title)
                        .setMessage(R.string.safety_warning)
                        .setPositiveButton(R.string.continue_button, (d, w) -> d.dismiss())
                        .show();
            }
            viewModel.setFormAnswer(question, false);
        });

        Boolean saved = viewModel.getEventForm().get(question);
        if (saved != null) {
            if (saved) {
                highlightYes(btnYes, btnNo);
            } else {
                highlightNo(btnYes, btnNo);
            }
        }
    }

    // =======================================
    // highlightYes/highlightNo - Visual indication for selection
    // =======================================
    private void highlightYes(Button yes, Button no) {
        yes.setBackgroundResource(R.drawable.rounded_button_green);
        yes.setBackgroundTintList(null);
        yes.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
        no.setBackgroundResource(R.drawable.rounded_button_gray);
        no.setBackgroundTintList(null);
        no.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black));
    }

    private void highlightNo(Button yes, Button no) {
        yes.setBackgroundResource(R.drawable.rounded_button_gray);
        yes.setBackgroundTintList(null);
        yes.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black));
        no.setBackgroundResource(R.drawable.rounded_button_red);
        no.setBackgroundTintList(null);
        no.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
    }
}
