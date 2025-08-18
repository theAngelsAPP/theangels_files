// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.util;

// IMPORTS
import android.os.Handler;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;

// TimerUtils - Helpers for formatting and running simple timers
public class TimerUtils {

    // formatDuration - Converts seconds to a mm:ss formatted string
    public static String formatDuration(long seconds) {
        long mins = seconds / 60;
        long secs = seconds % 60;
        return String.format(java.util.Locale.getDefault(), "%02d:%02d", mins, secs);
    }

    // startTimer - Updates a TextView every second using either a start time or counter
    public static void startTimer(TextView view, Handler handler,
                                  LongSupplier startTimeSupplier,
                                  BooleanSupplier runningSupplier,
                                  AtomicLong counter) {
        handler.post(new Runnable() {
            @Override public void run() {
                long elapsed;
                long start = startTimeSupplier.getAsLong();
                if (start > 0) {
                    // Calculate elapsed time since start
                    elapsed = (System.currentTimeMillis() - start) / 1000;
                } else {
                    // Use counter when no start time is provided
                    elapsed = counter.get();
                    if (runningSupplier.getAsBoolean()) counter.incrementAndGet();
                }
                view.setText(formatDuration(elapsed));
                // Schedule next tick in one second
                handler.postDelayed(this, 1000);
            }
        });
    }

    // stopTimer - Removes all pending timer callbacks
    public static void stopTimer(Handler handler) {
        handler.removeCallbacksAndMessages(null);
    }
}
