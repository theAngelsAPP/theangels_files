// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.util;

// IMPORTS

// MessageStyle - Holds resource IDs for message background and text colors
public class MessageStyle {

    // VARIABLES
    private final int backgroundResId;
    private final int textColorResId;

    // MessageStyle - Stores background and text color IDs for a message
    public MessageStyle(int backgroundResId, int textColorResId) {
        this.backgroundResId = backgroundResId;
        this.textColorResId = textColorResId;
    }

    // getBackgroundResId - Returns the background drawable resource ID
    public int getBackgroundResId() {
        return backgroundResId;
    }

    // getTextColorResId - Returns the text color resource ID
    public int getTextColorResId() {
        return textColorResId;
    }
}
