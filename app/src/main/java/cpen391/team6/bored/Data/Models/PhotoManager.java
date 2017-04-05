package cpen391.team6.bored.Data.Models;

/**
 * Created by andytertzakian on 2017-03-16.
 */

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import com.google.firebase.storage.StorageReference;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

public class PhotoManager {
    private static PhotoManager instance = null;
    private StorageReference storageRef;
    private static HashMap photos = new HashMap<String, Drawable>();
    private static HashMap thumbnails = new HashMap<String, Bitmap>();

    private static final long MEGABYTE = 1024 * 1024;

    private PhotoManager() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://beacon-b6fd8.appspot.com");
    }

    public static PhotoManager getInstance() {
        if (instance == null) {
            instance = new PhotoManager();
        }
        return instance;
    }

    public void upload(String eventId, ArrayList<Bitmap> photos) {
        if (photos.size() == 0)
            return;

        ByteArrayOutputStream stream;
        byte[] data;
        int i = 0;

        stream = new ByteArrayOutputStream();
        Bitmap square = Bitmap.createBitmap(photos.get(0), 90, 0, 360, 360);
        Bitmap thumb = Bitmap.createScaledBitmap(square, 90, 90, true);
        thumb.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        data = stream.toByteArray();

        StorageReference smallRef = storageRef.child(eventId + "/thumb.jpg");
        smallRef.putBytes(data);

        for (Bitmap full : photos) {
            stream = new ByteArrayOutputStream();
            full.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            data = stream.toByteArray();

            StorageReference fullRef = storageRef.child(eventId + "/photos/" + i++ + ".jpg");
            fullRef.putBytes(data);
        }

    }

    public Bitmap getThumb(String eventId) {
        if (thumbnails.containsKey(eventId))
            return (Bitmap) thumbnails.get(eventId);
        else
            return null;
    }

    public void downloadThumbs(final String eventId) {
        if (!thumbnails.containsKey(eventId)) {
            StorageReference smallRef = storageRef.child(eventId + "/thumb.jpg");

            smallRef.getBytes(MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap downloaded = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    thumbnails.put(eventId, downloaded);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //leave default image
                }
            });
        }
    }
}
