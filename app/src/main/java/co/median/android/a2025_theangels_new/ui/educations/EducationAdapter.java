// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.ui.educations;

// IMPORTS
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import co.median.android.a2025_theangels_new.R;
import co.median.android.a2025_theangels_new.data.models.Education;

// EducationAdapter - Binds education data to list items and opens details on click
public class EducationAdapter extends ArrayAdapter<Education> {

    // VARIABLES
    private Context context;
    private ArrayList<Education> educationsList;
    private java.util.Map<String, String> typeImages;

    // getColorForType - returns color resource for a type, handling English and Hebrew names
    private int getColorForType(String type) {
        if (type == null) {
            return R.color.event_default_color;
        }
        String normalized = type.toLowerCase();
        switch (normalized) {
            case "medical":
            case "\u05e8\u05e4\u05d5\u05d0\u05d9":
                return R.color.medical_event_color;
            case "car":
            case "vehicle":
            case "\u05e8\u05db\u05d1":
                return R.color.car_event_color;
            case "animals":
            case "animal":
            case "\u05d1\u05e2\u05dc\u05d9 \u05d7\u05d9\u05d9\u05dd":
                return R.color.animal_event_color;
            case "security":
            case "\u05d1\u05d9\u05d8\u05d7\u05d5\u05e0\u05d9":
                return R.color.education_security_color;
            default:
                return R.color.event_default_color;
        }
    }

    // EducationAdapter - constructs adapter with context and data list
    public EducationAdapter(Context context, int resource, ArrayList<Education> educationsList) {
        super(context, resource, educationsList);
        this.context = context;
        this.educationsList = educationsList;
    }

    // setTypeImages - provides mapping from type name to image url; returns void
    public void setTypeImages(java.util.Map<String, String> typeImages) {
        this.typeImages = typeImages;
    }

    // getCount - returns number of items in the list
    @Override
    public int getCount() {
        return educationsList.size();
    }

    // getItem - returns the item at the requested position
    @Nullable
    @Override
    public Education getItem(int position) {
        return educationsList.get(position);
    }

    // getView - inflates and binds a single education row; receives position, recycled view, and parent; returns the populated view
    @Override
    public View getView(int position, View rootView, ViewGroup parent) {
        if (rootView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            rootView = inflater.inflate(R.layout.education_item, parent, false);
        }

        Education education = getItem(position);

        TextView title = rootView.findViewById(R.id.training_title);
        TextView typeLabel = rootView.findViewById(R.id.training_type_label);
        ImageView picture = rootView.findViewById(R.id.training_picture);

        if (education != null) {
            // Populate text fields
            title.setText(education.getEduTitle());
            typeLabel.setText(education.getEduType());

            int colorRes = getColorForType(education.getEduType());
            int color = ContextCompat.getColor(context, colorRes);

            // Tint the background of the type label
            android.graphics.drawable.Drawable labelBg = ContextCompat.getDrawable(context, R.drawable.event_label_bg);
            if (labelBg != null) {
                labelBg = DrawableCompat.wrap(labelBg.mutate());
                DrawableCompat.setTint(labelBg, color);
                typeLabel.setBackground(labelBg);
            }

            // Load the remote image with a placeholder
            Glide.with(context)
                    .load(education.getEduImageURL())
                    .placeholder(R.drawable.training1)
                    .into(picture);

            // Launch details screen when the row is tapped
            rootView.setOnClickListener(v -> {
                Intent intent = new Intent(context, EducationDetailsActivity.class);
                intent.putExtra("eduTitle", education.getEduTitle());
                intent.putExtra("eduData", education.getEduData());
                intent.putExtra("eduImageURL", education.getEduImageURL());
                context.startActivity(intent);
            });
        }

        return rootView;
    }
}
