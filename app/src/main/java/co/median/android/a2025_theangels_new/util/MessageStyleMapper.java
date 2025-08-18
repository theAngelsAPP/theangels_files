// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.util;

// IMPORTS
import android.text.TextUtils;

import co.median.android.a2025_theangels_new.R;

// MessageStyleMapper - Maps hex color codes to message style resources
public class MessageStyleMapper {

    // getStyleFromColor - Returns a MessageStyle matching the given hex color or a default gray style
    public static MessageStyle getStyleFromColor(String hexColor) {
        if (TextUtils.isEmpty(hexColor)) {
            return getDefaultStyle();
        }

        String color = hexColor.toLowerCase();
        switch (color) {
            case "#ffd60a":
                return new MessageStyle(R.drawable.message_bg_yellow, android.R.color.black);
            case "#d32f2f":
                return new MessageStyle(R.drawable.message_bg_red, android.R.color.black);
            case "#2196f3":
            case "#1976d2":
                return new MessageStyle(R.drawable.message_bg_blue, android.R.color.black);
            default:
                // Unknown color - return default style
                return getDefaultStyle();
        }
    }

    // getDefaultStyle - Provides the standard gray style used when no color matches
    private static MessageStyle getDefaultStyle() {
        return new MessageStyle(R.drawable.message_bg_default, android.R.color.black);
    }
}
