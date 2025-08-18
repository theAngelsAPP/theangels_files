// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)

package co.median.android.a2025_theangels_new.ui.onboarding;

// IMPORTS
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

// OnboardingAdapter - Supplies onboarding pages to the ViewPager
public class OnboardingAdapter extends FragmentStateAdapter {

    // VARIABLES
    private final List<Integer> images;

    // OnboardingAdapter - Initializes adapter with host activity and image resources
    public OnboardingAdapter(@NonNull FragmentActivity fragmentActivity, List<Integer> images) {
        super(fragmentActivity);
        this.images = images;
    }

    // createFragment - Generates the fragment for the provided page index
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return OnboardingFragment.newInstance(images.get(position));
    }

    // getItemCount - Provides total number of onboarding pages
    @Override
    public int getItemCount() {
        return images.size();
    }
}

