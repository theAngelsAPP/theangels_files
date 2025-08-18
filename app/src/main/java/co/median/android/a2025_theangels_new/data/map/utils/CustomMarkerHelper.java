// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.map.utils;
// IMPORTS
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
// CustomMarkerHelper - Builds custom markers for the map
public class CustomMarkerHelper {
    public interface MarkerCallback {
        void onMarkerReady(@Nullable BitmapDescriptor descriptor);
    }
    public static void loadMarker(Context ctx, String url, int size, @ColorInt int borderColor,
                                  MarkerCallback cb) {
        Glide.with(ctx).asBitmap().load(url).circleCrop().into(new CustomTarget<Bitmap>(size, size) {
            @Override
// Performs on resource ready.
            public void onResourceReady(Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(output);
                canvas.drawBitmap(resource, 0, 0, null);
                Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
                p.setStyle(Paint.Style.STROKE);
                p.setColor(borderColor);
                p.setStrokeWidth(6f);
                float r = size / 2f;
                canvas.drawCircle(r, r, r - 3f, p);
                cb.onMarkerReady(BitmapDescriptorFactory.fromBitmap(output));
            }
            @Override
// Performs on load cleared.
            public void onLoadCleared(@Nullable Drawable placeholder) { }
        });
    }
}
