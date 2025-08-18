// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.ui.home;

// IMPORTS
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Map;

import co.median.android.a2025_theangels_new.R;
import co.median.android.a2025_theangels_new.data.models.Message;
import co.median.android.a2025_theangels_new.data.models.MessageType;

// MessagesAdapter - Binds messages to list items and manages click actions
public class MessagesAdapter extends ArrayAdapter<Message> {

    // Listener for taps on a message
    public interface OnMessageClickListener {
        void onMessageClicked(Message message);
    }

    // VARIABLES
    private final Context context;
    private final ArrayList<Message> messages;
    private final int resource;
    private Map<String, MessageType> typeMap;
    private OnMessageClickListener clickListener;

    // Creates the adapter with context, layout resource, and message list.
    // context - host activity; resource - layout ID; messages - data set
    public MessagesAdapter(@NonNull Context context, int resource, ArrayList<Message> messages) {
        super(context, resource, messages);
        this.context = context;
        this.resource = resource;
        this.messages = messages;
    }

    // Sets the map of message types for styling.
    // typeMap - mapping between type IDs and their details; returns nothing
    public void setTypeMap(Map<String, MessageType> typeMap) {
        this.typeMap = typeMap;
    }

    // Registers a callback for item clicks.
    // listener - click handler; returns nothing
    public void setOnMessageClickListener(OnMessageClickListener listener) {
        this.clickListener = listener;
    }

    // Returns the number of messages in the adapter.
    // No params; returns size of list
    @Override
    public int getCount() {
        return messages.size();
    }

    // Retrieves a message at a given position.
    // position - index of item; returns the message or null
    @Nullable
    @Override
    public Message getItem(int position) {
        return messages.get(position);
    }

    // Inflates and populates a message row.
    // position - list index; convertView - recycled view; parent - parent view group
    // Returns the prepared row view
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        Message msg = getItem(position);
        ImageView icon = convertView.findViewById(R.id.message_icon);
        LinearLayout container = convertView.findViewById(R.id.message_container);
        TextView title = convertView.findViewById(R.id.message_title);
        TextView content = convertView.findViewById(R.id.message_content);

        container.setBackgroundResource(R.drawable.message_bg_unified);
        title.setTextColor(context.getResources().getColor(android.R.color.black));
        content.setTextColor(context.getResources().getColor(android.R.color.black));

        if (msg != null) {
            MessageType type = typeMap != null ? typeMap.get(msg.getMessageType()) : null;
            if (type != null) {
                try {
                    int color = Color.parseColor(type.getColor());
                    title.setTextColor(color);
                } catch (Exception ignored) {
                    // Ignore invalid color strings
                }

                // Load type icon
                Glide.with(context.getApplicationContext())
                        .load(type.getIconURL())
                        .placeholder(R.drawable.messagebox_icon)
                        .into(icon);
            } else {
                icon.setImageResource(R.drawable.messagebox_icon);
            }

            title.setText(msg.getMessageTitle());
            content.setText(msg.getMessageData());
        }

        // Enable click only when a reference exists
        boolean hasRef = msg != null && msg.getMessageRef() != null && !msg.getMessageRef().isEmpty();
        convertView.setClickable(hasRef);

        if (hasRef && clickListener != null) {
            convertView.setOnClickListener(v -> clickListener.onMessageClicked(msg));
        } else {
            convertView.setOnClickListener(null);
        }

        return convertView;
    }
}
