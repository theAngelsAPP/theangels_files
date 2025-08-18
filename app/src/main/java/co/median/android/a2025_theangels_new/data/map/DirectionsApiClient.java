// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.map;
// IMPORTS
import androidx.annotation.NonNull;
import com.google.android.gms.maps.model.LatLng;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
// DirectionsApiClient - Communicates with the directions API
public class DirectionsApiClient {
// VARIABLES
    public static class DirectionsResponse {
        public List<Route> routes;
    }
    public static class Route {
        public OverviewPolyline overview_polyline;
        public List<Leg> legs;
    }
    public static class OverviewPolyline { public String points; }
    public static class Leg {
        public Distance distance;
        public Duration duration;
    }
    public static class Distance { public String text; }
    public static class Duration { public String text; }
    interface ApiService {
        @GET("directions/json")
        Call<DirectionsResponse> getDirections(
                @Query("origin") String origin,
                @Query("destination") String destination,
                @Query("key") String key);
    }
    private final ApiService service;
    private final String apiKey;
// Constructs a new DirectionsApiClient.
    public DirectionsApiClient(@NonNull String apiKey) {
        this.apiKey = apiKey;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/")
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(ApiService.class);
    }
    public static class RouteData {
        public List<com.google.android.gms.maps.model.LatLng> points;
        public String distanceText;
        public String durationText;
    }
    public interface DirectionsCallback {
        void onSuccess(RouteData data);
        void onError(Throwable t);
    }
// Performs fetch route.
    public void fetchRoute(LatLng origin, LatLng dest, DirectionsCallback cb) {
        String o = origin.latitude + "," + origin.longitude;
        String d = dest.latitude + "," + dest.longitude;
        service.getDirections(o, d, apiKey).enqueue(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(@NonNull Call<DirectionsResponse> call,
                                   @NonNull Response<DirectionsResponse> response) {
                DirectionsResponse body = response.body();
                if (response.isSuccessful() && body != null &&
                        body.routes != null && !body.routes.isEmpty()) {
                    Route r = body.routes.get(0);
                    RouteData data = new RouteData();
                    if (r.overview_polyline != null && r.overview_polyline.points != null) {
                        data.points = com.google.maps.android.PolyUtil.decode(r.overview_polyline.points);
                    }
                    if (r.legs != null && !r.legs.isEmpty()) {
                        Leg l = r.legs.get(0);
                        if (l.distance != null) data.distanceText = l.distance.text;
                        if (l.duration != null) data.durationText = l.duration.text;
                    }
                    cb.onSuccess(data);
                } else {
                    cb.onError(new Exception("Directions response empty"));
                }
            }
            @Override
// Performs on failure.
            public void onFailure(@NonNull Call<DirectionsResponse> call, @NonNull Throwable t) {
                cb.onError(t);
            }
        });
    }
}
