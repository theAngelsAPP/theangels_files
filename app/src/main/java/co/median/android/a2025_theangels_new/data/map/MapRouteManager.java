// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.map;
// IMPORTS
import android.location.Location;
import android.os.SystemClock;
import android.content.Context;
import android.graphics.Color;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;
import androidx.annotation.NonNull;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.maps.android.SphericalUtil;
import java.util.List;
import co.median.android.a2025_theangels_new.data.models.Event;
import co.median.android.a2025_theangels_new.data.services.EventDataManager;
import co.median.android.a2025_theangels_new.data.map.utils.CustomMarkerHelper;
import co.median.android.a2025_theangels_new.data.map.utils.MapConstants;
import co.median.android.a2025_theangels_new.data.map.utils.MapUtils;
// MapRouteManager - Calculates and draws routes on the map
public class MapRouteManager {
// VARIABLES
    private final DirectionsApiClient apiClient;
    private GoogleMap map;
    private Marker eventMarker;
    private Marker volunteerMarker;
    private Circle pulseCircle;
    private Polyline routeLine;
    private LatLng eventPos;
    private LatLng lastVolunteerPos;
    private long lastFetch = 0L;
    private String eventId;
    private String userImageUrl;
    private String volunteerImageUrl;
    private Context context;
    private EtaListener etaListener;
    public interface EtaListener {
        void onEtaUpdated(int minutes);
    }
// Constructs a new MapRouteManager.
    public MapRouteManager(@NonNull String apiKey) {
        apiClient = new DirectionsApiClient(apiKey);
    }
    public void startTracking(@NonNull Event event, @NonNull GoogleMap googleMap,
                              @NonNull Context ctx) {
        this.map = googleMap;
        this.context = ctx;
        this.eventId = event.getId();
        eventPos = new LatLng(event.getEventLocation().getLatitude(),
                event.getEventLocation().getLongitude());
        setupEventMarker();
        MapUtils.applyDarkStyle(map, ctx);
        map.setBuildingsEnabled(true);
        if (event.getVolunteerLocation() != null) {
            LatLng vol = new LatLng(event.getVolunteerLocation().getLatitude(),
                    event.getVolunteerLocation().getLongitude());
            lastVolunteerPos = vol;
            setupVolunteerMarker(vol);
            requestRoute(vol);
            MapUtils.fitMarkers(map, eventPos, vol);
        } else {
            MapUtils.move3DCamera(map, eventPos, 0f);
        }
    }
// Performs stop tracking.
    public void stopTracking() {
        if (routeLine != null) routeLine.remove();
        if (eventMarker != null) eventMarker.remove();
        if (volunteerMarker != null) volunteerMarker.remove();
        map = null;
    }
// Performs update route if needed.
    public void updateRouteIfNeeded(@NonNull LatLng newPos) {
        if (map == null) return;
        if (lastVolunteerPos == null) {
            lastVolunteerPos = newPos;
            setupVolunteerMarker(newPos);
            MapUtils.fitMarkers(map, eventPos, newPos);
            requestRoute(newPos);
            return;
        }
        float[] res = new float[1];
        Location.distanceBetween(lastVolunteerPos.latitude, lastVolunteerPos.longitude,
                newPos.latitude, newPos.longitude, res);
        long now = SystemClock.elapsedRealtime();
        if (res[0] > 30 || now - lastFetch > 60000) {
            animateMarker(newPos);
            requestRoute(newPos);
        }
    }
// Performs animate marker.
    private void animateMarker(LatLng to) {
        if (volunteerMarker != null) {
            LatLng start = volunteerMarker.getPosition();
            ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
            anim.setDuration(MapConstants.MARKER_ANIM_DURATION_MS);
            anim.setInterpolator(new LinearInterpolator());
            anim.addUpdateListener(v -> {
                float f = (float) v.getAnimatedValue();
                double lat = (to.latitude - start.latitude) * f + start.latitude;
                double lng = (to.longitude - start.longitude) * f + start.longitude;
                volunteerMarker.setPosition(new LatLng(lat, lng));
            });
            anim.start();
            animatePulse(to);
            float bearing = (float) SphericalUtil.computeHeading(start, to);
            volunteerMarker.setRotation(bearing);
            MapUtils.fitMarkers(map, eventPos, to);
        }
        lastVolunteerPos = to;
    }
// Performs animate pulse.
    private void animatePulse(LatLng pos) {
        if (map == null) return;
        if (pulseCircle != null) pulseCircle.remove();
        pulseCircle = map.addCircle(new com.google.android.gms.maps.model.CircleOptions()
                .center(pos)
                .radius(10)
                .strokeColor(Color.TRANSPARENT)
                .fillColor(Color.parseColor("#5581D4FA")));
        ValueAnimator anim = ValueAnimator.ofFloat(10f, 60f);
        anim.setDuration(MapConstants.MARKER_ANIM_DURATION_MS);
        anim.addUpdateListener(v -> {
            if (pulseCircle == null) return;
            float r = (float) v.getAnimatedValue();
            pulseCircle.setRadius(r);
            int alpha = (int) (255 * (1f - r / 60f));
            pulseCircle.setFillColor(Color.argb(alpha, 129, 212, 250));
        });
        anim.start();
    }
// Performs request route.
    private void requestRoute(LatLng from) {
        lastFetch = SystemClock.elapsedRealtime();
        apiClient.fetchRoute(from, eventPos, new DirectionsApiClient.DirectionsCallback() {
            @Override
// Performs on success.
            public void onSuccess(DirectionsApiClient.RouteData data) {
                drawRoute(data.points);
                checkArrival(from);
                int eta = parseMinutes(data.durationText);
                if (eta >= 0 && eventId != null) {
                    EventDataManager.updateVolunteerETA(eventId, eta, null, null);
                    if (etaListener != null) etaListener.onEtaUpdated(eta);
                }
            }
            @Override
// Performs on error.
            public void onError(Throwable t) {
                drawStraightLine(from);
                checkArrival(from);
            }
        });
    }
// Updates the up event marker.
    private void setupEventMarker() {
        if (map == null) return;
        if (eventMarker != null) eventMarker.remove();
        if (context != null && userImageUrl != null && !userImageUrl.isEmpty()) {
            CustomMarkerHelper.loadMarker(context, userImageUrl, 120,
                    Color.parseColor("#2196F3"), descriptor -> {
                        String title = context != null ?
                                context.getString(co.median.android.a2025_theangels_new.R.string.marker_event)
                                : "Event";
                        eventMarker = map.addMarker(new MarkerOptions()
                                .position(eventPos)
                                .icon(descriptor)
                                .anchor(0.5f, 0.5f)
                                .title(title));
                    });
        } else {
            eventMarker = MapHelper.addMarker(map, eventPos, "Event");
        }
    }
// Updates the up volunteer marker.
    private void setupVolunteerMarker(LatLng pos) {
        if (map == null) return;
        if (volunteerMarker != null) volunteerMarker.remove();
        if (context != null && volunteerImageUrl != null && !volunteerImageUrl.isEmpty()) {
            CustomMarkerHelper.loadMarker(context, volunteerImageUrl, 120,
                    Color.parseColor("#FF9800"), descriptor -> {
                        String title = context != null ?
                                context.getString(co.median.android.a2025_theangels_new.R.string.marker_volunteer)
                                : "Volunteer";
                        volunteerMarker = map.addMarker(new MarkerOptions()
                                .position(pos)
                                .icon(descriptor)
                                .anchor(0.5f, 0.5f)
                                .flat(true)
                                .title(title));
                        animatePulse(pos);
                    });
        } else {
            volunteerMarker = MapHelper.addMarker(map, pos, "Volunteer");
            animatePulse(pos);
        }
    }
// Updates the user profile image.
    public void setUserProfileImage(Context ctx, String url) {
        this.context = ctx;
        this.userImageUrl = url;
        if (eventMarker != null) setupEventMarker();
    }
// Updates the volunteer profile image.
    public void setVolunteerProfileImage(Context ctx, String url) {
        this.context = ctx;
        this.volunteerImageUrl = url;
        if (volunteerMarker != null) setupVolunteerMarker(volunteerMarker.getPosition());
    }
// Updates the eta listener.
    public void setEtaListener(EtaListener listener) {
        this.etaListener = listener;
    }
// Performs draw route.
    private void drawRoute(List<LatLng> pts) {
        if (map == null) return;
        if (routeLine != null) routeLine.remove();
        if (pts == null || pts.isEmpty()) return;
        routeLine = map.addPolyline(new PolylineOptions().addAll(pts)
                .width(10f)
                .startCap(new com.google.android.gms.maps.model.RoundCap())
                .endCap(new com.google.android.gms.maps.model.RoundCap())
                .color(Color.CYAN)
                .pattern(java.util.Arrays.asList(
                        new com.google.android.gms.maps.model.Dash(20f),
                        new com.google.android.gms.maps.model.Gap(20f))));
        MapUtils.fadeInPolyline(routeLine);
    }
// Performs draw straight line.
    private void drawStraightLine(LatLng from) {
        if (map == null) return;
        if (routeLine != null) routeLine.remove();
        routeLine = map.addPolyline(new PolylineOptions().add(from).add(eventPos)
                .width(10f)
                .startCap(new com.google.android.gms.maps.model.RoundCap())
                .endCap(new com.google.android.gms.maps.model.RoundCap())
                .color(Color.CYAN)
                .pattern(java.util.Arrays.asList(
                        new com.google.android.gms.maps.model.Dash(20f),
                        new com.google.android.gms.maps.model.Gap(20f))));
        MapUtils.fadeInPolyline(routeLine);
    }
// Performs check arrival.
    private void checkArrival(LatLng vol) {
        float[] res = new float[1];
        Location.distanceBetween(vol.latitude, vol.longitude,
                eventPos.latitude, eventPos.longitude, res);
        if (res[0] < 30 && eventId != null) {
            EventDataManager.updateEventStatus(eventId,
                    co.median.android.a2025_theangels_new.data.models.UserEventStatus.VOLUNTEER_AT_EVENT.getDbValue(),
                    null, null);
        }
    }
// Performs parse minutes.
    private int parseMinutes(String text) {
        if (text == null) return -1;
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)").matcher(text);
        if (m.find()) {
            try { return Integer.parseInt(m.group(1)); } catch (Exception ignore) {}
        }
        return -1;
    }
}
