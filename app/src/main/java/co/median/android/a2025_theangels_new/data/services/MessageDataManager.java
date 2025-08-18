// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.services;
// IMPORTS
import android.util.Log;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import co.median.android.a2025_theangels_new.data.models.Message;
import co.median.android.a2025_theangels_new.data.models.MessageType;
// MessageDataManager - Handles message retrieval and storage
public class MessageDataManager {
// VARIABLES
    private static final String TAG = "MessageDataManager";
    public interface MessagesCallback {
        void onMessagesLoaded(ArrayList<Message> messages, Map<String, MessageType> types);
        void onError(Exception e);
    }
// Returns the messages with types.
    public static void getMessagesWithTypes(MessagesCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("messageType").get()
                .addOnSuccessListener(typeSnap -> {
                    Map<String, MessageType> typeMap = new HashMap<>();
                    for (DocumentSnapshot doc : typeSnap.getDocuments()) {
                        MessageType type = doc.toObject(MessageType.class);
                        if (type != null) {
                            typeMap.put(type.getTypeName(), type);
                        }
                    }
                    db.collection("messages").get()
                            .addOnSuccessListener(msgSnap -> {
                                ArrayList<Message> list = new ArrayList<>();
                                for (DocumentSnapshot doc : msgSnap.getDocuments()) {
                                    Message m = doc.toObject(Message.class);
                                    if (m != null) {
                                        list.add(m);
                                    }
                                }
                                callback.onMessagesLoaded(list, typeMap);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error fetching messages", e);
                                callback.onError(e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching message types", e);
                    callback.onError(e);
                });
    }
}
