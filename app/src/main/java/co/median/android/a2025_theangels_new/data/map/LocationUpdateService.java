// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.map;
// IMPORTS
import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.GeoPoint;
import co.median.android.a2025_theangels_new.R;
import co.median.android.a2025_theangels_new.data.services.EventDataManager;
// LocationUpdateService - Service that pushes location updates
public class LocationUpdateService extends Service {
// VARIABLES
    private static final String CHANNEL_ID = "route_updates";
    private FusedLocationProviderClient client;
    private LocationCallback callback;
    private String eventId;
    @Override
// Performs on start command.
    public int onStartCommand(Intent intent, int flags, int startId) {
        eventId = intent != null ? intent.getStringExtra("eventId") : null;
        client = LocationServices.getFusedLocationProviderClient(this);
        startForeground(1, createNotification());
        startUpdates();
        return START_STICKY;
    }
// Performs create notification.
    private Notification createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Route Updates", NotificationManager.IMPORTANCE_LOW);
            NotificationManager mgr = getSystemService(NotificationManager.class);
            if (mgr != null) mgr.createNotificationChannel(channel);
        }
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Updating location")
                .setSmallIcon(R.drawable.ic_location)
                .build();
    }
// Performs start updates.
    private void startUpdates() {
        LocationRequest req = LocationRequest.create();
        req.setInterval(60000);
        req.setFastestInterval(30000);
        req.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        callback = new LocationCallback() {
            @Override
// Performs on location result.
            public void onLocationResult(LocationResult result) {
                Location loc = result.getLastLocation();
                if (loc != null && eventId != null) {
                    GeoPoint gp = new GeoPoint(loc.getLatitude(), loc.getLongitude());
                    EventDataManager.updateVolunteerLocation(eventId, gp, null, null);
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            client.requestLocationUpdates(req, callback, Looper.getMainLooper());
        }
    }
    @Override
// Performs on destroy.
    public void onDestroy() {
        if (client != null && callback != null) client.removeLocationUpdates(callback);
        super.onDestroy();
    }
    @Nullable
    @Override
// Performs on bind.
    public IBinder onBind(Intent intent) {
        return null;
    }
}
