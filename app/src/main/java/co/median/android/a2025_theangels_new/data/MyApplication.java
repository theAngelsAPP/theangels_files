// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data;
// IMPORTS
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.onesignal.Continue;
import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;
import com.onesignal.notifications.INotificationLifecycleListener;
import com.onesignal.notifications.INotificationWillDisplayEvent;
import com.onesignal.notifications.INotificationClickListener;
import com.onesignal.notifications.INotificationClickEvent;
import org.json.JSONObject;
// MyApplication - Initializes global application state
public class MyApplication extends Application {
    @Override
// Performs on create.
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "urgent_channel";
            CharSequence channelName = "התראות חירום";
            String description = "התראות עם סאונד מיוחד למצבי חירום";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(description);
            Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/raw/emergency_notification");
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.setSound(soundUri, audioAttributes);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);
        OneSignal.initWithContext(this, "3d4f5b57-b984-4d3d-896d-2d3c970c1615");
        OneSignal.getNotifications().requestPermission(false, Continue.none());
        OneSignal.getNotifications().addForegroundLifecycleListener(new INotificationLifecycleListener() {
            @Override
// Performs on will display.
            public void onWillDisplay(INotificationWillDisplayEvent event) {
                event.getNotification().display();
            }
        });
        OneSignal.getNotifications().addClickListener(new INotificationClickListener() {
            @Override
// Performs on click.
            public void onClick(INotificationClickEvent event) {
                JSONObject data = event.getNotification().getAdditionalData();
                if (data == null) return;
                String role = data.optString("role", "");
                String eventId = data.optString("eventId", "");
                Intent intent;
                if ("volunteer".equals(role)) {
                    intent = new Intent(getApplicationContext(), co.median.android.a2025_theangels_new.ui.events.active.EventVolActivity.class);
                } else {
                    intent = new Intent(getApplicationContext(), co.median.android.a2025_theangels_new.ui.events.active.EventUserActivity.class);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("eventId", eventId);
                startActivity(intent);
            }
        });
    }
}
