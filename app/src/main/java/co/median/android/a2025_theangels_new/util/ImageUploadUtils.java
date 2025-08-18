// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.util;

// IMPORTS
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// ImageUploadUtils - Handles uploading bitmaps to Imgur.
public class ImageUploadUtils {

    // VARIABLES
    private static final String IMGUR_CLIENT_ID = "47eaf978d864043";

    // OnImageUploadListener - Callback for upload completion
    public interface OnImageUploadListener {

        // onUploaded - Provides the Imgur link once upload finishes
        void onUploaded(String url);
    }

    // uploadBitmapToImgur - Sends a bitmap to Imgur and returns the URL via listener
    public static void uploadBitmapToImgur(Bitmap bitmap, OnImageUploadListener listener) {
        if (bitmap == null) {
            listener.onUploaded("");
            return;
        }

        // Compress bitmap to PNG and encode to Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageBase64 = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            imageBase64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        }

        // Build OkHttp client and request
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder().add("image", imageBase64).build();
        Request request = new Request.Builder()
                .url("https://api.imgur.com/3/image")
                .header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                .post(requestBody)
                .build();

        // Execute request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ImageUploadUtils", "Imgur upload failed", e);
                listener.onUploaded("");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                try {
                    // Parse response and notify listener with link
                    JSONObject data = new JSONObject(json).getJSONObject("data");
                    String link = data.getString("link");
                    listener.onUploaded(link);
                } catch (Exception e) {
                    listener.onUploaded("");
                }
            }
        });
    }
}
