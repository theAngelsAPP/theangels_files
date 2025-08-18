// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.services;
// IMPORTS
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.DocumentSnapshot;
import co.median.android.a2025_theangels_new.R;
import co.median.android.a2025_theangels_new.data.models.Event;
import co.median.android.a2025_theangels_new.data.models.EventStatus;
import co.median.android.a2025_theangels_new.ui.events.active.EventUserActivity;
import co.median.android.a2025_theangels_new.ui.events.active.EventVolActivity;
import co.median.android.a2025_theangels_new.data.services.EventDataManager;
// EmergencyStatusService - Monitors and updates emergency status
public class EmergencyStatusService extends Service {
// VARIABLES
    private static final String CHANNEL_ID = "emergency_status";
    private static final int NOTIFICATION_ID = 22;
    private ListenerRegistration listener;
    private String eventId;
    private boolean isVolunteer;
    @Override
// Performs on start command.
    public int onStartCommand(Intent intent, int flags, int startId) {
        eventId = intent != null ? intent.getStringExtra("eventId") : null;
        String role = intent != null ? intent.getStringExtra("role") : null;
        isVolunteer = "volunteer".equals(role);
        if (eventId == null) {
            stopSelf();
            return START_NOT_STICKY;
        }
        startForeground(NOTIFICATION_ID, buildNotification(getString(R.string.status_looking_for_volunteer)));
        attachListener();
        return START_STICKY;
    }
// Performs create channel.
    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Emergency Status",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Active emergency tracking");
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            channel.enableVibration(true);
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(channel);
        }
    }
// Performs build notification.
    private Notification buildNotification(String content) {
        createChannel();
        Intent intent = new Intent(this, isVolunteer ? EventVolActivity.class : EventUserActivity.class);
        intent.putExtra("eventId", eventId);
        PendingIntent pi = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_IMMUTABLE : 0));
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.status_title))
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setSmallIcon(R.drawable.ic_notifications_on)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setContentIntent(pi)
                .build();
    }
// Performs attach listener.
    private void attachListener() {
        listener = EventDataManager.listenToEvent(eventId, (snapshot, e) -> {
            if (e != null) {
                stopSelf();
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                updateFromSnapshot(snapshot);
            } else {
                stopSelf();
            }
        });
    }
// Performs update from snapshot.
    private void updateFromSnapshot(DocumentSnapshot snap) {
        Event ev = snap.toObject(Event.class);
        if (ev == null) {
            stopSelf();
            return;
        }
        EventStatus status = EventStatus.fromDbValue(ev.getEventStatus());
        if (status == null) return;
        String text = mapStatus(status);
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (nm != null) nm.notify(NOTIFICATION_ID, buildNotification(text));
        if (status == EventStatus.EVENT_FINISHED) {
            stopSelf();
        }
    }
// Performs map status.
    private String mapStatus(EventStatus status) {
        switch (status) {
            case LOOKING_FOR_VOLUNTEER:
                return getString(R.string.status_looking_for_volunteer);
            case VOLUNTEER_ON_THE_WAY:
                return getString(R.string.status_volunteer_on_the_way);
            case VOLUNTEER_AT_EVENT:
                return getString(R.string.status_volunteer_arrived);
            case EVENT_FINISHED:
                return getString(R.string.status_event_finished);
            default:
                return "";
        }
    }
    @Override
// Performs on destroy.
    public void onDestroy() {
        if (listener != null) listener.remove();
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (nm != null) nm.cancel(NOTIFICATION_ID);
        super.onDestroy();
    }
    @Nullable
    @Override
// Performs on bind.
    public IBinder onBind(Intent intent) {
        return null;
    }
}
